package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AirEnemy extends AbstractEnemy {

	private transient TextureRegion[] shadowSprites;
	private transient TextureRegion currentShadowSprite;

	public AirEnemy() {
	}

	public AirEnemy(float x, float y, Vector2 velocity, ArmorType armorType, Array<PathNode> nodes, String textureSheetPath, AbstractWorld abstractWorld,
			float healthPoints, float speed, int wave, float money) {
		super(x, y, velocity, EnemyType.AIR, armorType, nodes, textureSheetPath, abstractWorld, healthPoints, speed, wave, money);

		postSerialized();
	}

	@Override
	protected void onKilled() {
		abstractWorld.addOverlayActor(new Explosion(getX(), getY(), Assets.OVERLAY_ATLAS, Assets.EXPLOSION_BIG, 7, 2, abstractWorld));
	}

	@Override
	public int compareTo(AbstractEnemy o) {
		if (o instanceof GroundEnemy)
			return 1;
		return (int) (getY() - o.getY());
	}

	@Override
	protected void changeSprite(int index) {
		super.changeSprite(index);

		currentShadowSprite = shadowSprites[index];
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		// draw shadow
		batch.draw(currentShadowSprite, getX() + 9, getY() + 9);

		super.draw(batch, parentAlpha);
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		final AtlasRegion shadowRegion = enemyAtlas.findRegion(Assets.COPTER_SHADOW);
		this.shadowSprites = Assets.getOrientationSprites(shadowRegion, getTextureWidth(), getTextureHeight());
		this.currentShadowSprite = shadowSprites[0];

		updateRotation();
	}

}
