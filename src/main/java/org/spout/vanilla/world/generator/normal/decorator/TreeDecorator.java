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
package org.spout.vanilla.world.generator.normal.decorator;

import java.util.Random;

import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.Decorator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;

import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.world.generator.VanillaBiomes;
import org.spout.vanilla.world.generator.normal.object.tree.HugeTreeObject;
import org.spout.vanilla.world.generator.normal.object.tree.ShrubObject;
import org.spout.vanilla.world.generator.normal.object.tree.SmallTreeObject;
import org.spout.vanilla.world.generator.normal.object.tree.TreeObject;
import org.spout.vanilla.world.generator.normal.object.tree.TreeObject.TreeType;

public class TreeDecorator extends Decorator {
	@Override
	public void populate(Chunk chunk, Random random) {
		if (chunk.getY() != 4) {
			return;
		}
		final Biome biome = chunk.getBiomeType(7, 7, 7);
		final World world = chunk.getWorld();
		final byte amount = getNumberOfTrees(biome);
		for (byte i = 0; i < amount; i++) {
			final TreeObject tree = getTree(random, biome);
			if (tree == null) {
				continue;
			}

			final int worldX = chunk.getBlockX(random);
			final int worldZ = chunk.getBlockZ(random);
			final int worldY = getHighestWorkableBlock(world, worldX, worldZ);
			if (!tree.canPlaceObject(world, worldX, worldY, worldZ)) {
				continue;
			}

			tree.placeObject(world, worldX, worldY, worldZ);
			tree.randomizeHeight();
		}
	}

	private int getHighestWorkableBlock(World w, int x, int z) {
		int y = 127;
		while (w.getBlockMaterial(x, y, z) == VanillaMaterials.AIR) {
			y--;
			if (y == 0) {
				return -1;
			}
		}
		y++;
		return y;
	}

	// trees in jungle : 50
	//		jungle trees have a custom height of random.nextInt(3) + random.nextInt(7) + 4 (according to mc)
	// trees in forest : 10
	// trees in swamp : 2
	// plains don't have trees!!!
	private byte getNumberOfTrees(Biome biome) {
		if (biome == VanillaBiomes.FOREST) {
			return 10;
		} else if (biome == VanillaBiomes.SWAMP) {
			return 2;
		} else if (biome == VanillaBiomes.JUNGLE) {
			return 50;
		} else {
			return 0;
		}
	}

	private TreeObject getTree(Random random, Biome biome) {
		if (biome == VanillaBiomes.FOREST) {
			return new SmallTreeObject(random, TreeType.OAK);
		} else if (biome == VanillaBiomes.SWAMP) {
			final SmallTreeObject tree = new SmallTreeObject(random, TreeType.OAK);
			tree.addLeavesVines(true);
			tree.setLeavesRadiusIncreaseXZ((byte) 1);
			return tree;
		} else if (biome == VanillaBiomes.JUNGLE) {
			if (random.nextInt(27) == 0) {
				return new HugeTreeObject(random);
			} else if (random.nextInt(3) == 0) {
				final SmallTreeObject tree = new SmallTreeObject(random, TreeType.JUNGLE);
				tree.setBaseHeight((byte) 4);
				tree.setRandomHeight((byte) 10);
				tree.addLogVines(true);
				return tree;
			} else {
				return new ShrubObject(random);
			}
		} else {
			return null;
		}
	}
}
