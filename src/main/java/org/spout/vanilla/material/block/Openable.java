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
package org.spout.vanilla.material.block;

import org.spout.api.geo.cuboid.Block;

/**
 * Defines a block material that can be opened and closed<br>
 * For example: door and trapdoors
 */
public interface Openable {
	/**
	 * Toggles the open state of this block
	 * @param block to toggle it of
	 */
	public void toggleOpen(Block block);

	/**
	 * Sets whether this material is open
	 * @param block to apply to
	 * @param open whether the block should be open
	 */
	public void setOpen(Block block, boolean open);

	/**
	 * Gets whether or not this material is opened
	 * @param block to get it of
	 */
	public boolean isOpen(Block block);
}
