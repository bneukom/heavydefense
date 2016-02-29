package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.ArmorType;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.bullets.TurretBullet;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

public class TurretTower extends RotatableShootingTower {

	// offset shoot position
	private static final float[] OFFSETS = new float[] { 13, 11, // 0
			30, 5, // 1
			48, 12, // 2
			5, 29, // 3
			57, 29, // 4
			13, 47, // 5
			30, 54,// 6
			48, 47 // 7
	};

	private static final float MIN_SHOOT_DISTANCE = 65;

	private static final float MEDIUM_BONUS = 0.4f;

	public TurretTower() {
	}

	public TurretTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, cooldown, damage, Assets.TURRET_TOWER, Assets.TURRET_TOWER_MUZZLE, abstractWorld);

		postSerialized();
	}

	public static float mediumArmorBonus() {
		return TD.getUpgrade(UpgradeType.TURRET_MEDIUM_BONUS).getValue() + MEDIUM_BONUS;
	}

	@Override
	public int getMaxLevel() {
		return TD.isUpgraded(UpgradeType.TURRET_LEVEL_4) ? super.getMaxLevel() + 1 : super.getMaxLevel();
	}

	@Override
	public void shoot() {
		float x = getX() + getRenderOffsetX() + OFFSETS[2 * spriteIndex + 0];
		float y = getY() + getRenderOffsetY() + OFFSETS[2 * spriteIndex + 1];
		final int targetsToHit = TD.getUpgrade(UpgradeType.TURRET_PIERCING_BULLET).getLevel() + 1;

		float realDamage = damage;
		if (target.getArmorType() == ArmorType.MEDIUM) {
			realDamage += damage * mediumArmorBonus();
		}

		// do not fire bullet for performance reasons
		if (distanceTo(target) < MIN_SHOOT_DISTANCE && targetsToHit <= 1) {
			target.doDamage(realDamage);
			registerDamage(damage);
		} else {

			abstractWorld.addOverlayActor(new TurretBullet(x, y, this, getRotation(), 450, realDamage, targetsToHit, abstractWorld, Assets.TURRET_BULLET));
		}

	}

	@Override
	protected float getDamageUpgrade(int level) {
		return 10;
	}

	@Override
	public float getSellValue() {
		return 150f + 50 * level;
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 300 * (1 - TD.getUpgradeValue(UpgradeType.TURRET_CHEAPER_UPGRADE)));
		return cost;
	}

	@Override
	public float getUpgradeOffsetX() {
		return -10;
	}

	@Override
	public float getUpgradeOffsetY() {
		return -10;
	}

	@Override
	public boolean canTarget(AbstractEnemy abstractEnemy) {
		return abstractEnemy instanceof GroundEnemy;
	}

	@Override
	public float getRenderOffsetX() {
		return -2;
	}

	@Override
	public float getRenderOffsetY() {
		return -2;
	}

	@Override
	public int getSpriteWidth() {
		return 63;
	}

	@Override
	public int getSpriteHeight() {
		return 60;
	}

}
