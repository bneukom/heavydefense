package net.benjaminneukom.heavydefense.enemies;

import net.benjaminneukom.heavydefense.enemies.Wave.EasyAirWave;
import net.benjaminneukom.heavydefense.enemies.Wave.EasyFastWave;
import net.benjaminneukom.heavydefense.enemies.Wave.EasyHeavyWave;
import net.benjaminneukom.heavydefense.enemies.Wave.EasyWave;
import net.benjaminneukom.heavydefense.enemies.Wave.EndBossWave;
import net.benjaminneukom.heavydefense.enemies.Wave.FastAirWave;
import net.benjaminneukom.heavydefense.enemies.Wave.FastWave;
import net.benjaminneukom.heavydefense.enemies.Wave.HeavyAndAirWave;
import net.benjaminneukom.heavydefense.enemies.Wave.HeavyAndFastWave;
import net.benjaminneukom.heavydefense.enemies.Wave.HeavyWave;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.util.Rand;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class WaveSpawner extends Actor {

	private AbstractWorld abstractWorld;

	private int difficulty;

	private int wave;
	private int maxWaves;

	private int startWaveSize;
	private int waveSize;

	private boolean spawnWave = false;
	private int currentSpawnSize = 0;
	private static final float RESPAWN_DELAY = 0.6f;
	private float respawnDelay;

	private static final float NEXT_SPAWN_DELAY = 7f;
	private float nextSpawnDelay;

	private static transient final Wave BOSS_WAVE = new EndBossWave();
	private static transient final Wave[] EASY_WAVES = new Wave[] { new EasyWave() };
	private static transient final Wave[] WAVES = new Wave[] { new EasyAirWave(), new EasyHeavyWave(), new EasyFastWave(), new EasyWave() };
	private static transient final Wave[] MEDIUM_WAVES = new Wave[] { new HeavyAndAirWave(), new HeavyAndFastWave() };
	private static transient final Wave[] DIFFICULT_WAVES = new Wave[] { new FastAirWave(), new HeavyWave(), new FastWave() };
	private Wave currentWave;

	private transient Array<WaveListener> waveListeners;
	private transient Array<KillListener> killListeners;
	private transient Array<ReachedEndListener> reachedEndListeners;

	public WaveSpawner() {
		waveListeners = new Array<WaveListener>();
		killListeners = new Array<KillListener>();
		reachedEndListeners = new Array<ReachedEndListener>();
	}

	public WaveSpawner(AbstractWorld abstractWorld, int startWaveSize, int maxWaves, int difficulty) {
		this.abstractWorld = abstractWorld;
		this.waveSize = startWaveSize;
		this.startWaveSize = startWaveSize;
		this.maxWaves = maxWaves;
		this.difficulty = difficulty;

		waveListeners = new Array<WaveListener>();
		killListeners = new Array<KillListener>();
		reachedEndListeners = new Array<ReachedEndListener>();
	}

	public void setWorld(AbstractWorld abstractWorld) {
		this.abstractWorld = abstractWorld;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (wave > maxWaves) {
			return;
		}

		// next round
		if (abstractWorld.getActiveEnemyCount() == 0 && !spawnWave) {
			waveEnd();

			nextSpawnDelay += delta;

			if (nextSpawnDelay > NEXT_SPAWN_DELAY) {
				spawnWave = true;
				nextSpawnDelay = 0;

				nextWave();
			}
		}

		if (spawnWave) {
			respawnDelay += delta;
			if (currentSpawnSize < waveSize) {
				if (respawnDelay > RESPAWN_DELAY) {
					spawnEnemy();
					++currentSpawnSize;
					respawnDelay = 0;
				}
			} else {
				currentSpawnSize = 0;
				spawnWave = false;
			}
		}
	}

	private void waveEnd() {
		for (WaveListener listener : waveListeners) {
			listener.waveEnd(wave);
		}
	}

	public void addWaveListener(WaveListener waveListener) {
		waveListeners.add(waveListener);
	}

	public void addKillListener(KillListener killListener) {
		killListeners.add(killListener);
	}

	public void addReachedEndListener(ReachedEndListener reachedEndListener) {
		reachedEndListeners.add(reachedEndListener);
	}

	private void nextWave() {
		wave++;

		waveSize = startWaveSize + wave / 10;

		if (wave < 25) {
			// 0 - 25
			currentWave = Rand.selectRandom(EASY_WAVES);
		} else if (wave < 45) {
			// 25 - 45
			currentWave = selectWave(0.25f, EASY_WAVES, WAVES);
		} else if (wave < 80) {
			// 25 - 80
			currentWave = selectWave(0.25f, WAVES, MEDIUM_WAVES);
		} else if (wave < 100) {
			// 80 - 99
			currentWave = selectWave(0.25f, MEDIUM_WAVES, DIFFICULT_WAVES);
		} else {
			// boss wave
			currentWave = BOSS_WAVE;
			waveSize = 1;
		}

		for (WaveListener listener : waveListeners) {
			listener.nextWave(wave);
		}
	}

	private Wave selectWave(float firstChance, Wave[] first, Wave[] second) {
		if (Math.random() < firstChance) {
			return Rand.selectRandom(first);
		} else {
			return Rand.selectRandom(second);
		}
	}

	private void spawnEnemy() {
		//http://www.wolframalpha.com/input/?i=plot+%286.25+*x+%2B+sqrt%28x%29+*+15%29+%2F+100+%2B+1+from+x+%3D0+to+100
		final float healthBonus = (float) ((6.25 * difficulty + Math.sqrt(difficulty) * 15) / 100 + 1);

		final AbstractEnemy abstractEnemy = currentWave.spawn(abstractWorld, wave, healthBonus);
		abstractEnemy.addKillListeners(killListeners);
		abstractEnemy.addReachedEndListeners(reachedEndListeners);
		abstractWorld.addEnemy(abstractEnemy);
	}

	public void setWave(int wave) {
		this.wave = wave;
	}

	public int getMaxWaves() {
		return maxWaves;
	}

	public int getWave() {
		return wave;
	}

	public int getWaveSize() {
		return waveSize;
	}

	public float getRespawnDelay() {
		return respawnDelay;
	}

	public boolean isSpawnWave() {
		return spawnWave;
	}

	public int getCurrentSpawnSize() {
		return currentSpawnSize;
	}

	public Wave getCurrentWave() {
		return currentWave;
	}

	public float getNextSpawnDelay() {
		return nextSpawnDelay;
	}

	public void setAbstractWorld(AbstractWorld abstractWorld) {
		this.abstractWorld = abstractWorld;
	}

	public static interface ReachedEndListener {
		public void reachedEnd(AbstractEnemy abstractEnemy);
	}

	public static interface KillListener {
		public void killed(AbstractEnemy abstractEnemy, int wave);
	}

	public static abstract class WaveListener {
		public void nextWave(int wave) {
		}

		public void waveEnd(int wave) {
		}
	}

}
