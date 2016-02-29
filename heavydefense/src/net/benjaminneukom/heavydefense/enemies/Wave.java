package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

public abstract class Wave {
	public abstract AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus);

	public static class EasyWave extends Wave {

		public EasyWave() {
		}

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			return EnemyFactory.createEasyGroundUnit(game, wave, healthBonus);
		}

	}

	public static class EndBossWave extends Wave {

		public EndBossWave() {
		}

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			return EnemyFactory.createEndboss(game, wave, healthBonus);
		}

	}

	public static class EasyAirWave extends Wave {
		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.25) {
				return EnemyFactory.createCopter(game, wave, healthBonus);
			} else {
				return EnemyFactory.createEasyGroundUnit(game, wave, healthBonus);
			}
		}
	}

	public static class FastAirWave extends Wave {
		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.4) {
				return EnemyFactory.createCopter(game, wave, healthBonus);
			} else {
				return EnemyFactory.createFastTank(game, wave, healthBonus);
			}
		}
	}

	public static class HeavyAndAirWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.65) {
				return EnemyFactory.createHeavyTank(game, wave, healthBonus);
			} else {
				return EnemyFactory.createCopter(game, wave, healthBonus);
			}
		}

	}

	public static class HeavyAndFastWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.4) {
				return EnemyFactory.createFastTank(game, wave, healthBonus);
			} else {
				return EnemyFactory.createHeavyTank(game, wave, healthBonus);
			}
		}

	}

	public static class EasyHeavyWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.65) {
				return EnemyFactory.createHeavyTank(game, wave, healthBonus);
			} else {
				return EnemyFactory.createEasyGroundUnit(game, wave, healthBonus);
			}
		}

	}

	public static class HeavyWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			return EnemyFactory.createHeavyTank(game, wave, healthBonus);
		}

	}

	public static class EasyFastWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			if (Math.random() < 0.55) {
				return EnemyFactory.createFastTank(game, wave, healthBonus);
			} else {
				return EnemyFactory.createEasyGroundUnit(game, wave, healthBonus);
			}
		}

	}

	public static class FastWave extends Wave {

		@Override
		public AbstractEnemy spawn(AbstractWorld game, int wave, float healthBonus) {
			return EnemyFactory.createFastTank(game, wave, healthBonus);
		}

	}
}
