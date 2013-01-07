/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.plugin.material.block.ore;

import org.spout.api.inventory.ItemStack;

import org.spout.vanilla.plugin.data.tool.ToolLevel;
import org.spout.vanilla.plugin.data.tool.ToolType;
import org.spout.vanilla.api.material.InitializableMaterial;
import org.spout.vanilla.api.material.TimedCraftable;
import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.api.material.block.Ore;
import org.spout.vanilla.plugin.material.block.component.FurnaceBlock;

public class RedstoneOre extends Ore implements TimedCraftable, InitializableMaterial {
	private final boolean glowing;

	public RedstoneOre(String name, int id, boolean glowing) {
		super(name, id, "model://Vanilla/materials/block/solid/redstoneore/redstoneore.spm");
		this.glowing = glowing;
		this.setHardness(3.0F).setResistance(5.0F).addMiningType(ToolType.PICKAXE).setMiningLevel(ToolLevel.STONE);
	}

	@Override
	public void initialize() {
		getDrops().clear();
		getDrops().add(VanillaMaterials.REDSTONE_DUST, 4);
		getDrops().SILK_TOUCH.add(VanillaMaterials.REDSTONE_ORE);
	}

	@Override
	public ItemStack getResult() {
		return new ItemStack(VanillaMaterials.REDSTONE_DUST, 1);
	}

	@Override
	public float getCraftTime() {
		return FurnaceBlock.SMELT_TIME;
	}

	@Override
	public byte getLightLevel(short data) {
		return glowing ? (byte) 13 : (byte) 0;
	}

	/**
	 * Whether this redstone ore block material is glowing
	 * @return true if glowing
	 */
	public boolean isGlowing() {
		return glowing;
	}
}
