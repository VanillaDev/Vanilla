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

import java.util.Random;
import org.spout.api.generator.Populator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.vanilla.VanillaBlocks;

public class TreePopulator implements Populator {
	
	private Random random = new Random();
	
	public void generateNormalTree(Chunk c, Point point) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		int z = (int) point.getZ();
		short wood = VanillaBlocks.LOG.getId();
		short leaves = VanillaBlocks.LEAVES.getId();
		World world = c.getWorld();
		
		// Trunk
		for (int yy = 1; yy <= 6; yy++) {
			c.setBlockId(x, y+yy, z, wood, world);
		}
		
		// Top leaves layer
		c.setBlockId(x, y+7, z, leaves, world);
		c.setBlockId(x+1, y+7, z, leaves, world);
		c.setBlockId(x, y+7, z-1, leaves, world);
		c.setBlockId(x-1, y+7, z, leaves, world);
		c.setBlockId(x, y+7, z+1, leaves, world);
		
		// Second leaves layer
		c.setBlockId(x+1, y+6, z, leaves, world);
		c.setBlockId(x+1, y+6, z-1, leaves, world);
		c.setBlockId(x, y+6, z-1, leaves, world);
		c.setBlockId(x-1, y+6, z-1, leaves, world);
		c.setBlockId(x-1, y+6, z, leaves, world);
		c.setBlockId(x-1, y+6, z+1, leaves, world);
		c.setBlockId(x, y+6, z+1, leaves, world);
		c.setBlockId(x+1, y+6, z+1, leaves, world);
		
		// Third and fourth leaves layer
		for (int off = 4; off <= 5; off++) {
			c.setBlockId(x+1, y+off, z, leaves, world);
			c.setBlockId(x+1, y+off, z-1, leaves, world);
			c.setBlockId(x, y+off, z-1, leaves, world);
			c.setBlockId(x-1, y+off, z-1, leaves, world);
			c.setBlockId(x-1, y+off, z, leaves, world);
			c.setBlockId(x-1, y+off, z+1, leaves, world);
			c.setBlockId(x, y+off, z+1, leaves, world);
			c.setBlockId(x+1, y+off, z+1, leaves, world);
			
			c.setBlockId(x+2, y+off, z, leaves, world);
			c.setBlockId(x+2, y+off, z-1, leaves, world);
			c.setBlockId(x+2, y+off, z-2, leaves, world);
			c.setBlockId(x+1, y+off, z-2, leaves, world);
			c.setBlockId(x, y+off, z-2, leaves, world);
			c.setBlockId(x-1, y+off, z-2, leaves, world);
			c.setBlockId(x-2, y+off, z-2, leaves, world);
			c.setBlockId(x-2, y+off, z-1, leaves, world);
			c.setBlockId(x-2, y+off, z, leaves, world);
			c.setBlockId(x-2, y+off, z+1, leaves, world);
			c.setBlockId(x-2, y+off, z+2, leaves, world);
			c.setBlockId(x-1, y+off, z+2, leaves, world);
			c.setBlockId(x, y+off, z+2, leaves, world);
			c.setBlockId(x+1, y+off, z+2, leaves, world);
			c.setBlockId(x+2, y+off, z+2, leaves, world);
			c.setBlockId(x+2, y+off, z+1, leaves, world);
		}
	}
	
	public void generateSmallTree(Chunk c, Point point) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		int z = (int) point.getZ();
		short wood = VanillaBlocks.LOG.getId();
		short leaves = VanillaBlocks.LEAVES.getId();
		World world = c.getWorld();
		
		// Trunk
		for (int yy = 1; yy <= 4; yy++) {
			c.setBlockId(x, y+yy, z, wood, world);
		}
		
		// Top leaves layer
		c.setBlockId(x, y+5, z, leaves, world);
		c.setBlockId(x+1, y+5, z, leaves, world);
		c.setBlockId(x, y+5, z-1, leaves, world);
		c.setBlockId(x-1, y+5, z, leaves, world);
		c.setBlockId(x, y+5, z+1, leaves, world);
		
		// Second leaves layer
		c.setBlockId(x+1, y+4, z, leaves, world);
		c.setBlockId(x+1, y+4, z-1, leaves, world);
		c.setBlockId(x, y+4, z-1, leaves, world);
		c.setBlockId(x-1, y+4, z-1, leaves, world);
		c.setBlockId(x-1, y+4, z, leaves, world);
		c.setBlockId(x-1, y+4, z+1, leaves, world);
		c.setBlockId(x, y+4, z+1, leaves, world);
		c.setBlockId(x+1, y+4, z+1, leaves, world);
		
		// Third and fourth leaves layer
		for (int off = 2; off <= 3; off++) {
			c.setBlockId(x+1, y+off, z, leaves, world);
			c.setBlockId(x+1, y+off, z-1, leaves, world);
			c.setBlockId(x, y+off, z-1, leaves, world);
			c.setBlockId(x-1, y+off, z-1, leaves, world);
			c.setBlockId(x-1, y+off, z, leaves, world);
			c.setBlockId(x-1, y+off, z+1, leaves, world);
			c.setBlockId(x, y+off, z+1, leaves, world);
			c.setBlockId(x+1, y+off, z+1, leaves, world);
			
			c.setBlockId(x+2, y+off, z, leaves, world);
			c.setBlockId(x+2, y+off, z-1, leaves, world);
			c.setBlockId(x+2, y+off, z-2, leaves, world);
			c.setBlockId(x+1, y+off, z-2, leaves, world);
			c.setBlockId(x, y+off, z-2, leaves, world);
			c.setBlockId(x-1, y+off, z-2, leaves, world);
			c.setBlockId(x-2, y+off, z-2, leaves, world);
			c.setBlockId(x-2, y+off, z-1, leaves, world);
			c.setBlockId(x-2, y+off, z, leaves, world);
			c.setBlockId(x-2, y+off, z+1, leaves, world);
			c.setBlockId(x-2, y+off, z+2, leaves, world);
			c.setBlockId(x-1, y+off, z+2, leaves, world);
			c.setBlockId(x, y+off, z+2, leaves, world);
			c.setBlockId(x+1, y+off, z+2, leaves, world);
			c.setBlockId(x+2, y+off, z+2, leaves, world);
			c.setBlockId(x+2, y+off, z+1, leaves, world);
		}
	}
	
	public void generateBigTree(Chunk c, Point p) {
		
	}
	
	public void generateRedwoodTree(Chunk c, Point p) {
		
	}
	
	public void generateBirchTree(Chunk c, Point point) {
		
	}
	
	@Override
	public void populate(Chunk c) {
		for (int a = 0; a < random.nextInt(20); a++) {
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			int y = 0;
			for (int yy = 128; yy > 0; yy--) {
				if (c.getBlockId(x, yy, z) != 0) {
					y = yy;
					break;
				}
			}
			
			if (c.getBlockId(x, y, z) == 2) {
				Point point = new Point(c.getWorld(), x, y, z);
				if (random.nextInt(100) < 50) {
					this.generateNormalTree(c, point);
				} else {
					this.generateSmallTree(c, point);
				}
			}
		}
	}
}