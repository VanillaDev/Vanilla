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
package org.spout.vanilla.api.inventory;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;

/**
 * Wraps around an Inventory and slot index to provide simple getters and setters<br>
 * This is not an element of an inventory. It is just a wrapper. It does not contain an ItemStack itself.
 */
public class Slot {
	private final Inventory inventory;
	private final int index;

	public Slot(Inventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}

	/**
	 * Gets the Inventory this Slot is in
	 * @return Slot Inventory
	 */
	public Inventory getInventory() {
		return this.inventory;
	}

	/**
	 * Gets the index of this Slot in the Inventory
	 * @return Slot index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Returns the ItemStack contained in this Slot
	 * @return Slot ItemStack
	 */
	public ItemStack get() {
		return this.inventory.get(index);
	}

	/**
	 * Replaces the ItemStack at this slot in this inventory with the specified ItemStack
	 * @param item to set this slot
	 * @return the item previously contained
	 */
	public ItemStack set(ItemStack item) {
		return this.inventory.set(this.index, item);
	}

	/**
	 * Adds a value to the amount of an item in this Slot
	 * @param amount to add
	 * @return True if the amount was added, False if not
	 */
	public boolean addAmount(int amount) {
		return this.inventory.addAmount(this.index, amount);
	}

	/**
	 * Adds a value to the data of an item in this Slot
	 * @param amount to add
	 * @return True of the data was added, False if not
	 */
	public boolean addData(int amount) {
		return this.inventory.addData(this.index, amount);
	}
}
