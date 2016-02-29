package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.ArmorType;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;
import net.benjaminneukom.heavydefense.util.MoreMath;

import com.badlogic.gdx.utils.Array;

public class ArtilleryTower extends RotatableShootingTower {

	private static final int DEFAULT_DAMAGE_RADIUS = 25;
	private static final float HEAVY_DAMAGE_BONUS = 0.75f;

	public ArtilleryTower() {
	}

	public ArtilleryTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, cooldown, damage, Assets.ARTILLERY_TOWER, Assets.ARTILLERY_TOWER_SHOOTING, abstractWorld);

		postSerialized();
	}

	public static float heavyDamageBonus() {
		return TD.getUpgrade(UpgradeType.ARTILLERY_HEAVY_DAMAGE).getValue() + HEAVY_DAMAGE_BONUS;
	}

	@Override
	public void shoot() {
		float radius = DEFAULT_DAMAGE_RADIUS;

		radius += radius * TD.getUpgradeValue(UpgradeType.ARTILLERY_SPLASH);

		final Array<AbstractEnemy> enemies = abstractWorld.getEnemies();
		for (AbstractEnemy enemy : enemies) {
			if (MoreMath.dist(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2, target.getX() + target.getWidth() / 2, target.getY() + target.getHeight()
					/ 2) < radius && canTarget(enemy)) {

				float realDamage = damage;
				if (target.getArmorType() == ArmorType.HEAVY) {
					realDamage += damage * (heavyDamageBonus());
				}

				enemy.doDamage(realDamage);
				registerDamage(realDamage);
			}
		}

		final float renderX = (float) (target.getX() + target.getWidth() / 2 - target.getWidth() / 4 + Math.random() * target.getWidth() / 2) - 20;
		final float renderY = (float) (target.getY() + target.getHeight() / 2 - target.getHeight() / 4 + Math.random() * target.getHeight() / 2) - 20;

		abstractWorld.addOverlayActor(new Explosion(renderX, renderY, Assets.OVERLAY_ATLAS, Assets.EXPLOSION_BIG, 7, 2, abstractWorld));

	}

	@Override
	protected float getDamageUpgrade(int level) {
		return 10;
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 600 * (1 - TD.getUpgradeValue(UpgradeType.ARTILLERY_CHEAPER_UPGRADE)));
		return cost;
	}

	@Override
	public int getMaxLevel() {
		if (TD.isUpgraded(UpgradeType.ARTILLERY_TOWER_LEVEL_4))
			return super.getMaxLevel() + 1;
		else
			return super.getMaxLevel();
	}

	@Override
	public boolean canTarget(AbstractEnemy enemy) {
		return enemy instanceof GroundEnemy;
	}

	@Override
	public float getRenderOffsetX() {
		return 0;
	}

	@Override
	public float getRenderOffsetY() {
		return -15;
	}

	@Override
	public int getSpriteWidth() {
		return 71;
	}

	@Override
	public int getSpriteHeight() {
		return 72;
	}
}
