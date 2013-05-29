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
package org.spout.vanilla.component.entity.player;

import org.spout.api.component.type.EntityComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Vector3;
import org.spout.api.render.Camera;
import org.spout.api.render.ViewFrustum;
import org.spout.vanilla.component.entity.misc.Head;

public class PlayerHead extends Head implements Camera{
	private Matrix projection;
	private Matrix view;
	private ViewFrustum frustum = new ViewFrustum();
	private float fieldOfView = 75f;

	public void setScale(float scale) { //1/2
		projection = MatrixMath.createPerspective(fieldOfView * scale, 4.0f / 3.0f, .001f * scale, 1000f * scale);
		updateView();
	}

	@Override
	public void onAttached() {
		// TODO Get FOV
		projection = MatrixMath.createPerspective(fieldOfView, 4.0f / 3.0f, .001f, 1000f);
		updateView();
	}

	@Override
	public Matrix getProjection() {
		return projection;
	}

	@Override
	public Matrix getView() {
		return view;
	}

	@Override
	public void updateView() {
		Transform transform = getOwner().getScene().getRenderTransform();
		Point point = transform.getPosition().add(0.0f, this.getHeight(), 0.0f);
		Matrix pos = MatrixMath.createTranslated(point.multiply(-1));
		Matrix rot = getRotation();
		view = pos.multiply(rot);
		frustum.update(projection, view, transform.getPosition());
	}

	@Override
	public void updateReflectedView() {
		Transform transform = getOwner().getScene().getRenderTransform();
		Point point = transform.getPosition().add(0.0f, this.getHeight(), 0.0f);
		Matrix pos = MatrixMath.createTranslated(point.multiply(-1, 1, -1));
		Matrix rot = getRotation();
		view = MatrixMath.createScaled(new Vector3(1,-1,1)).multiply(pos).multiply(rot);
		frustum.update(projection, view, transform.getPosition());
	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public ViewFrustum getFrustum() {
		return frustum;
	}

	@Override
	public Matrix getRotation(){
		return MatrixMath.createRotated(this.getHeadTransform().getRotation());
	}
}
