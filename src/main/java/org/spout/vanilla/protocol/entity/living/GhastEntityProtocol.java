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
package org.spout.vanilla.protocol.entity.living;

import java.util.List;

import org.spout.api.entity.Controller;
import org.spout.api.util.Parameter;

import org.spout.vanilla.entity.creature.hostile.Ghast;
import org.spout.vanilla.protocol.entity.BasicMobEntityProtocol;

public class GhastEntityProtocol extends BasicMobEntityProtocol {
	public GhastEntityProtocol() {
		super(56);
	}

	@Override
	public List<Parameter<?>> getSpawnParameters(Controller controller) {
		List<Parameter<?>> parameters = super.getSpawnParameters(controller);
		if (controller instanceof Ghast) {
			Ghast ghast = (Ghast) controller;
			byte data = ghast.hasRedEyes() ? (byte) 1 : 0;
			parameters.add(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, data));
		}

		return parameters;
	}
}
