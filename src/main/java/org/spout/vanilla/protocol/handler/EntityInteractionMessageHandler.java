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
package org.spout.vanilla.protocol.handler;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;

import org.spout.vanilla.components.basic.HealthComponent;
import org.spout.vanilla.components.player.GameModeComponent;
import org.spout.vanilla.components.player.VanillaPlayer;
import org.spout.vanilla.configuration.VanillaConfiguration;
import org.spout.vanilla.data.ExhaustionLevel;
import org.spout.vanilla.material.VanillaMaterial;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.item.tool.Tool;
import org.spout.vanilla.protocol.msg.entity.EntityInteractionMessage;
import org.spout.vanilla.source.DamageCause;
import org.spout.vanilla.util.VanillaPlayerUtil;

public class EntityInteractionMessageHandler extends MessageHandler<EntityInteractionMessage> {
	@Override
	public void handleServer(Session session, EntityInteractionMessage message) {
		if (!session.hasPlayer()) {
			return;
		}

		Player player = session.getPlayer();
		Entity clickedEntity = player.getWorld().getEntity(message.getTarget());
		if (clickedEntity == null) {
			return;
		}

		ItemStack holding = VanillaPlayerUtil.getCurrentItem(player);
		Material holdingMat = holding == null ? VanillaMaterials.AIR : holding.getMaterial();
		if (holdingMat == null) {
			holdingMat = VanillaMaterials.AIR;
		}
		if (message.isPunching()) {
			holdingMat.onInteract(player, clickedEntity, Action.LEFT_CLICK);
			clickedEntity.getController().onInteract(player, Action.LEFT_CLICK);

			if (clickedEntity.has(VanillaPlayer.class) && !VanillaConfiguration.PLAYER_PVP_ENABLED.getBoolean()) {
				return;
			}

			if (clickedEntity.has(VanillaPlayer.class)) {
				if (clickedEntity.has(VanillaPlayer.class) && VanillaPlayerUtil.isSurvival(player) || !VanillaPlayerUtil.isSurvival(clickedEntity))) {
					return;
				}
				vPlayer.getSurvivalComponent().addExhaustion(ExhaustionLevel.ATTACK_ENEMY.getAmount());

				if (clickedEntity.has(VanillaPlayer.class)) {
					((VanillaPlayerController) clickedEntity.getController()).getSurvivalComponent().addExhaustion(ExhaustionLevel.RECEIVE_DAMAGE.getAmount());
				}

				int damage = 1;
				if (holding != null && holdingMat instanceof VanillaMaterial) {
					damage = ((VanillaMaterial) holdingMat).getDamage();
					if (holdingMat instanceof Tool) {
						// This is a bit of a hack due to the way Tool hierarchy is now (Only Swords can have a damage modifier, but Sword must be an interface and therefore is not able to contain getDamageModifier without code duplication)
						damage += ((Tool) holdingMat).getDamageBonus(clickedEntity, holding);
						vPlayer.getInventory().getQuickbar().getCurrentSlotInventory().addItemData(1);
					}
				}
				if (damage != 0) {
					if (!clickedEntity.get(HealthComponent.class).isDead()) {
						clickedEntity.get(HealthComponent.class).damage(damage, DamageCause.ATTACK, vPlayer.getParent(), damage > 0);
					}
				}
			}
		} else {
			holdingMat.onInteract(player, clickedEntity, Action.RIGHT_CLICK);
			clickedEntity.getController().onInteract(player, Action.RIGHT_CLICK);
		}
	}
}
