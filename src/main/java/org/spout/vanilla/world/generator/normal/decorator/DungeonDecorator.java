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
package org.spout.vanilla.world.generator.normal.decorator;

import java.util.Random;

import org.spout.api.generator.WorldGeneratorObject;
import org.spout.api.generator.biome.BiomeDecorator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.vanilla.world.generator.normal.object.DungeonObject;

public class DungeonDecorator implements BiomeDecorator {

	private final static int PROBABILITY = 2000;

	@Override
	public void populate(Chunk chunk, Random random) {
		if (random.nextInt(PROBABILITY) == 0) {
			final World world = chunk.getWorld();
			final int worldX = chunk.getX() * 16 + random.nextInt(16);
			final int worldY = chunk.getY() * 16 + random.nextInt(16);
			final int worldZ = chunk.getZ() * 16 + random.nextInt(16);
			WorldGeneratorObject dungeon = new DungeonObject(random);
			if (dungeon.canPlaceObject(world, worldX, worldY, worldZ)) {
				dungeon.placeObject(world, worldX, worldY, worldZ);
			}
		}
	}
}
