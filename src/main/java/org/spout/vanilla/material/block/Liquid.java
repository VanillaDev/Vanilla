/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.material.block;

import java.util.Collections;
import java.util.List;

import org.spout.api.Source;
import org.spout.api.collision.CollisionStrategy;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.range.EffectRange;
import org.spout.api.util.LogicUtil;

import org.spout.vanilla.material.VanillaBlockMaterial;

public abstract class Liquid extends VanillaBlockMaterial implements DynamicMaterial, Source {
	private final boolean flowing;
	private static boolean useDelay = false; //TODO: This is here to prevent a lot of problems...

	public Liquid(String name, int id, boolean flowing) {
		super(name, id);
		this.flowing = flowing;
		this.setLiquidObstacle(false).setHardness(100.0F).setResistance(166.7F).setOpacity(2).setCollision(CollisionStrategy.SOFT);
	}

	@Override
	public boolean hasPhysics() {
		return true;
	}

	@Override
	public List<ItemStack> getDrops(Block block, ItemStack holding) {
		return Collections.emptyList();
	}

	@Override
	public void onUpdate(Block block) {
		super.onUpdate(block);
		if (useDelay) {
			//TODO: Dynamic updates are not continuous
			// It will flow upon placement, but requires physics to continue
			block.dynamicUpdate(block.getWorld().getAge() + this.getTickDelay());
		} else {
			this.doPhysics(block);
		}
	}

	private boolean onFlow(Block block) {
		boolean flowed = false;
		// Flow below, and if not possible, spread outwards
		if (!this.onFlow(block, BlockFace.BOTTOM)) {
			for (BlockFace face : BlockFaces.NESW) {
				flowed |= this.onFlow(block, face);
			}
		}
		return flowed;
	}

	/**
	 * Let's this liquid flow from the block to the direction given
	 * @param block to flow from
	 * @param to flow to
	 * @return True if flowing was successful
	 */
	public boolean onFlow(Block block, BlockFace to) {
		int level;
		if (to == BlockFace.BOTTOM) {
			level = this.getMaxLevel();
		} else {
			level = this.getLevel(block) - 1;
			if (level < 0) {
				return false;
			}
		}
		block = block.translate(to).setSource(this);
		BlockMaterial material = block.getMaterial();
		if (this.isMaterial(material)) {
			if (this.isSource(block)) {
				return true;
			} else {
				// Compare levels
				if (level > this.getLevel(block)) {
					if (this.flowing) {
						// Make sure the material is adjusted
						block.setMaterial(this.getFlowingMaterial(), block.getData());
					}
					this.setLevel(block, level);
					if (to == BlockFace.BOTTOM) {
						this.setFlowingDown(block, true);
					}
					// Update blocks around
					return true;
				}
			}
		} else {
			if (material instanceof VanillaBlockMaterial) {
				if (((VanillaBlockMaterial) material).isLiquidObstacle()) {
					return false;
				}
			} else if (material.isPlacementObstacle()) {
				return false;
			}
			// Create a new liquid
			this.onSpread(block, level, to.getOpposite());
			return true;
		}
		return false;
	}

	/**
	 * Called when this liquid created a new liquid because it spread
	 * @param block of the Liquid that got created
	 * @param from where it spread
	 * @return True to notify spreading was allowed, False to deny
	 */
	public void onSpread(Block block, int newLevel, BlockFace from) {
		block.getMaterial().onDestroy(block);
		block.setMaterial(this.getFlowingMaterial());
		this.setLevel(block, newLevel);
		if (from == BlockFace.TOP) {
			this.setFlowingDown(block, true);
		}
	}

	/**
	 * Gets the maximum possible water level
	 * @return the max level
	 */
	public abstract int getMaxLevel();

	/**
	 * Gets the liquid type to use for flowing liquids
	 * @return the flowing material
	 */
	public abstract Liquid getFlowingMaterial();

	/**
	 * Gets the liquid type to use for stationary liquids
	 * @return the stationary material
	 */
	public abstract Liquid getStationaryMaterial();

