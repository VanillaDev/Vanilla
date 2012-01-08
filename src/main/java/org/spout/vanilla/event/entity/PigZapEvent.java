/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.vanilla.event.entity;

import org.getspout.api.entity.Entity;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.event.entity.EntityEvent;

/**
 * Called when a pig is zapped.
 */
public class PigZapEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Entity pigZombie;

	private Entity lightning;

	public Entity getLightning() {
		return lightning;
	}

	public void setLightning(Entity lightning) {
		this.lightning = lightning;
	}

	public Entity getPigZombie() {
		return pigZombie;
	}

	public void setPigZombie(Entity pigZombie) {
		this.pigZombie = pigZombie;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}