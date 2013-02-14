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
package org.spout.vanilla.plugin.enchantment.armor;

import org.spout.vanilla.api.enchantment.ArmorEnchantment;
import org.spout.vanilla.api.enchantment.Enchantment;
import org.spout.vanilla.api.material.VanillaMaterial;
import org.spout.vanilla.api.material.item.armor.Boots;
import org.spout.vanilla.api.material.item.armor.Helmet;

import org.spout.vanilla.plugin.enchantment.VanillaEnchantments;

public class Protection extends ArmorEnchantment {
	public Protection(String name, int id) {
		super(name, id, 1, 11, 20);
		setMaximumLevel(4).setWeight(10);
	}

	@Override
	public boolean compatibleWith(Enchantment enchantment, VanillaMaterial material) {
		if (material instanceof Helmet) {
			return enchantment.equals(VanillaEnchantments.AQUA_AFFINITY, VanillaEnchantments.RESPIRATION);
		} else if (material instanceof Boots) {
			return enchantment.equals(VanillaEnchantments.FEATHER_FALLING);
		} else {
			return !enchantment.equals(VanillaEnchantments.FIRE_PROTECTION, VanillaEnchantments.BLAST_PROTECTION, VanillaEnchantments.PROJECTILE_PROTECTION);
		}
	}
}
