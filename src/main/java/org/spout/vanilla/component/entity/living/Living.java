/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.vanilla.component.entity.living;

import org.spout.api.ai.goap.GoapAIComponent;
import org.spout.api.component.entity.NavigationComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;

import org.spout.vanilla.ai.examiner.VanillaBlockExaminer;
import org.spout.vanilla.component.entity.VanillaNetworkComponent;
import org.spout.vanilla.component.entity.VanillaEntityComponent;
import org.spout.vanilla.component.entity.misc.Burn;
import org.spout.vanilla.component.entity.misc.Drowning;
import org.spout.vanilla.component.entity.misc.Effects;
import org.spout.vanilla.component.entity.misc.EntityHead;
import org.spout.vanilla.component.entity.misc.Health;
import org.spout.vanilla.component.entity.misc.MetadataComponent;
import org.spout.vanilla.data.Metadata;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.data.effect.EntityEffectType;

public abstract class Living extends VanillaEntityComponent {
	private EntityHead head;
	private Health health;
	private Drowning drowning;
	private NavigationComponent navigation;
	private GoapAIComponent ai;

	@Override
	public void onAttached() {
		super.onAttached();
		Entity holder = getOwner();
		head = holder.add(EntityHead.class);
		health = holder.add(Health.class);
		drowning = holder.add(Drowning.class);
		navigation = holder.add(NavigationComponent.class);
		navigation.setDefaultExaminers(new VanillaBlockExaminer());
		ai = holder.add(GoapAIComponent.class);
		holder.add(Burn.class);
		if (!(holder instanceof Player)) {
			holder.add(VanillaNetworkComponent.class);
		}

		// Add metadata associated with general living Entity properties
		getOwner().add(MetadataComponent.class).addMeta(new Metadata<Byte>(Metadata.TYPE_BYTE, 0) {
			@Override
			public Byte getValue() {
				byte value = 0;
				Burn burn = getOwner().get(Burn.class);
				if (burn != null) {
					value = (byte) (value | (burn.isOnFire() ? 1 : 0));
				}

				value = (byte) (value | ((isSneaking() ? 1 : 0) << 1));
				value = (byte) (value | ((isRiding() ? 1 : 0) << 2));

				Human human = getOwner().get(Human.class);
				if (human != null) {
					value = (byte) (value | ((human.isSprinting() ? 1 : 0) << 3));
				}

				value = (byte) (value | ((isEatingBlocking() ? 1 : 0) << 4));

				Effects effects = getOwner().get(Effects.class);
				if (effects != null) {
					value = (byte) (value | ((effects.contains(EntityEffectType.INVISIBILITY) ? 1 : 0) << 5));
				}

				return value;
			}

			@Override
			public void setValue(Byte value) {
				// TODO Read a new value (as client) and apply it in the Entity
				// Requires the same logic as above, but in the opposite way
			}
		});
	}

	public boolean isOnGround() {
		return getOwner().getData().get(VanillaData.IS_ON_GROUND);
	}

	public void setOnGround(boolean onGround) {
		getOwner().getData().put(VanillaData.IS_ON_GROUND, onGround);
	}

	public EntityHead getHead() {
		return head;
	}

	public Health getHealth() {
		return health;
	}

	public Drowning getDrowning() {
		return drowning;
	}

	public NavigationComponent getNavigation() {
		return navigation;
	}

	public GoapAIComponent getAI() {
		return ai;
	}

	public boolean isRiding() {
		return getOwner().getData().get(VanillaData.IS_RIDING);
	}

	public void setRiding(boolean isRiding) {
		getOwner().getData().put(VanillaData.IS_RIDING, isRiding);
	}

	public boolean isEatingBlocking() {
		return getOwner().getData().get(VanillaData.IS_EATING_BLOCKING);
	}

	public void setEatingBlocking(boolean isEatingBlocking) {
		getOwner().getData().put(VanillaData.IS_EATING_BLOCKING, isEatingBlocking);
	}

	public boolean isSneaking() {
		return getOwner().getData().get(VanillaData.IS_SNEAKING);
	}

	public void setSneaking(boolean isSneaking) {
		getOwner().getData().put(VanillaData.IS_SNEAKING, isSneaking);
	}
}
