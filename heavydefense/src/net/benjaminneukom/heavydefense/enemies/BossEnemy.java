package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BossEnemy extends GroundEnemy {

	public BossEnemy() {
		super();
	}

	public BossEnemy(float x, float y, Vector2 velocity, ArmorType armorType, Array<PathNode> nodes, String textureSheetPath, AbstractWorld abstractWorld, float healthPoints,
			float speed, int wave, float money) {
		super(x, y, velocity, armorType, nodes, textureSheetPath, abstractWorld, healthPoints, speed, wave, money);
	}

	@Override
	protected void onKilled() {
		float centerX = getX() + getWidth() / 2;
		float centerY = getY() + getHeight() / 2;

		for (int explosion = 0; explosion < 15; ++explosion) {
			abstractWorld.addOverlayActor(new Explosion((float) (centerX + -80 + Math.random() * 100), (float) (centerY + -80 + Math.random() * 100), Assets.OVERLAY_ATLAS,
					Assets.EXPLOSION_BIG, 7,
					2, (float) (Math.random() * 0.5f), abstractWorld));

		}
	}
}
