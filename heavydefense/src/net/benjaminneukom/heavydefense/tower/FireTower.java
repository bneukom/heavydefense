package net.benjaminneukom.heavydefense.tower;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.enemies.debuffs.FireDebuff;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

// TODO somehow duplicate code with slow tower...
public class FireTower extends RotatableShootingTower {

	private static final float HALF_CONE_SIZE = (float) (Math.PI / 4);

	private transient ParticleEffect effect;
	private boolean hasTarget;

	private boolean targetLost = false;

	private float runningTime = 0;
	private static final float TIME_UNTIL_DAMAGE = 1f;

	private static final float BURN_DURATION = 6f;

	// burn some time longer
	private static final float STOP_TIME = 1f;
	private float timeSinceTargetLost = STOP_TIME;

	private static final float[] OFFSETS = new float[] { 9, 6, // 0
			17, 2, // 1
			26, 6, // 2
			4, 18, // 3
			30, 18, // 4
			8, 29,// 5
			17, 33, // 6
			28, 28 // 7
	};

	private transient Vector2 dirBuffer = new Vector2();
	private transient Vector2 toEnemyBuffer = new Vector2();

	public FireTower() {
	}

	public FireTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, cooldown, damage, Assets.FIRE_TOWER, Assets.FIRE_TOWER, abstractWorld);

		postSerialized();
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		effect = new ParticleEffect(Assets.getParticleEffect(Assets.FIRE_EFFECT));
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		float x = getX() + getRenderOffsetX() + OFFSETS[2 * spriteIndex + 0];
		float y = getY() + getRenderOffsetY() + OFFSETS[2 * spriteIndex + 1];
		effect.setPosition(x, y);

		setEmitterAngle((float) angle - 90);

		if (targetLost && timeSinceTargetLost > 0) {
			timeSinceTargetLost -= delta;

			if (timeSinceTargetLost <= 0) {
				effect.allowCompletion();
				targetLost = false;
				timeSinceTargetLost = STOP_TIME;
				abstractWorld.removeParticleEffectAfterCompletion(effect);
			}
		}

		final double angradAngle = Math.toRadians(angle);
		dirBuffer.set((float) cos(angradAngle), (float) sin(angradAngle));

		if (hasTarget) {
			runningTime += delta;

			for (AbstractEnemy abstractEnemy : abstractWorld.getEnemies()) {
				if (!canTarget(abstractEnemy))
					continue;

				float shootX = getX() + getRenderOffsetX() + OFFSETS[2 * spriteIndex + 0];
				float shootY = getY() + getRenderOffsetY() + OFFSETS[2 * spriteIndex + 1];

				toEnemyBuffer.set(abstractEnemy.getX() + abstractEnemy.getWidth() / 2 - shootX, abstractEnemy.getY() + abstractEnemy.getHeight() / 2 - shootY);
				final float distToEnemy = toEnemyBuffer.len();
				toEnemyBuffer.nor();

				float dot = toEnemyBuffer.dot(dirBuffer);
				float angleToEnemy = (float) Math.acos(dot);

				if (runningTime > TIME_UNTIL_DAMAGE && distToEnemy < range && angleToEnemy < HALF_CONE_SIZE) {
					abstractEnemy.doDamage(damage * delta);
					registerDamage(damage * delta);

					final float duration = BURN_DURATION + BURN_DURATION * TD.getUpgradeValue(UpgradeType.FIRE_BURN_DURATION);

					final FireDebuff debuff = abstractEnemy.getDebuff(FireDebuff.class);
					if (debuff != null) {
						// reset
						debuff.setDuration(duration);
					} else {
						// add new
						abstractEnemy.addDebuff(new FireDebuff(abstractWorld, this, abstractEnemy, duration, 7));
					}

				}
			}
		}

	}

	@Override
	protected void targetAcquired() {
		super.targetAcquired();
		abstractWorld.addParticleEffect(effect);
		effect.start();
		effect.reset();

		hasTarget = true;
		timeSinceTargetLost = STOP_TIME;
		targetLost = false;
	}

	@Override
	protected void targetLost() {
		super.targetLost();
		hasTarget = false;

		runningTime = 0;
		timeSinceTargetLost = STOP_TIME;
		targetLost = true;
	}

	public void setEmitterAngle(float angle) {
		for (ParticleEmitter emitter : effect.getEmitters()) {
			emitter.getAngle().setHigh(45 + angle, 135 + angle);
			emitter.getAngle().setLow(90 + angle, 90 + angle);
		}
	}

	@Override
	protected void directionChanged(int newSpriteIndex) {

	}

	@Override
	public void removed() {
		effect.allowCompletion();
		abstractWorld.removeParticleEffectAfterCompletion(effect);
	}

	@Override
	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle upgradeLabelStyle) {
		final Label dpsLabel = new Label("DPS: " + (int) damage, infoLabelStyle);
		final Label dpsUpgradeLabel = new Label("+" + getDpsUpgrade(level + 1), upgradeLabelStyle);
		final Label rangeLabel = new Label("Range: " + (int) range, infoLabelStyle);
		final Label rapgeUpgradeLabel = new Label("+" + getRangeUpgrade(level + 1), upgradeLabelStyle);

		info.add(dpsLabel).left().fill(false, false);
		info.add(dpsUpgradeLabel).left().fill(true, false);
		info.row();
		info.add(rangeLabel).left().fill(false, false);
		info.add(rapgeUpgradeLabel).left().fill(true, false);
		info.row();
	}

	@Override
	protected void doUpgrade(int level) {
		damage += getDpsUpgrade(level);
		range += getRangeUpgrade(level);
	}

	protected float getDpsUpgrade(float level) {
		return 2.5f;
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 600 * (1 - TD.getUpgradeValue(UpgradeType.FIRE_CHEAPER_UPGRADE)));
		return cost;
	}

	@Override
	protected float getRangeUpgrade(int level) {
		return super.getRangeUpgrade(level);
	}

	@Override
	public float getUpgradeOffsetX() {
		return -5;
	}

	@Override
	public float getUpgradeOffsetY() {
		return -5;
	}

	@Override
	public int getMaxLevel() {
		return TD.isUpgraded(UpgradeType.FIRE_TOWER_LEVEL_4) ? super.getMaxLevel() + 1 : super.getMaxLevel();
	}

	@Override
	public boolean canTarget(AbstractEnemy abstractEnemy) {
		return abstractEnemy instanceof GroundEnemy;
	}

	@Override
	public float getRenderOffsetX() {
		return 12;
	}

	@Override
	public float getRenderOffsetY() {
		return 3;
	}

	@Override
	public int getSpriteWidth() {
		return 36;
	}

	@Override
	public int getSpriteHeight() {
		return 38;
	}

}
