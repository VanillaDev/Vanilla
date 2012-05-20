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
package org.spout.vanilla.inventory;

import org.spout.api.inventory.Inventory;

import org.spout.vanilla.controller.block.DispenserController;
import org.spout.vanilla.controller.living.player.VanillaPlayer;

/**
 * Represents a dispenser inventory belonging to a dispenser controller.
 */
public class DispenserInventory extends VanillaInventory implements WindowInventory {
	private static final long serialVersionUID = 1L;
	private static final int SLOTS[] = {36, 37, 38, 39, 40, 41, 42, 43, 44, 27, 28, 29, 30, 31, 32, 33, 34, 35, 18, 19, 20, 21, 22, 23, 24, 25, 26, 9, 10, 11, 12, 13, 14, 15, 16, 17, 0, 1, 2, 3, 4, 5, 6, 7, 8};
	private final DispenserController owner;

	public DispenserInventory(DispenserController owner) {
		super(45);
		this.owner = owner;
	}

	/**
	 * Returns the dispenser controller that this inventory belongs to.
	 * @return owner the dispenser controller
	 */
	public DispenserController getOwner() {
		return owner;
	}

	@Override
	public int getNativeSlotIndex(int index) {
		return SLOTS[index];
	}

	@Override
	public int getSlotIndex(int nativeIndex) {
		for (int i = 0; i < SLOTS.length; i++) {
			if (SLOTS[i] == nativeIndex) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Window getWindow() {
		return Window.DISPENSER;
	}

	@Override
	public void open(VanillaPlayer player) {
		Inventory inventory = player.getPlayer().getEntity().getInventory();
		for (int slot = 0; slot < 36; slot++) {
			setItem(slot, inventory.getItem(slot));
		}
		addViewer(player.getPlayer().getNetworkSynchronizer());
		player.setActiveInventory(this);
		player.openWindow(Window.DISPENSER, getSize());
	}

	@Override
	public boolean onClicked(VanillaPlayer player, int clickedSlot, VanillaItemStack slotStack) {
		setItem(clickedSlot, slotStack);
		return true;
	}

	@Override
	public void onClosed(VanillaPlayer player) {
	}
}
