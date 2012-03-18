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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.material.attachable;

import org.spout.api.geo.World;
import org.spout.api.material.block.BlockFace;

public interface Attachable {
	/**
	 * Gets which data id should be set for the given face
	 * @param face where the block will be attached to
	 * @return data for the given face
	 */
	public short getDataForFace(BlockFace face);

	/**
	 * Gets the face the block is attached to by given data
	 * @param data that the block has
	 * @return to which face the block is attached to
	 */
	public BlockFace getFaceAttachedTo(short data);

	/**
	 * Returns the block that the attachable at x,y,z is attached to.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return the block the attachable at those coordinates is attached to.
	 */
	public org.spout.api.geo.cuboid.Block getBlockAttachedTo(World world, int x, int y, int z);
}
