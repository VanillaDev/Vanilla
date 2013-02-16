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
package org.spout.vanilla.plugin.world.generator.theend;

import java.util.Random;

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import net.royawesome.jlibnoise.module.source.Perlin;

import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;

import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.plugin.material.block.Liquid;
import org.spout.vanilla.plugin.world.generator.biome.VanillaSingleBiomeGenerator;
import org.spout.vanilla.plugin.world.generator.biome.VanillaBiomes;

public class TheEndGenerator extends VanillaSingleBiomeGenerator {
	private static final int ISLAND_RADIUS = 128;
	private static final int ISLAND_BOTTOM = 8;
	private static final int ISLAND_SURFACE = 58;
	private static final int ISLAND_TOP = 64;
	private static final double LOWER_SMOOTH_SIZE = ISLAND_SURFACE - ISLAND_BOTTOM;
	private static final double UPPER_SMOOTH_SIZE = ISLAND_TOP - ISLAND_SURFACE;
	public static final int HEIGHT = ISLAND_TOP;
	// noise for generation
	private static final Perlin PERLIN = new Perlin();
	private static final ScalePoint NOISE = new ScalePoint();

	static {
		PERLIN.setFrequency(0.01);
		PERLIN.setLacunarity(2);
		PERLIN.setNoiseQuality(NoiseQuality.BEST);
		PERLIN.setPersistence(0.5);
		PERLIN.setOctaveCount(16);

		NOISE.SetSourceModule(0, PERLIN);
		NOISE.setxScale(1);
		NOISE.setyScale(1);
		NOISE.setzScale(1);
	}

	public TheEndGenerator() {
		super(VanillaBiomes.ENDSTONE);
		hasVoidBellowZero(true);
	}

	@Override
	public void registerBiomes() {
		super.registerBiomes();
		register(VanillaBiomes.ENDSTONE);
	}

	@Override
	public String getName() {
		return "VanillaTheEnd";
	}

	@Override
	protected void generateTerrain(CuboidBlockMaterialBuffer blockData, int x, int y, int z, BiomeManager biomes, long seed) {
		PERLIN.setSeed((int) seed * 23);
		final Vector3 size = blockData.getSize();
		final int sizeX = size.getFloorX();
		final int sizeY = Math.min(size.getFloorY(), HEIGHT);
		final int sizeZ = size.getFloorZ();
		final double[][][] noise = WorldGeneratorUtils.fastNoise(NOISE, sizeX, sizeY, sizeZ, 4, x, y, z);
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				final int totalX = x + xx;
				final int totalZ = z + zz;
				final double distance = Math.sqrt(totalX * totalX + totalZ * totalZ);
				for (int yy = 0; yy < sizeY; yy++) {
					final int totalY = y + yy;
					double density = noise[xx][yy][zz] * 0.5 + 0.5;
					if (totalY < ISLAND_SURFACE) {
						density -= square(1 / LOWER_SMOOTH_SIZE * (totalY - ISLAND_SURFACE));
					} else if (totalY >= ISLAND_SURFACE) {
						density -= square(1 / UPPER_SMOOTH_SIZE * (totalY - ISLAND_SURFACE));
					}
					density *= ISLAND_RADIUS / distance;
					if (density >= 1) {
						blockData.set(totalX, totalY, totalZ, VanillaMaterials.END_STONE);
					}
				}
			}
		}
	}

	private static double square(double x) {
		return x * x;
	}

	@Override
	public Point getSafeSpawn(World world) {
		final Random random = new Random();
		for (byte attempts = 0; attempts < 10; attempts++) {
			final int x = random.nextInt(32) - 16;
			final int z = random.nextInt(32) - 16;
			final int y = getHighestSolidBlock(world, x, z);
			if (y != -1) {
				return new Point(world, x, y + 0.5f, z);
			}
		}
		return new Point(world, 0, 80, 0);
	}

	private int getHighestSolidBlock(World world, int x, int z) {
		int y = HEIGHT - 1;
		while (world.getBlockMaterial(x, y, z).isInvisible()) {
			if (--y == 0 || world.getBlockMaterial(x, y, z) instanceof Liquid) {
				return -1;
			}
		}
		return ++y;
	}

	@Override
	public int[][] getSurfaceHeight(World world, int chunkX, int chunkY) {
		int[][] heights = new int[Chunk.BLOCKS.SIZE][Chunk.BLOCKS.SIZE];
		for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
				heights[x][z] = ISLAND_SURFACE;
			}
		}
		return heights;
	}
}
