package net.benjaminneukom.heavydefense.tower;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.enemies.debuffs.SlowDebuff;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class SlowTower extends AbstractTower {

	private static final float HALF_CONE_SIZE = (float) (Math.PI / 4);

	private float cooldown;
	protected float currentCooldown;
	private float slowFactor;
	private float slowDuration;

	private transient ParticleEffect effect;
	private float angle;
	private float freezeDelay;
	private boolean canFreeze;

	// temporary vectors
	private transient Vector2 a = new Vector2();
	private transient Vector2 b = new Vector2();

	private transient Vector2 dirBuffer = new Vector2();
	private transient Vector2 toEnemyBuffer = new Vector2();

	private static final float FREEZE_DELAY = 4f;

	public SlowTower() {
	}

	public SlowTower(float x, float y, String name, String iconPath, float range, float slowFactor, float slowDuration, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, abstractWorld);

		this.slowFactor = slowFactor;
		this.slowDuration = slowDuration;

		postSerialized();

	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		this.currentSprite = new TextureRegion(towerTextureAtlas.findRegion(Assets.SLOW_TOWER));
		this.currentSprite.flip(false, true);
		this.canFreeze = TD.isUpgraded(UpgradeType.SLOW_FREEZE);
		this.freezeDelay = FREEZE_DELAY;

		effect = new ParticleEffect(Assets.getParticleEffect(Assets.FROST_EFFECT));
		effect.load(Gdx.files.internal("effects/ice.p"), Gdx.files.internal("effects"));
		effect.setPosition(getX() + 28, getY() - 4);
	}

	public void setEmitterAngle(float angle) {
		for (ParticleEmitter emitter : effect.getEmitters()) {
			emitter.getAngle().setHigh(45 + angle, 135 + angle);
			emitter.getAngle().setLow(90 + angle, 90 + angle);
		}
	}

	@Override
	protected void targetAcquired() {
		super.targetAcquired();

		abstractWorld.addParticleEffect(effect);

		effect.reset();
	}

	@Override
	protected void targetLost() {
		super.targetLost();

		effect.allowCompletion();
		abstractWorld.removeParticleEffectAfterCompletion(effect);
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 600 * (1 - TD.getUpgradeValue(UpgradeType.SLOW_CHEAPER_UPGRADE)));
		return cost;
	}

	@Override
	public void removed() {
		effect.allowCompletion();
		abstractWorld.removeParticleEffectAfterCompletion(effect);
	}

	@Override
	public void act(float delta) {
		setEmitterAngle(angle - 90);

		freezeDelay -= delta;
		freezeDelay = Math.max(0, freezeDelay);

		super.act(delta);

		if (target != null) {
			// change angle
			a.set(getX() + getRenderOffsetX() + getSpriteWidth() / 2, getY() + getRenderOffsetY() + getSpriteHeight() / 2);
			b.set(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2);

			final Vector2 direction = b.sub(a).nor();

			final double rotation = Math.atan2(direction.y, direction.x);
			angle = (float) Math.toDegrees(rotation);

			// apply slow buff
			if (currentCooldown > 0) {
				currentCooldown -= delta;
			} else {
				currentCooldown = cooldown;

				dirBuffer.set((float) cos(rotation), (float) sin(rotation));

				final Array<AbstractEnemy> abstractEnemies = abstractWorld.getEnemies();
				for (AbstractEnemy abstractEnemy : abstractEnemies) {
					if (!canTarget(abstractEnemy))
						continue;

					float shootX = getX() + getRenderOffsetX() + getWidth() / 2;
					float shootY = getY() + getRenderOffsetY() + getHeight() / 2;

					toEnemyBuffer.set(abstractEnemy.getX() + abstractEnemy.getWidth() / 2 - shootX, abstractEnemy.getY() + abstractEnemy.getHeight() / 2 - shootY);
					final float distToEnemy = toEnemyBuffer.len();
					toEnemyBuffer.nor();

					float dot = toEnemyBuffer.dot(dirBuffer);
					float angleToEnemy = (float) Math.acos(dot);

					if (distToEnemy < range && angleToEnemy < HALF_CONE_SIZE) {

						float realSlowFactor = slowFactor;
						float realSlowDuration = slowDuration;

						if (freezeDelay <= 0 && canFreeze) {
							freezeDelay = FREEZE_DELAY;
							realSlowFactor = 0.05f;
							realSlowDuration = 1f;
						}

						final SlowDebuff debuff = abstractEnemy.getDebuff(SlowDebuff.class);
						if (debuff != null) {
							// reset
							if (debuff.getSlowFactor() > realSlowFactor) {
								debuff.setSlowFactor(realSlowFactor);
								debuff.setDuration(realSlowDuration);
							}

						} else {
							// add new
							abstractEnemy.addDebuff(new SlowDebuff(realSlowDuration, realSlowFactor));
						}
					}
				}
			}
		}
	}

	@Override
	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle upgradeLabelStyle) {
		super.fillInfoTable(info, infoLabelStyle, headerLabelStyle, upgradeLabelStyle);

		final Label rangeLabel = new Label("Range: " + (int) range, infoLabelStyle);
		final Label rangeUpgrade = new Label("+" + (int) getRangeUpgrade(level + 1), upgradeLabelStyle);
		final Label slowFactorLabel = new Label("Slow Factor: " + (int) (slowFactor * 100) + "%", infoLabelStyle);
		final Label slowFactorUpgradeLabel = new Label("-" + (int) (getSlowFactorUpgrade(level + 1) * 100) + "%", upgradeLabelStyle);

		final Table rangeTable = new Table();
		rangeTable.add(rangeLabel).padRight(10);
		rangeTable.add(rangeUpgrade);
		info.add(rangeTable).left();
		info.row();

		final Table slowTable = new Table();
		slowTable.add(slowFactorLabel).left().padRight(10);
		slowTable.add(slowFactorUpgradeLabel);
		info.add(slowTable).left();
		info.row();

	}

	protected float getSlowFactorUpgrade(int level) {
		return 0.08f;
	}

	protected float getRangeUpgrade(int level) {
		return 10;
	}

	@Override
	protected void doUpgrade(int level) {
		super.doUpgrade(level);

		range += getRangeUpgrade(level);
		slowFactor -= getSlowFactorUpgrade(level);
	}

	@Override
	public boolean canTarget(AbstractEnemy abstractEnemy) {
		return abstractEnemy instanceof GroundEnemy;
	}

	@Override
	public float getRenderOffsetX() {
		return -3;
	}

	@Override
	public float getRenderOffsetY() {
		return -16;
	}

	@Override
	public int getSpriteWidth() {
		return 64;
	}

	@Override
	public int getSpriteHeight() {
		return 64;
	}
}
