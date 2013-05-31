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
package org.spout.vanilla.material.item.vehicle;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector2;

import org.spout.vanilla.component.entity.misc.EntityHead;
import org.spout.vanilla.component.entity.substance.vehicle.Boat;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.item.VanillaItemMaterial;

public class BoatItem extends VanillaItemMaterial {
	public BoatItem(String name, int id, Vector2 pos) {
		super(name, id, pos);
	}

	@Override
	public void onInteract(Entity entity, Action type) {
		if (!(entity instanceof Player) || type != Action.RIGHT_CLICK) {
			return;
		}

		EntityHead head = entity.add(EntityHead.class);
		Block b = head.getBlockView(1).next();
		if (!b.isMaterial(VanillaMaterials.WATER)) {
			System.out.println("No water");
			return;
		}

		World world = b.getWorld();
		world.createAndSpawnEntity(b.getPosition().add(0, 0.25f, 0), LoadOption.LOAD_ONLY, Boat.class);
	}

	@Override
	public void onInteract(Entity entity, Block block, Action type, BlockFace face) {
		onInteract(entity, type);
	}
}
