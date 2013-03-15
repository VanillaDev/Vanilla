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
package org.spout.vanilla.inventory.window;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.ServerOnly;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.entity.Player;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.gui.component.TexturedRectComponent;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.inventory.Slot;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;

import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.component.entity.inventory.PlayerInventory;
import org.spout.vanilla.component.entity.living.Human;
import org.spout.vanilla.component.entity.substance.Item;
import org.spout.vanilla.data.VanillaRenderMaterials;
import org.spout.vanilla.event.window.WindowCloseEvent;
import org.spout.vanilla.event.window.WindowOpenEvent;
import org.spout.vanilla.event.window.WindowPropertyEvent;
import org.spout.vanilla.inventory.entity.QuickbarInventory;
import org.spout.vanilla.inventory.player.CraftingInventory;
import org.spout.vanilla.inventory.player.PlayerMainInventory;
import org.spout.vanilla.inventory.util.GridInventoryConverter;
import org.spout.vanilla.inventory.util.InventoryConverter;

/**
 * Represents a Window that players can view to display an inventory.
 */
public abstract class Window extends AbstractWindow {
	private final List<InventoryConverter> converters = new LinkedList<InventoryConverter>();
	protected boolean opened;
	// Widgets
	protected final Screen popup = new Screen();
	protected final Widget background;
	protected final Widget label;
	// Measurements
	// Background
	public static final float WIDTH = 0.6875f;
	public static final float HEIGHT = 0.6484375f;
	public static final Vector2 BACKGROUND_EXTENTS = new Vector2(WIDTH, HEIGHT);
	// Quickbar
	public static final float QUICKBAR_X = -0.475f;
	public static final float QUICKBAR_Y = -0.63f;
	public static final Vector2 QUICKBAR_POSITION = new Vector2(QUICKBAR_X, QUICKBAR_Y);
	// Main
	public static final float MAIN_X = QUICKBAR_X;
	public static final float MAIN_Y = QUICKBAR_Y + 0.17f;
	public static final Vector2 MAIN_POSITION = new Vector2(MAIN_X, MAIN_Y);
	private static final float SCALE = 0.75f;

