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
package org.spout.vanilla.plugin.material.block;

import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.model.Model;
import org.spout.api.resource.ResourcePointer;

import org.spout.vanilla.plugin.component.substance.object.FallingBlock;
import org.spout.vanilla.plugin.material.VanillaBlockMaterial;
import org.spout.vanilla.plugin.material.VanillaMaterials;

public class SolidMoving extends Solid {
	public SolidMoving(String name, int id, int data, VanillaBlockMaterial parent) {
		this(name, id, data, parent, (ResourcePointer<Model>) null);
	}

	public SolidMoving(String name, int id, int data, VanillaBlockMaterial parent, ResourcePointer<Model> model) {
		super(name, id, data, parent, model);
	}

	public SolidMoving(String name, int id, ResourcePointer<Model> model) {
		super(name, id, model);
	}

	@Override
	public boolean hasPhysics() {
		return true;
	}

	@Override
	public void onUpdate(BlockMaterial oldMaterial, Block block) {
		super.onUpdate(oldMaterial, block);
		if (!block.translate(BlockFace.BOTTOM).getMaterial().isPlacementObstacle()) {
			// turn this block into a mobile block
			Entity e = block.getWorld().createAndSpawnEntity(block.getPosition(), FallingBlock.class, LoadOption.NO_LOAD);
			e.add(FallingBlock.class).setMaterial(this);
			block.setMaterial(VanillaMaterials.AIR);
		}
	}
}
