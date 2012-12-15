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
package org.spout.vanilla.component.living.neutral;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.component.implementation.TextModelComponent;
import org.spout.api.data.Data;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Vector3;

import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.component.inventory.PlayerInventory;
import org.spout.vanilla.component.living.Living;
import org.spout.vanilla.component.misc.DiggingComponent;
import org.spout.vanilla.component.misc.HealthComponent;
import org.spout.vanilla.component.misc.PickupItemComponent;
import org.spout.vanilla.component.substance.Item;
import org.spout.vanilla.configuration.VanillaConfiguration;
import org.spout.vanilla.data.GameMode;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.event.player.PlayerGameModeChangedEvent;
import org.spout.vanilla.event.player.network.PlayerGameStateEvent;
import org.spout.vanilla.inventory.player.PlayerQuickbar;
import org.spout.vanilla.protocol.entity.HumanEntityProtocol;
import org.spout.vanilla.protocol.msg.player.PlayerGameStateMessage;

/**
 * A component that identifies the entity as a Vanilla player.
 */
public class Human extends Living {
	public static final int SPAWN_HEALTH = 20;

	@Override
	public void onAttached() {
		super.onAttached();
		Entity holder = getOwner();
		holder.add(PickupItemComponent.class);
		holder.add(DiggingComponent.class);
		holder.getNetwork().setEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID, new HumanEntityProtocol());
		//Add height offset if loading from disk
		if (getAttachedCount() > 1 && holder instanceof Player) {
			holder.add(HealthComponent.class).setSpawnHealth(SPAWN_HEALTH);
			((Player) holder).teleport(holder.getTransform().getPosition().add(0, 1.85F, 0));
		}
		if (getOwner().has(TextModelComponent.class)) {
			getOwner().get(TextModelComponent.class).setSize(0.5f);
			getOwner().get(TextModelComponent.class).setTranslation(new Vector3(0, 3f, 0));
		}
	}

	public boolean isOnGround() {
		return getOwner().getData().get(VanillaData.IS_ON_GROUND);
	}

	public void setOnGround(boolean onGround) {
		getOwner().getData().put(VanillaData.IS_ON_GROUND, onGround);
	}

	public boolean isFlying() {
		return getOwner().getData().get(VanillaData.IS_FLYING);
	}

	public void setFlying(boolean isFlying) {
		getOwner().getData().put(VanillaData.IS_FLYING, isFlying);
	}

	public boolean isSprinting() {
		return getOwner().getData().get(VanillaData.IS_SPRINTING);
	}

	public void setSprinting(boolean isSprinting) {
		getOwner().getData().put(VanillaData.IS_SPRINTING, isSprinting);
	}
	
	public boolean isSneaking() {
		return getOwner().getData().get(VanillaData.IS_SNEAKING);
	}

	public void setSneaking(boolean isSneaking) {
		getOwner().getData().put(VanillaData.IS_SNEAKING, isSneaking);
	}

	public boolean isFalling() {
		return getOwner().getData().get(VanillaData.IS_FALLING);
	}

	public void setFalling(boolean isFalling) {
		getOwner().getData().put(VanillaData.IS_FALLING, isFalling);
	}

	public boolean isJumping() {
		return getOwner().getData().get(VanillaData.IS_JUMPING);
	}

	public void setJumping(boolean isJumping) {
		getOwner().getData().put(VanillaData.IS_JUMPING, isJumping);
	}

	public boolean isAdventure() {
		return getGameMode() == GameMode.ADVENTURE;
	}

	public boolean isCreative() {
		return getGameMode() == GameMode.CREATIVE;
	}

	public boolean isSurvival() {
		return getGameMode() == GameMode.SURVIVAL;
	}

	public String getName() {
		return getData().get(Data.NAME);
	}

	public void setName(String name) {
		getData().put(Data.NAME, name);
		if (getOwner().has(TextModelComponent.class)) {
			getOwner().get(TextModelComponent.class).setText(new ChatArguments(name));
		}
	}

	public GameMode getGameMode() {
		return getData().get(VanillaData.GAMEMODE);
	}

	public boolean isOp() {
		return getOwner() instanceof Player && VanillaConfiguration.OPS.isOp(getName());
	}

	public void setGamemode(GameMode mode, boolean updateClient) {
		Entity holder = getOwner();
		if (holder instanceof Player) {
			if (PlayerGameModeChangedEvent.getHandlerList().getRegisteredListeners().length > 0) {
				PlayerGameModeChangedEvent event = Spout.getEventManager().callEvent(new PlayerGameModeChangedEvent((Player) getOwner(), mode));
				if (event.isCancelled()) {
					return;
				}
				mode = event.getMode();
			}
		}
		if (holder instanceof Player && updateClient) {
			holder.getNetwork().callProtocolEvent(new PlayerGameStateEvent((Player) holder, PlayerGameStateMessage.CHANGE_GAME_MODE, mode), (Player) getOwner());
		}
		getData().put(VanillaData.GAMEMODE, mode);
	}

	public void setGamemode(GameMode mode) {
		setGamemode(mode, true);
	}

	/**
	 * Drops the item specified into the direction the player looks
	 * @param item to drop
	 */
	public void dropItem(ItemStack item) {
		float yaw = getOwner().getTransform().getYaw();
		Vector3 impulse = new Vector3(Math.cos(yaw), 0.4F, Math.sin(yaw));
		Item.drop(this.getOwner().getTransform().getPosition(), item, impulse);
	}

	/**
	 * Drops the player's current item.
	 */
	public void dropItem() {
		if (!getOwner().has(PlayerInventory.class)) {
			return;
		}

		PlayerQuickbar quickbar = getOwner().get(PlayerInventory.class).getQuickbar();
		ItemStack current = quickbar.getCurrentItem();
		if (current == null) {
			return;
		}

		ItemStack drop = current.clone().setAmount(1);
		quickbar.addAmount(quickbar.getCurrentSlot(), -1);
		dropItem(drop);
	}
}
