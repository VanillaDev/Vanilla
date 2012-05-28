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
package org.spout.vanilla.material.item;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.inventory.InventoryBase;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;

import org.spout.vanilla.util.VanillaPlayerUtil;

public class FullContainer extends BlockItem {
	private Material container;

	public FullContainer(String name, int id, BlockMaterial onPlaceMaterial, EmptyContainer emptyContainer) {
		this(name, id, onPlaceMaterial, (short) 0, emptyContainer);
	}

	public FullContainer(String name, int id, BlockMaterial onPlaceMaterial, short onPlaceData, EmptyContainer emptyContainer) {
		super(name, id, onPlaceMaterial, onPlaceData);
		this.container = emptyContainer;
		emptyContainer.register(this);
	}

	public Material getContainer() {
		return container;
	}

	@Override
	public void onInteract(Entity entity, Block block, Action type, BlockFace clickedFace) {
		super.onInteract(entity, block, type, clickedFace);

		InventoryBase inventory = VanillaPlayerUtil.getInventory(block.getSource());
		if (inventory != null && inventory.getCurrentItem() == null) {
			inventory.setCurrentItem(new ItemStack(getContainer(), 1));
		}
	}
}