	public Window(Player owner, WindowType type, String title, int offset) {
		super(owner, type, title, offset);

		PlayerInventory inventory = getPlayerInventory();
		GridInventoryConverter main = new GridInventoryConverter(inventory.getMain(), 9, offset, MAIN_POSITION);
		addInventoryConverter(main);
		addInventoryConverter(new GridInventoryConverter(inventory.getQuickbar(), 9, offset + main.getGrid().getSize(), QUICKBAR_POSITION));

		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				background = label = null;
				break;
			case CLIENT:
				background = ((Client)  VanillaPlugin.getInstance().getEngine()).getScreenStack().createWidget();
				label = ((Client)  VanillaPlugin.getInstance().getEngine()).getScreenStack().createWidget();
				VanillaPlugin plugin = VanillaPlugin.getInstance();
				popup.setGrabsMouse(false);

				// Setup the window to render
				TexturedRectComponent backgroundRect = background.add(TexturedRectComponent.class);
				backgroundRect.setRenderMaterial(type.getRenderMaterial());
				backgroundRect.setSprite(new Rectangle(-WIDTH * SCALE, -WIDTH, HEIGHT * 2 * SCALE, HEIGHT * 2));
				backgroundRect.setSource(new Rectangle(0, 0, WIDTH, HEIGHT));
				popup.attachWidget(plugin, background);

				// Draw title
				LabelComponent labelComponent = label.add(LabelComponent.class);
				labelComponent.setFont(VanillaRenderMaterials.FONT);
				labelComponent.setText(new ChatArguments(ChatStyle.GRAY, title));
				label.getTransform().add(0, 0.45f);
				popup.attachWidget(plugin, label);

				for (InventoryConverter converter : converters) {
					for (Widget widget : converter.getWidgets()) {
						popup.attachWidget(plugin, widget);
					}
				}
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform().toString());
		}
	}

	public Window(Player owner, WindowType type, String title) {
		this(owner, type, title, 0);
	}

	@Override
	public void open() {
		opened = true;
		System.out.println("OPEN");
		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				callProtocolEvent(new WindowOpenEvent(this));
				reload();
				break;
			case CLIENT:
				((Client)  VanillaPlugin.getInstance().getEngine()).getScreenStack().openScreen(popup);
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform().toString());
		}
	}

	@Override
	public void close() {
		removeAllInventoryConverters();
		opened = false;
		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				if (getHuman() == null || getHuman().isSurvival()) {
					dropCursorItem();
				}
				callProtocolEvent(new WindowCloseEvent(this));
				break;
			case CLIENT:
				((Client)  VanillaPlugin.getInstance().getEngine()).getScreenStack().closeScreen(popup);
				// TODO: Send close packet
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform().toString());
		}
	}

	@Override
	public boolean onShiftClick(ItemStack stack, int slot, Inventory from) {
		final PlayerInventory inventory = getPlayerInventory();
		if (from instanceof CraftingInventory) {
			if (((CraftingInventory) from).onShiftClick(slot, inventory)) {
				return true;
			}
		}

		// Transferring to the main inventory, top to bottom
		if (!(from instanceof PlayerMainInventory)) {
			final Inventory main = inventory.getMain();
			for (int row = PlayerMainInventory.HEIGHT - 1; row >= 0; row--) {
				int startSlot = PlayerMainInventory.LENGTH * row;
				int endSlot = startSlot + PlayerMainInventory.LENGTH - 1;
				main.add(startSlot, endSlot, stack);
				from.set(slot, stack);
				if (stack.isEmpty()) {
					return true;
				}
			}
		}

		// Transferring to the quickbar inventory
		if (!(from instanceof QuickbarInventory)) {
			inventory.getQuickbar().add(stack);
			from.set(slot, stack);
			if (stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onClick(ClickArguments args) {
		if (handleClick(args)) {
			reload();
			return true;
		}
		return false;
	}

	private boolean handleClick(ClickArguments args) {
		Slot s = args.getSlot();
		Inventory inventory = s.getInventory();
		int slot = s.getIndex();
		ItemStack clicked = s.get();
		if (args.isShiftClick()) {
			debug("[Window] Shift-Clicked slot " + slot);
			if (clicked != null) {
				return onShiftClick(clicked, slot, inventory);
			}
		} else if (args.isRightClick()) {
			debug("[Window] Right-Clicked slot " + slot + " using Cursor: " + cursorItem);
			debug("[Window] Item at clicked slot: " + (clicked == null ? "Empty" : clicked.getMaterial().getName()));
			if (clicked == null) {
				if (cursorItem != null) {
					debug("[Window] Add one");
					// slot is empty with a not empty cursor
					// add one
					clicked = cursorItem.clone();
					clicked.setAmount(1);
					// Can it be set?
					if (canSet(inventory, slot, clicked)) {
						inventory.set(slot, clicked);
						// remove from cursor
						cursorItem.setAmount(cursorItem.getAmount() - 1);
						if (cursorItem.isEmpty()) {
							cursorItem = null;
						}
						return true;
					}
				}
			} else if (cursorItem != null) {
				// slot is not empty with not empty cursor
				if (cursorItem.equalsIgnoreSize(clicked)) {
					// only stack materials that are the same
					if (clicked.getMaxStackSize() > clicked.getAmount()) {
						debug("[Window] Stacking");
						// add one if can fit
						clicked.setAmount(clicked.getAmount() + 1);
						if (canSet(inventory, slot, clicked)) {
							inventory.set(slot, clicked);
							cursorItem.setAmount(cursorItem.getAmount() - 1);
							if (cursorItem.isEmpty()) {
								cursorItem = null;
							}
							return true;
						} else {
							//Crafting result slot?
							//Reset state
							clicked.setAmount(clicked.getAmount() - 1);
							cursorItem.stack(clicked);
							if (clicked.isEmpty()) {
								clicked = null;
								inventory.set(slot, null, true /*will trigger crafting table to create new result if possible*/);
							} else {
								inventory.set(slot, clicked, false /*will not trigger crafting table to create new result (some result still left in slot)*/);
							}
						}
					}
				} else {
					// Can it be set?
					if (canSet(inventory, slot, cursorItem)) {
						debug("[Window] Materials don't match. Swapping stacks.");
						// materials don't match
						// swap stacks
						ItemStack newCursor = clicked.clone();
						inventory.set(slot, cursorItem);
						cursorItem = newCursor;
						return true;
					}
				}
			} else {
				// slot is not empty with an empty cursor
				// split the stack
				int x = clicked.getAmount();
				int y = x / 2;
				int z = x % 2;
				clicked.setAmount(y);
				inventory.set(slot, clicked);
				// cursor gets any remainder
				cursorItem = clicked.clone();
				cursorItem.setAmount(y + z);
				return true;
			}
		} else {
			debug("[Window] Left-Clicked slot " + slot + " using Cursor: " + cursorItem);
			debug("[Window] Item at clicked slot: " + (clicked == null ? "Empty" : clicked.getMaterial().getName()));
			if (clicked == null) {
				if (cursorItem != null) {
					debug("[Window] Put whole stack in slot");
					// slot is empty; cursor is not empty.
					// put whole stack down
					clicked = cursorItem.clone();
					// Can it be set?
					if (canSet(inventory, slot, clicked)) {
						inventory.set(slot, clicked);
						cursorItem = null;
						return true;
					}
				}
			} else if (cursorItem != null) {
				// slot is not empty; cursor is not empty.
				// stack
				if (cursorItem.equalsIgnoreSize(clicked)) {
					debug("[Window] Stacking");
					//Try to set items
					if (canSet(inventory, slot, clicked)) {
						clicked.stack(cursorItem);
						inventory.set(slot, clicked);
						if (cursorItem.isEmpty()) {
							cursorItem = null;
						}
						//Else try to pick them up (crafting)
					} else {
						cursorItem.stack(clicked);
						if (clicked.isEmpty()) {
							clicked = null;
							inventory.set(slot, null, true /*will trigger crafting table to create new result if possible*/);
						} else {
							inventory.set(slot, clicked, false /*will not trigger crafting table to create new result (some result still left in slot)*/);
						}
					}
					return true;
				} else {
					// Can it be set?
					if (canSet(inventory, slot, clicked)) {
						debug("[Window] Materials don't match. Swapping stacks.");
						// materials don't match
						// swap stacks
						ItemStack newCursor = clicked.clone();
						inventory.set(slot, cursorItem);
						cursorItem = newCursor;
					}
				}
			} else {
				// slot is not empty; cursor is empty.
				// pick up stack
				cursorItem = clicked.clone();
				inventory.set(slot, null);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCreativeClick(Inventory inventory, int clickedSlot, ItemStack item) {
		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				cursorItem = null;
				inventory.set(clickedSlot, item);
				break;
			case CLIENT:
				// TODO: Creative handling
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform().toString());
		}
	}

	@Override
	public boolean onOutsideClick() {
		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				dropCursorItem();
				return true;
			case CLIENT:
				// TODO: Client handling
				return false;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform().toString());
		}
	}

	@ServerOnly
	@Override
	public void dropCursorItem() {
		if (VanillaPlugin.getInstance().getEngine().getPlatform() == Platform.CLIENT) {
			throw new IllegalStateException("Cannot drop cursor item from client.");
		}

		if (cursorItem != null) {
			Item.dropNaturally(getPlayer().getScene().getPosition(), cursorItem);
			cursorItem = null;
		}
	}

	@Override
	public int getSize() {
		int size = 0;
		for (InventoryConverter converter : converters) {
			size += converter.getInventory().size();
		}
		return size;
	}

	@Override
	public boolean isOpened() {
		return opened;
	}

	@Override
	public Slot getSlot(int nativeSlot) {
		int slot;
		for (InventoryConverter converter : converters) {
			slot = converter.convert(nativeSlot);
			if (slot != -1) {
				debug("[MC=" + nativeSlot + "] -> Converted -> [Spout=" + slot + "]");
				return new Slot(converter.getInventory(), slot);
			}
		}
		debug("[MC=" + nativeSlot + "] -> Converted -> NOT FOUND!");
		return null;
	}

	@Override
	public ClickArguments getClickArguments(int nativeSlot, boolean rightClick, boolean shiftClick) {
		Slot entry = getSlot(nativeSlot);
		if (entry != null) {
			return new ClickArguments(entry.getInventory(), entry.getIndex(), rightClick, shiftClick);
		}
		return null;
	}

	/**
	 * Gets the human viewing the window
	 * @return human
	 */
	public final Human getHuman() {
		return getPlayer().get(Human.class);
	}

	/**
	 * Gets the converter for the specified inventory.
	 * @param inventory
	 * @return
	 */
	public InventoryConverter getInventoryConverter(Inventory inventory) {
		for (InventoryConverter converter : converters) {
			if (converter.getInventory().equals(inventory)) {
				return converter;
			}
		}
		return null;
	}

	/**
	 * Returns all inventory converters
	 * @return
	 */
	public List<InventoryConverter> getInventoryConverters() {
		return converters;
	}

	/**
	 * Adds an inventory converter
	 * @param converter
	 */
	public void addInventoryConverter(InventoryConverter converter) {
		converter.getInventory().addViewer(this);
		converters.add(converter);
	}

	/**
	 * Removes an inventory conveter
	 * @param converter
	 */
	public void removeInventoryConverter(InventoryConverter converter) {
		converter.getInventory().removeViewer(this);
		converters.remove(converter);
	}

	/**
	 * Clears all inventory converters
	 */
	public void removeAllInventoryConverters() {
		Iterator<InventoryConverter> i = converters.iterator();
		while (i.hasNext()) {
			InventoryConverter converter = i.next();
			converter.getInventory().removeViewer(this);
			i.remove();
		}
	}

	/**
	 * Sets a property of the window
	 * @param id of the property
	 * @param value value of property
	 */
	public void setProperty(int id, int value) {
		properties.put(id, value);
		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				callProtocolEvent(new WindowPropertyEvent(this, id, value));
				break;
			case CLIENT:
				// TODO: Window properties
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform());
		}
	}

	@Override
	public void onSlotSet(Inventory inventory, int slot, ItemStack item, ItemStack previous) {
		InventoryConverter slots = getInventoryConverter(inventory);
		if (slots == null) {
			return;
		}

		switch (VanillaPlugin.getInstance().getEngine().getPlatform()) {
			case PROXY:
			case SERVER:
				QuickbarInventory quickbar = getPlayerInventory().getQuickbar();
				debug("[Window] Slot changed: " + slot + " = " + item);
				//callProtocolEvent(new WindowSlotEvent(this, inventory, slots.revert(slot), item));
				reload();
				// Update the held item
				if (inventory instanceof QuickbarInventory && slot == quickbar.getSelectedSlot().getIndex()) {
					((QuickbarInventory) inventory).updateHeldItem(getPlayer());
				}
				break;
			case CLIENT:
				slots.getWidgets()[slot].update();
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + VanillaPlugin.getInstance().getEngine().getPlatform());
		}
	}
}
