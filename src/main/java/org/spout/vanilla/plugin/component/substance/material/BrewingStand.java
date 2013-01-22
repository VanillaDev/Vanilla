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

import org.spout.vanilla.api.component.substance.material.ViewedBlockComponent;
import org.spout.vanilla.api.inventory.Container;

import org.spout.vanilla.plugin.component.inventory.WindowHolder;
import org.spout.vanilla.plugin.event.inventory.BrewingStandCloseEvent;
import org.spout.vanilla.plugin.event.inventory.BrewingStandOpenEvent;
import org.spout.vanilla.plugin.inventory.block.BrewingStandInventory;
import org.spout.vanilla.plugin.inventory.window.block.BrewingStandWindow;

public class BrewingStand extends ViewedBlockComponent implements Container {
	private final BrewingStandInventory inventory = new BrewingStandInventory();

	@Override
	public BrewingStandInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean open(Player player) {
		BrewingStandOpenEvent event = Spout.getEventManager().callEvent(new BrewingStandOpenEvent(this, player));
		if (!event.isCancelled()) {
			player.get(WindowHolder.class).openWindow(new BrewingStandWindow(player, inventory));
			return true;
		}
		return false;
	}

	@Override
	public boolean close(Player player) {
		BrewingStandCloseEvent event = Spout.getEventManager().callEvent(new BrewingStandCloseEvent(this, player));
		if (!event.isCancelled()) {
			return super.close(player);
		}
		return false;
	}
}
