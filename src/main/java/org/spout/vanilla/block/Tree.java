package org.spout.vanilla.block;

import org.spout.vanilla.material.Plant;

public class Tree extends GenericBlockMaterial implements Plant {

	public Tree(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean hasGrowthStages() {
		return false;
	}

	public int getNumGrowthStages() {
		return 0;
	}

	public int getMinimumLightToGrow() {
		return 0;
	}

}
