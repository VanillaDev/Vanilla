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
package org.spout.vanilla.material.block.misc;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;

import org.spout.vanilla.material.VanillaBlockMaterial;

public class Fire extends VanillaBlockMaterial {
	public Fire() {
		super("Fire", 51);
	}

	@Override
	public void initialize() {
		super.initialize();
		this.setHardness(0.0F).setResistance(0.0F).setLightLevel(15).setDrop(null);
	}

	@Override
	public void onDestroy(Block block) {
		super.onDestroy(block);
	}

	@Override
	public boolean hasPhysics() {
		return true;
	}

	@Override
	public void onUpdate(Block block) {
		super.onUpdate(block);
		if (!this.canPlace(block, block.getData(), BlockFace.BOTTOM, false)) {
			this.onDestroy(block);
		}
	}

	@Override
	public boolean canPlace(Block block, short data, BlockFace attachedFace, boolean isClickedBlock) {
		if (super.canPlace(block, data, attachedFace, isClickedBlock)) {
			BlockMaterial mat = block.getMaterial();
			for (BlockFace face : BlockFaces.BTNSWE) {
				mat = block.translate(face).getSubMaterial();
				if (mat instanceof VanillaBlockMaterial) {
					if (((VanillaBlockMaterial) mat).canSupport(this, face.getOpposite())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isPlacementObstacle() {
		return false;
	}
}
