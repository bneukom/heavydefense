package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.util.Rand;

import com.badlogic.gdx.math.Vector2;

public class EnemyFactory {

	private static final String[] EASY_GROUND_UNITS = new String[] { Assets.GENERIC_TANK_1, Assets.GENERIC_TANK_2 };

	public static AbstractEnemy createEasyGroundUnit(final AbstractWorld abstractWorld, int wave, float healthBonus) {
		//		float healthPoints = (float) (Math.pow(wave, 2) / 6 + 75) * healthBonus; // http://www.wolframalpha.com/input/?i=x%5E2+%2F+6+%2B+75+from+x+%3D+0+to+100
		float healthPoints = (float) (5 * wave + Math.pow(wave, 2) / 15 + 75) * healthBonus; // http://www.wolframalpha.com/input/?i=5*x+%2B+x%5E2+%2F+15+%2B+75+from+x+%3D+0+to+100
		final Vector2 initialPos = abstractWorld.getPathNodes().get(0).getPosition();
		float speed = (float) (Math.random() * 10 + 30);
		float money = (float) (1.25 * Math.sqrt(wave) + 15); // http://www.wolframalpha.com/input/?i=1.25+*+sqrt%28x%29+%2B+15+from+x+%3D+0+to+100

		final AbstractEnemy abstractEnemy = new GroundEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.MEDIUM, abstractWorld.getPathNodes(),
				Rand.selectRandom(EASY_GROUND_UNITS), abstractWorld, healthPoints, speed, wave, money);

		abstractEnemy.centerAroundNodePosition();

		return abstractEnemy;
	}

	public static AirEnemy createCopter(final AbstractWorld abstractWorld, int wave, float healthBonus) {
		float healthPoints = (float) (4 * wave + Math.pow(wave, 2) / 20) * healthBonus; // http://www.wolframalpha.com/input/?i=4+*+x%2B+x%5E2+%2F+20+from+x+%3D+0+to+100
		final Vector2 initialPos = abstractWorld.getPathNodes().get(0).getPosition();
		float money = (float) (30 + Math.sqrt(wave));
		float speed = (float) (Math.random() * 15 + 25); // http://www.wolframalpha.com/input/?i=25+%2B+sqrt%28x%29++from+x+%3D+0+to+100

		final AirEnemy enemy = new AirEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.HEAVY, abstractWorld.getPathNodes(), Assets.COPTER, abstractWorld, healthPoints,
				speed, wave, money);

		enemy.centerAroundNodePosition();

		return enemy;
	}

	public static AbstractEnemy createHeavyTank(final AbstractWorld abstractWorld, int wave, float healthBonus) {
		//		float healthPoints = (float) (10 * wave + Math.pow(wave, 2) / 5f) * healthBonus; // http://www.wolframalpha.com/input/?i=10+*x%2B+x%5E2+%2F+5+from+x+%3D+0+to+100
		float healthPoints = (float) (20 * wave + Math.pow(wave, 2) / 10f) * healthBonus; // http://www.wolframalpha.com/input/?i=20+*x%2B+x%5E2+%2F+10+from+x+%3D+0+to+100
		final Vector2 initialPos = abstractWorld.getPathNodes().get(0).getPosition();
		float money = (float) (30 + Math.sqrt(wave));
		float speed = (float) (Math.random() * 5 + 15); // http://www.wolframalpha.com/input/?i=25+%2B+sqrt%28x%29++from+x+%3D+0+to+100

		final AbstractEnemy abstractEnemy = new GroundEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.HEAVY, abstractWorld.getPathNodes(),
				Assets.HEAVY_TANK, abstractWorld, healthPoints, speed, wave, money);

		abstractEnemy.centerAroundNodePosition();

		return abstractEnemy;
	}

	public static AbstractEnemy createEndboss(final AbstractWorld abstractWorld, int wave, float healthBonus) {
		//		float healthPoints = (float) (10 * wave + Math.pow(wave, 2) / 5f) * healthBonus; // http://www.wolframalpha.com/input/?i=10+*x%2B+x%5E2+%2F+5+from+x+%3D+0+to+100
		float healthPoints = (float) (20 * wave + Math.pow(wave, 2) / 10f) * healthBonus * 7f; // http://www.wolframalpha.com/input/?i=20+*x%2B+x%5E2+%2F+10+from+x+%3D+0+to+100
		final Vector2 initialPos = abstractWorld.getPathNodes().get(0).getPosition();
		float money = (float) (30 + Math.sqrt(wave)) * 50f;
		float speed = (float) (Math.random() * 5 + 15); // http://www.wolframalpha.com/input/?i=25+%2B+sqrt%28x%29++from+x+%3D+0+to+100

		final BossEnemy boss = new BossEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.HEAVY, abstractWorld.getPathNodes(),
				Assets.END_BOSS, abstractWorld, healthPoints, speed, wave, money);

		boss.centerAroundNodePosition();

		return boss;
	}

	public static AbstractEnemy createFastTank(final AbstractWorld abstractWorld, int wave, float healthBonus) {
		float healthPoints = (float) (10 * wave + Math.pow(wave, 2) / 30) * healthBonus; // http://www.wolframalpha.com/input/?i=10+*+x%2B+x%5E2+%2F+30+from+x+%3D+0+to+100
		final Vector2 initialPos = abstractWorld.getPathNodes().get(0).getPosition();
		float speed = (float) (Math.random() * 15 + 35);
		float money = (float) (30 + Math.sqrt(wave)); // http://www.wolframalpha.com/input/?i=25+%2B+sqrt%28x%29++from+x+%3D+0+to+100

		final AbstractEnemy abstractEnemy = new GroundEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.MEDIUM, abstractWorld.getPathNodes(),
				Assets.FAST_TANK, abstractWorld, healthPoints, speed, wave, money);

		abstractEnemy.centerAroundNodePosition();

		return abstractEnemy;
	}

}
