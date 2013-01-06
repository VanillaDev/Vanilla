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
package org.spout.vanilla.world.generator.biome.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class NoiseRangeLayer implements BiomeSelectorLayer {
	protected final List<ElementRange> ranges = new ArrayList<ElementRange>();

	public NoiseRangeLayer addElement(BiomeSelectorElement element, float min, float max) {
		return addElement(new ElementRange(element, min, max));
	}

	public NoiseRangeLayer addElement(ElementRange element) {
		ranges.add(element);
		return this;
	}

	public NoiseRangeLayer addElements(ElementRange... elements) {
		return addElements(Arrays.asList(elements));
	}

	public NoiseRangeLayer addElements(Collection<ElementRange> elements) {
		ranges.addAll(elements);
		return this;
	}

	public NoiseRangeLayer sortElements() {
		Collections.sort(ranges);
		return this;
	}

	protected abstract float getNoiseValue(int x, int y, int z, int seed);

	@Override
	public BiomeSelectorElement pick(int x, int y, int z, long seed) {
		final float value = getNoiseValue(x, y, z, (int) seed);
		for (ElementRange range : ranges) {
			if (range.isInRange(value)) {
				return range.getElement();
			}
		}
		return null;
	}

	public static class ElementRange implements Comparable<ElementRange> {
		private final BiomeSelectorElement element;
		private final float min;
		private final float max;

		public ElementRange(BiomeSelectorElement element, float min, float max) {
			this.element = element;
			this.min = min;
			this.max = max;
		}

		protected boolean isInRange(float value) {
			return value >= min && value <= max;
		}

		public BiomeSelectorElement getElement() {
			return element;
		}

		@Override
		public int compareTo(ElementRange t) {
			if (min < t.min || max < t.max) {
				return -1;
			} else if (min == t.min && max == t.max) {
				return 0;
			}
			return 1;
		}
	}
}
