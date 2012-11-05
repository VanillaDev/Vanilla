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
package org.spout.vanilla.component.inventory.window;

import java.awt.Color;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.RenderPartsHolderComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Rectangle;
import org.spout.api.plugin.Platform;
import org.spout.api.render.RenderMaterial;

import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.component.inventory.PlayerInventory;
import org.spout.vanilla.event.entity.EntityEquipmentEvent;
import org.spout.vanilla.inventory.player.PlayerArmorInventory;
import org.spout.vanilla.inventory.util.InventoryConverter;
import org.spout.vanilla.inventory.window.WindowType;

public class DefaultWindow extends Window {
	private static final float SCALE = 0.75f; // TODO: Apply directly from engine
	private final Screen windowScreen = new Screen();
	private final RenderMaterial bgMaterial = (RenderMaterial) Spout.getFilesystem().getResource("material://Vanilla/resources/gui/smt/InventoryGUIMaterial.smt");
	
	@Override
	public void onAttached() {
		super.onAttached();
		init(WindowType.DEFAULT, "Inventory", 9);
		PlayerInventory inventory = getHuman().getInventory();
		addInventoryConverter(new InventoryConverter(inventory.getArmor(), "8, 7, 6, 5"));
		addInventoryConverter(new InventoryConverter(inventory.getCraftingGrid(), "3-4, 1-2, 0"));
		
		if (Spout.getPlatform() == Platform.CLIENT) {
			initScreen();
		}
	}
	
	private void initScreen() {
		Widget background = new Widget();
		RenderPart bg = new RenderPart();
		bg.setRenderMaterial(bgMaterial);
		bg.setColor(Color.WHITE);
		bg.setSprite(new Rectangle(-0.5f * SCALE, -0.5f, 1f * SCALE, 1f));
		bg.setSource(new Rectangle(0, 0, 176f / 256f, 166f / 256f));
		background.add(RenderPartsHolderComponent.class).add(bg);
		windowScreen.attachWidget(VanillaPlugin.getInstance(), background);
	}

	@Override
	public void onSlotSet(Inventory inventory, int slot, ItemStack item) {
		super.onSlotSet(inventory, slot, item);
		if (inventory instanceof PlayerArmorInventory) {
			Player player = getPlayer();
			player.getNetwork().callProtocolEvent(new EntityEquipmentEvent(player, slot + 1, item));
		}
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void open() {
		if (Spout.getPlatform() == Platform.SERVER) {
			throw new UnsupportedOperationException("A player's inventory window cannot be opened from the server.");
		}
		
		((Client) Spout.getEngine()).getScreenStack().openScreen(windowScreen);
	}
}
