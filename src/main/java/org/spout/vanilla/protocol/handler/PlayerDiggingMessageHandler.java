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
package org.spout.vanilla.protocol.handler;

import org.spout.api.Spout;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.basic.BasicAir;
import org.spout.api.player.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;

import org.spout.vanilla.controller.living.player.VanillaPlayer;
import org.spout.vanilla.material.Mineable;
import org.spout.vanilla.material.VanillaMaterial;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.item.MiningTool;
import org.spout.vanilla.protocol.VanillaNetworkSynchronizer;
import org.spout.vanilla.protocol.msg.PlayEffectMessage;
import org.spout.vanilla.protocol.msg.PlayEffectMessage.Messages;
import org.spout.vanilla.protocol.msg.PlayerDiggingMessage;
import org.spout.vanilla.util.VanillaMessageHandlerUtils;

public final class PlayerDiggingMessageHandler extends MessageHandler<PlayerDiggingMessage> {
	@Override
	public void handleServer(Session session, Player player, PlayerDiggingMessage message) {
		if (player == null || player.getEntity() == null) {
			return;
		}

		int x = message.getX();
		int y = message.getY();
		int z = message.getZ();

		Block block = player.getEntity().getWorld().getBlock(x, y, z, player.getEntity());
		BlockMaterial blockMaterial = block.getSubMaterial();
		boolean isInteractable = true;

		//FIXME: How so not interactable? I am pretty sure I can interact with water to place a boat, no?
		if (blockMaterial == VanillaMaterials.AIR || blockMaterial == BasicAir.AIR || blockMaterial == VanillaMaterials.WATER || blockMaterial == VanillaMaterials.LAVA) {
			isInteractable = false;
		}

		Inventory inv = player.getEntity().getInventory();
		ItemStack heldItem = inv.getCurrentItem();
		VanillaPlayer vp = ((VanillaPlayer) player.getEntity().getController());

		if (message.getState() == PlayerDiggingMessage.STATE_START_DIGGING) {
			PlayerInteractEvent event = new PlayerInteractEvent(player, block.getPosition(), heldItem, Action.LEFT_CLICK, isInteractable);
			if (Spout.getEngine().getEventManager().callEvent(event).isCancelled()) {
				return;
			}

			//Perform interactions
			if (!isInteractable && heldItem == null) {
				//interacting with nothing using fist
				return;
			} else if (heldItem == null) {
				//interacting with block using fist
				blockMaterial.onInteractBy(player.getEntity(), block, Action.LEFT_CLICK, VanillaMessageHandlerUtils.messageToBlockFace(message.getFace()));
			} else if (!isInteractable) {
				//interacting with nothing using item
				heldItem.getSubMaterial().onInteract(player.getEntity(), Action.LEFT_CLICK);
			} else {
				//interacting with block using item
				heldItem.getSubMaterial().onInteract(player.getEntity(), block, Action.LEFT_CLICK, VanillaMessageHandlerUtils.messageToBlockFace(message.getFace()));
				blockMaterial.onInteractBy(player.getEntity(), block, Action.LEFT_CLICK, VanillaMessageHandlerUtils.messageToBlockFace(message.getFace()));
			}

			if (isInteractable) {
				//only dig if in survival mode and block has hardness
				if (vp.isSurvival() && blockMaterial.getHardness() != 0.0f) {
					vp.setDigging(true);
				} else {
					//insta-break
					blockMaterial.onDestroy(block);
					PlayEffectMessage pem = new PlayEffectMessage(Messages.PARTICLE_BREAKBLOCK.getId(), block, blockMaterial.getId());
					VanillaNetworkSynchronizer.sendPacketsToNearbyPlayers(player.getEntity(), player.getEntity().getViewDistance(), pem);
				}
			}
		} else if (message.getState() == PlayerDiggingMessage.STATE_DONE_DIGGING) {
			vp.setDigging(false);
			long diggingTicks = ((VanillaPlayer) player.getEntity().getController()).getDiggingTicks();
			int damageDone = 0;
			int totalDamage = 0;


			if (heldItem != null) {
				if (heldItem.getMaterial() instanceof MiningTool && blockMaterial instanceof Mineable) {
					MiningTool tool = (MiningTool) heldItem.getMaterial();
					Mineable mineable = (Mineable) blockMaterial;
					inv.addCurrentItemData(mineable.getDurabilityPenalty(tool));
				}
			}
			if (isInteractable) {
				if (heldItem == null) {
					damageDone = ((int) diggingTicks * 1);
				} else {
					damageDone = ((int) diggingTicks * ((VanillaMaterial) heldItem.getMaterial()).getDamage());
				}

				totalDamage = ((int) blockMaterial.getHardness() - damageDone);
				if (totalDamage <= 0) {
					blockMaterial.onDestroy(block);
					PlayEffectMessage pem = new PlayEffectMessage(Messages.PARTICLE_BREAKBLOCK.getId(), block, blockMaterial.getId());
					VanillaNetworkSynchronizer.sendPacketsToNearbyPlayers(player.getEntity(), player.getEntity().getViewDistance(), pem);
				}
			}
		}
	}
}
