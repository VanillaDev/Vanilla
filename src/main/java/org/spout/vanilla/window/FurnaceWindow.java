/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.vanilla.window;

import org.spout.api.inventory.InventoryBase;
import org.spout.vanilla.controller.living.player.VanillaPlayer;

public class FurnaceWindow extends Window {
	private static final int[] SLOTS = {30, 31, 32, 33, 34, 35, 36, 37, 38, 21, 22, 23, 24, 25, 26, 27, 28, 29, 12, 13, 14, 15, 16, 17, 18, 19, 20, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 2, 0};

	public FurnaceWindow(VanillaPlayer owner, InventoryBase furnaceInventory) {
		super(2, "Furnace", owner);
		this.setInventory(furnaceInventory, owner.getInventory().getItems());
		this.setSlotConversionArray(SLOTS);
	}

	@Override
	public boolean onLeftClick(int clickedSlot, boolean shift) {
		return false;
//		ItemStack cursorStack = controller.getItemOnCursor();
//		if (clickedSlot == 37 && cursorStack != null) {
//			return false;
//		}
//
//		return super.onClicked(controller, clickedSlot, slotStack);
	}
}
