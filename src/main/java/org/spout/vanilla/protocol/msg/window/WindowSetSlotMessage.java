/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
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
package org.spout.vanilla.protocol.msg.window;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.inventory.ItemStack;
import org.spout.api.util.SpoutToStringStyle;

import org.spout.nbt.CompoundMap;

import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.protocol.msg.WindowMessage;
import org.spout.vanilla.window.Window;

public final class WindowSetSlotMessage extends WindowMessage {
	private final int slot, item, count, damage;
	private final CompoundMap nbtData;

	public WindowSetSlotMessage(Window window, int slot) {
		this(window.getInstanceId(), slot);
	}

	public WindowSetSlotMessage(Window window, int slot, ItemStack item) {
		this(window.getInstanceId(), slot, item);
	}

	public WindowSetSlotMessage(int windowInstanceId, int slot, ItemStack item) {
		super(windowInstanceId);
		this.slot = slot;
		if (item == null) {
			this.item = -1;
			this.count = 0;
			this.damage = 0;
			this.nbtData = null;
		} else {
			this.item = VanillaMaterials.getMinecraftId(item.getMaterial());
			this.damage = item.getData();
			this.count = item.getAmount();
			this.nbtData = item.getNBTData();
		}
	}

	public WindowSetSlotMessage(Window window, int slot, int item, int count, int damage, CompoundMap nbtData) {
		this(window.getInstanceId(), slot, item, count, damage, nbtData);
	}

	public WindowSetSlotMessage(int windowInstanceId, int slot) {
		this(windowInstanceId, slot, -1, 0, 0, null);
	}

	public WindowSetSlotMessage(int windowInstanceId, int slot, int item, int count, int damage, CompoundMap nbtData) {
		super(windowInstanceId);
		this.slot = slot;
		this.item = item;
		this.count = count;
		this.damage = damage;
		this.nbtData = nbtData;
	}

	public int getSlot() {
		return slot;
	}

	public int getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

	public CompoundMap getNbtData() {
		return nbtData;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("id", this.getWindowInstanceId())
				.append("slot", slot)
				.append("item", item)
				.append("count", count)
				.append("damage", damage)
				.append("nbtData", nbtData)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final WindowSetSlotMessage other = (WindowSetSlotMessage) obj;
		return new org.apache.commons.lang3.builder.EqualsBuilder()
				.append(this.getWindowInstanceId(), other.getWindowInstanceId())
				.append(this.slot, other.slot)
				.append(this.item, other.item)
				.append(this.count, other.count)
				.append(this.damage, other.damage)
				.append(this.nbtData, other.nbtData)
				.isEquals();
	}
}
