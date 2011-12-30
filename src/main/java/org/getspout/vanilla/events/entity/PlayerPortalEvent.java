/*
 * This file is part of Vanilla (http://www.getspout.org/).
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
package org.getspout.vanilla.events.entity;

import org.getspout.api.entity.Entity;
import org.getspout.api.event.HandlerList;
import org.getspout.api.event.entity.EntityTeleportEvent;
import org.getspout.unchecked.api.TravelAgent;

/**
 * Called when a player teleports via a portal.
 */
public class PlayerPortalEvent extends EntityTeleportEvent {
	private static HandlerList handlers = new HandlerList();

	protected TravelAgent travelAgent;

	public Entity getPlayer() {
		return (Entity) getEntity();
	}

	public TravelAgent getTravelAgent() {
		return travelAgent;
	}

	public void setTravelAgent(TravelAgent travelAgent) {
		this.travelAgent = travelAgent;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}