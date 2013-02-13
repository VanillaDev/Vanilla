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
package org.spout.vanilla.plugin.protocol.handler.player;

import org.spout.api.entity.Player;
import org.spout.api.geo.World;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.vanilla.api.data.Difficulty;
import org.spout.vanilla.api.data.GameMode;
import org.spout.vanilla.api.data.VanillaData;
import org.spout.vanilla.plugin.component.living.neutral.Human;
import org.spout.vanilla.plugin.protocol.msg.player.pos.PlayerRespawnMessage;

public final class PlayerRespawnHandler extends MessageHandler<PlayerRespawnMessage> {

	@Override
	public void handleClient(Session session, PlayerRespawnMessage message) {
		if (!session.hasPlayer()) {
			return;
		}

		Player player = session.getPlayer();
		
		//TODO : Take Dimension ? : message.getDimension()
		//TODO : Take World height ? : message.getWorldHeight()
		//TODO : Take Level tpye ? : message.getWorldType()

		World world = player.getWorld();
		world.getDataMap().put(VanillaData.DIFFICULTY,Difficulty.get(message.getDifficulty()));
		
		Human human = player.get(Human.class);
		human.setGamemode(GameMode.get(message.getGameMode()));
	}
}
