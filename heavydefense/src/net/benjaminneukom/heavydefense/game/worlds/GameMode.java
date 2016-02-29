package net.benjaminneukom.heavydefense.game.worlds;

public enum GameMode {
	CLASSIC("Classic"), NO_ROADS("No Roads");

	private String name;

	private GameMode(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
