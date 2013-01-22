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
package org.spout.vanilla.api.event.block;

import org.spout.api.event.Cancellable;
import org.spout.api.event.Cause;
import org.spout.api.event.HandlerList;
import org.spout.api.event.block.BlockEvent;
import org.spout.api.inventory.ItemStack;

import org.spout.vanilla.api.component.substance.material.FurnaceComponent;

/**
 * Event which is called when an unit of an ItemStack is smelted.
 * todo implement calling of this event
 */
public class FurnaceSmeltEvent extends BlockEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private final FurnaceComponent furnace;
	private final Cause cause;
	private final ItemStack source;
	private ItemStack result;

	public FurnaceSmeltEvent(FurnaceComponent furnace, Cause<?> reason, ItemStack source, ItemStack result) {
		super(furnace.getBlock(), reason);
		this.furnace = furnace;
		this.cause = reason;
		this.source = source;
		this.result = result;
	}

	/**
	 * Gets the smelted ItemStack
	 * @return ItemStack which was smelted
	 */
	public ItemStack getSource() {
		return source;
	}

	/**
	 * Gets the result of the smelting process for this furnace
	 * @return the result ItemStack
	 */
	public ItemStack getResult() {
		return result.clone();
	}

	/**
	 * Sets the result ItemStack
	 * @param newResult the result ItemStack
	 */
	public void setResult(ItemStack newResult) {
		result = newResult;
	}

	/**
	 * Returns the FurnaceComponent in which an item was smelted.
	 * @return furnace
	 */
	public FurnaceComponent getFurnace() {
		return furnace;
	}

	/**
	 * Returns the cause which caused the FurnaceSmeltEvent
	 * @return cause
	 */
	public Cause<?> getCause() {
		return cause;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
