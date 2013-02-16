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
package org.spout.vanilla.plugin.world.generator.normal.decorator;

import org.spout.vanilla.plugin.world.generator.decorator.VariableAmountDecorator;
import java.util.Random;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.block.BlockFace;

import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.plugin.material.block.plant.Mushroom;
import org.spout.vanilla.plugin.world.generator.normal.NormalGenerator;

public class MushroomDecorator extends VariableAmountDecorator {
	private int odd = 64;

	public MushroomDecorator() {
		super(1, 0);
	}

	@Override
	public void populate(Chunk chunk, Random random) {
		if (chunk.getY() != 4) {
			return;
		}
		if (random.nextInt(odd) != 0) {
			return;
		}
		final World world = chunk.getWorld();
		final int amount = getAmount(random);
		for (byte count = 0; count < amount; count++) {
			final int x = chunk.getBlockX(random);
			final int y = random.nextBoolean() ? NormalGenerator.HEIGHT - 1 : random.nextInt(64);
			final int z = chunk.getBlockZ(random);
			final Mushroom mushroom = random.nextInt(4) == 0 ? VanillaMaterials.RED_MUSHROOM : VanillaMaterials.BROWN_MUSHROOM;
			for (byte size = 6; size > 0; size--) {
				final int xx = x - 7 + random.nextInt(15);
				final int zz = z - 7 + random.nextInt(15);
				final int yy = getHighestWorkableBlock(world, xx, y, zz);
				if (yy != -1 && world.getBlockMaterial(xx, yy, zz) == VanillaMaterials.AIR
						&& mushroom.isValidPosition(world.getBlock(xx, yy, zz), BlockFace.BOTTOM, false)) {
					world.setBlockMaterial(xx, yy, zz, mushroom, (short) 0, null);
				}
			}
		}
	}

	private int getHighestWorkableBlock(World w, int x, int y, int z) {
		while (!w.getBlockMaterial(x, y, z).isOpaque()) {
			if (--y <= 0) {
				return -1;
			}
		}
		return ++y;
	}

	public void setOdd(int odd) {
		this.odd = odd;
	}
}
