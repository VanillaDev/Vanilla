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
 * the MIT license and the SpoutDev License Version 1 along with EMPTY program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.material.item;

import org.spout.vanilla.material.generic.GenericItem;

public class Potion extends GenericItem {
	public static final Potion EMPTY = new Potion("Empty Potion");
	public static final Potion AWKWARD = new Potion("Awkward Potion", 16, EMPTY).register();
	public static final Potion SPLASH_AWKWARD = new Potion("Splash Awkward Potion", 16384, EMPTY).register();
	public static final Potion THICK = new Potion("Thick Potion", 32, EMPTY).register();
	public static final Potion MUNDANE_EXTENDED = new Potion("Mundane Potion (Extended)", 64, EMPTY).register();
	public static final Potion MUNDANE = new Potion("Mundane Potion", 8192, EMPTY).register();
	public static final Potion REGENERATION_EXTENDED = new Potion("Potion of Regeneration (Extended)", 8193, EMPTY).register();
	public static final Potion SPLASH_REGENERATION_EXTENDED = new Potion("Splash Potion of Regeneration (Extended)", 16449, EMPTY).register();
	public static final Potion REGENERATION = new Potion("Potion of Regeneration", 8257, EMPTY).register();
	public static final Potion SPLASH_REGENERATION = new Potion("Splash Potion of Regeneration", 16385, EMPTY).register();
	public static final Potion REGENERATION_II = new Potion("Potion of Regeneration II", 8225, EMPTY).register();
	public static final Potion SPLASH_REGENERATION_II = new Potion("Splash Potion of Regeneration II", 16417, EMPTY).register();
	public static final Potion SWIFTNESS_EXTENDED = new Potion("Potion of Swiftness (Extended)", 8194, EMPTY).register();
	public static final Potion SPLASH_SWIFTNESS_EXTENDED = new Potion("Splash Potion of Swiftness (Extended)", 16450, EMPTY).register();
	public static final Potion SWIFTNESS = new Potion("Potion of Swiftness", 8258, EMPTY).register();
	public static final Potion SPLASH_SWIFTNESS = new Potion("Splash Potion of Swiftness", 16386, EMPTY).register();
	public static final Potion SWIFTNESS_II = new Potion("Potion of Swiftness II", 8226, EMPTY).register();
	public static final Potion SPLASH_SWIFTNESS_II = new Potion("Splash Potion of Swiftness II", 16418, EMPTY).register();
	public static final Potion FIRE_EXTENDED = new Potion("Potion of Fire Resistance (Extended)", 8194, EMPTY).register();
	public static final Potion SPLASH_FIRE_EXTENDED = new Potion("Splash Potion of Fire Resistance (Extended)", 16415, EMPTY).register();
	public static final Potion FIRE = new Potion("Potion of Fire Resistance", 8258, EMPTY).register();
	public static final Potion SPLASH_FIRE = new Potion("Splash Potion of Fire Resistance", 16387, EMPTY).register();
	public static final Potion FIRE_II = new Potion("Potion of Fire Resistance II", 8226, EMPTY).register();
	public static final Potion FIRE_REVERTED = new Potion("Potion of Fire Resistance", 8227, EMPTY).register();
	public static final Potion SPLASH_FIRE_REVERTED = new Potion("Splash Potion of Fire Resistance", 16419, EMPTY).register();
	public static final Potion HEALING = new Potion("Potion of Healing", 8197, EMPTY).register();
	public static final Potion SPLASH_HEALING = new Potion("Splash Potion of Healing", 16389, EMPTY).register();
	public static final Potion HEALING_REVERTED = new Potion("Potion of Healing", 8261, EMPTY).register();
	public static final Potion SPLASH_HEALING_REVERTED = new Potion("Splash Potion of Healing", 16453, EMPTY).register();
	public static final Potion HEALING_II = new Potion("Potion of Healing II", 8229, EMPTY).register();
	public static final Potion SPLASH_HEALING_II = new Potion("Splash Potion of Healing II", 16421, EMPTY).register();
	public static final Potion STRENGTH = new Potion("Potion of Strength", 8201, EMPTY).register();
	public static final Potion SPLASH_STRENGTH = new Potion("Splash Potion of Strength", 16393, EMPTY).register();
	public static final Potion STRENGTH_EXTENDED = new Potion("Potion of Strength (Extended)", 8265, EMPTY).register();
	public static final Potion SPLASH_STRENGTH_EXTENDED = new Potion("Spash Potion of Strength (Extended)", 16457, EMPTY).register();
	public static final Potion SLOWNESS_EXTENDED = new Potion("Potion of Slowness (Extended)", 8226, EMPTY).register();
	public static final Potion SPLASH_SLOWNESS_EXTENDED = new Potion("Splash Potion of Slowness (Extended)", 16458, EMPTY).register();
	public static final Potion SLOWNESS_REVERTED = new Potion("Potion of Slowness", 8234, EMPTY).register();
	public static final Potion SPLASH_SLOWNESS_REVERTED = new Potion("Splash Potion of Slowness", 16426, EMPTY).register();
	public static final Potion HARMING = new Potion("Potion of Harming", 8202, EMPTY).register();
	public static final Potion SPLASH_HARMING = new Potion("Splash Potion of Harming", 16396, EMPTY).register();
	public static final Potion HARMING_REVERTED = new Potion("Potion of Harming", 8264, EMPTY).register();
	public static final Potion SPLASH_HARMING_REVERTED = new Potion("Splash Potion of Harming", 16460, EMPTY).register();
	public static final Potion HARMING_II = new Potion("Potion of Harming II", 8236, EMPTY).register();
	public static final Potion SPLASH_HARMING_II = new Potion("Splash Potion of Harming", 16428, EMPTY).register();

	public Potion(String name) {
		super(name, 373);
	}

	public Potion(String name, int data, Potion parent) {
		super(name, 373, data, parent);
	}

	@Override
	public Potion getParentMaterial() {
		return (Potion) super.getParentMaterial();
	}
}
