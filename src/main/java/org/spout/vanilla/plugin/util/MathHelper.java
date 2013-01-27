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
package org.spout.vanilla.plugin.util;

import java.util.Random;

import org.spout.api.math.GenericMath;
import org.spout.api.math.TrigMath;
import org.spout.api.math.Vector3;
import org.spout.api.math.VectorMath;


public class MathHelper {
	public static float getBlockDensity() {
		// TODO: Not sure how this is supposed to be calculated
		return 1;
	}

	/**
	 * Gets the celestial angle at a certain time of the day
	 * @param timeMillis time
	 * @param timeMillisTune fine runing
	 * @return celestial angle
	 */
	public static float getCelestialAngle(long timeMillis, float timeMillisTune) {
		float timeFactor = ((float) (timeMillis % 24000) + timeMillisTune) / 24000f - 0.25f;
		if (timeFactor < 0) {
			timeFactor++;
		} else if (timeFactor > 1) {
			timeFactor--;
		}

		float value = (TrigMath.cos(timeFactor * (float) TrigMath.PI) + 1.0f) / 2.0f;
		timeFactor += (1.0f - value - timeFactor) / 3f;
		return timeFactor;
	}

	/**
	 * Gets the (real?) celestial angle at a certain time of the day<br> The use
	 * of this function is unknown...
	 * @param timeMillis time
	 * @param timeMillisTune fine runing
	 * @return celestial angle, a value from 0 to 1
	 */
	public static float getRealCelestialAngle(long timeMillis, float timeMillisTune) {
		float celestial = MathHelper.getCelestialAngle(timeMillis, timeMillisTune);
		celestial *= TrigMath.TWO_PI;
		celestial = 1.0f - ((float) TrigMath.cos(celestial) * 2.0f + 0.5f);
		if (celestial < 0) {
			celestial = 0.0f;
		} else if (celestial > 1) {
			celestial = 1.0f;
		}
		return 1.0f - celestial;
	}

	/**
	 * Calculates a new random direction
	 * @param maxXZForce of the direction
	 * @param maxYForce of the direction
	 * @return a random Vector3 direction
	 */
	public static Vector3 getRandomDirection(float maxXZForce, float maxYForce) {
		Random rand = new Random();
		float xzLength = maxXZForce * rand.nextFloat();
		float yLength = maxYForce * (rand.nextFloat() - rand.nextFloat());
		return VectorMath.getRandomDirection2D(rand).multiply(xzLength).toVector3(yLength);
	}

	// TODO: Get these two functions working in the API!
	public static float getLookAtYaw(Vector3 offset) {
		float yaw = 0;
		// Set yaw
		if (offset.getX() != 0) {
			// Set yaw start value based on dx
			if (offset.getX() < 0) {
				yaw = 270;
			} else {
				yaw = 90;
			}
			yaw -= Math.toDegrees(Math.atan(offset.getZ() / offset.getX()));
		} else if (offset.getZ() < 0) {
			yaw = 180;
		}
		return yaw;
	}

	public static float getLookAtPitch(Vector3 offset) {
		return (float) -Math.toDegrees(Math.atan(offset.getY() / GenericMath.length(offset.getX(), offset.getZ())));
	}
}
