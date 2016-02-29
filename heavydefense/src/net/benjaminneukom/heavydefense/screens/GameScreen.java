package net.benjaminneukom.heavydefense.screens;

import static net.benjaminneukom.heavydefense.util.StringUtil.numberSepeartor;

import java.util.HashSet;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.Player;
import net.benjaminneukom.heavydefense.Player.StatChangedListener;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.TD.AchievementListener;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.BossEnemy;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner.KillListener;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner.ReachedEndListener;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner.WaveListener;
import net.benjaminneukom.heavydefense.game.worlds.GameWorld;
import net.benjaminneukom.heavydefense.serializers.DefaultReferenceStore;
import net.benjaminneukom.heavydefense.serializers.ReferenceJSonSerializer;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.tower.ArtilleryTower;
import net.benjaminneukom.heavydefense.tower.FireTower;
import net.benjaminneukom.heavydefense.tower.RocketTower;
import net.benjaminneukom.heavydefense.tower.TeslaTower;
import net.benjaminneukom.heavydefense.tower.TurretTower;
import net.benjaminneukom.heavydefense.tower.builders.SlowTowerBuilder;
import net.benjaminneukom.heavydefense.tower.builders.TowerBuilder;
import net.benjaminneukom.heavydefense.ui.towers.HorizontalTowersWidget;
import net.benjaminneukom.heavydefense.ui.towers.TowerWidget;
import net.benjaminneukom.heavydefense.ui.towers.achievements.Achievement;
import net.benjaminneukom.heavydefense.ui.towers.achievements.Achievement.AchievementType;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Value;

public class GameScreen extends AbstractScreen {

	private static final String INITIAL_TOOLTIP =
			"- Tap screen to build towers\n" +
					"- Use different Towers against medium and heavy armored units\n" +
					"- Tanks are heavy armored\n " +
					"- Fast units are medium armored\n" +
					"- After wave 25 air units spawn\n" +
					"- Rocket Towers can hit air units";
	private boolean isShowingInitialTooltip;

	private boolean isDragging = false;
	private GameWorld gameWorld;
	private HeavyDefenseGame heavyDefenseGame;

	// buy tower widgets
	private Table buyTowerSelectionTable;
	private HorizontalTowersWidget towerListWidget;
	private Label waveLabel;
	private Label waveDescriptionLabel;

	// used for tower information
	private Table towerInfoTable;
	private TowerWidget towerInfoWidget;

	// achievement display
	private Table achievementTable;
	private Label achievementTitle;
	private Label achievementDescriptionLabel;

	// gui when active or inactive
	private Table pausedTable;
	private Stack gameStack;

	private Button fastforwardButton;
	private Button pauseButton;

	private Label finishLabel;
	private LabelStyle finishLabelStyle;
	private Label endScoreLabel;
	private Label levelDifficultyBonusLabel;
	private Label mapOfTheDayBonusLabel;
	private Label remainingLivesBonusLabel;
	private Label victoryBonusLabel;
	private Label finalScoreLabel;

	private Table endScreenTable;

	private boolean ended = false;

	private boolean isShowingAchievement = false;
	private Array<Achievement> achievementQueue = new Array<Achievement>();
	private AchievementListener achievementListener;

	private static final float DIALOG_OPEN_SPEED = 0.0f;
	private static final float FAST_FORWARD_SPEED = 3.75f;

	private static float MIN_DRAG_DISTANCE = 20f;

	private final ReferenceJSonSerializer referenceJSonSerializer;

