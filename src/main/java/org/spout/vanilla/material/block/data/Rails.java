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
package org.spout.vanilla.material.block.data;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DataSource;
import org.spout.api.material.block.BlockData;
import org.spout.api.material.block.BlockFace;
import org.spout.vanilla.VanillaMaterials;
import org.spout.vanilla.util.RailsState;

public class Rails implements BlockData {
	private RailsState state;

	public Rails(short data) {
		this.setData(data);
	}

	public Rails(RailsState state) {
		this.setState(state);
	}

	public boolean canCurve() {
		return true;
	}

	public boolean isSloped() {
		return this.state.isSloped();
	}

	public boolean isCurved() {
		return this.state.isCurved();
	}

	public boolean isConnected(BlockFace face) {
		return this.state.isConnected(face);
	}

	public RailsState getState() {
		return this.state;
	}

	public BlockFace[] getDirections() {
		return this.state.getDirections();
	}

	public void setDirection(BlockFace direction) {
		this.setDirection(direction, false);
	}

	public void setDirection(BlockFace direction, boolean sloped) {
		this.setState(RailsState.get(direction, sloped));
	}

	public void setDirection(BlockFace from, BlockFace to) {
		this.setState(RailsState.get(from, to));
	}

	/**
	 * Sets the direction in such a way that both directions are connected
	 * @param dir1
	 * @param dir2
	 * @return true if the operation succeeded
	 */
	public void setState(RailsState state) {
		if (state == null) {
			throw new IllegalArgumentException("Rails state can't be null!");
		} else if (!this.canCurve() && state.isCurved()) {
			throw new IllegalArgumentException("This type of rails can't curve!");
		} else {
			this.state = state;
		}
	}

	@Override
	public void setData(short data) {
		RailsState state = RailsState.get(data);
		if (state == null) {
			throw new IllegalArgumentException("Invalid rails material data: " + data);
		} else {
			this.setState(state);
		}
	}

	@Override
	public short getData() {
		return this.state.getData();
	}

	public BlockMaterial getMaterial() {
		return VanillaMaterials.RAILS;
	}

	@Override
	public void setData(DataSource datasource) {
		this.setData(datasource.getData());
	}
}
