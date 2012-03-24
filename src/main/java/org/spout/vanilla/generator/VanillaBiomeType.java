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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.generator;

import org.spout.api.generator.biome.BiomeDecorator;
import org.spout.api.generator.biome.BiomeType;

public abstract class VanillaBiomeType extends BiomeType {
	private final int biomeId;
	protected int minHumid = -1;
	protected int maxHumid = -1;
	protected int minTemp = -1;
	protected int maxTemp = -1;

	protected VanillaBiomeType(int biomeId, BiomeDecorator... decorators) {
		super(decorators);
		this.biomeId = biomeId;
	}

	public int getBiomeId() {
		return biomeId;
	}
	
	public boolean isValidPlacement(double temp, double humid) {
		if(minHumid == -1 || maxHumid == -1 || minTemp == -1 || maxTemp == -1) {
			return false;
		} else if(temp <= maxTemp && minTemp <= temp && humid <= maxHumid && minHumid <= humid) {
			return true;
		} else {
			return false;
		}
	}
}
