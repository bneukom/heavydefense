package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GroundEnemy extends AbstractEnemy {

	public GroundEnemy() {
		super();
	}

	public GroundEnemy(float x, float y, Vector2 velocity, ArmorType armorType, Array<PathNode> nodes, String textureSheetPath, AbstractWorld abstractWorld,
			float healthPoints, float speed, int wave, float money) {
		super(x, y, velocity, EnemyType.GROUND, armorType, nodes, textureSheetPath, abstractWorld, healthPoints, speed, wave, money);

		postSerialized();
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		updateRotation();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