	public GameScreen(HeavyDefenseGame towerdefenseGame, final GameWorld gameWorld) {
		this.heavyDefenseGame = towerdefenseGame;
		this.gameWorld = gameWorld;
		this.gameWorld.setCamera(stageCamera);

		final DefaultReferenceStore referenceStore = new DefaultReferenceStore();
		this.referenceJSonSerializer = new ReferenceJSonSerializer(referenceStore);

		stage.addActor(gameWorld);

		achievementListener = new AchievementListener() {

			@Override
			public void unlocked(Achievement achievement) {
				achievementQueue.add(achievement);
			}
		};

		TD.addAchievementListener(achievementListener);

		gameWorld.getPlayer().addStatChangedListener(new StatChangedListener() {

			@Override
			public void statsChanged(Player player, String stat) {
				if (stat.equals("money")) {
					towerInfoWidget.updateMoney(player.getMoney());
				}
			}
		});

		// defeat
		gameWorld.getPlayer().addStatChangedListener(new StatChangedListener() {

			@Override
			public void statsChanged(Player player, String stat) {
				if (stat.equals("lives")) {
					if (player.getLives() <= 0) {
						end(false);
					}
				}
			}
		});

		// victory
		gameWorld.getWaveSpawner().addWaveListener(new WaveListener() {

			@Override
			public void waveEnd(int wave) {
				if (wave >= gameWorld.getWaveSpawner().getMaxWaves()) {
					end(true);
				}
			}
		});

		gameWorld.getWaveSpawner().addWaveListener(new WaveListener() {

			@Override
			public void nextWave(int wave) {
				gameWorld.cleanUpParticleEffects();
			}
		});

		// debug information
		gameWorld.getWaveSpawner().addWaveListener(new WaveListener() {

			private HashSet<DamageData> damageDataSet = new HashSet<DamageData>();

			@Override
			public void nextWave(int wave) {
				// clear data from last wave
				damageDataSet.clear();

				// collect data
				Array<Actor> towerActors = gameWorld.getTowerActors();
				TOWER: for (Actor actor : towerActors) {
					AbstractTower tower = (AbstractTower) actor;

					for (DamageData data : damageDataSet) {
						if (data.towerType == tower.getClass() && data.towerLevel == tower.getLevel()) {
							data.addDamage(tower.getTotalDamageDone(), tower.getActiveTime());

							continue TOWER;
						}
					}

					damageDataSet.add(new DamageData(tower.getClass(), tower.getLevel(), tower.getTotalDamageDone(), tower.getActiveTime()));
				}

				/*
				Gdx.app.log("HeavyDefense", "=== Damage Stats (" + System.currentTimeMillis() + ") ===");
				for (DamageData damageData : damageDataSet) {
					final String damageMessage = damageData.towerType.getSimpleName() + " (" + damageData.towerLevel + ") DPS: " + damageData.getDps() + " (dmg: "
							+ damageData.totalDamage
							+ ", active: " + damageData.activeTime + ", avg active: " + damageData.activeTime / damageData.towers + ")";
					Gdx.app.log("HeavyDefense", damageMessage);

					//					TD.tracking.sendEvent("TowerDamage", damageData.towerType.getName(), damageMessage, 0L);
				}
				Gdx.app.log("HeavyDefense", "============== End Stats ==============");
				*/
			}

			class DamageData {
				public Class<? extends AbstractTower> towerType;
				public int towerLevel = 0;

				public float totalDamage;
				public float activeTime;
				public int towers;

				public DamageData(Class<? extends AbstractTower> towerType, int towerLevel, float totalDamage, float activeTime) {
					super();
					this.towerType = towerType;
					this.towerLevel = towerLevel;
					this.totalDamage = totalDamage;
					this.activeTime = activeTime;
				}

				public void addDamage(float totalDamage, float activeTime) {
					this.totalDamage += totalDamage;
					this.activeTime += activeTime;
					this.towers++;
				}

				private float getDps() {
					if (activeTime == 0) {
						return 0;
					}
					return totalDamage / activeTime;
				}

			}
		});

		// add kill listener
		final KillListener killListener = new KillListener() {

			@Override
			public void killed(AbstractEnemy abstractEnemy, int wave) {
				// TODO should have been TD.increaseKillCount and the kill achievements should have listened onto this count...
				TD.increaseAchievement(AchievementType.KILL_1);
				TD.increaseAchievement(AchievementType.KILL_2);
				TD.increaseAchievement(AchievementType.KILL_3);

				if (abstractEnemy instanceof BossEnemy) {
					TD.unlockAchievement(AchievementType.BOSS);
					if (gameWorld.getDifficulty() == 100) {
						TD.unlockAchievement(AchievementType.END_GAME);
					}
				}

				{
					float score = abstractEnemy.getMoney() * wave / 6;

					// TODO into final score calculation?
					// increase score from difficulty
					score *= ((10f * gameWorld.getDifficulty()) / 275f + 1);

					gameWorld.getPlayer().increaseScore(score);
				}

				{
					float money = abstractEnemy.getMoney();

					// increase money from upgrade
					money *= TD.getUpgradeValue(UpgradeType.INCREASED_MONEY) + 1;

					// increase money from difficulty 
					// http://www.wolframalpha.com/input/?i=sqrt%28x%29+%2F+18+%2B+x+%2F+125+%2B+1+from+x+%3D+0+to+100
					money *= (Math.sqrt(gameWorld.getDifficulty()) / 18 + gameWorld.getDifficulty() / 125 + 1);

					gameWorld.getPlayer().increaseMoney(money);
				}
			}
		};
		gameWorld.getWaveSpawner().addKillListener(killListener);

		final ReachedEndListener reachedEndListener = new ReachedEndListener() {

			@Override
			public void reachedEnd(AbstractEnemy abstractEnemy) {
				gameWorld.getPlayer().decreaseLives();
			}
		};
		gameWorld.getWaveSpawner().addReachedEndListener(reachedEndListener);

		// incease of deserializing also add kill listener to active enemies
		final Array<AbstractEnemy> enemies = gameWorld.getEnemies();
		for (AbstractEnemy abstractEnemy : enemies) {
			abstractEnemy.addKillListener(killListener);
			abstractEnemy.addReachedEndListener(reachedEndListener);
		}

		gameWorld.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isDragging && !isPaused()) {
					handleClick(gameWorld, x, y);

				}
			}
		});

		// create ui
		createUI();

		// dragging
		stage.addListener(new InputListener() {

			private float x;
			private float y;
			private float distance;

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					Gdx.app.exit();
					return true;
				}

				return false;
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				this.x = x;
				this.y = y;

				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isDragging = false;
				distance = 0;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (buyTowerSelectionTable.isVisible())
					return;

				float offsetX = this.x - x;
				float offsetY = this.y - y;

				distance += Math.hypot(offsetX, offsetY);

				if (distance < MIN_DRAG_DISTANCE)
					return;

				isDragging = true;

				stageCamera.translate(offsetX, offsetY);

				if (stageCamera.position.x - stageCamera.viewportWidth / 2 <= 0)
					stageCamera.position.x = stageCamera.viewportWidth / 2;
				if (stageCamera.position.x + stageCamera.viewportWidth / 2 >= gameWorld.getWidth())
					stageCamera.position.x = gameWorld.getWidth() - stageCamera.viewportWidth / 2;

				if (stageCamera.position.y - stageCamera.viewportHeight / 2 <= 0)
					stageCamera.position.y = stageCamera.viewportHeight / 2;
				if (stageCamera.position.y + stageCamera.viewportHeight / 2 >= gameWorld.getHeight())
					stageCamera.position.y = gameWorld.getHeight() - stageCamera.viewportHeight / 2;

				stageCamera.update();

				this.x = x + offsetX;
				this.y = y + offsetY;

			}

		});

	}

	private void end(final boolean victory) {
		sendGameInfo();

		ended = true;

		// remove possible save game
		final FileHandle saveGame = Gdx.files.local(Assets.SAVE_GAME_NAME_2_0_0);
		if (saveGame.exists()) {
			saveGame.delete();
		}

		setPaused(true, false);

		endScreenTable.setVisible(true);

		float realScore = gameWorld.getPlayer().getScore();
		// depending on difficulty (0%, 15%, 30%)
		float levelDifficultyBonus = realScore * gameWorld.getLevelDifficulty().getBonusPoints();
		// 20%
		float mapOfTheDayBonus = realScore * (gameWorld.isMapOfTheDay() ? 0.15f : 0);
		// http://www.wolframalpha.com/input/?i=5000+*+15++%2B+%2815+*++x+*+150%29+from+x+%3D+0+to+100
		float remainingLivesBonus = (5000 * gameWorld.getPlayer().getLives()) + (gameWorld.getPlayer().getLives() * gameWorld.getDifficulty() * 150);
		// 25% if victorious
		float victoryBonus = 0;
		if (victory) {
			victoryBonus = realScore * 0.25f;

			finishLabel.setText("Victory!");
			finishLabelStyle.fontColor = Assets.COLOR_GREEN;

			if (gameWorld.getDifficulty() >= 5) TD.unlockAchievement(AchievementType.DIFFICULTY_1);
			if (gameWorld.getDifficulty() >= 30) TD.unlockAchievement(AchievementType.DIFFICULTY_2);
			if (gameWorld.getDifficulty() >= 60) TD.unlockAchievement(AchievementType.DIFFICULTY_3);
			if (gameWorld.getDifficulty() >= 90) TD.unlockAchievement(AchievementType.DIFFICULTY_4);

			if (gameWorld.getPlayer().getMoney() > 5000) TD.unlockAchievement(AchievementType.GREEDY_1);
			if (gameWorld.getPlayer().getMoney() > 10000) TD.unlockAchievement(AchievementType.GREEDY_2);

			if (gameWorld.getTotalTowersBuilt() <= 12) TD.unlockAchievement(AchievementType.SKILL_1);
			if (gameWorld.getTotalTowersBuilt() <= 8) TD.unlockAchievement(AchievementType.SKILL_2);

			if (!hasUpgradedTower()) {
				TD.unlockAchievement(AchievementType.NO_UPGRADES);
			}

			if (gameWorld.getPlayer().getLives() == gameWorld.getPlayer().getStartLives()) TD.unlockAchievement(AchievementType.HEALTH);
			if (gameWorld.getPlayer().getLives() == 1) TD.unlockAchievement(AchievementType.SURVIVOR);

		} else {
			finishLabel.setText("Defeated!");
			finishLabelStyle.fontColor = Assets.COLOR_RED;
		}

		final float finalScore = realScore + levelDifficultyBonus + mapOfTheDayBonus + remainingLivesBonus + victoryBonus;

		endScoreLabel.setText(numberSepeartor((int) realScore));
		levelDifficultyBonusLabel.setText(numberSepeartor((int) levelDifficultyBonus));
		mapOfTheDayBonusLabel.setText(numberSepeartor((int) mapOfTheDayBonus));
		remainingLivesBonusLabel.setText(numberSepeartor((int) remainingLivesBonus));
		victoryBonusLabel.setText(numberSepeartor((int) victoryBonus));
		finalScoreLabel.setText(numberSepeartor((int) finalScore));

		TD.addScore(finalScore);
		TD.increaseTotalScore(finalScore);

		fastforwardButton.setVisible(false);
		pauseButton.setVisible(false);
	}

	private void sendGameInfo() {
		// win loss
		// difficulty
		// tower type and level
		// total tower damage
	}

	@Override
	public void render(float delta) {

		super.render(delta);

		if (!isShowingAchievement && achievementQueue.size > 0) {
			final Achievement pop = achievementQueue.pop();
			fadeInAchievement(pop);
		}
	}

	private boolean hasUpgradedTower() {
		boolean hasUpgradedTower = false;
		final Array<AbstractTower> towers = gameWorld.getTowers();
		for (AbstractTower abstractTower : towers) {
			if (abstractTower.getLevel() > 0) {
				hasUpgradedTower = true;
				break;
			}
		}
		return hasUpgradedTower;
	}

	@Override
	protected InputMultiplexer createStageMultiplexer() {
		// TODO implement zoom
		GestureDetector gesture = new GestureDetector(new GestureAdapter() {

		});
		return new InputMultiplexer(gesture, stage);
	}

	@Override
	public void setPaused(boolean paused) {
		// pausing not enabled while showing the initial tooltip
		if (isShowingInitialTooltip) return;

		super.setPaused(paused);

		pausedTable.setVisible(paused);

		hideTowerInfoDialog();
		hideBuyTowerDialog();

		fastforwardButton.setDisabled(paused);
		pauseButton.setVisible(!paused);

		setSpeed(1);
	}

	public void setPaused(boolean paused, boolean showPause) {
		// pausing not enabled while showing the initial tooltip
		if (isShowingInitialTooltip) return;

		super.setPaused(true);

		hideBuyTowerDialog();
		hideTowerInfoDialog();

		setSpeed(1);
	}

	private void showTowerInfoDialg(AbstractTower tower) {
		if (tower == null) {
			hideTowerInfoDialog();
			return;
		}

		towerInfoWidget.setTower(tower, gameWorld.getPlayer().getMoney());
		towerInfoTable.setVisible(true);

		if (fastforwardButton.isChecked())
			fastforwardButton.toggle();

		fastforwardButton.setVisible(false);

		setSpeed(DIALOG_OPEN_SPEED);

		gameWorld.setDisplayTower(tower);
	}

	private void showBuyTowerDialog() {
		//		towerScrollPane.setVisible(true);

		buyTowerSelectionTable.setVisible(true);
		towerListWidget.updateEnablement();

		if (fastforwardButton.isChecked())
			fastforwardButton.toggle();

		fastforwardButton.setVisible(false);
		pauseButton.setVisible(false);
		waveLabel.setVisible(false);
		waveDescriptionLabel.setVisible(false);

		setSpeed(DIALOG_OPEN_SPEED);

	}

	public boolean gameHasEnded() {
		return ended;
	}

	public void hideTowerInfoDialog() {
		towerInfoTable.setVisible(false);
		fastforwardButton.setVisible(true);

		setSpeed(1f);
		fastforwardButton.setChecked(false);

		gameWorld.setDisplayTower(null);
	}

	public void hideBuyTowerDialog() {
		gameWorld.stopBuyingTower();
		buyTowerSelectionTable.setVisible(false);
		fastforwardButton.setVisible(true);
		pauseButton.setVisible(true);
		waveLabel.setVisible(true);
		waveDescriptionLabel.setVisible(true);

		setSpeed(1f);
		fastforwardButton.setChecked(false);
	}

	private void toggleFastForward() {
		setSpeed(fastforwardButton.isChecked() ? FAST_FORWARD_SPEED : 1f);
	}

	private void createUI() {

		// separation between paused and normal mode
		final Stack mainUiStack = new Stack();
		mainUiStack.setFillParent(true);

		// towers
		{

			final TowerBuilder turretTowerBuilder = new TowerBuilder(Assets.TURRET_ICON, Assets.TURRET_ICON_SMALL, Assets.TURRET_ICON_SMALL_SELECTED,
					"Turret", "Medium range fast\nshooting tower.", 35, 90, 200, 1.5f) {
				public TurretTower createTower(float x, float y) {
					return new TurretTower(x, y, name, iconPath, range, cooldown, damage, gameWorld);

				}

				@Override
				public void additionalDescription(Table info, LabelStyle infoLabelStyle) {
					super.additionalDescription(info, infoLabelStyle);

					final LabelStyle bonusLabelStyle = new LabelStyle(infoLabelStyle);
					bonusLabelStyle.fontColor = Assets.COLOR_GREEN;

					info.add(new Label("Medium armor: +" + (int) (TurretTower.mediumArmorBonus() * 100) + "%.", bonusLabelStyle)).left();
					info.row();
				}
			};

			final TowerBuilder rocketTowerBuilder = new TowerBuilder(Assets.ROCKET_ICON, Assets.ROCKET_ICON_SMALL, Assets.ROCKET_ICON_SMALL_SELECTED, "Rocket Tower",
					"Medium range\nhigh damage tower.\nCan hit air enemies.",
					125,
					100, 500, 3f) {
				public RocketTower createTower(float x, float y) {
					return new RocketTower(x, y, name, iconPath, range, cooldown, damage, gameWorld);
				}

				@Override
				public void additionalDescription(Table info, LabelStyle infoLabelStyle) {
					super.additionalDescription(info, infoLabelStyle);

					final LabelStyle bonusLabelStyle = new LabelStyle(infoLabelStyle);
					bonusLabelStyle.fontColor = Assets.COLOR_GREEN;

					info.add(new Label("Medium armor: +" + (int) (RocketTower.mediumArmorBonus() * 100) + "%.", bonusLabelStyle)).left();
					info.row();
					info.add(new Label("Air enemy: +300%", bonusLabelStyle)).left();
					info.row();
				}
			};
			final TowerBuilder fireTowerBuilder = new TowerBuilder(Assets.FIRE_ICON, Assets.FIRE_ICON_SMALL, Assets.FIRE_ICON_SMALL_SELECTED, "Fire Tower",
					"Burns all targets\nin range.", 8, 100, 500, 0) {
				public AbstractTower createTower(float x, float y) {
					return new FireTower(x, y, name, iconPath, range, cooldown, damage, gameWorld);
				}

				@Override
				public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle sizeLabelStyle, LabelStyle costLabelStyle) {
					final LabelStyle valueLabelStyle = new LabelStyle(infoLabelStyle);
					valueLabelStyle.fontColor = Assets.COLOR_YELLOW;

					final Label costDescriptionLabel = new Label("Cost: " + (int) cost, costLabelStyle);
					final Label damageDescriptionLabel = new Label("DPS: ", infoLabelStyle);
					final Label damageLabel = new Label(String.valueOf((int) damage), valueLabelStyle);
					final Label rangeDescriptionLabel = new Label("Range: ", infoLabelStyle);
					final Label rangeLabel = new Label(String.valueOf((int) range), valueLabelStyle);

					info.row();
					info.add(costDescriptionLabel).left();
					info.row();

					final Table damageTable = new Table();
					damageTable.add(damageDescriptionLabel).left();
					damageTable.add(damageLabel).left();
					info.add(damageTable).expandX().left();
					info.row();

					final Table rangeTable = new Table();
					rangeTable.add(rangeDescriptionLabel).left();
					rangeTable.add(rangeLabel).left();
					info.add(rangeTable).expandX().left();
					info.row();

				}
			};

			final TowerBuilder slowTowerBuilder = new SlowTowerBuilder(Assets.SLOW_ICON, Assets.SLOW_ICON_SMALL, Assets.SLOW_ICON_SMALL_SELECTED, "Slow Tower",
					"Slows all targets\nin range.", 0, 95, 500, 1.0f, 0.75f, 1.75f, gameWorld);

			final TowerBuilder artileryTowerBuilder = new TowerBuilder(Assets.ARTILLERY_ICON, Assets.ARTILLERY_ICON_SMALL, Assets.ARTILLERY_ICON_SMALL_SELECTED, "Artillery",
					"Long range tower\nwith area damage.", 40, 250, 500, 3f) {
				public ArtilleryTower createTower(float x, float y) {
					return new ArtilleryTower(x, y, name, iconPath, range, cooldown, damage, gameWorld);
				}

				@Override
				public void additionalDescription(Table info, LabelStyle infoLabelStyle) {
					super.additionalDescription(info, infoLabelStyle);

					final LabelStyle bonusLabelStyle = new LabelStyle(infoLabelStyle);
					bonusLabelStyle.fontColor = Assets.COLOR_GREEN;

					info.add(new Label("Heavy armor: +" + (int) (ArtilleryTower.heavyDamageBonus() * 100) + "%.", bonusLabelStyle)).left();
					info.row();
				}
			};

			final TowerBuilder teslaTowerBuilder = new TowerBuilder(Assets.TESLA_ICON, Assets.TESLA_ICON_SMALL, Assets.TESLA_ICON_SMALL_SELECTED, "Tesla Tower",
					"Lightning tower.\nStuns enemies hit\nfor a short time.", 175, 160, 500, 4f) {
				public TeslaTower createTower(float x, float y) {
					return new TeslaTower(x, y, name, iconPath, range, cooldown, damage, gameWorld);
				}
			};

			buyTowerSelectionTable = new Table();
			buyTowerSelectionTable.setVisible(false);

			towerListWidget = new HorizontalTowersWidget(this, turretTowerBuilder, rocketTowerBuilder, slowTowerBuilder, fireTowerBuilder,
					artileryTowerBuilder, teslaTowerBuilder);

			buyTowerSelectionTable.add(towerListWidget).expand().bottom().minWidth(Value.percentWidth(1));

			mainUiStack.add(buyTowerSelectionTable);
		}

		// create game screen
		{
			gameStack = new Stack();
			gameStack.setFillParent(true);

			endScreenTable = new Table();
			endScreenTable.setFillParent(true);
			endScreenTable.setVisible(false);

			finishLabelStyle = new LabelStyle();
			finishLabelStyle.font = Assets.TITLE_FONT_64;
			finishLabel = new Label("", finishLabelStyle);

			endScreenTable.add(finishLabel).expandX().top().padTop(20).colspan(4);
			endScreenTable.row();

			final ButtonStyle exitButtonStyle = new ButtonStyle();
			exitButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exittotitlebutton.png")))));
			exitButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exittotitlebuttonpressed.png")))));
			Button exitToTitleButton = new Button(exitButtonStyle);

			exitToTitleButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					heavyDefenseGame.switchToMenu();
				}

			});

			final Table scoreTable = new Table();
			scoreTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));

			final LabelStyle endScoreLabelStyle = new LabelStyle();
			endScoreLabelStyle.font = Assets.TEXT_FONT;
			endScoreLabelStyle.fontColor = Color.BLACK;

			final LabelStyle endScoreValueLabelStyle = new LabelStyle();
			endScoreValueLabelStyle.font = Assets.TEXT_FONT;
			endScoreValueLabelStyle.fontColor = Assets.COLOR_YELLOW;
			//			endScoreValueLabelStyle.fontColor = new Color(160 / 255f, 155 / 255f, 16 / 255f, 1);

			endScoreLabel = new Label("", endScoreValueLabelStyle);
			levelDifficultyBonusLabel = new Label("", endScoreValueLabelStyle);
			mapOfTheDayBonusLabel = new Label("", endScoreValueLabelStyle);
			remainingLivesBonusLabel = new Label("", endScoreValueLabelStyle);
			victoryBonusLabel = new Label("", endScoreValueLabelStyle);
			finalScoreLabel = new Label("", endScoreValueLabelStyle);

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Score:", endScoreLabelStyle)).left();
			scoreTable.add(endScoreLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Map of the Day Bonus:", endScoreLabelStyle)).left();
			scoreTable.add(mapOfTheDayBonusLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Level Difficulty Bonus:", endScoreLabelStyle)).left();
			scoreTable.add(levelDifficultyBonusLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Remaining Lives Bonus:", endScoreLabelStyle)).left();
			scoreTable.add(remainingLivesBonusLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Victory Bonus:", endScoreLabelStyle)).left();
			scoreTable.add(victoryBonusLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			scoreTable.add().expandX();
			scoreTable.add(new LeftBorderLabel("Final Score:", endScoreLabelStyle)).left();
			scoreTable.add(finalScoreLabel).left().padLeft(3);
			scoreTable.add().expandX();
			scoreTable.row();

			endScreenTable.add(scoreTable).expand().center();
			endScreenTable.row();

			endScreenTable.add(exitToTitleButton).colspan(4).expand().bottom().padBottom(3);

			gameStack.add(endScreenTable);

			final TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.BACKGROUND_SMALL)));

			// active when the game is in progress
			final Table defaultScreen = new Table();
			defaultScreen.setFillParent(true);

			// info labels
			final LabelStyle infoLabelsStyle = new LabelStyle();
			infoLabelsStyle.font = Assets.TEXT_FONT;
			infoLabelsStyle.fontColor = Color.BLACK;

			final LabelStyle scoreLabelStyle = new LabelStyle();
			scoreLabelStyle.font = Assets.TEXT_FONT;
			scoreLabelStyle.fontColor = Assets.COLOR_YELLOW;

			final LabelStyle liveLabelStyle = new LabelStyle();
			liveLabelStyle.font = Assets.TEXT_FONT;
			liveLabelStyle.fontColor = Assets.COLOR_GREEN;

			final Table statsTable = new Table();
			statsTable.setBackground(background);

			// score
			statsTable.add(new Label("Score: ", infoLabelsStyle)).left().padLeft(10).expandX();
			final Label scoreLabel = new Label(numberSepeartor((int) gameWorld.getPlayer().getScore()), scoreLabelStyle);
			statsTable.add(scoreLabel).left().align(BaseTableLayout.RIGHT);

			// money
			statsTable.add(new Label("Money: ", infoLabelsStyle)).left().padLeft(10).expandX();
			final Label resourcesLabel = new Label(numberSepeartor((int) gameWorld.getPlayer().getMoney()), scoreLabelStyle);
			statsTable.add(resourcesLabel).left().align(BaseTableLayout.RIGHT);

			// lives
			statsTable.add(new Label("Lives: ", infoLabelsStyle)).left().padLeft(10).expandX();
			final Label livesLabel = new Label(numberSepeartor(gameWorld.getPlayer().getLives()), liveLabelStyle);
			statsTable.add(livesLabel).left().align(BaseTableLayout.RIGHT);

			defaultScreen.add(statsTable).top().center().expandX().colspan(4);
			defaultScreen.row();

			defaultScreen.add().expand().colspan(3);
			defaultScreen.row();

			gameWorld.getPlayer().addStatChangedListener(new StatChangedListener() {

				@Override
				public void statsChanged(Player player, String stat) {
					scoreLabel.setText(numberSepeartor((int) player.getScore()));
					resourcesLabel.setText(numberSepeartor((int) player.getMoney()));
					livesLabel.setText(numberSepeartor(player.getLives()));
				}
			});

			// achievements
			achievementTable = new Table();
			achievementTable.setVisible(false);
			achievementTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));

			final Image achievementIcon = new Image(new TextureDrawable(Assets.getTexture(Assets.ACHIEVEMENT_UNLOCKED_ICON)));
			achievementTable.add(achievementIcon).left();

			final Table achievementInfo = new Table();
			achievementInfo.setFillParent(true);

			final LabelStyle achievementTitleStyle = new LabelStyle();
			achievementTitleStyle.font = Assets.TEXT_FONT;
			achievementTitleStyle.fontColor = Color.BLACK;
			achievementTitle = new Label("", achievementTitleStyle);
			achievementInfo.add(achievementTitle).left().expandX();
			achievementInfo.row();

			final LabelStyle achievementDescriptionStyle = new LabelStyle();
			achievementDescriptionStyle.font = Assets.TEXT_FONT;
			achievementDescriptionStyle.fontColor = Color.BLACK;
			achievementDescriptionLabel = new Label("", achievementTitleStyle);
			achievementDescriptionLabel.setWrap(true);
			achievementInfo.add(achievementDescriptionLabel).left().expandX().minWidth(Value.percentWidth(0.75f));

			achievementTable.add(achievementInfo).left();

			defaultScreen.add(achievementTable).expandX().colspan(3).minWidth(Value.percentWidth(0.8f));
			defaultScreen.row();

			final Table pauseFastForwardTable = new Table();

			// paused button
			final TextureRegionDrawable pauseUp = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.PAUSE_BUTTON_UP)));
			final TextureRegionDrawable pauseDown = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.PAUSE_BUTTON_DOWN)));
			pauseButton = new Button(pauseUp, pauseDown);
			pauseButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					setPaused(true);
				}
			});
			pauseFastForwardTable.add(pauseButton).left();

			// fastforward button
			final ButtonStyle fastForwardButtonStyle = new ButtonStyle();
			fastForwardButtonStyle.up = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.FAST_FORWARD_UP)));
			fastForwardButtonStyle.down = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.FAST_FORWARD_DOWN)));
			fastForwardButtonStyle.checked = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.FAST_FORWARD_CHECKED)));

			fastforwardButton = new Button(fastForwardButtonStyle);
			fastforwardButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					toggleFastForward();
				}

			});

			pauseFastForwardTable.add(fastforwardButton).left();

			defaultScreen.add(pauseFastForwardTable).expandX().left();

			// wave
			final LabelStyle waveStyle = new LabelStyle();
			waveStyle.font = Assets.TEXT_FONT;
			waveStyle.fontColor = Color.BLACK;

			waveDescriptionLabel = new Label("Wave: ", waveStyle);
			defaultScreen.add(waveDescriptionLabel).expandX().right();
			waveLabel = new Label(String.valueOf(gameWorld.getWaveSpawner().getWave()) + "/" + String.valueOf(gameWorld.getWaveSpawner().getMaxWaves()),
					waveStyle);
			defaultScreen.add(waveLabel).right().padRight(5);
			gameWorld.getWaveSpawner().addWaveListener(new WaveListener() {

				@Override
				public void nextWave(int wave) {
					waveLabel.setText(String.valueOf(wave) + "/" + String.valueOf(gameWorld.getWaveSpawner().getMaxWaves()));
				}
			});

			gameStack.add(defaultScreen);

			// tower info
			towerInfoTable = new Table();
			towerInfoTable.setVisible(false);
			towerInfoTable.setFillParent(true);

			towerInfoWidget = new TowerWidget();
			towerInfoWidget.addSellClickListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					AbstractTower currentTower = towerInfoWidget.getCurrentTower();
					gameWorld.removeTower(currentTower);
					gameWorld.getPlayer().increaseMoney(currentTower.getSellValue());

					hideTowerInfoDialog();

				}
			});

			towerInfoWidget.addUpgradeClickListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					final AbstractTower currentTower = towerInfoWidget.getCurrentTower();

					currentTower.upgrade();
					gameWorld.getPlayer().increaseMoney(-currentTower.getUpgradeCost());

					if (currentTower.getLevel() == 4) {
						TD.unlockAchievement(AchievementType.LEVEL_4);
					}

					towerInfoWidget.refresh();
				}
			});

			towerInfoTable.add(towerInfoWidget);

			gameStack.add(towerInfoTable);

			// create initial tooltip if needed
			if (TD.getPlayed() == 1 && TD.getGamesPlayed() == 1) {
				setPaused(true, false);
				isShowingInitialTooltip = true;

				fastforwardButton.setDisabled(true);
				pauseButton.setDisabled(true);

				final Table startInfoTable = new Table();
				startInfoTable.setFillParent(true);
				startInfoTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));

				final LabelStyle infoTableLabelStyle = new LabelStyle();
				infoTableLabelStyle.font = Assets.TEXT_FONT;
				infoTableLabelStyle.fontColor = Color.BLACK;

				final Label infoLabel = new Label(
						INITIAL_TOOLTIP,
						infoTableLabelStyle);
				infoLabel.setAlignment(Align.center);
				infoLabel.setWrap(true);

				startInfoTable.add().expand();
				startInfoTable.row();

				startInfoTable.add(infoLabel).expandX().center().minWidth(Value.percentWidth(0.95f));
				startInfoTable.row();

				ButtonStyle continueButtonStyle = new ButtonStyle();
				continueButtonStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/continuebuttonsmall.png")));
				continueButtonStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/continuebuttonsmallpressed.png")));

				final Button continueButton = new Button(continueButtonStyle);
				continueButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						startInfoTable.setVisible(false);
						isShowingInitialTooltip = false;

						fastforwardButton.setDisabled(false);
						pauseButton.setDisabled(false);

						setPaused(false);
					}
				});

				startInfoTable.add(continueButton).center().spaceTop(10).expandX();
				startInfoTable.row();

				startInfoTable.add().expand();
				startInfoTable.row();

				gameStack.add(startInfoTable);
			}

			mainUiStack.add(gameStack);
		}

		// create paused screen
		{
			pausedTable = new Table();
			pausedTable.setVisible(false);
			pausedTable.setFillParent(true);
			pausedTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));

			final LabelStyle infoLabelStyle = new LabelStyle();
			infoLabelStyle.font = Assets.TITLE_FONT_90;
			infoLabelStyle.fontColor = Color.BLACK;

			final Label pausedLabel = new Label("Paused", infoLabelStyle);
			pausedTable.add(pausedLabel).colspan(2).center().expand();

			pausedTable.row();

			final TextureRegionDrawable resumeUp = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.PLAY_BUTTON_UP)));
			final TextureRegionDrawable resumeDown = new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.PLAY_BUTTON_DOWN)));
			final Button resumeButton = new Button(resumeUp, resumeDown);
			resumeButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					setPaused(false);
				}
			});
			pausedTable.add(resumeButton).bottom().left();

			final ButtonStyle exitButtonStyle = new ButtonStyle();
			exitButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exittotitlebutton.png")))));
			exitButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exittotitlebuttonpressed.png")))));
			Button exitToTitleButton = new Button(exitButtonStyle);

			exitToTitleButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					heavyDefenseGame.switchToMenu();
				}

			});

			pausedTable.add(exitToTitleButton).bottom().right();

			mainUiStack.add(pausedTable);
		}

		ui.addActor(mainUiStack);

	}

	private void fadeInAchievement(Achievement achievement) {
		if (isShowingAchievement) return;

		achievementTitle.setText(achievement.getName() + " (+" + (int) achievement.getPoints() + " Points)");
		achievementDescriptionLabel.setText(achievement.getDescription());
		achievementTable.setVisible(true);
		achievementTable.getColor().a = 1;
		isShowingAchievement = true;

		final SequenceAction achievementAction = new SequenceAction();

		// fade the level in
		final AlphaAction fadeIn = new AlphaAction();
		fadeIn.setDuration(0.9f);
		fadeIn.setReverse(true);

		// wait
		final DelayAction delay = new DelayAction(4);

		// fade out
		final AlphaAction fadeOut = new AlphaAction();
		fadeOut.setDuration(0.9f);

		final RunnableAction doneAction = new RunnableAction() {
			@Override
			public void run() {
				isShowingAchievement = false;
				achievementTable.setVisible(false);
			}
		};

		achievementAction.addAction(fadeIn);
		achievementAction.addAction(delay);
		achievementAction.addAction(fadeOut);
		achievementAction.addAction(doneAction);

		achievementTable.addAction(achievementAction);
	}

	@Override
	public void hide() {
		super.hide();

		persistGame();

	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	/**
	 * Persists the current game state. This method will overwrite the last saved game state.
	 */
	public void persistGame() {
		final FileHandle saveGame = Gdx.files.local(Assets.CURRENT_SAVE_GAME_NAME);

		// persist if the game has not ended
		if (!ended) {
			referenceJSonSerializer.toJson(gameWorld, saveGame);
		} else {

			// delete old save game so when restarting the game it will not be possible to "continue".
			if (saveGame.exists()) {
				saveGame.delete();
			}
		}
	}

	@Override
	protected boolean handleBack() {
		return true;
	}

	@Override
	protected void onBack() {
		if (buyTowerSelectionTable.isVisible()) {
			hideBuyTowerDialog();
		} else if (towerInfoTable.isVisible()) {
			hideTowerInfoDialog();
		}
		//
		//		heavyDefenseGame.switchToMenu();
	}

	@Override
	public void dispose() {
		TD.removeAchievementListener(achievementListener);
	}

	@Override
	public void show() {
		super.show();

		TD.tracking.sendScreen("GameScreen");

		// -----------------------------------
		// FIX for done achievement which needed 20/20 achievements. If the user has already unlocked every achievement (except Done)
		// he will receive the done achievement the first time a GameScreen has been shown (meaning he started and entered a game)
		// -----------------------------------
		final Achievement doneAchievement = TD.getAchievements().get(AchievementType.DONE);

		// 19/20 achievements unlocked (everything except done)
		if (TD.getNumberOfFinishedAchievements() == AchievementType.values().length - 1) {
			doneAchievement.unlock();
		}
	}

	private void handleClick(final GameWorld gameWorld, float x, float y) {
		// set possible open dialog invisible
		if (buyTowerSelectionTable.isVisible()) {
			hideBuyTowerDialog();
			return;
		}
		if (towerInfoTable.isVisible()) {
			hideTowerInfoDialog();
			return;
		}

		final Actor hit = stage.hit(x, y, true);

		if (hit instanceof AbstractTower) {
			// show tower info
			showTowerInfoDialg((AbstractTower) hit);
		} else {
			// place tower
			final int tileWidth = HeavyDefenseGame.GRID_SIZE;
			final int tileHeight = HeavyDefenseGame.GRID_SIZE;
			final float xGrid = x - x % tileWidth;
			final float yGrid = y - y % tileHeight;
			final boolean canBuildTower = gameWorld.canBuildTower(xGrid, yGrid);

			if (canBuildTower) {
				gameWorld.setIsBuyingTower(xGrid, yGrid);

				showBuyTowerDialog();
			} else {
				gameWorld.showInvalidClick((int) xGrid, (int) yGrid);
			}
		}
	}

	private static class LeftBorderLabel extends Label {

		private static final int BORDER_SIZE = 2;

		public LeftBorderLabel(CharSequence text, LabelStyle style) {
			super(text, style);
		}

		@Override
		public float getX() {
			return super.getX() + BORDER_SIZE;
		}

	}

}
