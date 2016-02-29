package net.benjaminneukom.heavydefense.tools;

public class PointsTest {
	// 164'000'000 needed for all upgrades
	public static void main(String[] args) {
		//		final int startPoints = 350000;
		//		final int endPoints = 2800000;
		//		final int levelIncrease = (endPoints - startPoints) / 100;
		//
		//		int points = 0;
		//		for (int level = 0; level <= 100; ++level) {
		//			points += levelIncrease * level + 300000;
		//		}
		//		System.out.println("increase " + levelIncrease);
		//		System.out.println("total points gained for all levels: " + points);

		// 300000 * ((8.5*100) / 100 + 1)

		int points = 0;
		for (int level = 0; level <= 10; ++level) {
			final int levelPoint = (int) (324000f * (10f * level / 275f + 1));
			final int victorybonus = (int) (levelPoint * 0.25f);
			final int livesBonus = (5000 * 18) + (18 * level * 150);

			points += levelPoint;
			points += victorybonus;
			points += livesBonus;
			System.out.println(level + ": " + (levelPoint + victorybonus + livesBonus) + " (" + levelPoint + " + " + victorybonus + " + " + livesBonus + ")");
		}
		System.out.println(points);
		System.out.println("Multiply by 1.2 for map of the day and map difficulty bonuses: " + (int) (points * 1.2));
	}
}
