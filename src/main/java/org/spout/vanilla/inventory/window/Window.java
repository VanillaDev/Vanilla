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
import java.util.logging.Level;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.spout.api.Client;
import org.spout.api.ServerOnly;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.entity.Player;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.gui.component.TexturedRectComponent;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.InventoryViewer;
import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.event.ProtocolEvent;

import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.component.inventory.PlayerInventory;
import org.spout.vanilla.component.living.neutral.Human;
import org.spout.vanilla.component.substance.Item;
import org.spout.vanilla.data.VanillaRenderMaterials;
import org.spout.vanilla.event.window.WindowCloseEvent;
import org.spout.vanilla.event.window.WindowItemsEvent;
import org.spout.vanilla.event.window.WindowOpenEvent;
import org.spout.vanilla.event.window.WindowPropertyEvent;
import org.spout.vanilla.event.window.WindowSlotEvent;
import org.spout.vanilla.inventory.player.PlayerMainInventory;
import org.spout.vanilla.inventory.player.PlayerQuickbar;
import org.spout.vanilla.inventory.util.GridInventoryConverter;
import org.spout.vanilla.inventory.util.InventoryConverter;
import org.spout.vanilla.inventory.window.gui.InventorySlot;
import org.spout.vanilla.inventory.window.gui.RenderItemStack;
import org.spout.vanilla.inventory.window.prop.WindowProperty;

/**
 * Represents a Window that players can view to display an inventory.
 */
public abstract class Window implements InventoryViewer {
	private final Player owner;
	private final List<InventoryConverter> converters = new LinkedList<InventoryConverter>();
	protected final TObjectIntMap<Integer> properties = new TObjectIntHashMap<Integer>();
	protected final int offset;
	protected final WindowType type;
	protected final String title;
	protected boolean opened;
	protected ItemStack cursorItem;
	private static int windowId = 0;
	protected int id = -1;
	// Client only
	protected boolean shiftDown;
	// Widgets
	protected final Screen popup = new Screen();
	protected final Widget background = new Widget();
	protected final Widget label = new Widget();
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

		this.owner = owner;
		this.type = type;
		this.title = title;
		this.offset = offset;

		PlayerInventory inventory = getPlayerInventory();
		GridInventoryConverter main = new GridInventoryConverter(inventory.getMain(), 9, offset, MAIN_POSITION);
		addInventoryConverter(main);
		addInventoryConverter(new GridInventoryConverter(inventory.getQuickbar(), 9, offset + main.getGrid().getSize(), QUICKBAR_POSITION));

		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				// Initialize the window id on the server
				id = windowId++;
				break;
			case CLIENT:
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
				label.setGeometry(new Rectangle(0, 0.45f, 0, 0));
				popup.attachWidget(plugin, label);

				for (InventoryConverter converter : converters) {
					for (Widget widget : converter.getWidgets()) {
						popup.attachWidget(plugin, widget);
					}
				}

