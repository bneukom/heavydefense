package net.benjaminneukom.heavydefense.serializers;

import net.benjaminneukom.heavydefense.enemies.AirEnemy;
import net.benjaminneukom.heavydefense.enemies.BossEnemy;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.game.worlds.ClassicGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.ClassicLavaGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsLavaGameWorld;
import net.benjaminneukom.heavydefense.tower.FireTower;
import net.benjaminneukom.heavydefense.tower.RocketTower;
import net.benjaminneukom.heavydefense.tower.TeslaTower;
import net.benjaminneukom.heavydefense.tower.bullets.Rocket;
import net.benjaminneukom.heavydefense.tower.bullets.TurretBullet;

public class DefaultReferenceStore extends ReferenceStore {
	public DefaultReferenceStore() {
		addReferences(ClassicGameWorld.class, ClassicLavaGameWorld.class, NoRoadsLavaGameWorld.class, NoRoadsGameWorld.class, FireTower.class, TeslaTower.class, RocketTower.class,
				Rocket.class, TurretBullet.class, GroundEnemy.class, AirEnemy.class, BossEnemy.class);
	}
}
