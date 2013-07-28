/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.vanilla.material.item.misc;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.player.Action;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector2;

import org.spout.vanilla.component.entity.substance.ItemFrame;
import org.spout.vanilla.material.item.VanillaItemMaterial;

public class ItemFrameItem extends VanillaItemMaterial {
	public ItemFrameItem(String name, int id, Vector2 pos) {
		super(name, id, pos);
	}

	@Override
	public void onInteract(Entity entity, Block block, Action type, BlockFace face) {
		if (!(entity instanceof Player) || type != Action.RIGHT_CLICK || face == BlockFace.BOTTOM || face == BlockFace.THIS || face == BlockFace.TOP) {
			return;
		}

		World world = block.getWorld();
		Point pos = block.getPosition();
		Entity e = world.createEntity(new Point(world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()), ItemFrame.class);
		ItemFrame frame = e.add(ItemFrame.class);
		frame.setOrientation(face);
		world.spawnEntity(e);
	}
}
