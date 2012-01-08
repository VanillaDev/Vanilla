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
package org.spout.vanilla.protocol.msg;

import org.getspout.api.protocol.Message;

public final class EntityVelocityMessage extends Message {
	private final int id, velocityX, velocityY, velocityZ;

	public EntityVelocityMessage(int id, int velocityX, int velocityY, int velocityZ) {
		this.id = id;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
	}

	public int getId() {
		return id;
	}

	public int getVelocityX() {
		return velocityX;
	}

	public int getVelocityY() {
		return velocityY;
	}

	public int getVelocityZ() {
		return velocityZ;
	}

	@Override
	public String toString() {
		return "EntityVelocityMessage{id=" + id + ",velocityX=" + velocityX + ",velocityY=" + velocityY + ",velocityZ=" + velocityZ + "}";
	}
}