package net.benjaminneukom.heavydefense.tower.bullets;

import java.util.HashSet;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.PostSerialization;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractBullet extends Actor implements PostSerialization {

	protected Vector2 velocity;
	private float damage;
	protected float speed;

	private transient TextureRegion texture;
	private String textureName;

	protected float age;
	private static final float MAX_AGE = 2f;

	protected AbstractWorld abstractWorld;

	private transient Rectangle a = new Rectangle();
	private transient Rectangle b = new Rectangle();

	private AbstractTower owner;

	private int targetsToHit;
	private int targetsHit;

	private transient HashSet<AbstractEnemy> enemiesHit = new HashSet<AbstractEnemy>();

	public AbstractBullet() {
	}

	public AbstractBullet(float x, float y, final AbstractTower owner, float rotation, float speed, float damage, int targetsToHit, AbstractWorld abstractWorld, String textureName) {
		this.owner = owner;
		this.speed = speed;
		this.damage = damage;
		this.targetsToHit = targetsToHit;
		this.abstractWorld = abstractWorld;
		this.textureName = textureName;
		this.velocity = new Vector2((float) (Math.cos(rotation) * speed), (float) (Math.sin(rotation) * speed));

		setPosition(x, y);

		setRotation(rotation);
	}

	@Override
	public void act(float delta) {
		translate(velocity.x * delta, velocity.y * delta);

		// just remove
		age += delta;
		if (age > MAX_AGE) {
			timedOut();
		}

		final Array<AbstractEnemy> abstractEnemies = abstractWorld.getEnemies();

		for (int enemyIndex = 0; enemyIndex < abstractEnemies.size; ++enemyIndex) {
			final AbstractEnemy abstractEnemy = abstractEnemies.get(enemyIndex);

			if (!owner.canTarget(abstractEnemy)) {
				continue;
			}

			a.set(abstractEnemy.getX(), abstractEnemy.getY(), abstractEnemy.getWidth(), abstractEnemy.getHeight());
			b.set(getX(), getY(), getWidth(), getHeight());

			if (a.overlaps(b) && !enemiesHit.contains(abstractEnemy)) {
				// 10% less per enemy hit
				float realDamage = damage * (1f - (0.10f * targetsHit));

				targetsHit++;
				enemiesHit.add(abstractEnemy);

				abstractEnemy.doDamage(realDamage);
				owner.registerDamage(realDamage);

				collided();

				// no more damage
				if (targetsHit >= targetsToHit) {
					abstractWorld.removeOverlayActor(this);
					break;
				}
			}
		}
	}

	protected void timedOut() {
		abstractWorld.removeOverlayActor(this);
	}

	protected void collided() {

	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		// + Math.PI / 2 because 0 is right and the sprite is upwards by default
		batch.draw(texture, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, (float) Math.toDegrees(getRotation() + Math.PI / 2));
	}

	@Override
	public void postSerialized() {
		final TextureAtlas textureAtlas = Assets.getTextureAtlas(Assets.OVERLAY_ATLAS);
		texture = new TextureRegion(textureAtlas.findRegion(textureName));
		texture.flip(false, true);
		setBounds(getX(), getY(), texture.getRegionWidth(), texture.getRegionHeight());
	}
}
