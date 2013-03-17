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
package org.spout.vanilla.component.entity.living.passive;

import java.util.Random;

import org.spout.api.event.cause.EntityCause;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.ItemStack;
import org.spout.api.util.Parameter;
import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.ai.action.FollowMaterialHolderAction;
import org.spout.vanilla.ai.goal.FollowMaterialHolderGoal;
import org.spout.vanilla.ai.sensor.NearbyMaterialHolderSensor;
import org.spout.vanilla.component.entity.living.Animal;
import org.spout.vanilla.component.entity.living.Passive;
import org.spout.vanilla.component.entity.misc.DeathDrops;
import org.spout.vanilla.component.entity.misc.Health;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.event.entity.EntityStatusEvent;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.block.solid.Wool;
import org.spout.vanilla.material.block.solid.Wool.WoolColor;
import org.spout.vanilla.protocol.entity.creature.SheepEntityProtocol;
import org.spout.vanilla.protocol.msg.entity.EntityStatusMessage;

/**
 * A component that identifies the entity as a Sheep.
 */
public class Sheep extends Animal implements Passive {
	private static final int TICKS_UNTIL_ADULT = 24000;
	private static final int GRASS_GROWTH_TICK_BONUS = 1200;
	private static final int GRASS_CHANCE_AS_ADULT = 1000;
	private static final int GRASS_CHANCE_AS_BABY = 50;

	@Override
	public void onAttached() {
		super.onAttached();
		getOwner().getNetwork().setEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID, new SheepEntityProtocol());
		DeathDrops dropComponent = getOwner().add(DeathDrops.class);
		dropComponent.addDrop(new ItemStack(VanillaMaterials.WOOL, 1));
		dropComponent.addXpDrop((short) (getRandom().nextInt(3) + 1));

		if (getAttachedCount() == 1) {
			getOwner().add(Health.class).setSpawnHealth(10);
		}
		NearbyMaterialHolderSensor materialHolderSensor = new NearbyMaterialHolderSensor(getAI(), VanillaMaterials.WHEAT);
		materialHolderSensor.setSensorRadius(16);
		getAI().registerSensor(materialHolderSensor);
		getAI().registerGoal(new FollowMaterialHolderGoal(getAI()));
		getAI().registerAction(new FollowMaterialHolderAction(getAI()));
	}

	@Override
	public void onTick(float dt) {
		if (shouldEatGrass()) {
			eatGrass();
		}
	}

	public boolean isSheared() {
		return getOwner().getData().get(VanillaData.SHEARED);
	}

	public void setSheared(boolean sheared) {
		getOwner().getData().put(VanillaData.SHEARED, sheared);
		Parameter<Byte> param;
		if (sheared) {
			param = new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) (getColor().getData() | 16));
		} else {
			param = new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) (getColor().getData() & -17));
		}
		setMetadata(param);
	}

	/**
	 * Gets the color of the sheep.
	 * @return color of the sheep.
	 */
	public WoolColor getColor() {
		return WoolColor.getById(getOwner().getData().get(VanillaData.WOOL_COLOR).shortValue());
	}

	/**
	 * Sets the color of the sheep.
	 * @param color
	 */
	public void setColor(Wool.WoolColor color) {
		short oldData = getColor().getData();
		short newData = color.getData();
		getOwner().getData().put(VanillaData.WOOL_COLOR, newData);
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) (oldData & 240 | newData & 15)));
	}

	private boolean isBlockEatableTallGrass(int x, int y, int z) {
		return getOwner().getWorld().getBlockMaterial(x, y, z) == VanillaMaterials.TALL_GRASS && getOwner().getWorld().getBlockData(x, y, z) == 1;
	}

	/**
	 * Is called when the sheep eats grass. Applies the bonuses to the sheep.
	 */
	private void onGrassEaten() {
		getOwner().getNetwork().callProtocolEvent(new EntityStatusEvent(getOwner(), EntityStatusMessage.SHEEP_EAT_GRASS));
		getOwner().getData().put(VanillaData.SHEARED, false);
		long newGrowthTicks = getOwner().getData().get(VanillaData.GROWTH_TICKS) + GRASS_GROWTH_TICK_BONUS;
		if (newGrowthTicks > TICKS_UNTIL_ADULT) {
			newGrowthTicks = TICKS_UNTIL_ADULT;
		}
		getOwner().getData().put(VanillaData.GROWTH_TICKS, newGrowthTicks);
	}

	private void eatGrass() {
		final Point position = getOwner().getScene().getPosition();
		final int x = position.getFloorX();
		final int y = position.getFloorY() - 1;
		final int z = position.getFloorZ();
		if (isBlockEatableTallGrass(x, y, z)) {
			getOwner().getWorld().setBlockMaterial(x, y, z, VanillaMaterials.AIR, (short) 0, new EntityCause(getOwner()));
			onGrassEaten();
		} else if (getOwner().getWorld().getBlockMaterial(x, y, z) == VanillaMaterials.GRASS) {
			getOwner().getWorld().setBlockMaterial(x, y, z, VanillaMaterials.DIRT, (short) 0, new EntityCause(getOwner()));
			onGrassEaten();
		}
	}

	public boolean shouldEatGrass() {
		final Random random = getRandom();
		final Point position = getOwner().getScene().getPosition();
		final int x = position.getFloorX();
		final int y = position.getFloorY() - 1;
		final int z = position.getFloorZ();
		int maxInt = 0;
		if (getOwner().getData().get(VanillaData.GROWTH_TICKS) < 24000) {
			maxInt = GRASS_CHANCE_AS_BABY;
		} else {
			maxInt = GRASS_CHANCE_AS_ADULT;
		}
		if (random.nextInt(maxInt) != 0) {
			return false;
		}
		if (isBlockEatableTallGrass(x, y, z)) {
			return true;
		}
		return getOwner().getWorld().getBlockMaterial(x, y, z) == VanillaMaterials.GRASS;
	}
}
