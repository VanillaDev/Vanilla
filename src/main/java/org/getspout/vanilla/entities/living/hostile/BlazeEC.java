package org.getspout.vanilla.entities.living.hostile;

import org.getspout.api.metadata.MetadataStringValue;
import org.getspout.vanilla.entities.living.MonsterEC;
import org.getspout.vanilla.mobs.MobID;

public class BlazeEC extends MonsterEC {
	@Override
	public void onAttached() {
		super.onAttached();
		parent.setMetadata("MobID", new MetadataStringValue(MobID.Blaze.id));
	}

	@Override
	public void onTick(float dt) {
		super.onTick(dt);
	}

}
