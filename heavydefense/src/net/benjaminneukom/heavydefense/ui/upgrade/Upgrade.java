package net.benjaminneukom.heavydefense.ui.upgrade;

import static net.benjaminneukom.heavydefense.util.StringUtil.numberSepeartor;
import net.benjaminneukom.heavydefense.Assets;

public class Upgrade {

	private int level;
	private float value;
	private UpgradeType upgradeType;

	public Upgrade(int level, UpgradeType upgradeType) {
		super();
		this.level = level;
		this.upgradeType = upgradeType;

		this.value = level * upgradeType.getValueUpgrade();
	}

	public enum UpgradeType {
		TURRET_PIERCING_BULLET(0, Assets.TURRET_ICON, "Piercing Bullet", new PiercingDescription(), 3, 250000, 1),
		TURRET_MEDIUM_BONUS(1, Assets.TURRET_ICON, "Medium Turret", new MediumArmorDamageBonusDescription("Turret"), 3, 250000, 0.5f),
		TURRET_CHEAPER_UPGRADE(2, Assets.TURRET_ICON, "Cheaper Turret", new CheaperTowerUpgradeDescription("Turret"), 3, 250000, 0.25f),
		TURRET_LEVEL_4(3, Assets.TURRET_ICON, "Turret Level 4", new Level4Description("Turret"), 1, 1500000, 0),
		ROCKET_MEDIUM_BONUS(4, Assets.ROCKET_ICON, "Medium Rocket", new MediumArmorDamageBonusDescription("Rocket Tower"), 3, 250000, 0.5f),
		ROCKET_LEVEL_4(5, Assets.ROCKET_ICON, "Rocket Level 4", new Level4Description("Rocket Tower"), 1, 1500000, 0),
		ROCKET_CHEAPER_UPGRADE(6, Assets.ROCKET_ICON, "Cheaper Rocket", new CheaperTowerUpgradeDescription("Rocket Tower"), 3, 250000, 0.25f),
		FIRE_BURN_DURATION(7, Assets.FIRE_ICON, "Longer Burn", new LongerBurnDescription(), 3, 250000, 0.25f),
		FIRE_BURN_DEBUFF(8, Assets.FIRE_ICON, "Burn Debuff", new BurningTargetsDescription(), 3, 250000, 0.15f),
		FIRE_TOWER_LEVEL_4(9, Assets.FIRE_ICON, "Fire Level 4", new Level4Description("Fire Tower"), 1, 1500000, 0),
		FIRE_CHEAPER_UPGRADE(10, Assets.FIRE_ICON, "Cheaper Fire", new CheaperTowerUpgradeDescription("Fire Tower"), 3, 250000, 0.25f),
		TESLA_OVERLOAD(11, Assets.TESLA_ICON, "Tesla Overload", new TeslaOverloadDescription(), 3, 250000, 0.2f),
		TESLA_AIR(12, Assets.TESLA_ICON, "Air Tesla", new AirTeslaDescription(), 1, 1500000, 0f),
		TESLA_TOWER_LEVEL_4(13, Assets.TESLA_ICON, "Tesla Level 4", new Level4Description("Tesla Tower"), 1, 1500000, 0.15f),
		TESLA_CHEAPER_UPGRADE(14, Assets.TESLA_ICON, "Cheaper Tesla", new CheaperTowerUpgradeDescription("Tesla Tower"), 3, 250000, 0.25f),
		ARTILLERY_SPLASH(15, Assets.ARTILLERY_ICON, "Splash", new IncreasedSplashDescription(), 3, 250000, 0.125f),
		ARTILLERY_HEAVY_DAMAGE(16, Assets.ARTILLERY_ICON, "Heavy Damage", new HeavyArmorDamageBonusDescription("Artillery Tower"), 3, 250000, 0.65f),
		ARTILLERY_TOWER_LEVEL_4(17, Assets.ARTILLERY_ICON, "Artil. Level 4", new Level4Description("Artillery Tower"), 1, 1500000, 0f),
		ARTILLERY_CHEAPER_UPGRADE(18, Assets.ARTILLERY_ICON, "Cheaper Artil.", new CheaperTowerUpgradeDescription("Artillery Tower"), 3, 250000, 0.25f),
		SLOW_FREEZE(19, Assets.SLOW_ICON, "Freeze", new SlowFreezeDescription(), 1, 2000000, 0f),
		SLOW_CHEAPER_UPGRADE(20, Assets.SLOW_ICON, "Cheaper Slow", new CheaperTowerUpgradeDescription("Slow Tower"), 3, 250000, 0.25f),
		INCREASED_LIVE(21, Assets.EMPTY_ICON, "Increased Lives", new IncreasedLivesDescription(), 3, 250000, 3f),
		INCREASED_START_MONEY(22, Assets.EMPTY_ICON, "Start Money", new IncreasedStartMoneyDescription(), 3, 250000, 1000f),
		INCREASED_MONEY(23, Assets.EMPTY_ICON, "More Money", new IncreasedMoneyDescription(), 3, 250000, 0.05f);

