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
package org.spout.vanilla.data.effect;

import org.spout.api.entity.component.controller.action.TimedAction;

import org.spout.vanilla.controller.living.player.VanillaPlayer;

/**
 * Represents an entity effect that is applied to an entity.
 */
public class Effect extends TimedAction<VanillaPlayer> {
	private int strength;
	private final EffectType type;

	public Effect(VanillaPlayer effected, EffectType type, float duration, int strength) {
		super(effected, duration);
		this.type = type;
		this.strength = strength;
	}

	@Override
	public void onRegistration() {
		VanillaPlayer controller = getActor();
		int duration = Math.round(getDelay()) * 20;
		controller.getPlayer().getSession().send(type.getApplianceMessage(controller.getParent(), strength, duration));
	}

	@Override
	public void run() {
		VanillaPlayer controller = getActor();
		controller.getPlayer().getSession().send(type.getRemovalMessage(controller.getParent()));
	}

	/**
	 * Gets the type of effect
	 * @return type of effect
	 */
	public EffectType getType() {
		return type;
	}

	/**
	 * Gets the strength of the effect.
	 * @return strength of effect.
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * Sets the strength of the effect.
	 * @param strength of effect
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}
}
