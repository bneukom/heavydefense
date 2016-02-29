package net.benjaminneukom.heavydefense.tower.bullets;

import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

public class TurretBullet extends AbstractBullet {

	public TurretBullet() {
	}

	public TurretBullet(float x, float y, AbstractTower owner, float rotation, float speed, float damage, int targetsToHit, AbstractWorld abstractWorld, String textureName) {
		super(x, y, owner, rotation, speed, damage, targetsToHit, abstractWorld, textureName);

		postSerialized();
	}

}