		public final int id;
		public final String iconPath;
		public final String name;
		public final int maxLevel;
		public final float upgradeCost;
		public final float valueUpgrade;
		public Description description;

		private UpgradeType(int id, String iconPath, String name, Description description, int maxLevel, float upgradeCost, float valueUpgrade) {
			this.id = id;
			this.iconPath = iconPath;
			this.name = name;
			this.description = description;
			this.maxLevel = maxLevel;
			this.upgradeCost = upgradeCost;
			this.valueUpgrade = valueUpgrade;
		}

		public String getIconPath() {
			return iconPath;
		}

		public String getName() {
			return name;
		}

		public int getMaxLevel() {
			return maxLevel;
		}

		public float getUpgradeCost() {
			return upgradeCost;
		}

		public float getValueUpgrade() {
			return valueUpgrade;
		}

		public Description getDescription() {
			return description;
		}

	}

	public String getIconPath() {
		return upgradeType.getIconPath();
	}

	public String getName() {
		return upgradeType.getName();
	}

	public String getDescription() {
		return upgradeType.getDescription().getText(this);
	}

	public int getMaxLevel() {
		return upgradeType.getMaxLevel();
	}

	public float getUpgradeCost() {
		final int nextLevel = getLevel() + 1;
		return upgradeType.getUpgradeCost() * nextLevel * nextLevel * nextLevel;
	}

	public float getValueUpgrade() {
		return upgradeType.getValueUpgrade();
	}

	public void upgrade() {
		level++;
		value += upgradeType.valueUpgrade;
	}

	public int getLevel() {
		return level;
	}

	public float getNextValue() {
		if (level == upgradeType.maxLevel) {
			return level * upgradeType.getValueUpgrade();
		}

		return (level + 1) * upgradeType.getValueUpgrade();
	}

	public float getValue() {
		return value;
	}

	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public abstract interface Description {
		public String getText(Upgrade upgrade);
	}

	public static class PiercingDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			int nextUpgradeValue = (int) upgrade.getNextValue();

			if (nextUpgradeValue == 1) {
				return "Turret Bullets will pierce through " + (int) upgrade.getNextValue() + " enemy.";
			} else {
				return "Turret Bullets will pierce through " + (int) upgrade.getNextValue() + " enemies.";
			}

		}

	}

	public static class CheaperTowerUpgradeDescription implements Description {

		private String tower;

		public CheaperTowerUpgradeDescription(String tower) {
			this.tower = tower;
		}

		@Override
		public String getText(Upgrade upgrade) {
			return "Decreases " + tower + " in-game upgrade cost by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class Level4Description implements Description {

		private String tower;

		public Level4Description(String tower) {
			this.tower = tower;
		}

		@Override
		public String getText(Upgrade upgrade) {
			return tower + " Level 4 unlocked.";
		}

	}

	public static class DamageBonusDescription implements Description {
		private String tower;

		public DamageBonusDescription(String tower) {
			this.tower = tower;
		}

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases " + tower + " damage against all enemies by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}
	}

	public static class HeavyArmorDamageBonusDescription implements Description {
		private String tower;

		public HeavyArmorDamageBonusDescription(String tower) {
			this.tower = tower;
		}

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases " + tower + " damage against heavy armored enemies by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class MediumArmorDamageBonusDescription implements Description {
		private String tower;

		public MediumArmorDamageBonusDescription(String tower) {
			this.tower = tower;
		}

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases " + tower + " damage against medium armored enemies by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class LongerBurnDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases burn duration by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class TeslaOverloadDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Tesla towers have a chance of " + (int) (upgrade.getNextValue() * 100) + "% of overloading and hitting another target.";
		}

	}

	public static class AirTeslaDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Tesla Towers now can hit air enemies.";
		}

	}

	public static class IncreasedLivesDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases starting lives by " + (int) (upgrade.getNextValue()) + ".";
		}

	}

	public static class IncreasedMoneyDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases the money you gain from killing enemies by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class BurningTargetsDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Burning targets take " + (int) (upgrade.getNextValue() * 100) + "% more damage.";
		}

	}

	public static class IncreasedStartMoneyDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases starting money by " + numberSepeartor((int) (upgrade.getNextValue())) + ".";
		}

	}

	public static class IncreasedSplashDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Increases the splash radius by " + (int) (upgrade.getNextValue() * 100) + "%.";
		}

	}

	public static class SlowFreezeDescription implements Description {

		@Override
		public String getText(Upgrade upgrade) {
			return "Slow Towers now have a small chance of freezing an Enemy in place for a short time.";
		}

	}

	public boolean isMaxLevel() {
		return getLevel() == getMaxLevel();
	}

}