				break;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform().toString());
		}
	}

	public Window(Player owner, WindowType type, String title) {
		this(owner, type, title, 0);
	}

	/**
	 * Opens the window from the server on the client.
	 */
	public void open() {
		opened = true;
		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				callProtocolEvent(new WindowOpenEvent(this));
				reload();
				break;
			case CLIENT:
				((Client) Spout.getEngine()).getScreenStack().openScreen(popup);
				break;
		}
	}

	/**
	 * Closes this window
	 */
	public void close() {
		removeAllInventoryConverters();
		opened = false;
		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				if (getHuman() == null || getHuman().isSurvival()) {
					dropCursorItem();
				}
				callProtocolEvent(new WindowCloseEvent(this));
				break;
			case CLIENT:
				((Client) Spout.getEngine()).getScreenStack().closeScreen(popup);
				// TODO: Send close packet
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform().toString());
		}
	}

	/**
	 * Reloads the window's items
	 */
	@ServerOnly
	public void reload() {
		if (Spout.getPlatform() == Platform.CLIENT) {
			throw new IllegalStateException("Cannot reload window in client mode.");
		}

		ItemStack[] items = new ItemStack[getSize()];
		for (int i = 0; i < items.length; i++) {
			InventoryEntry entry = getInventoryEntry(i);
			if (entry != null) {
				items[i] = entry.getInventory().get(entry.getSlot());
			}
		}

		callProtocolEvent(new WindowItemsEvent(this, items));
	}

	/**
	 * Handles a click when the shift button is held down
	 * @param stack clicked
	 * @param slot clicked
	 * @param from inventory with item
	 * @return true if successful
	 */
	@ServerOnly
	public boolean onShiftClick(ItemStack stack, int slot, Inventory from) {
		if (Spout.getPlatform() == Platform.CLIENT) {
			throw new IllegalStateException("Shift click handling is handled server side.");
		}

		PlayerInventory inventory = getPlayerInventory();
		if (from instanceof PlayerQuickbar) {

			// Custom adding for shift clicking

			final PlayerMainInventory main = inventory.getMain();
			final int height = 3;
			final int length = 9;

			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < length; x++) {
					int x1 = length * y;
					int x2 = x1 + length - 1;
					main.add(x1, x2, stack);
					from.set(slot, stack.isEmpty() ? null : stack);
					if (stack.isEmpty()) {
						break;
					}
				}
				break;
			}

			return true;
		}

		if (from instanceof PlayerMainInventory) {
			inventory.getQuickbar().add(stack);
			from.set(slot, stack.isEmpty() ? null : stack);
			return true;
		}

		return false;
	}

	/**
	 * Handles a click on the server or the client.
	 * @param args to handle
	 * @return true if successful
	 */
	public boolean onClick(ClickArguments args) {
		Inventory inventory = args.getInventory();
		int slot = args.getSlot();
		ItemStack clicked = inventory.get(slot);
		debug("Spout slot: " + slot);
		if (args.isShiftClick()) {
			debug("Shift clicked");
			if (clicked != null) {
				return onShiftClick(clicked, slot, inventory);
			}
		} else if (args.isRightClick()) {
			debug("Right click");
			if (clicked == null) {
				debug("Empty slot");
				if (cursorItem != null) {
					debug("Cursor: " + cursorItem.getMaterial().getName());
					debug("Add one");
					// slot is empty with a not empty cursor
					// add one
					clicked = cursorItem.clone();
					clicked.setAmount(1);
					inventory.set(slot, clicked);
					// remove from cursor
					cursorItem.setAmount(cursorItem.getAmount() - 1);
					if (cursorItem.isEmpty()) {
						cursorItem = null;
					}
					return true;
				}
			} else if (cursorItem != null) {
				debug("Slot: " + clicked.getMaterial().getName());
				debug("Cursor: " + cursorItem.getMaterial().getName());
				// slot is not empty with not empty cursor
				if (cursorItem.equalsIgnoreSize(clicked)) {
					// only stack materials that are the same
					if (clicked.getMaxStackSize() > clicked.getAmount()) {
						debug("Stacking");
						// add one if can fit
						clicked.setAmount(clicked.getAmount() + 1);
						inventory.set(slot, clicked);
						cursorItem.setAmount(cursorItem.getAmount() - 1);
						if (cursorItem.isEmpty()) {
							cursorItem = null;
						}
						return true;
					}
				} else {
					debug("Materials don't match. Swapping stacks.");
					// materials don't match
					// swap stacks
					ItemStack newCursor = clicked.clone();
					inventory.set(slot, cursorItem);
					cursorItem = newCursor;
					return true;
				}
			} else {
				debug("Slot: " + clicked.getMaterial().getName());
				debug("Empty cursor");
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
			debug("Left click");
			if (clicked == null) {
				debug("Empty slot");
				if (cursorItem != null) {
					debug("Cursor: " + cursorItem.getMaterial().getName());
					debug("Put whole stack in slot");
					// slot is empty; cursor is not empty.
					// put whole stack down
					clicked = cursorItem.clone();
					inventory.set(slot, clicked);
					cursorItem = null;
					return true;
				}
			} else if (cursorItem != null) {
				debug("Clicked: " + clicked.getMaterial().getName());
				debug("Cursor: " + cursorItem.getMaterial().getName());
				// slot is not empty; cursor is not empty.
				// stack
				if (cursorItem.equalsIgnoreSize(clicked)) {
					debug("Stacking");
					clicked.stack(cursorItem);
					inventory.set(slot, clicked);
					if (cursorItem.isEmpty()) {
						cursorItem = null;
					}
					return true;
				} else {
					debug("Materials don't match. Swapping stacks.");
					// materials don't match
					// swap stacks
					ItemStack newCursor = clicked.clone();
					inventory.set(slot, cursorItem);
					cursorItem = newCursor;
				}
			} else {
				debug("Clicked: " + clicked.getMaterial().getName());
				debug("Empty cursor");
				// slot is not empty; cursor is empty.
				// pick up stack
				cursorItem = clicked.clone();
				inventory.set(slot, null);
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles a click on the creative message
	 * @param inventory clicked
	 * @param clickedSlot slot clicked
	 * @param item clicked
	 */
	public void onCreativeClick(Inventory inventory, int clickedSlot, ItemStack item) {
		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				cursorItem = null;
				inventory.set(clickedSlot, item);
				break;
			case CLIENT:
				// TODO: Creative handling
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform().toString());
		}
	}

	/**
	 * Called when the cursor clicks outside of the window.
	 * @return true if successful
	 */
	public boolean onOutsideClick() {
		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				dropCursorItem();
				return true;
			case CLIENT:
				// TODO: Client handling
				return false;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform().toString());
		}
	}

	/**
	 * Drops the cursor
	 */
	@ServerOnly
	public void dropCursorItem() {
		if (Spout.getPlatform() == Platform.CLIENT) {
			throw new IllegalStateException("Cannot drop cursor item from client.");
		}

		if (cursorItem != null) {
			Item.dropNaturally(this.owner.getTransform().getPosition(), cursorItem);
			cursorItem = null;
		}
	}

	/**
	 * Gets the number of slots on the window.
	 * @return size of window
	 */
	public int getSize() {
		int size = 0;
		for (InventoryConverter converter : converters) {
			size += converter.getInventory().size();
		}
		return size;
	}

	/**
	 * Whether the window is currently being viewed.
	 * @return true if being viewed
	 */
	public boolean isOpened() {
		return opened;
	}

	/**
	 * Gets the inventory at the specified native slot. Returns -1 if
	 * non-existent.
	 * @param nativeSlot clicked
	 * @return inventory entry at slot
	 */
	public InventoryEntry getInventoryEntry(int nativeSlot) {
		int slot;
		debug("Getting InventoryEntry from: " + nativeSlot);
		for (InventoryConverter converter : converters) {
			slot = converter.convert(nativeSlot);
			if (slot != -1) {
				debug("Found: " + slot);
				return new InventoryEntry(converter.getInventory(), slot);
			}
		}
		return null;
	}

	/**
	 * Arguments to handle
	 * @param nativeSlot
	 * @param rightClick
	 * @param shiftClick
	 * @return
	 */
	public ClickArguments getClickArguments(int nativeSlot, boolean rightClick, boolean shiftClick) {
		InventoryEntry entry = getInventoryEntry(nativeSlot);
		if (entry != null) {
			return new ClickArguments(entry.getInventory(), entry.getSlot(), rightClick, shiftClick);
		}
		return null;
	}

	/**
	 * Gets the owner of this window
	 * @return player
	 */
	public final Player getPlayer() {
		return owner;
	}

	/**
	 * Gets the human viewing the window
	 * @return human
	 */
	public final Human getHuman() {
		return owner.get(Human.class);
	}

	/**
	 * Returns the player inventory.
	 * @return player inventory
	 */
	public final PlayerInventory getPlayerInventory() {
		return owner.get(PlayerInventory.class);
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
		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				callProtocolEvent(new WindowPropertyEvent(this, id, value));
				break;
			case CLIENT:
				// TODO: Window properties
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform());
		}
	}

	/**
	 * Sets a property of the window.
	 * @param prop property to set
	 * @param value set
	 */
	public void setProperty(WindowProperty prop, int value) {
		setProperty(prop.getId(), value);
	}

	/**
	 * Returns the value of the specified property.
	 * @param prop
	 * @return value of property
	 */
	public int getProperty(WindowProperty prop) {
		return properties.get(prop);
	}

	/**
	 * Returns the id of the window.
	 * @return id window
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the type of the window.
	 * @return window type
	 */
	public WindowType getType() {
		return type;
	}

	/**
	 * Returns the title of the window.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Whether or not there is an item on the cursor
	 * @return true if has item on cursor
	 */
	public boolean hasCursorItem() {
		return cursorItem != null;
	}

	/**
	 * Gets the item on the cursor
	 * @return item on cursor
	 */
	public ItemStack getCursorItem() {
		return cursorItem;
	}

	/**
	 * Sets the item on the cursor.
	 * @param cursorItem
	 */
	public void setCursorItem(ItemStack cursorItem) {
		this.cursorItem = cursorItem;
		if (Spout.getPlatform() == Platform.CLIENT) {
			// TODO: Attach item to cursor
		}
	}

	public void setShiftDown(boolean shiftDown) {
		this.shiftDown = shiftDown;
	}

	public boolean isShiftDown() {
		return shiftDown;
	}

	public boolean canTick() {
		return false;
	}

	public void onTick(float dt) {
	}

	protected void callProtocolEvent(ProtocolEvent event) {
		// TODO: C->S events
		if (getPlayer() == null) {
			debug(Level.WARNING, "Sending protocol message with null player");
			return;
		}
		if (getPlayer().getNetworkSynchronizer() == null) {
			debug(Level.WARNING, "Sending protocol message with null network synchronizer");
			return;
		}
		getPlayer().getNetworkSynchronizer().callProtocolEvent(event);
	}

	private void debug(Level level, String msg) {
		if (Spout.debugMode()) {
			Spout.getLogger().log(level, msg);
		}
	}

	private void debug(String msg) {
		//debug(Level.INFO, msg);
	}

	@Override
	public void onSlotSet(Inventory inventory, int slot, ItemStack item) {

		InventoryConverter slots = getInventoryConverter(inventory);
		if (slots == null) {
			return;
		}

		switch (Spout.getPlatform()) {
			case PROXY:
			case SERVER:
				// update held item
				PlayerQuickbar quickbar = getPlayerInventory().getQuickbar();
				debug("Slot: " + slot);
				debug("Current slot: " + quickbar.getCurrentSlot());
				// TODO: Setting boots too for some reason...?
				//if (inventory instanceof PlayerQuickbar && slot == quickbar.getCurrentSlot()) {
				//Player player = getPlayer();
				//player.getNetwork().callProtocolEvent(new EntityEquipmentEvent(player, 0, item));
				//}
				callProtocolEvent(new WindowSlotEvent(this, inventory, slots.revert(slot), item));
				break;
			case CLIENT:
				slots.getWidgets()[slot].get(InventorySlot.class).setRenderItemStack(new RenderItemStack(item));
				break;
			default:
				throw new IllegalStateException("Unknown platform: " + Spout.getPlatform());
		}
	}
}
