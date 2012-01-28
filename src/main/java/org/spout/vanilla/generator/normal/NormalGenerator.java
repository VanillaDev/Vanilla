/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.  
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license, 
 * including the MIT license.
 */
package org.spout.vanilla.generator.normal;

import java.util.ArrayList;
import java.util.Random;

import net.royawesome.jlibnoise.module.source.Perlin;

import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.util.cuboid.CuboidShortBuffer;
import org.spout.vanilla.VanillaBlocks;

public class NormalGenerator implements WorldGenerator {
	int seed = 42;
	Perlin layerCount = new Perlin(), heightMap = new Perlin();
	ArrayList<Perlin> layers = new ArrayList<Perlin>();


	public NormalGenerator() {
		layerCount.setSeed(seed + 10);
		layerCount.setOctaveCount(5);
		
		heightMap.setSeed(seed);
		heightMap.setOctaveCount(5);
	}

	public Perlin getLayer(int layer) {
		if(layer >= 0) {
			if(layer < layers.size()) {
				return layers.get(layer);
			} else {
				Perlin p = new Perlin();
				p.setSeed(seed + layer);
				p.setOctaveCount(5);
				layers.add(p);
				return p;
			}
		} else {
			return null;
		}
	}

	private final Populator[] populators = new Populator[]{new OrePopulator(), new TreePopulator(), new PondPopulator(), new StrongholdPopulator(), new VillagePopulator(), new AbandonedMineshaftPopulator(), new DungeonPopulator()};

	public Populator[] getPopulators() {
		return populators;
	}

	public void generate(CuboidShortBuffer blockData, int chunkX, int chunkY, int chunkZ) {
		int x = chunkX * 16;
		int y = chunkY * 16;
		int z = chunkZ * 16;

		if (y > 127) {
			blockData.flood((short)0);
			//return;
		}
		if (chunkY < 0) {
			blockData.flood(VanillaBlocks.bedrock.getId());
			//return;
		}

		for (int dx = x; dx < (x+16); dx++) {
			for (int dz = z; dz < (z+16); dz++) {
				
				int height = (int) ((heightMap.GetValue(dx / 16.0 + 0.005, 0.05, dz / 16.0 + 0.005) + 1.0) * 4.0 + 60.0);
				
				boolean wateredStack = height < 64;
				
				for(int dy = y; dy < y + 16; dy++) {
					short id;
					
					id = getBlockId(height, dy);
					
					blockData.set(dx, dy, dz, id);
				}
				int ilayers = (int) ((layerCount.GetValue(dx / 16.0 + 0.05, 0.05, dz / 16.0 + 0.05) + 1.0) * 5.0 + 2);

				if(ilayers <= 0) {
					ilayers = 2;
				}

				int heightPerLayer = 59/ilayers;

				for (int layer = 0; layer < ilayers; layer+=2) {
					Perlin bottom = getLayer(layer);
					Perlin top = getLayer(layer+1);
					int min = 3 + heightPerLayer * layer;
					int max = 3 + heightPerLayer * (layer + 1);
					int b = (int) ((getPerlinValueXZ(bottom, dx, dz) + 1.0) * heightPerLayer / 3.0 + min);
					int t = (int) ((getPerlinValueXZ(top, dx, dz) + 1.0) * heightPerLayer / 3.0 + max);
					for(int dy = y; dy < y + 16; dy++) {
						if(dy > b && dy < t) {
							blockData.set(dx, dy, dz, VanillaBlocks.air.getId());
						}
					}
				}

				if(wateredStack) {
					for(int dy = y + 15; dy >= y; dy--) {
						if(dy < 64 && blockData.get(dx, dy, dz) == VanillaBlocks.air.getId()) {
							blockData.set(dx, dy, dz, VanillaBlocks.water.getId());
						} else {
							break;
						}
					}
				}
				
			}
		}
	}

	public static double getPerlinValueXZ(Perlin perlin, int x, int z) {
		return perlin.GetValue(x / 16.0 + 0.05, 0.05, z / 16.0 + 0.05);
	}

	private short getBlockId(int top, int dy) {
		short id;
		if(dy > top) {
			id = VanillaBlocks.air.getId();
		} else if(dy == (int)top && dy >= 63) {
			id = VanillaBlocks.grass.getId();
		} else if(dy + 4 >=(int)top) {
			id = VanillaBlocks.dirt.getId();
		} else if(dy != 0){
			id = VanillaBlocks.stone.getId();
		} else {
			id = VanillaBlocks.bedrock.getId();
		}
		return id;
	}
}