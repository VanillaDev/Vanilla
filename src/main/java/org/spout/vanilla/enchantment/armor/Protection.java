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
package org.spout.vanilla.enchantment.armor;

import org.spout.vanilla.enchantment.ArmorEnchantment;
import org.spout.vanilla.enchantment.Enchantment;
import org.spout.vanilla.enchantment.Enchantments;
import org.spout.vanilla.material.VanillaMaterial;
import org.spout.vanilla.material.item.armor.Boots;
import org.spout.vanilla.material.item.armor.Helmet;

public class Protection extends ArmorEnchantment {
	public Protection(String name, int id) {
		super(name, id);
	}

	@Override
	public boolean compatibleWith(Enchantment enchantment, VanillaMaterial material) {
		if (material instanceof Helmet) {
			return enchantment.equals(Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION);
		} else if (material instanceof Boots) {
			return enchantment.equals(Enchantments.FEATHER_FALLING);
		} else {
			return !enchantment.equals(Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION);
		}
	}

	@Override
	public int getWeight() {
		return 10;
	}
}
