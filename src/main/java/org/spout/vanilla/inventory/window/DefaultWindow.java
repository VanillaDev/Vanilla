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
package org.spout.vanilla.inventory.window;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Vector2;

import org.spout.vanilla.component.inventory.PlayerInventory;
import org.spout.vanilla.inventory.player.PlayerArmorInventory;
import org.spout.vanilla.inventory.util.InventoryConverter;

public class DefaultWindow extends Window {
	public DefaultWindow(Player owner) {
		super(owner, WindowType.DEFAULT, "Inventory", 9);
		PlayerInventory inventory = getPlayerInventory();

		addInventoryConverter(new InventoryConverter(inventory.getArmor(), "8, 7, 6, 5", new Vector2[]{
				Vector2.ZERO, Vector2.ZERO, Vector2.ZERO, Vector2.ZERO
		}));

		addInventoryConverter(new InventoryConverter(inventory.getCraftingGrid(), "3-4, 1-2, 0", new Vector2[]{
				Vector2.ZERO, Vector2.ZERO, Vector2.ZERO, Vector2.ZERO
		}));
	}

	@Override
	public void onSlotSet(Inventory inventory, int slot, ItemStack item) {
		super.onSlotSet(inventory, slot, item);
		if (inventory instanceof PlayerArmorInventory) {
			Player player = getPlayer();
			//TODO: Fix
			//player.getNetwork().callProtocolEvent(new EntityEquipmentEvent(player, slot + 1, item));
		}
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void open() {
		if (!(Spout.getEngine() instanceof Client)) {
			throw new UnsupportedOperationException("A player's inventory window cannot be opened from the server.");
		}
		super.open();
	}
}
