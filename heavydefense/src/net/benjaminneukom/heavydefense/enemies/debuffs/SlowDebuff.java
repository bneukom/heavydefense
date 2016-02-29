package net.benjaminneukom.heavydefense.enemies.debuffs;

import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SlowDebuff extends Debuff {

	// for example 0.2 means the enemy will move at 20% of their original speed
	private float slowFactor;

	public SlowDebuff() {
	}

	public SlowDebuff(float duration, float factor) {
		super(true, duration, 0);
		this.slowFactor = factor;
	}

	@Override
	public void end(AbstractEnemy target) {
	}

	@Override
	public void start(AbstractEnemy target) {
	}

	@Override
	public void applyDebuff(AbstractEnemy target) {
		target.multiplySpeed(slowFactor);
	}

	@Override
	public void beforeRender(SpriteBatch batch) {
		super.beforeRender(batch);

		batch.setColor(201f / 255f, 242f / 255f, 1f, 1f);
	}

	@Override
	public void afterRender(SpriteBatch batch) {
		super.afterRender(batch);

		batch.setColor(1f, 1f, 1f, 1f);
	}

	public float getSlowFactor() {
		return slowFactor;
	}

	public void setSlowFactor(float factor) {
		this.slowFactor = factor;
	}

	@Override
	public int compareTo(Debuff o) {
		if (o instanceof SlowDebuff) {
			final SlowDebuff otherSlowDebuff = (SlowDebuff) o;
			if (slowFactor < otherSlowDebuff.slowFactor) {
				return 1;
			} else if (slowFactor > otherSlowDebuff.slowFactor) {
				return -1;
			}
		}
		return 0;
	}

	@Override
	public void postSerialized() {

	}

}
