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
package org.spout.vanilla.protocol.handler;

import org.spout.api.entity.Entity;
import org.spout.api.player.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.vanilla.protocol.msg.EntityActionMessage;

/**
 * A {@link MessageHandler} which handles {@link Entity} action messages.
 */
public final class EntityActionMessageHandler extends MessageHandler<EntityActionMessage> {
	@Override
	public void handle(Session session, Player player, EntityActionMessage message) {
		/*switch (message.getAction()) {
			case EntityActionMessage.ACTION_SNEAKING:
				player.setSneaking(true);
				break;
			case EntityActionMessage.ACTION_STOP_SNEAKING:
				player.setSneaking(false);
				break;
			default:
				// TODO: bed support
				return;
		}*/
	}
}