	/**
	 * Checks if the material given is either the flowing or stationary type of this Liquid
	 * @param material to check
	 * @return True if it is this liquid, False if not
	 */
	public boolean isMaterial(BlockMaterial material) {
		return material.equals(this.getFlowingMaterial(), this.getStationaryMaterial());
	}

	/**
	 * Gets the level a liquid receives from nearby blocks<br>
	 * The level equals the expected level of the block specified
	 * @param block of the liquid
	 * @return the level, or negative if it has no liquids nearby to use
	 */
	public int getReceivingLevel(Block block) {
		if (this.isMaterial(block.translate(BlockFace.TOP).getMaterial())) {
			return this.getMaxLevel();
		} else {
			int max = -2;
			int counter = 0;
			Block neigh;
			for (BlockFace face : BlockFaces.NESW) {
				neigh = block.translate(face);
				if (this.isMaterial(neigh.getMaterial())) {
					max = Math.max(max, this.getLevel(neigh) - 1);
					if (this.hasFlowSource() && this.isSource(neigh) && !this.isFlowingDown(neigh)) {
						counter++;
						if (counter >= 2) {
							return this.getMaxLevel();
						}
					}
				}
			}
			return max;
		}
	}

	/**
	 * Gets the level of a liquid
	 * @param block of the liquid
	 * @return the level, or negative if it has no liquid
	 */
	public abstract int getLevel(Block block);

	/**
	 * Sets the level of a liquid<br>
	 * A level lower than 0 converts the liquid into air
	 * @param block of the liquid
	 * @param level to set to
	 */
	public abstract void setLevel(Block block, int level);

	/**
	 * Gets if this liquid can create sources by flowing
	 * @return True if it can make sources when flowing, False if not
	 */
	public abstract boolean hasFlowSource();

	/**
	 * Gets the tick delay between updates of this liquid
	 * @return the tick delay of this Liquid
	 */
	public abstract int getTickDelay();

	/**
	 * Sets whether this liquid is flowing down
	 * @param block of the liquid
	 * @param flowing down state of the liquid
	 */
	public void setFlowingDown(Block block, boolean flowing) {
		block.setData(LogicUtil.setBit(block.getData(), 0x8, flowing));
	}

	/**
	 * Gets whether this liquid is flowing down
	 * @param block of the liquid
	 * @return True if is flowing down, False if not
	 */
	public boolean isFlowingDown(Block block) {
		return LogicUtil.getBit(block.getData(), 0x8);
	}

	/**
	 * Gets whether this liquid is a source
	 * @param block of the liquid
	 * @return True if it is a source, False if not
	 */
	public boolean isSource(Block block) {
		return (block.getData() & 0x7) == 0x0;
	}

	public boolean isFlowing() {
		return flowing;
	}

	@Override
	public boolean isPlacementObstacle() {
		return false;
	}

	@Override
	public void onPlacement(Block b, Region r, long currentTime) {
	}

	private boolean doPhysics(Block block) {
		int level;
		if (this.isSource(block)) {
			level = this.getMaxLevel();
			// Still flowing down?
			if (this.isFlowingDown(block) && !this.isMaterial(block.translate(BlockFace.TOP).getMaterial())) {
				this.setFlowingDown(block, false);
				this.setLevel(block, level - 1);
				// Update blocks around
				return true;
			} else {
				return this.onFlow(block);
			}
		}
		// Update level of liquid
		level = this.getReceivingLevel(block);
		int oldlevel = this.getLevel(block);
		if (level != oldlevel) {
			this.setLevel(block, level);
			if (level < oldlevel) {
				// Update blocks around
				block = block.setSource(this);
				return true;
			}
		}

		return this.onFlow(block);
	}

	@Override
	public void onDynamicUpdate(Block block, Region r, long updateTime, long lastUpdateTime, int data, Object hint) {
		if (useDelay) {
			// TODO: Does not always update, or does not always fire this update function
			this.doPhysics(block);
		}
	}

	@Override
	public EffectRange getDynamicRange() {
		return EffectRange.THIS_AND_NEIGHBORS;
	}
}
