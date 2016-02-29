package net.benjaminneukom.heavydefense;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private float money;
	private float score;
	private int lives;
	private int startLives;

	private transient List<StatChangedListener> statChangedListeners = new ArrayList<Player.StatChangedListener>();

	public Player() {
	}

	public Player(int lives, float money) {
		this.lives = lives;
		this.startLives = lives;
		this.money = money;
	}

	public int getStartLives() {
		return startLives;
	}

	public float getMoney() {
		return money;
	}

	public float getScore() {
		return score;
	}

	public int getLives() {
		return lives;
	}

	public void increaseMoney(float value) {
		money += value;
		fireStatChanged("money");
	}

	public void increaseScore(float value) {
		score += value;
		fireStatChanged("score");
	}

	private void fireStatChanged(String stat) {
		for (StatChangedListener statChangedListener : statChangedListeners) {
			statChangedListener.statsChanged(this, stat);
		}
	}

	public void addStatChangedListener(StatChangedListener listener) {
		statChangedListeners.add(listener);
	}

	public static interface StatChangedListener {
		public void statsChanged(Player player, String stat);
	}

	public void decreaseLives() {
		lives--;
		fireStatChanged("lives");
	}

}
