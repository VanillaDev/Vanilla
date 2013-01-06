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
package org.spout.vanilla.event.cause;

/**
 * Represents the cause of an entity being healed.
 */
public enum HealCause {
	/**
	 * Health gained due to regeneration on peaceful mode.
	 */
	REGENERATION,
	/**
	 * Health gained due to regeneration from being satiated.
	 */
	SATIATED,
	/**
	 * Health gained from consumables.
	 */
	CONSUMABLE,
	/**
	 * Health gained by an Ender Dragon from an Ender Crystal.
	 */
	ENDER_CRYSTAL,
	/**
	 * Health gained from a potion.
	 */
	MAGIC,
	/**
	 * Health gained from the HoT effect of a potion.
	 */
	MAGIC_REGEN,
	/**
	 * Health gained by the Wither when it is spawning.
	 */
	WITHER_SPAWN,
	/**
	 * Health gained due to an unknown source.
	 */
	UNKNOWN;

	public boolean equals(HealCause... causes) {
		for (HealCause cause : causes) {
			if (equals(cause)) {
				return true;
			}
		}
		return false;
	}
}
