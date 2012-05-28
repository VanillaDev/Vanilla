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
package org.spout.vanilla.inventory;

import java.util.HashSet;
import java.util.Set;

import org.spout.api.inventory.Inventory;
import org.spout.vanilla.controller.living.player.VanillaPlayer;
import org.spout.vanilla.window.Window;

public abstract class WindowInventory extends Inventory implements VanillaInventory {
	private static final long serialVersionUID = 1L;

	private HashSet<VanillaPlayer> viewers = new HashSet<VanillaPlayer>();

	public WindowInventory(int size) {
		super(size);
	}

	public abstract Window createWindow(VanillaPlayer player);

	public Set<VanillaPlayer> getViewingPlayers() {
		return this.viewers;
	}

	public void open(VanillaPlayer player) {
		this.viewers.add(player);
		player.setWindow(this.createWindow(player));
	}

	public void close(VanillaPlayer player) {
		this.viewers.remove(player);
		player.closeWindow();
	}
}
