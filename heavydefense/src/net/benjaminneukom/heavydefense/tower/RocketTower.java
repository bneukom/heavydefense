package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.ArmorType;
import net.benjaminneukom.heavydefense.enemies.EnemyType;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.bullets.Rocket;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

public class RocketTower extends RotatableShootingTower {

	// offset shoot position
	private static final float[] OFFSETS = new float[] { 17, 8, // 0
			24, 4, // 1
			32, 8, // 2
			6, 17, // 3
			42, 17, // 4
			8, 22, // 5.
			24, 29, // 6
			41, 22, // 7
	};

	private static final float MEDIUM_BONUS = 0.4f;

	public RocketTower() {
	}

	public RocketTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, cooldown, damage, Assets.ROCKET_TOWER, Assets.ROCKET_TOWER, abstractWorld);

		postSerialized();
	}

	public static float mediumArmorBonus() {
		return TD.getUpgrade(UpgradeType.ROCKET_MEDIUM_BONUS).getValue() + MEDIUM_BONUS;
	}

	@Override
	public void shoot() {
		float x = getX() + getRenderOffsetX() + OFFSETS[2 * spriteIndex + 0];
		float y = getY() + getRenderOffsetY() + OFFSETS[2 * spriteIndex + 1];

		float realDamage = damage;
		if (target.getArmorType() == ArmorType.MEDIUM) {
			realDamage += damage * (mediumArmorBonus());
		}

		// tripple damage against air
		if (target.getEnemyType() == EnemyType.AIR) {
			realDamage *= 3;
		}

		abstractWorld.addOverlayActor(new Rocket(x, y, this, target, getRotation(), 125, 350, realDamage, abstractWorld, Assets.ROCKET, Assets.ROCKET_SMOKE));

	}

	@Override
	protected float getDamageUpgrade(int level) {
		return 30;
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 600 * (1 - TD.getUpgradeValue(UpgradeType.ROCKET_CHEAPER_UPGRADE)));
		return cost;
	}

	@Override
	public float getUpgradeOffsetX() {
		return -6;
	}

	@Override
	public float getUpgradeOffsetY() {
		return -10;
	}

	@Override
	protected float getRangeUpgrade(int level) {
		return 15;
	}

	@Override
	public boolean canTarget(AbstractEnemy abstractEnemy) {
		return true;
	}

	@Override
	public int getMaxLevel() {
		return TD.isUpgraded(UpgradeType.ROCKET_LEVEL_4) ? super.getMaxLevel() + 1 : super.getMaxLevel();
	}

	@Override
	public float getRenderOffsetX() {
		return 4;
	}

	@Override
	public float getRenderOffsetY() {
		return -2;
	}

	@Override
	public int getSpriteWidth() {
		return 50;
	}

	@Override
	public int getSpriteHeight() {
		return 50;
	}

}
