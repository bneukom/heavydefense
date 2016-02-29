package net.benjaminneukom.heavydefense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.benjaminneukom.heavydefense.system.Billing;
import net.benjaminneukom.heavydefense.system.Tracking;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.ui.towers.achievements.Achievement;
import net.benjaminneukom.heavydefense.ui.towers.achievements.Achievement.AchievementType;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class TD {
	private static float score;
	private static float totalScore;
	private static int played = 0;
	private static int games = 0;

	private static Map<UpgradeType, Upgrade> upgrades = new LinkedHashMap<UpgradeType, Upgrade>();
	private static Map<AchievementType, Achievement> achievements = new LinkedHashMap<AchievementType, Achievement>();

	public static Billing billing;
	public static Tracking tracking;

	private static List<ScoreListener> scoreListeners = new ArrayList<TD.ScoreListener>();

	private static final String ACHIEVEMENTS_PREF = "achievements.pref";
	private static final String UPDATES_PREF = "updates.pref";

	public static String appVersion;

	public static void unlockAchievement(AchievementType type) {
		achievements.get(type).unlock();
	}

	public static void logDamage(AbstractTower tower, float damage) {

	}

	public void sendDamage() {

	}

	public static void increaseAchievement(AchievementType type) {
		increaseAchievement(type, 1);
	}

	private static void setAchievementStep(AchievementType type, int step) {
		achievements.get(type).setStep(step);
	}

	public static int getAchievementStep(AchievementType type) {
		return achievements.get(type).getCurrentStep();
	}

	public static void increaseAchievement(AchievementType type, int amount) {
		achievements.get(type).step(amount);
	}

	public static int getUpgradeLevel(UpgradeType type) {
		return upgrades.get(type).getLevel();
	}

	public static boolean isUpgraded(UpgradeType upgradeType) {
		return upgrades.get(upgradeType).getLevel() > 0;
	}

	public static float getUpgradeValue(UpgradeType upgradeType) {
		return upgrades.get(upgradeType).getValue();
	}

	public static Upgrade getUpgrade(UpgradeType upgradeType) {
		return upgrades.get(upgradeType);
	}

	public static void loadAchievements() {
		final Preferences preferences = Gdx.app.getPreferences(ACHIEVEMENTS_PREF);

		for (AchievementType achievementType : AchievementType.values()) {
			int step = preferences.getInteger(String.valueOf(achievementType.getId()));

			// TODO TEST
			//
			//			if (achievementType == AchievementType.KILL_1) {
			//				step = 99;
			//			}
			//			if (achievementType == AchievementType.KILL_2) {
			//				step = 4999;
			//			}
			//			if (achievementType == AchievementType.DONE) {
			//				step = 19;
			//			}

			achievements.put(achievementType, new Achievement(achievementType, step));
		}

	}

	public static int getNumberOfFinishedAchievements() {
		int finishedAchievements = 0;
		for (Achievement achievement : achievements.values()) {
			if (achievement.isUnlocked()) {
				finishedAchievements++;
			}
		}
		return finishedAchievements;
	}

	public static void loadUpgrades() {
		final Preferences preferences = Gdx.app.getPreferences(UPDATES_PREF);

		for (UpgradeType upgradeType : UpgradeType.values()) {
			final int level = preferences.getInteger(String.valueOf(upgradeType.id));
			upgrades.put(upgradeType, new Upgrade(level, upgradeType));
		}
	}

	public static Collection<Achievement> getAchievementCollection() {
		return achievements.values();
	}

	public static Map<AchievementType, Achievement> getAchievements() {
		return achievements;
	}

	public static Collection<Upgrade> getUpgrades() {
		return upgrades.values();
	}

	public void increaseScore(float amount) {
		score += amount;
	}

	public static float getScore() {
		return score;
	}

	public static float getTotalScore() {
		return totalScore;
	}

	public static void persistAchievements() {
		final Preferences preferences = Gdx.app.getPreferences(ACHIEVEMENTS_PREF);

		for (Achievement achievement : achievements.values()) {
			preferences.putInteger(String.valueOf(achievement.getAchievementType().getId()), achievement.getCurrentStep());
		}
		preferences.flush();
	}

	public static void persistScore() {
		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		preferences.putFloat("score", score);
		preferences.putFloat("totalScore", totalScore);
		preferences.flush();
	}

	public static void persistUpgrades() {
		final Preferences preferences = Gdx.app.getPreferences(UPDATES_PREF);

		for (Upgrade upgrade : upgrades.values()) {
			preferences.putInteger(String.valueOf(upgrade.getUpgradeType().id), upgrade.getLevel());
		}
		preferences.flush();
	}

	public static void addScore(float amount) {
		score += amount;
		fireScoreChanged();
	}

	public static void increaseTotalScore(float amount) {
		totalScore += amount;

		TD.setAchievementStep(AchievementType.RICH_1, (int) totalScore);
		TD.setAchievementStep(AchievementType.RICH_2, (int) totalScore);

		fireScoreChanged();
	}

	/**
	 * Returns the amount of times the app has been started.
	 * 
	 * @return
	 */
	public static int getPlayed() {
		return played;
	}

	/**
	 * Returns the amount of times the user has started a new game.
	 * 
	 * @return
	 */
	public static int getGamesPlayed() {
		return games;
	}

	public static void increasePlayed() {
		played++;
	}

	public static void increaseGamesStarted() {
		games++;
	}

	public static void loadScore() {
		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		score = preferences.contains("score") ? preferences.getFloat("score") : 0;
		totalScore = preferences.contains("totalScore") ? preferences.getFloat("totalScore") : 0;

		// fix for version < 1.25. Rich achievements were not updated correctly, so just reset them when loading the score
		TD.setAchievementStep(AchievementType.RICH_1, (int) totalScore);
		TD.setAchievementStep(AchievementType.RICH_2, (int) totalScore);

		fireScoreChanged();
	}

	public static void addScoreChangedListener(ScoreListener scoreListener) {
		scoreListeners.add(scoreListener);
	}

	/**
	 * Must be called after loading achievements.
	 * 
	 * @param achievementListener
	 */
	public static void removeAchievementListener(AchievementListener achievementListener) {
		for (Achievement achievement : achievements.values()) {
			achievement.removeAchievementListener(achievementListener);
		}
	}

	/**
	 * Must be called after loading achievements.
	 * 
	 * @param achievementListener
	 */
	public static void addAchievementListener(AchievementListener achievementListener) {
		for (Achievement achievement : achievements.values()) {
			achievement.addAchievementListener(achievementListener);
		}
	}

	private static void fireScoreChanged() {
		for (ScoreListener scoreListener : scoreListeners) {
			scoreListener.scoreChanged(score);
			scoreListener.totalScoreChanged(totalScore);
		}

	}

	public static void persist() {
		persistScore();
		persistUpgrades();
		persistAchievements();

		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		preferences.putInteger("played", played);
		preferences.putInteger("games", games);
		preferences.flush();
	}

	public static void load() {
		loadUpgrades();
		loadAchievements();
		loadScore();

		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		played = preferences.getInteger("played");
		games = preferences.getInteger("games");
	}

	public abstract static class AchievementListener {
		public void unlocked(Achievement achievement) {
		}

		public void changed(Achievement achievement) {
		}
	}

	public abstract static class ScoreListener {
		public void scoreChanged(float newScore) {
		}

		public void totalScoreChanged(float newScore) {
		}
	}

	public abstract static class UpgradeChangedListener {
	}

}
