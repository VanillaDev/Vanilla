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
package org.spout.vanilla.protocol.msg;

import org.spout.api.protocol.Message;

import org.spout.nbt.CompoundMap;

public final class WindowClickMessage extends Message {
	private final int id, slot;
	private final boolean rightClick, shift;
	private final int transaction, item, count, damage;
	private final CompoundMap nbtData;

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, boolean shift) {
		this(id, slot, rightClick, transaction, shift, -1, 0, 0, null);
	}

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, boolean shift, int item, int count, int damage, CompoundMap nbtData) {
		this.id = id;
		this.slot = slot;
		this.rightClick = rightClick;
		this.transaction = transaction;
		this.shift = shift;
		this.item = item;
		this.count = count;
		this.damage = damage;
		this.nbtData = nbtData;
	}

	public int getWindowId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isRightClick() {
		return rightClick;
	}

	public boolean isShift() {
		return shift;
	}

	public int getTransaction() {
		return transaction;
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
		return "WindowClickMessage{id=" + id + ",slot=" + slot + ",rightClick=" + rightClick + ",shift=" + shift + ",transaction=" + transaction + ",item=" + item + ",count=" + count + ",damage=" + damage + ",nbtData=" + nbtData + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final WindowClickMessage other = (WindowClickMessage) obj;
		return new org.apache.commons.lang3.builder.EqualsBuilder()
				.append(this.id, other.id)
				.append(this.slot, other.slot)
				.append(this.rightClick, other.rightClick)
				.append(this.shift, other.shift)
				.append(this.transaction, other.transaction)
				.append(this.item, other.item)
				.append(this.count, other.count)
				.append(this.damage, other.damage)
				.append(this.nbtData, other.nbtData)
				.isEquals();
	}

	@Override
	public int hashCode() {
		// FIXME: Add a proper hashCode method!
		throw new UnsupportedOperationException("hashCode is not supported.");
	}
}
