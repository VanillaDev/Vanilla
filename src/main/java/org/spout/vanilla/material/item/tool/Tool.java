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
package org.spout.vanilla.material.item.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;

import org.spout.vanilla.enchantment.Enchantments;
import org.spout.vanilla.material.Mineable;
import org.spout.vanilla.material.item.VanillaItemMaterial;
import org.spout.vanilla.util.EnchantmentUtil;

public class Tool extends VanillaItemMaterial {
	private final Random rand = new Random();
	private short durability;
	private Map<BlockMaterial, Float> strengthModifiers = new HashMap<BlockMaterial, Float>();

	public Tool(String name, int id, short durability) {
		super(name, id);
		this.durability = durability;
	}

	public short getDurabilityPenalty(Mineable mineable, ItemStack item) {
		short penalty = mineable.getDurabilityPenalty(this);
		if (EnchantmentUtil.hasEnchantment(item, Enchantments.UNBREAKING)) {
			// Level 1 = 50%, Level 2 = 67%, Level 3 = 75% chance to not consume durability
			if (100 - (100 / (EnchantmentUtil.getEnchantmentLevel(item, Enchantments.UNBREAKING) + 1)) > rand.nextInt(100)) {
				penalty = 0;
			}
		}
		return penalty;
	}

	public short getMaxDurability() {
		return durability;
	}

	public Tool setMaxDurability(short durability) {
		this.durability = durability;
		return this;
	}

	public float getStrengthModifier(BlockMaterial block) {
		if (!(strengthModifiers.containsKey(block))) {
			return (float) 1.0;
		}
		return strengthModifiers.get(block);
	}

	public Tool setStrengthModifier(BlockMaterial block, float modifier) {
		strengthModifiers.put(block, modifier);
		return this;
	}

	public Set<BlockMaterial> getStrengthModifiedBlocks() {
		return strengthModifiers.keySet();
	}

	@Override
	public boolean getNBTData() {
		return true;
	}
}
