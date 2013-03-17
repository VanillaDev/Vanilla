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
package org.spout.vanilla.ai.action;

import java.util.List;

import org.spout.api.ai.goap.Action;
import org.spout.api.ai.goap.PlannerAgent;
import org.spout.api.ai.goap.WorldState;
import org.spout.api.component.impl.NavigationComponent;
import org.spout.api.entity.Player;
import org.spout.vanilla.ai.sensor.NearbyMaterialHolderSensor;

/**
 * Follow action that can be used for animals following players holding specific food items.
 *
 */
public class FollowMaterialHolderAction implements Action {

	private static final WorldState EFFECTS = WorldState.createImmutable("hasNearbyMaterialHolders", false);
	private static final WorldState PRECONDITIONS = WorldState.createImmutable("hasNearbyMaterialHolders", true);
	private PlannerAgent agent;
	private Player target;

	public FollowMaterialHolderAction(PlannerAgent agent) {
		this.agent = agent;
	}

	@Override
	public void activate() {
		List<Player> targets = agent.getSensor(NearbyMaterialHolderSensor.class).getPlayers();
		target = targets.size() > 0 ? targets.iterator().next() : null;
	}

	@Override
	public boolean evaluateContextPreconditions() {
		return true;
	}

	@Override
	public float getCost() {
		return 1;
	}

	@Override
	public WorldState getEffects() {
		return EFFECTS;
	}

	@Override
	public WorldState getPreconditions() {
		return PRECONDITIONS;
	}

	@Override
	public boolean isComplete() {
		final NavigationComponent navi = agent.getEntity().add(NavigationComponent.class);
		return target == null || target.isRemoved() || !agent.getSensor(NearbyMaterialHolderSensor.class).hasFoundPlayers() || navi != null && !navi.isNavigating();
	}

	@Override
	public void update() {
		final NavigationComponent navi = agent.getEntity().get(NavigationComponent.class);
		if (navi == null) {
			return;
		}
		navi.setDestination(target.getScene().getPosition());
	}

}
