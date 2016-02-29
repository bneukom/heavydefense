package net.benjaminneukom.heavydefense.enemies.debuffs;

import net.benjaminneukom.heavydefense.PostSerialization;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Debuff extends Actor implements Comparable<Debuff>, PostSerialization {

	private boolean isUnique;
	private float duration;
	private int renderPriority;

	public Debuff() {
	}

	public Debuff(boolean isUnique, float duration, int renderPriority) {
		super();
		this.isUnique = isUnique;
		this.duration = duration;
		this.renderPriority = renderPriority;
	}

	public void applyDebuff(AbstractEnemy target) {

	}

	@Override
	public void act(float delta) {
		this.duration -= delta;

	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public boolean ended() {
		return duration <= 0;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public int getRenderPriority() {
		return renderPriority;
	}

	public float getDuration() {
		return duration;
	}

	public abstract void end(AbstractEnemy abstractEnemy);

	public abstract void start(AbstractEnemy abstractEnemy);

	public void beforeRender(SpriteBatch batch) {

	}

	public void afterRender(SpriteBatch batch) {

	}

	/**
	 * Equality based on class (mostly used for uniqnuess checks).
	 */
	@Override
	public final boolean equals(Object obj) {
		return obj.getClass() == getClass();
	}

}
