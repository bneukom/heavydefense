package net.benjaminneukom.heavydefense.ui.towers.achievements;

import static net.benjaminneukom.heavydefense.util.StringUtil.numberSepeartor;

import java.util.ArrayList;
import java.util.List;

import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.TD.AchievementListener;

public class Achievement {

	private AchievementType achievementType;
	private int currentStep;
	private List<AchievementListener> achievementListeners = new ArrayList<TD.AchievementListener>();

	public Achievement(AchievementType achievementType, int currentStep) {
		super();
		this.achievementType = achievementType;
		this.currentStep = currentStep;
	}

	public enum AchievementType {

		SURVIVOR(0, 1, 1500000, "Survivor", "Survive a game with only one live left."),
		HEALTH(1, 1, 100000, "Health", "Finnish a game with all lives left."),
		NO_UPGRADES(2, 1, 1500000, "No upgrades", "Finnish a game without upgrading any towers."),
		RICH_1(3, 10000000, 1000000, "Rich 1", "Gain a total of " + numberSepeartor(10000000) + " points"),
		RICH_2(4, 100000000, 5000000, "Rich 2", "Gain a total of " + numberSepeartor(100000000) + " points"),
		SKILL_1(5, 1, 1000000, "Skills 1", "Finnish a game with building 12 Towers or less"),
		SKILL_2(6, 1, 2000000, "Skills 2", "Finnish a game with building 8 Towers or less"),
		GREEDY_1(7, 1, 500000, "Greedy 1", "Finnish a game with more than " + numberSepeartor(5000) + " money left."),
		GREEDY_2(8, 1, 1000000, "Greedy 2", "Finnish a game with more than " + numberSepeartor(10000) + " money left."),
		LEVEL_4(9, 1, 1500000, "Super Tower", "Build a level 4 tower."),
		KILL_1(10, 100, 25000, "Kill 1", "Kill " + numberSepeartor(100) + " Enemies."),
		KILL_2(11, 5000, 200000, "Kill 2", "Kill " + numberSepeartor(5000) + " Enemies."),
		KILL_3(12, 100000, 2000000, "Kill 3", "Kill 100000 Enemies."),
		DIFFICULTY_1(13, 1, 100000, "Difficulty 1", "Finnish a game with difficulty 5 or higher."),
		DIFFICULTY_2(14, 1, 1000000, "Difficulty 2", "Finnish a game with difficulty 30 or higher."),
		DIFFICULTY_3(15, 1, 3000000, "Difficulty 3", "Finnish a game with difficulty 60 or higher."),
		DIFFICULTY_4(16, 1, 6000000, "Difficulty 4", "Finnish a game with difficulty 90 or higher."),
		BOSS(17, 1, 50000, "Boss", "Defeat an endboss."),
		END_GAME(18, 1, 25000000, "End Game", "Defeat the endboss on level 100."),
		DONE(19, 20, 50000000, "Done", "Unlock all the other achievements.");

		private final int id;
		private final int steps;
		private final float points;
		private final String name;
		private final String description;

		private AchievementType(int id, int steps, float points, String name, String description) {
			this.id = id;
			this.steps = steps;
			this.points = points;
			this.name = name;
			this.description = description;
		}

		public int getSteps() {
			return steps;
		}

		public float getPoints() {
			return points;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getId() {
			return id;
		}

	}

	public void fireAchievementUnlockedListeners() {
		for (AchievementListener achievementListener : achievementListeners) {
			achievementListener.unlocked(this);
		}
	}

	public void addAchievementListener(AchievementListener achievementListener) {
		achievementListeners.add(achievementListener);
	}

	public void removeAchievementListener(AchievementListener achievementListener) {
		achievementListeners.remove(achievementListener);
	}

	public boolean isUnlocked() {
		return currentStep == achievementType.steps;
	}

	public int getTotalSteps() {
		return achievementType.getSteps();
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public float getPoints() {
		return achievementType.getPoints();
	}

	public String getName() {
		return achievementType.getName();
	}

	public String getDescription() {
		return achievementType.getDescription();
	}

	public AchievementType getAchievementType() {
		return achievementType;
	}

	public void unlock() {
		if (currentStep >= getTotalSteps())
			return;

		currentStep = getTotalSteps();
		unlocked();

	}

	public void step() {
		step(1);
	}

	public void setStep(int step) {
		if (currentStep >= getTotalSteps())
			return;

		currentStep = step;
		currentStep = Math.min(currentStep, getTotalSteps());

		if (currentStep == getTotalSteps()) {
			unlocked();
		}
	}

	public void step(int amount) {
		if (currentStep >= getTotalSteps())
			return;

		currentStep += amount;
		currentStep = Math.min(currentStep, getTotalSteps());

		if (currentStep == getTotalSteps()) {
			unlocked();
		}
	}

	private void unlocked() {
		TD.addScore(getPoints());
		TD.increaseTotalScore(getPoints());
		TD.increaseAchievement(AchievementType.DONE);

		// -----------------------------------
		// FIX for done achievement which needed 20/20 achievements. If the user has unlocked the 19th achievement, 
		// he will receive the unlocked achievement (meaning 20/20 done)
		// -----------------------------------
		if (TD.getNumberOfFinishedAchievements() == AchievementType.values().length - 1) {
			TD.getAchievements().get(AchievementType.DONE).unlock();
		}

		fireAchievementUnlockedListeners();
	}

}
