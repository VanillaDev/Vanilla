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
package org.spout.vanilla.components.basic;

import org.spout.api.component.components.EntityComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Vector3;
import org.spout.api.util.BlockIterator;

/**
 * Component that controls the rotation of a head on Vanilla entities.
 */
public class HeadComponent extends EntityComponent {
	private Vector3 lookingAt = Vector3.ZERO;
	private int headYaw = 0;
	private int lastHeadYaw = 0;
	private float headHeight = 1.0f;

	@Override
	public boolean canTick() {
		return true;
	}

	@Override
	public void onTick(float dt) {
		lastHeadYaw = (int) getHolder().getTransform().getYaw();
		headYaw = (int) getHolder().getTransform().getTransformLive().getRotation().getYaw();
	}

	public boolean isDirty() {
		return headYaw != lastHeadYaw;
	}

	/**
	 * Sets the yaw of a entity's head for the next tick.
	 * @param headYaw
	 */
	public void setYaw(int headYaw) {
		this.headYaw = headYaw;
	}

	public int getYaw() {
		return headYaw;
	}

	/**
	 * Sets the position where the player should look.
	 * @param lookingAt {@link org.spout.api.math.Vector3} to look at
	 */
	public void setLooking(Vector3 lookingAt) {
		this.lookingAt = lookingAt;
	}

	/**
	 * Gets the {@link Vector3} the player is currently looking at.
	 * @return position the player is looking at
	 */
	public Vector3 getLookingAt() {
		return lookingAt;
	}

	/**
	 * Sets the current height of the head above the main position
	 * @param height
	 */
	public void setHeight(float height) {
		this.headHeight = height;
	}

	/**
	 * Gets the current height of the head above the main position
	 * @return the head height
	 */
	public float getHeight() {
		return this.headHeight;
	}

	/**
	 * Gets the position of the head of this living entity
	 * @return the head position
	 */
	public Point getPosition() {
		return getHolder().getTransform().getPosition().add(0.0f, this.getHeight(), 0.0f);
	}

	public Transform getHeadTransform() {
		Transform trans = new Transform();
		trans.setPosition(this.getPosition());
		trans.setRotation(getHolder().getTransform().getRotation());
		return trans;
	}

	public BlockIterator getBlockView() {
		return getBlockView(((InteractComponent)getHolder().getOrCreate(InteractComponent.class)).getReach());
	}

	public BlockIterator getBlockView(int maxDistance) {
		return new BlockIterator(getHolder().getWorld(), getHolder().getTransform().getTransform(), maxDistance);
	}
}
