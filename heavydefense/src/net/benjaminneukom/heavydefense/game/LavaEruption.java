package net.benjaminneukom.heavydefense.game;

import net.benjaminneukom.heavydefense.AnimatedActor;
import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

public class LavaEruption extends AnimatedActor {

	public LavaEruption() {
	}

	public LavaEruption(float x, float y, AbstractWorld abstractWorld) {
		super(x, y, Assets.OVERLAY_ATLAS, Assets.LAVA_ERUPTION, 8, 2, 0.09f, abstractWorld);
	}

	@Override
	protected float getRenderOffsetY() {
		return -HeavyDefenseGame.TILE_SIZE;
	}

}
