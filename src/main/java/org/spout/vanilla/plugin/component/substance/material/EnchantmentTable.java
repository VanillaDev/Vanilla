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
package org.spout.vanilla.plugin.component.substance.material;

import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.geo.cuboid.Block;

import org.spout.vanilla.api.component.substance.material.EnchantmentTableComponent;
import org.spout.vanilla.api.enchantment.Enchantment;
import org.spout.vanilla.api.event.inventory.EnchantmentTableCloseEvent;
import org.spout.vanilla.api.event.inventory.EnchantmentTableOpenEvent;

import org.spout.vanilla.plugin.component.inventory.WindowHolder;
import org.spout.vanilla.plugin.inventory.block.EnchantmentTableInventory;
import org.spout.vanilla.plugin.inventory.window.block.EnchantmentTableWindow;
import org.spout.vanilla.plugin.material.VanillaMaterials;

/**
 * Component that represents a enchantment table in a world.
 */
public class EnchantmentTable extends EnchantmentTableComponent {
	private final EnchantmentTableInventory inventory = new EnchantmentTableInventory();

	@Override
	public EnchantmentTableInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean open(Player player) {
		EnchantmentTableOpenEvent event = Spout.getEventManager().callEvent(new EnchantmentTableOpenEvent(this, player));
		if (!event.isCancelled()) {
			player.get(WindowHolder.class).openWindow(new EnchantmentTableWindow(player, this, inventory));
			return true;
		}
		return false;
	}

	@Override
	public boolean close(Player player) {
		EnchantmentTableCloseEvent event = Spout.getEventManager().callEvent(new EnchantmentTableCloseEvent(this, player));
		if (!event.isCancelled()) {
			return super.close(player);
		}
		return false;
	}

	/**
	 * Returns the amount of bookshelves within a 2 block radius for the X/Z coordinates and 1 block above this enchantment table for the Y coordinate
	 * @return Amount of bookshelves near this enchantment table
	 */
	public int getNearbyBookshelves() {
		Block block = getBlock();

		// if there are any blocks right next to the enchantment table, bookshelves in that direction are nullified
		// Note: unlike Mojang's version, a block at (x+1,y+1,z) won't nullify a bookshelf at (x+2,y,z)
		int bookshelves = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (dx == 0 && dz == 0)
					continue; // Ignore the enchantment table itself
				for (int dy = 0; dy <= 1; dy++) {
					if (!VanillaMaterials.AIR.equals(block.translate(dx, dy, dz).getMaterial()))
						break;
					if (VanillaMaterials.BOOKSHELF.equals(block.translate(2 * dx, dy, 2 * dz).getMaterial()))
						++bookshelves;
					if (dx != 0 && dz != 0) {
						if (VanillaMaterials.BOOKSHELF.equals(block.translate(dx, dy, 2 * dz).getMaterial()))
							++bookshelves;
						if (VanillaMaterials.BOOKSHELF.equals(block.translate(2 * dx, dy, dz).getMaterial()))
							++bookshelves;
					}

				}

			}
		}

		return bookshelves;
	}
}
