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
package org.spout.vanilla.world.generator.structure.stronghold;

import java.util.Arrays;
import java.util.List;

import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;

import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.world.generator.structure.ComponentCuboidPart;
import org.spout.vanilla.world.generator.structure.Structure;
import org.spout.vanilla.world.generator.structure.StructureComponent;
import org.spout.vanilla.world.generator.structure.stronghold.StrongholdDoor.EmptyDoorway;

public class StrongholdSpiralStaircase extends StructureComponent {
	public StrongholdSpiralStaircase(Structure parent) {
		super(parent);
	}

	@Override
	public boolean canPlace() {
		final ComponentCuboidPart box = new ComponentCuboidPart(this);
		box.setMinMax(-1, -7, -1, 5, 5, 5);
		return !box.intersectsLiquids();
	}

	@Override
	public void place() {
		// General shape
		final ComponentCuboidPart box = new ComponentCuboidPart(this);
		box.setPicker(new StrongholdBlockMaterialPicker(getRandom()));
		box.setMinMax(0, -6, 0, 4, 4, 4);
		box.fill(true);
		// Place the doors
		StrongholdDoor.getRandomDoor(this, getRandom()).place(1, 1, 0);
		new EmptyDoorway(this).place(1, -5, 4);
		// Place the steps
		setBlockMaterial(2, 0, 1, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -1, 1, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, 0, 1, VanillaMaterials.SLAB);
		setBlockMaterial(1, -1, 2, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -2, 3, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -1, 3, VanillaMaterials.SLAB);
		setBlockMaterial(2, -2, 3, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(3, -3, 3, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(3, -2, 3, VanillaMaterials.SLAB);
		setBlockMaterial(3, -3, 2, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(3, -4, 1, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(3, -3, 1, VanillaMaterials.SLAB);
		setBlockMaterial(2, -4, 1, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -5, 1, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -4, 1, VanillaMaterials.SLAB);
		setBlockMaterial(1, -5, 2, VanillaMaterials.STONE_BRICK);
		setBlockMaterial(1, -5, 3, VanillaMaterials.SLAB);
	}

	@Override
	public void randomize() {
	}

	@Override
	public List<StructureComponent> getNextComponents() {
		final StructureComponent component;
		final float draw = getRandom().nextFloat();
		if (draw > 0.95) {
			component = new StrongholdLibrary(parent);
			component.setPosition(position.add(rotate(-3, -6, 5)));
		} else if (draw > 0.90) {
			component = new StrongholdLargeIntersection(parent);
			component.setPosition(position.add(rotate(-3, -8, 5)));
		} else if (draw > 0.75) {
			component = new StrongholdChestCorridor(parent);
			component.setPosition(position.add(rotate(0, -6, 5)));
		} else if (draw > 0.60) {
			component = new StrongholdRoom(parent);
			component.setPosition(position.add(rotate(-3, -6, 5)));
		} else if (draw > 0.45) {
			component = new StrongholdPrison(parent);
			component.setPosition(position.add(rotate(0, -6, 5)));
		} else if (draw > 0.30) {
			component = new StrongholdIntersection(parent);
			component.setPosition(position.add(rotate(0, -6, 5)));
		} else if (draw > 0.15) {
			component = new StrongholdCorridor(parent);
			component.setPosition(position.add(rotate(0, -6, 5)));
		} else {
			component = new StrongholdTurn(parent);
			component.setPosition(position.add(rotate(0, -6, 5)));
		}
		component.setRotation(rotation);
		component.randomize();
		return Arrays.asList(component);
	}

	@Override
	public BoundingBox getBoundingBox() {
		final Vector3 rotatedMin = transform(0, -6, 0);
		final Vector3 rotatedMax = transform(4, 4, 4);
		return new BoundingBox(MathHelper.min(rotatedMin, rotatedMax), MathHelper.max(rotatedMin, rotatedMax));
	}
}
