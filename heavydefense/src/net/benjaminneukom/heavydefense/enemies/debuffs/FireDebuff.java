package net.benjaminneukom.heavydefense.enemies.debuffs;

import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FireDebuff extends Debuff {

	private AbstractEnemy target;
	private float dps;

	private AbstractTower shootingTower;

	public FireDebuff() {
	}

	public FireDebuff(final AbstractWorld abstractWorld, AbstractTower shootingTower, final AbstractEnemy target, float duration, float dps) {
		super(true, duration, 1);
		this.shootingTower = shootingTower;
		this.target = target;
		this.dps = dps;

		postSerialized();
	}

	@Override
	public void postSerialized() {
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		target.doDamage(dps * delta);
		shootingTower.registerDamage(dps * delta);
	}

	@Override
	public void end(AbstractEnemy abstractEnemy) {

	}

	@Override
	public void start(AbstractEnemy abstractEnemy) {
	}

	@Override
	public int compareTo(Debuff o) {
		return 0;
	}

	@Override
	public void beforeRender(SpriteBatch batch) {
		super.beforeRender(batch);

		batch.setColor(216f / 255f, 166f / 255f, 166f / 255f, 1f);
	}

	@Override
	public void afterRender(SpriteBatch batch) {
		super.afterRender(batch);

		batch.setColor(1f, 1f, 1f, 1f);
	}

}
