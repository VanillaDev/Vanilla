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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.material.block;

import org.spout.api.geo.World;
import org.spout.vanilla.material.attachable.GroundAttachable;
import org.spout.vanilla.util.block.MinecartTrackLogic;

public class MinecartTrack extends GroundAttachable {

	public MinecartTrack(String name, int id) {
		super(name, id);
	}
	
	public boolean canCurve() {
		return true;
	}
	
	@Override
	public void onUpdate(World world, int x, int y, int z) {
		MinecartTrackLogic logic = MinecartTrackLogic.create(world, x, y, z);
		if (logic != null) {
			logic.refresh();
		}
	}
		
}
