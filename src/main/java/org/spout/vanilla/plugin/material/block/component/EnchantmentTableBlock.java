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
package org.spout.vanilla.plugin.material.block.component;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;

import org.spout.vanilla.api.data.MoveReaction;
import org.spout.vanilla.plugin.component.substance.material.EnchantmentTable;
import org.spout.vanilla.plugin.data.Instrument;
import org.spout.vanilla.plugin.data.drops.flag.ToolTypeFlags;
import org.spout.vanilla.plugin.material.block.Directional;
import org.spout.vanilla.plugin.resources.VanillaMaterialModels;

public class EnchantmentTableBlock extends ComponentMaterial implements Directional {
	public EnchantmentTableBlock(String name, int id) {
		super(name, id, EnchantmentTable.class, VanillaMaterialModels.ENCHANTMENT_TABLE);
		this.setHardness(5.0F).setResistance(2000.0F).setOpacity(0).setOcclusion((short) 0, BlockFace.BOTTOM);
		this.getDrops().NOT_CREATIVE.addFlags(ToolTypeFlags.PICKAXE);
	}

	@Override
	public MoveReaction getMoveReaction(Block block) {
		return MoveReaction.DENY;
	}

	@Override
	public Instrument getInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public boolean isPlacementSuppressed() {
		return true;
	}

	@Override
	public BlockFace getFacing(Block block) {
		return BlockFace.TOP;
	}

	@Override
	public void setFacing(Block block, BlockFace facing) {
	}
}
