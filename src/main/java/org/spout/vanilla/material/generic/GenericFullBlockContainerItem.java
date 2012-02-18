/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.material.generic;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.ItemMaterial;

public class GenericFullBlockContainerItem extends GenericBlockItem {

	private ItemMaterial container;

	public GenericFullBlockContainerItem(String name, int id, int data, boolean subtypes, BlockMaterial onPlace, ItemMaterial container) {
		super(name, id, data, subtypes, onPlace);

		if (!(container instanceof GenericEmptyBlockContainerItem)) {
			throw new IllegalArgumentException();
		}

		GenericEmptyBlockContainerItem.class.cast(container).register(this);

		this.container = container;
	}

	public GenericFullBlockContainerItem(String name, int id, int data, BlockMaterial onPlace, ItemMaterial container) {
		super(name, id, data, onPlace);

		if (!(container instanceof GenericEmptyBlockContainerItem)) {
			throw new IllegalArgumentException();
		}

		GenericEmptyBlockContainerItem.class.cast(container).register(this);

		this.container = container;
	}

	public GenericFullBlockContainerItem(String name, int id, BlockMaterial onPlace, ItemMaterial container) {
		super(name, id, onPlace);

		if (!(container instanceof GenericEmptyBlockContainerItem)) {
			throw new IllegalArgumentException();
		}

		GenericEmptyBlockContainerItem.class.cast(container).register(this);

		this.container = container;
	}

	public ItemMaterial getContainer() {
		return container;
	}

	@Override
	public void onInteract(Entity entity, Point position, Action type) {
		super.onInteract(entity, position, type);

		Inventory inventory = entity.getInventory();
		if (inventory.getCurrentItem() == null) {
			inventory.setItem(new ItemStack(getContainer(), 1), inventory.getCurrentSlot());
		}
	}
}
