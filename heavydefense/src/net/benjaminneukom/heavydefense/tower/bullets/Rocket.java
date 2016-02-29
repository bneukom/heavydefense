package net.benjaminneukom.heavydefense.tower.bullets;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;

// TODO limit amount of particle effects
public class Rocket extends AbstractBullet {

	private AbstractEnemy target;
	private float maxSpeed;

	final Vector2 targetPosition = new Vector2();
	final Vector2 position = new Vector2();

	private transient ParticleEffect effect;
	private transient ParticleEmitter smokeEmitter;
	private String particleEffectPath;

	public Rocket() {
	}

	public Rocket(float x, float y, AbstractTower owner, AbstractEnemy target, float rotation, float initialSpeed, float maxSpeed, float damage, AbstractWorld abstractWorld,
			String textureName,
			String particleEffectPath) {
		super(x, y, owner, rotation, initialSpeed, damage, 1, abstractWorld, textureName);

		this.target = target;
		this.maxSpeed = maxSpeed;
		this.particleEffectPath = particleEffectPath;

		postSerialized();
	}

	@Override
	public void postSerialized() {
		super.postSerialized();
		this.effect = new ParticleEffect(Assets.getParticleEffect(particleEffectPath));
		effect.setPosition(getX(), getY());

		smokeEmitter = effect.findEmitter("smoke");
		abstractWorld.addParticleEffect(effect, true);
		effect.start();

	}

	@Override
	public void act(float delta) {
		speed += Math.max(Math.pow(age + 1, 3.5), 1.5);
		speed = Math.min(speed, maxSpeed);

		if (target != null && (target.isDead() || target.hasReachedEnd())) {
			target = null;
		}

		// aim for target
		if (target != null) {
			targetPosition.set(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2);
			position.set(getX(), getY());

			velocity.set(targetPosition).sub(position).nor();
			velocity.scl(speed);

			setRotation((float) Math.atan2(velocity.y, velocity.x));
		} else {
			velocity.nor().scl(speed);
		}

		float degrees = (float) Math.toDegrees(getRotation()) + 90;
		effect.setPosition(getX(), getY());
		smokeEmitter.getAngle().setHigh(80 + degrees, 100 + degrees);
		smokeEmitter.getAngle().setLow(90 + degrees, 90 + degrees);

		super.act(delta);

	}

	@Override
	protected void timedOut() {
		super.timedOut();

		effect.allowCompletion();
		abstractWorld.removeParticleEffectAfterCompletion(effect);
	}

	@Override
	protected void collided() {
		super.collided();

		abstractWorld.addOverlayActor(new Explosion(getX() - 10, getY() - 10, Assets.OVERLAY_ATLAS, Assets.EXPLOSION, 9, 1, abstractWorld));
		effect.allowCompletion();
		abstractWorld.removeParticleEffectAfterCompletion(effect);
	}

}
