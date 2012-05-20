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
package org.spout.vanilla.util;

import java.util.HashSet;
import java.util.Set;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.vanilla.inventory.VanillaItemStack;

public class InventoryUtil {
	private static int windowId = 0;

	private InventoryUtil() {
	}

	public static void mergeStack(VanillaItemStack dump, VanillaItemStack fill, int amount) {
		int dumpAmount = amount;
		int amount1 = dump.getAmount();
		int amount2 = fill.getAmount();
		int maxAmount = fill.getMaterial().getMaxStackSize();
		int freeSpace = maxAmount - amount2;

		// Stop if the stack can't fit
		if (freeSpace == 0 || dumpAmount > dump.getAmount()) {
			return;
		}

		// If the stack can fit
		if (freeSpace >= dumpAmount) {
			amount2 += dumpAmount;
			amount1 -= dumpAmount;
		}

		// If only part of the stack can fit.
		if (freeSpace < dumpAmount) {
			amount2 = maxAmount;
			amount1 -= freeSpace;
		}

		fill.setAmount(amount2);
		dump.setAmount(amount1);
	}

	public static void mergeStack(VanillaItemStack dump, VanillaItemStack fill) {
		mergeStack(dump, fill, dump.getAmount());
	}

	public static VanillaItemStack nullIfEmpty(VanillaItemStack s) {
		return (s != null && s.getAmount() == 0) ? null : s;
	}

	public static void quickMoveStack(Inventory inv, int pos) {
		ItemStack theStack = inv.getItem(pos);
		// Assume item is in quick-bar.
		int startSlot = 0, stopSlot = 8;
		if (pos > stopSlot) {
			startSlot = 9;
			stopSlot = inv.getSize() - 1;
		}

		Set<Integer> hiddenSlots = new HashSet<Integer>();
		for (int i = startSlot; i <= stopSlot; i++) {
			if (inv.isHiddenSlot(i)) {
				hiddenSlots.add(i);
			}

			inv.setHiddenSlot(i, true);
		}

		inv.addItem(theStack, false);
		for (int i = startSlot; i <= stopSlot; i++) {
			if (!(hiddenSlots.contains(i))) {
				inv.setHiddenSlot(i, false);
			}
		}
	}

	public static int nextWindowId() {
		return windowId++;
	}
}
