/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.event.entity;

import org.spout.api.entity.Entity;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.entity.EntityHealthChangedEvent;

/**
 * Called when an entity gains health.
 */
public class VanillaEntityHealthChangedEvent extends EntityHealthChangedEvent implements Cancellable {
	public VanillaEntityHealthChangedEvent(Entity e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	private static HandlerList handlers = new HandlerList();

	private GainHealthReason reason;

	/**
	 * Gets the reason for the gain of health.
	 *
	 * @return A GainHealthReason that is the reason for the gained health.
	 */
	public GainHealthReason getReason() {
		return reason;
	}

	/**
	 * Sets the reason for the gain of health.
	 *
	 * @param reason A GainedHealthReason that sets the reason for the gained health.
	 */
	public void setReason(GainHealthReason reason) {
		this.reason = reason;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum GainHealthReason {
		PEACEFUL,
		HUNGER,
		EATING,
		CUSTOM;
	}
}