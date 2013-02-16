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
package org.spout.vanilla.plugin.world.generator.normal.biome.grassy;

import java.awt.Color;
import java.util.Random;

import org.spout.vanilla.plugin.material.block.plant.TallGrass;
import org.spout.vanilla.plugin.world.generator.normal.decorator.FlowerDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.MushroomDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.PumpkinDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.SandAndClayDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.SugarCaneDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.TallGrassDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.TallGrassDecorator.TallGrassFactory;
import org.spout.vanilla.plugin.world.generator.normal.decorator.TreeDecorator;
import org.spout.vanilla.plugin.world.generator.normal.decorator.VineDecorator;
import org.spout.vanilla.plugin.world.generator.normal.object.variableheight.tree.BigTreeObject;
import org.spout.vanilla.plugin.world.generator.normal.object.variableheight.tree.HugeTreeObject;
import org.spout.vanilla.plugin.world.generator.normal.object.variableheight.tree.ShrubObject;
import org.spout.vanilla.plugin.world.generator.normal.object.variableheight.tree.SmallTreeObject;
import org.spout.vanilla.plugin.world.generator.normal.object.variableheight.tree.TreeObject;

public class JungleBiome extends GrassyBiome {
	public JungleBiome(int biomeId) {
		super(biomeId);
		final TreeDecorator trees = new TreeDecorator();
		trees.setFactory(new JungleTreeWGOFactory());
		final TallGrassDecorator tallGrass = new TallGrassDecorator();
		tallGrass.setFactory(new JungleTallGrassFactory());
		tallGrass.setBaseAmount(15);
		final FlowerDecorator flowers = new FlowerDecorator();
		flowers.setBaseAmount(4);
		addDecorators(new SandAndClayDecorator(), trees, flowers, tallGrass, new MushroomDecorator(),
				new SugarCaneDecorator(), new PumpkinDecorator(), new VineDecorator());
		setElevation(63, 92);
		setGrassColorMultiplier(new Color(83, 202, 55));
		setFoliageColorMultiplier(new Color(41, 188, 5));
	}

	@Override
	public String getName() {
		return "Jungle";
	}

	private static class JungleTreeWGOFactory extends NormalTreeWGOFactory {
		@Override
		public byte amount(Random random) {
			return (byte) (50 + super.amount(random));
		}

		@Override
		public TreeObject make(Random random) {
			if (random.nextInt(10) == 0) {
				return new BigTreeObject();
			}
			if (random.nextInt(2) == 0) {
				return new ShrubObject();
			}
			if (random.nextInt(3) == 0) {
				return new HugeTreeObject();
			}
			final SmallTreeObject tree = new SmallTreeObject();
			tree.setTreeType(TreeObject.TreeType.JUNGLE);
			tree.setBaseHeight((byte) 4);
			tree.setRandomHeight((byte) 10);
			tree.addLogVines(true);
			tree.addCocoaPlants(true);
			return tree;
		}
	}

	private static class JungleTallGrassFactory implements TallGrassFactory {
		@Override
		public TallGrass make(Random random) {
			if (random.nextInt(4) == 0) {
				return TallGrass.FERN;
			}
			return TallGrass.TALL_GRASS;
		}
	}
}
