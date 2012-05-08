/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.vanilla.material.item.generic;

import org.spout.api.entity.Entity;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Placeable;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.GenericMaterialSource;
import org.spout.api.material.source.MaterialSource;

import org.spout.vanilla.util.VanillaPlayerUtil;

public class BlockItem extends VanillaItemMaterial implements Placeable {
	GenericMaterialSource onPlace;

	public BlockItem(String name, int id, BlockMaterial onPlaceMaterial) {
		this(name, id, onPlaceMaterial, (short) 0);
	}

	public BlockItem(String name, int id, BlockMaterial onPlaceMaterial, short onPlaceData) {
		super(name, id);
		if (onPlaceMaterial == null) {
			throw new NullPointerException("Block block can not be null");
		} else {
			this.onPlace = new GenericMaterialSource(onPlaceMaterial, onPlaceData);
		}
	}

	@Override
	public boolean canPlace(Block block, short data, BlockFace against, boolean isClickedBlock) {
		return ((BlockMaterial) this.onPlace.getSubMaterial()).canPlace(block, data, against, isClickedBlock);
	}

	@Override
	public boolean onPlacement(Block block, short data, BlockFace against, boolean isClickedBlock) {
		if (block.getSource() instanceof Entity) {
			Entity entity = (Entity) block.getSource();
			if (!entity.getInventory().isCurrentItem(this)) {
				throw new IllegalStateException("Interaction with an controller that is not holding this block!");
			}
			if (VanillaPlayerUtil.isSurvival(entity)) {
				if (!entity.getInventory().addCurrentItemAmount(-1)) {
					throw new IllegalStateException("ControllerType is holding zero or negative sized item!");
				}
			}
		}

		System.out.println("Placing Block " + getBlock() + " on Interact at " + block);

		block.setMaterial(this.getBlock()).update();
		return true;
	}

	public MaterialSource getBlock() {
		return onPlace;
	}
}
