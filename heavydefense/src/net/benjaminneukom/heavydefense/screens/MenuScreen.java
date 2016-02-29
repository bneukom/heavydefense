package net.benjaminneukom.heavydefense.screens;

import static net.benjaminneukom.heavydefense.HeavyDefenseGame.GRID_SIZE;
import static net.benjaminneukom.heavydefense.HeavyDefenseGame.TILE_SIZE;

import java.util.Calendar;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.HoldableButton;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.TD.ScoreListener;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.ArmorType;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner;
import net.benjaminneukom.heavydefense.game.EruptionSpawner;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.game.worlds.ClassicGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.ClassicLavaGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.GameMode;
import net.benjaminneukom.heavydefense.game.worlds.GameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsLavaGameWorld;
import net.benjaminneukom.heavydefense.serializers.DefaultReferenceStore;
import net.benjaminneukom.heavydefense.serializers.ReferenceJSonSerializer;
import net.benjaminneukom.heavydefense.system.Billing;
import net.benjaminneukom.heavydefense.tower.ArtilleryTower;
import net.benjaminneukom.heavydefense.tower.FireTower;
import net.benjaminneukom.heavydefense.tower.TurretTower;
import net.benjaminneukom.heavydefense.ui.towers.achievements.AchievementListWidget;
import net.benjaminneukom.heavydefense.ui.upgrade.UpgradesListWidget;
import net.benjaminneukom.heavydefense.util.Rand;
import net.benjaminneukom.heavydefense.util.StringUtil;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Value;

public class MenuScreen extends AbstractScreen {

	private int currentDifficulty;

	private BackgroundLevel backgroundLevel;

	private HeavyDefenseGame towerdefenseGame;
	private GameScreen gameScreen;
	private Table mainTable;
	private Stack menuStack;

	private Button newGameButton;
	private Button exitButton;
	private Button upgradesButton;
	private Button achievementsButton;
	private Button continueButton;
	private Label versionLabel;
	private Button creditsButton;
	private Button donateButton;

	private Table upgradesTable;

	private Table difficultySelectionTable;
	private Label difficultyLabel;
	private Label activeGameLabelWarningDifficulty;
	private Label activeGameLabelWarningLevel;

	private Table levelSelectionTable;
	private Label gameTitleLabel;
	private Label levelDifficultyLabel;
	private Label difficultyPointGainLabel;
	private Label levelModeLabel;
	private LabelStyle levelDifficultyStyle;

	private Table achievementsTable;
	private AchievementListWidget achievementsListWidget;

	private Table creditsTable;

	private Table donationTable;

	private Label levelOfTheDayLabel;
	private int levelIndex = 0;
	private Level[] levels;
	private Label levelLabel;

	public static final String DIFFICULTY_KEY = "difficulty";

	public MenuScreen(final HeavyDefenseGame towerdefenseGame) {

		this.towerdefenseGame = towerdefenseGame;

		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		this.currentDifficulty = preferences.contains(DIFFICULTY_KEY) ? preferences.getInteger(DIFFICULTY_KEY) : 0;

		this.levels = new Level[] {
				new SnowLevel("Snow", "levels/snow.tmx", new SnowBackground(stageCamera), Difficulty.EASY),
				new WoodsLevel("Woods", "levels/woods.tmx", new WoodsBackground(stageCamera), Difficulty.EASY),
				new DesertLevel("Desert", "levels/desert.tmx", new DesertBackground(stageCamera), Difficulty.MEDIUM),
				new BeachLevel("Beach", "levels/beach.tmx", new BeachBackground(stageCamera), Difficulty.MEDIUM),
				new GrassLevel("Grass", "levels/grass.tmx", new GrassBackground(stageCamera), Difficulty.MEDIUM),
				new LavalLevel("Lava", "levels/lava.tmx", new LavaBackground(stageCamera), Difficulty.HARD),
				new VulcanoLevel("Vulcano", "levels/vulcano.tmx", new VulcanoBackground(stageCamera), Difficulty.HARD),
		};

		// set map of the day
		final int mapOfDayIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % levels.length;
		levels[mapOfDayIndex].setMapOfTheDay(true);

		levelIndex = mapOfDayIndex;

		menuStack = new Stack();
		menuStack.setFillParent(true);

		mainTable = new Table();
		mainTable.setFillParent(true);

		menuStack.add(mainTable);

		// main menu
		{
			final LabelStyle gameTitleLabelStyle = new LabelStyle();
			gameTitleLabelStyle.font = Assets.TITLE_FONT_64;
			gameTitleLabelStyle.fontColor = Color.BLACK;
			gameTitleLabelStyle.background = new TextureDrawable(Assets.getTexture(Assets.BACKGROUND));

			gameTitleLabel = new Label("Towerdefense", gameTitleLabelStyle);
			gameTitleLabel.setAlignment(Align.center);

			final TextButtonStyle style = new TextButtonStyle();
			style.font = Assets.TITLE_FONT_38;
			style.overFontColor = Color.WHITE;
			style.downFontColor = Color.WHITE;
			style.fontColor = Color.BLACK;
			style.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("background.png"))));

			final ButtonStyle continueButtonPressed = new ButtonStyle();
			continueButtonPressed.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/continuebutton.png"))));
			continueButtonPressed.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/continuebuttonpressed.png"))));

			continueButton = new Button(continueButtonPressed);
			continueButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					gameScreen.setPaused(true);
					towerdefenseGame.setScreen(gameScreen);
				}
			});

			final ButtonStyle newGameButtonStyle = new ButtonStyle();
			newGameButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/newgamebutton.png"))));
			newGameButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/newgamebuttonpressed.png"))));

			newGameButton = new Button(newGameButtonStyle);
			newGameButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {

					showDifficultySelection();
				}
			});

			final ButtonStyle upgradeButtonStyle = new ButtonStyle();
			upgradeButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/upgradesbutton.png"))));
			upgradeButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/upgradesbuttonpressed.png"))));

			upgradesButton = new Button(upgradeButtonStyle);
			upgradesButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					upgradesTable.setVisible(true);
					setMainMenuVisible(false);
					TD.tracking.sendScreen("UpgradesScreen");
				}
			});

			final ButtonStyle achievementsButtonStyle = new ButtonStyle();
			achievementsButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/achievementsbutton.png"))));
			achievementsButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/achievementsbuttonpressed.png"))));

			achievementsButton = new Button(achievementsButtonStyle);
			achievementsButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					achievementsTable.setVisible(true);
					achievementsListWidget.update();
					setMainMenuVisible(false);
					TD.tracking.sendScreen("AchievementsScreen");
				}
			});

			final ButtonStyle exitButtonStyle = new ButtonStyle();
			exitButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exitbutton.png"))));
			exitButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/exitbuttonpressed.png"))));

			exitButton = new Button(exitButtonStyle);
			exitButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					Gdx.app.exit();
				}
			});

			final LabelStyle versionLabelStyle = new LabelStyle();
			versionLabelStyle.font = Assets.TEXT_FONT;
			versionLabelStyle.fontColor = Color.BLACK;

			// TODO create plattform specific version
			versionLabel = new Label("Version " + TD.appVersion, versionLabelStyle);

			final ButtonStyle creditsButtonStyle = new ButtonStyle();
			creditsButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/creditsbutton.png"))));
			creditsButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/creditsbuttonpressed.png"))));

			creditsButton = new Button(creditsButtonStyle);
			creditsButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					creditsTable.setVisible(true);
					setMainMenuVisible(false);
					TD.tracking.sendScreen("CreditsScreen");

				}
			});

			final ButtonStyle donateButtonStyle = new ButtonStyle();
			donateButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/donatebutton.png"))));
			donateButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/donatebuttonpressed.png"))));
			donateButton = new Button(donateButtonStyle);
			donateButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					donationTable.setVisible(true);
					setMainMenuVisible(false);
					TD.tracking.sendScreen("DonateScreen");
				}
			});

		}

		// upgrade
		{
			upgradesTable = new Table();
			upgradesTable.setFillParent(true);
			upgradesTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			upgradesTable.setVisible(false);

			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					showMenu();
					TD.persistUpgrades();
				}
			});

			final LabelStyle scoreTextLabelStyle = new LabelStyle();
			scoreTextLabelStyle.font = Assets.TEXT_FONT;
			scoreTextLabelStyle.fontColor = Color.BLACK;

			final LabelStyle scoreLabelStyle = new LabelStyle();
			scoreLabelStyle.font = Assets.TEXT_FONT;
			scoreLabelStyle.fontColor = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);

			final Label scoreLabel = new Label(StringUtil.numberSepeartor((int) TD.getScore()), scoreLabelStyle);

			TD.addScoreChangedListener(new ScoreListener() {

				@Override
				public void scoreChanged(float newScore) {
					scoreLabel.setText(StringUtil.numberSepeartor((int) newScore));
				}

			});

			final Table scoreTable = new Table();

			scoreTable.add(scoreLabel).left().top();
			scoreTable.add(new Label(" Points to spend.", scoreTextLabelStyle)).left().top();

			upgradesTable.add(scoreTable).expandX();
			upgradesTable.row();

			final UpgradesListWidget upgradesListWidget = new UpgradesListWidget(TD.getUpgrades());

			final ScrollPaneStyle scrollPaneStyle = new ScrollPaneStyle();
			scrollPaneStyle.vScroll = new TextureDrawable(Assets.getTexture(Assets.V_SCROLL));
			scrollPaneStyle.vScrollKnob = new TextureDrawable(Assets.getTexture(Assets.V_SCROLL_KNOB));
			final ScrollPane upgradesScrollPane = new ScrollPane(upgradesListWidget, scrollPaneStyle);
			upgradesScrollPane.setFadeScrollBars(false);
			upgradesScrollPane.setScrollingDisabled(true, false);

			upgradesTable.add(upgradesScrollPane).expandX().center().minWidth(Value.percentWidth(1.0f));
			upgradesTable.row();

			upgradesTable.add(backButton).expandX().bottom().left();

			menuStack.add(upgradesTable);
		}

		// achievements
		{
			achievementsTable = new Table();
			achievementsTable.setFillParent(true);
			achievementsTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			achievementsTable.setVisible(false);

			final LabelStyle scoreTextLabelStyle = new LabelStyle();
			scoreTextLabelStyle.font = Assets.TEXT_FONT;
			scoreTextLabelStyle.fontColor = Color.BLACK;

			final LabelStyle scoreLabelStyle = new LabelStyle();
			scoreLabelStyle.font = Assets.TEXT_FONT;
			scoreLabelStyle.fontColor = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);

			final Label scoreLabel = new Label(StringUtil.numberSepeartor((int) TD.getTotalScore()), scoreLabelStyle);

			TD.addScoreChangedListener(new ScoreListener() {

				@Override
				public void totalScoreChanged(float newScore) {
					scoreLabel.setText(StringUtil.numberSepeartor((int) newScore));
				}

			});

			final Table scoreTable = new Table();

			scoreTable.add(new Label("Lifetime points: ", scoreTextLabelStyle)).left().top();
			scoreTable.add(scoreLabel).left().top();

			achievementsTable.add(scoreTable).expandX();
			achievementsTable.row();

			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					showMenu();
					TD.persistAchievements();
				}
			});

			achievementsListWidget = new AchievementListWidget(TD.getAchievementCollection());

			final ScrollPaneStyle scrollPaneStyle = new ScrollPaneStyle();
			scrollPaneStyle.vScroll = new TextureDrawable(Assets.getTexture(Assets.V_SCROLL));
			scrollPaneStyle.vScrollKnob = new TextureDrawable(Assets.getTexture(Assets.V_SCROLL_KNOB));
			final ScrollPane achievementsScrollPane = new ScrollPane(achievementsListWidget, scrollPaneStyle);
			achievementsScrollPane.setFadeScrollBars(false);
			achievementsScrollPane.setScrollingDisabled(true, false);

			achievementsTable.add(achievementsScrollPane).expandX().center().minWidth(Value.percentWidth(1.0f));
			achievementsTable.row();

			achievementsTable.add(backButton).expandX().bottom().left();

			menuStack.add(achievementsTable);
		}

		// difficulty selection
		{
			difficultySelectionTable = new Table();
			difficultySelectionTable.setFillParent(true);
			difficultySelectionTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			difficultySelectionTable.setVisible(false);

			// Title
			final LabelStyle difficultyTitleLabelStyle = new LabelStyle();
			difficultyTitleLabelStyle.fontColor = Color.BLACK;
			difficultyTitleLabelStyle.font = Assets.TITLE_FONT_64;
			final Label difficultyTitleLabel = new Label("Difficulty", difficultyTitleLabelStyle);

			difficultySelectionTable.add(difficultyTitleLabel).top().expandX().colspan(2).spaceBottom(5);
			difficultySelectionTable.row();

			// difficulty selection with tooltip
			{
				final Table difficultyTable = new Table();

				final LabelStyle difficultyTooltipStyle = new LabelStyle();
				difficultyTooltipStyle.fontColor = Color.BLACK;
				difficultyTooltipStyle.font = Assets.TEXT_FONT;
				final Label difficultyTooltipLabel = new Label("Increased difficulty will increase enemy health, but also increases the amount of points you gain.",
						difficultyTooltipStyle);
				difficultyTooltipLabel.setWrap(true);
				difficultyTooltipLabel.setAlignment(Align.center);

				difficultyTable.add(difficultyTooltipLabel).expandX().center().minWidth(Value.percentWidth(0.9f));
				difficultyTable.row();

				final Table difficultyChooserTable = new Table();
				final ButtonStyle decreaseDifficultyStyle = new ButtonStyle();
				decreaseDifficultyStyle.up = new TextureDrawable(Assets.getTexture(Assets.DECREASE_BUTTON));
				decreaseDifficultyStyle.down = new TextureDrawable(Assets.getTexture(Assets.DECREASE_BUTTON_PRESSED));

				final Button decreaseDifficultyButton = new HoldableButton(decreaseDifficultyStyle);
				decreaseDifficultyButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						currentDifficulty--;
						currentDifficulty = Math.max(currentDifficulty, 0);
						difficultyLabel.setText(String.valueOf(currentDifficulty) + "/100");
					}

				});
				difficultyChooserTable.add(decreaseDifficultyButton).padRight(5);

				final LabelStyle difficultyLabelStyle = new LabelStyle();
				difficultyLabelStyle.fontColor = Color.BLACK;
				difficultyLabelStyle.font = Assets.TEXT_FONT;
				difficultyLabel = new Label(String.valueOf(currentDifficulty) + "/100", difficultyLabelStyle);
				difficultyLabel.setAlignment(Align.center);
				difficultyChooserTable.add(difficultyLabel).minWidth(75).top().padTop(4);

				final ButtonStyle increaseDifficultyStyle = new ButtonStyle();
				increaseDifficultyStyle.up = new TextureDrawable(Assets.getTexture(Assets.INCREASE_BUTTON));
				increaseDifficultyStyle.down = new TextureDrawable(Assets.getTexture(Assets.INCREASE_BUTTON_PRESSED));

				final Button increaseDifficultyButton = new HoldableButton(increaseDifficultyStyle);
				increaseDifficultyButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						currentDifficulty++;
						currentDifficulty = Math.min(currentDifficulty, 100);
						difficultyLabel.setText(String.valueOf(currentDifficulty) + "/100");
					}

				});
				difficultyChooserTable.add(increaseDifficultyButton).padLeft(5);
				difficultyTable.add(difficultyChooserTable);

				difficultySelectionTable.add(difficultyTable).expandX().center().colspan(2).minWidth(Value.percentWidth(1.0f));
				difficultySelectionTable.row();
			}

			final LabelStyle activeGameWarningStyle = new LabelStyle();
			activeGameWarningStyle.fontColor = Assets.COLOR_RED;
			activeGameWarningStyle.font = Assets.TEXT_FONT;
			activeGameLabelWarningDifficulty = new Label("current game will be discarded if you start a new game!", activeGameWarningStyle);
			activeGameLabelWarningDifficulty.setWrap(true);
			activeGameLabelWarningDifficulty.setAlignment(Align.center);

			difficultySelectionTable.add(activeGameLabelWarningDifficulty).expand().bottom().minWidth(Value.percentWidth(0.95f)).colspan(2).spaceBottom(5);
			difficultySelectionTable.row();

			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showMenu();

					persistDifficulty();
				}

			});

			difficultySelectionTable.add(backButton).expandX().bottom().left();

			final ButtonStyle selecttLevelButtonStyle = new ButtonStyle();
			selecttLevelButtonStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/selectlevelbutton.png")));
			selecttLevelButtonStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/selectlevelbuttonpressed.png")));

			final Button selectLevelButton = new Button(selecttLevelButtonStyle);
			selectLevelButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showLevelSelection();

					persistDifficulty();
				}
			});

			difficultySelectionTable.add(selectLevelButton).bottom().right();

			menuStack.add(difficultySelectionTable);

		}

		// level selection
		{
			levelSelectionTable = new Table();
			levelSelectionTable.setFillParent(true);
			levelSelectionTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			levelSelectionTable.setVisible(false);

			final LabelStyle selectLevelStyle = new LabelStyle();
			selectLevelStyle.fontColor = Color.BLACK;
			selectLevelStyle.font = Assets.TITLE_FONT_64;
			final Label selectLevelLabel = new Label("Select Level", selectLevelStyle);

			levelSelectionTable.add(selectLevelLabel).top().expandX().colspan(2);
			levelSelectionTable.row();

			{
				final Table difficultyChooserTable = new Table();
				final ButtonStyle previousLevelButtonStyle = new ButtonStyle();
				previousLevelButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.DECREASE_BUTTON));
				previousLevelButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.DECREASE_BUTTON_PRESSED));

				final Button previousLevelButton = new Button(previousLevelButtonStyle);
				previousLevelButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						levelIndex--;
						if (levelIndex < 0) {
							levelIndex = levels.length - 1;
						}

						updateBackgroundLevel();
					}

				});
				difficultyChooserTable.add(previousLevelButton).padRight(5);

				final LabelStyle levelLabelStyle = new LabelStyle();
				levelLabelStyle.fontColor = Color.BLACK;
				levelLabelStyle.font = Assets.TEXT_FONT;
				levelLabel = new Label(levels[0].getName(), levelLabelStyle);
				levelLabel.setAlignment(Align.center);
				difficultyChooserTable.add(levelLabel).minWidth(160).top().padTop(4);

				final ButtonStyle nextLevelButtonStyle = new ButtonStyle();
				nextLevelButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.INCREASE_BUTTON));
				nextLevelButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.INCREASE_BUTTON_PRESSED));

				final Button nextLevelButton = new Button(nextLevelButtonStyle);
				nextLevelButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						levelIndex++;
						if (levelIndex == levels.length) {
							levelIndex = 0;
						}

						updateBackgroundLevel();
					}

				});
				difficultyChooserTable.add(nextLevelButton).padLeft(5);

				levelSelectionTable.add(difficultyChooserTable).expandX().center().colspan(2).minWidth(Value.percentWidth(1.0f)).spaceTop(5f);
				levelSelectionTable.row();
			}

			// difficulty indicator
			final Table levelDifficultyTable = new Table();

			final LabelStyle defaultTextStyle = new LabelStyle();
			defaultTextStyle.fontColor = Color.BLACK;
			defaultTextStyle.font = Assets.TEXT_FONT;

			levelDifficultyStyle = new LabelStyle();
			levelDifficultyStyle.fontColor = Assets.COLOR_YELLOW;
			levelDifficultyStyle.font = Assets.TEXT_FONT;

			levelDifficultyTable.add(new Label("Difficulty: ", defaultTextStyle));
			levelDifficultyLabel = new Label("Medium", levelDifficultyStyle);
			levelDifficultyTable.add(levelDifficultyLabel);
			difficultyPointGainLabel = new Label(" (+0% Points gain)", defaultTextStyle);
			levelDifficultyTable.add(difficultyPointGainLabel);

			levelSelectionTable.add(levelDifficultyTable).center().top().expandX().colspan(2).spaceTop(5f);
			levelSelectionTable.row();

			final Table modeTable = new Table();

			final LabelStyle modeTextStyle = new LabelStyle();
			modeTextStyle.fontColor = Assets.COLOR_YELLOW;
			modeTextStyle.font = Assets.TEXT_FONT;
			levelModeLabel = new Label("XXX", modeTextStyle);

			modeTable.add(new Label("Mode: ", defaultTextStyle)).center();
			modeTable.add(levelModeLabel).center();

			levelSelectionTable.add(modeTable).center().top().expandX().colspan(2).spaceTop(5f);
			levelSelectionTable.row();

			// level of the day
			final LabelStyle levelOfTheDayStyle = new LabelStyle();
			levelOfTheDayStyle.fontColor = Assets.COLOR_GREEN;
			levelOfTheDayStyle.font = Assets.TEXT_FONT;

			levelOfTheDayLabel = new Label("Level of the Day!", levelOfTheDayStyle);
			levelSelectionTable.add(levelOfTheDayLabel).center().top().expandX().colspan(2).spaceTop(5f);
			levelSelectionTable.row();

			// back button
			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showDifficultySelection();
				}
			});

			final LabelStyle activeGameWarningStyle = new LabelStyle();
			activeGameWarningStyle.fontColor = Assets.COLOR_RED;
			activeGameWarningStyle.font = Assets.TEXT_FONT;

			activeGameLabelWarningLevel = new Label("Current game will be discarded if you start a new game!", activeGameWarningStyle);
			activeGameLabelWarningLevel.setWrap(true);
			activeGameLabelWarningLevel.setAlignment(Align.center);

			levelSelectionTable.add(activeGameLabelWarningLevel).expand().bottom().minWidth(Value.percentWidth(0.95f)).colspan(2).spaceBottom(5);
			levelSelectionTable.row();

			levelSelectionTable.add(backButton).expandX().bottom().left();

			final ButtonStyle startButtonStyle = new ButtonStyle();
			startButtonStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/startbutton.png")));
			startButtonStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/startbuttonpressed.png")));
			final Button startButton = new Button(startButtonStyle);
			startButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showMenu();

					final Level level = levels[levelIndex];

					final GameWorld abstractWorld = level.createWorld();
					abstractWorld.setWaveSpawner(new WaveSpawner(abstractWorld, 5, 100, currentDifficulty));
					abstractWorld.setDifficulty(currentDifficulty);
					abstractWorld.setIsMapOfTheDay(level.isMapOfTheDay());
					abstractWorld.setLevelDifficulyt(level.getDifficulty());

					// dispose old resources
					if (gameScreen != null) {
						gameScreen.dispose();
					}

					gameScreen = new GameScreen(towerdefenseGame, abstractWorld);

					towerdefenseGame.setScreen(gameScreen);

				}
			});

			levelSelectionTable.add(startButton).bottom().right();

			menuStack.add(levelSelectionTable);

			updateBackgroundLevel();
		}

		// credits screen
		{
			creditsTable = new Table();
			creditsTable.setFillParent(true);
			creditsTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			creditsTable.setVisible(false);

			final LabelStyle creditsTitleStyle = new LabelStyle();
			creditsTitleStyle.fontColor = Color.BLACK;
			creditsTitleStyle.font = Assets.TITLE_FONT_64;

			final Label titleLabel = new Label("Credits", creditsTitleStyle);
			titleLabel.setAlignment(Align.center);

			creditsTable.add(titleLabel).expandX().center().top();
			creditsTable.row();

			//			final Image iconImage = new Image(new TextureDrawable(new Texture(Gdx.files.internal("icon.png"))), Scaling.none);
			//			creditsTable.add(iconImage).expand().center().top();
			//			creditsTable.row();

			final LabelStyle textStyle = new LabelStyle();
			textStyle.fontColor = Color.BLACK;
			textStyle.font = Assets.TEXT_FONT;
			final Label creditsLabel = new Label("\"Hard Vacuum\" art by Daniel Cook:", textStyle);
			creditsLabel.setWrap(true);
			creditsLabel.setAlignment(Align.center);
			creditsTable.add(creditsLabel).expandX().center().minWidth(Value.percentWidth(0.9f));
			creditsTable.row();

			// link to lostgarden
			final ButtonStyle lostGardenButtonLinkStyle = new ButtonStyle();
			lostGardenButtonLinkStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/lostgardenlink.png")));
			lostGardenButtonLinkStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/lostgardenlinkpressed.png")));

			final Button lostGardenLinkButton = new Button(lostGardenButtonLinkStyle);
			lostGardenLinkButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					Gdx.net.openURI("http://www.lostgarden.com/");
				}
			});

			creditsTable.add(lostGardenLinkButton).expandX().center();
			creditsTable.row();

			final Label programmingLabel = new Label("My Website:", textStyle);
			programmingLabel.setWrap(true);
			programmingLabel.setAlignment(Align.center);
			creditsTable.add(programmingLabel).expandX().center().minWidth(Value.percentWidth(0.9f)).spaceTop(20);
			creditsTable.row();

			// link to my website
			final ButtonStyle heavydefenseLinkStyle = new ButtonStyle();
			heavydefenseLinkStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/websitelink.png")));
			heavydefenseLinkStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/websitelinkpressed.png")));

			final Button heavyDefenseLinkButton = new Button(heavydefenseLinkStyle);
			heavyDefenseLinkButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					Gdx.net.openURI("http://www.benjaminneukom.net");
				}
			});

			creditsTable.add(heavyDefenseLinkButton).expandX().center();
			creditsTable.row();

			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					showMenu();
				}
			});

			creditsTable.add(backButton).expand().bottom().left();

			menuStack.add(creditsTable);
		}

		// donation screen
		{
			donationTable = new Table();
			donationTable.setFillParent(true);
			donationTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));
			donationTable.setVisible(false);

			// title
			//			final LabelStyle donationTitle = new LabelStyle();
			//			donationTitle.fontColor = Color.BLACK;
			//			donationTitle.font = Assets.TITLE_FONT_64;
			//
			//			final Label titleLabel = new Label("Donation", donationTitle);
			//			titleLabel.setAlignment(Align.center);
			//
			//			donationTable.add(titleLabel).expandX().center().top().spaceBottom(5).colspan(2);
			//			donationTable.row();

			if (TD.billing.isAvailable()) {
				final LabelStyle descriptionLabelStyle = new LabelStyle();
				descriptionLabelStyle.fontColor = Color.BLACK;
				descriptionLabelStyle.font = Assets.TEXT_FONT;

				final Label descriptionLabel = new Label(
						"Thank you for considering a donation. If you like the game and want to support me please choose one of the given donations.",
						descriptionLabelStyle);
				descriptionLabel.setWrap(true);
				descriptionLabel.setAlignment(Align.center);

				donationTable.add(descriptionLabel).expandX().center().top().minWidth(Value.percentWidth(1f)).colspan(2);
				donationTable.row();

				// small
				{
					final ButtonStyle smallDonationStyle = new ButtonStyle();
					smallDonationStyle.up = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/smalldonationbutton.png"))));
					smallDonationStyle.down = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/smalldonationbuttonpressed.png"))));

					final Button smallDonationButton = new Button(smallDonationStyle);
					smallDonationButton.addListener(new ClickListener() {
						@Override
						public void clicked(final InputEvent event, final float x, final float y) {
							TD.billing.initiateDonate(Billing.DONATE_SMALL);
						}
					});

					final Table smallDonationTable = new Table();
					smallDonationTable.add(smallDonationButton).center();
					smallDonationTable.add(new Label(" Of 2$.", descriptionLabelStyle)).left();

					donationTable.add(smallDonationTable).expandX().center().spaceTop(20);
					donationTable.row();
				}

				// medium
				{
					final ButtonStyle mediumDonationStyle = new ButtonStyle();
					mediumDonationStyle.up = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/mediumdonationbutton.png"))));
					mediumDonationStyle.down = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/mediumdonationbuttonpressed.png"))));

					final Button mediumDonationButton = new Button(mediumDonationStyle);
					mediumDonationButton.addListener(new ClickListener() {
						@Override
						public void clicked(final InputEvent event, final float x, final float y) {
							TD.billing.initiateDonate(Billing.DONATE_MEDIUM);
						}
					});

					final Table mediumDonationTable = new Table();
					mediumDonationTable.add(mediumDonationButton).center();
					mediumDonationTable.add(new Label(" Of 5$.", descriptionLabelStyle)).left();

					donationTable.add(mediumDonationTable).expandX().center().spaceTop(10);
					donationTable.row();
				}

				// big
				{
					final ButtonStyle bigDonationStyle = new ButtonStyle();
					bigDonationStyle.up = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/bigdonationbutton.png"))));
					bigDonationStyle.down = new TextureDrawable(new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/bigdonationbuttonpressed.png"))));

					final Button bigDonationButton = new Button(bigDonationStyle);
					bigDonationButton.addListener(new ClickListener() {
						@Override
						public void clicked(final InputEvent event, final float x, final float y) {
							TD.billing.initiateDonate(Billing.DONATE_BIG);
						}
					});

					final Table bigDonationTable = new Table();
					bigDonationTable.add(bigDonationButton).center();
					bigDonationTable.add(new Label(" Of 10$.", descriptionLabelStyle)).left();

					donationTable.add(bigDonationTable).expandX().center().spaceTop(10);
					donationTable.row();

				}
			} else {
				// billing not running (probably old google play version)
				final LabelStyle messageLabelStyle = new LabelStyle();
				messageLabelStyle.fontColor = Assets.COLOR_RED;
				messageLabelStyle.font = Assets.TEXT_FONT;

				final Label messageLabel = new Label(
						"It seems In-App donations don't work on your device :( Please visit my website for a Paypal donation if you'd like:",
						messageLabelStyle);
				messageLabel.setWrap(true);
				messageLabel.setAlignment(Align.center);

				donationTable.add(messageLabel).expand().bottom().minWidth(Value.percentWidth(1f)).colspan(2);
				donationTable.row();

				// link to my website
				final ButtonStyle heavydefenseLinkStyle = new ButtonStyle();
				heavydefenseLinkStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/websitelink.png")));
				heavydefenseLinkStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/websitelinkpressed.png")));

				final Button heavyDefenseLinkButton = new Button(heavydefenseLinkStyle);
				heavyDefenseLinkButton.addListener(new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						Gdx.net.openURI("http://benjaminneukom.net/?page_id=88");
					}
				});

				donationTable.add(heavyDefenseLinkButton).expandX().center();
				donationTable.row();

			}

			final ButtonStyle backButtonStyle = new ButtonStyle();
			backButtonStyle.up = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON));
			backButtonStyle.down = new TextureDrawable(Assets.getTexture(Assets.BACK_BUTTON_PRESSED));

			final Button backButton = new Button(backButtonStyle);
			backButton.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					showMenu();
				}
			});

			donationTable.add(backButton).expand().bottom().left();

			menuStack.add(donationTable);
		}

		ui.addActor(menuStack);
	}

	private void persistDifficulty() {
		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		preferences.putInteger(DIFFICULTY_KEY, currentDifficulty);
		preferences.flush();
	}

	private void updateBackgroundLevel() {
		stage.clear();

		final Level newLevel = levels[levelIndex];

		levelLabel.setText(newLevel.getName() + " (" + (levelIndex + 1) + "/" + levels.length + ")");
		backgroundLevel = newLevel.getBackgroundLevel();
		backgroundLevel.applyCamera(stageCamera);

		// fade the level in
		final AlphaAction alphaAction = new AlphaAction();
		alphaAction.setDuration(0.75f);
		alphaAction.setReverse(true);

		// Bugfix: Might be null if begin() has never been called and the action is already getting finished (0.75seconds lag)
		alphaAction.setColor(newLevel.getBackgroundLevel().getColor());

		// remove all old face actions
		Array<Action> actions = newLevel.getBackgroundLevel().getActions();
		for (Action action : actions) {
			if (action instanceof AlphaAction) {
				AlphaAction actionToFinish = (AlphaAction) action;
				actionToFinish.finish();
			}
		}

		// start
		backgroundLevel.addAction(alphaAction);

		stage.addActor(newLevel.getBackgroundLevel());

		levelOfTheDayLabel.setVisible(newLevel.isMapOfTheDay());

		if (newLevel.getDifficulty().getBonusPoints() == 0) {
			difficultyPointGainLabel.setText(null);
		} else {
			difficultyPointGainLabel.setText(" (+" + (int) (newLevel.getDifficulty().getBonusPoints() * 100) + "% Points gain)");
		}

		levelDifficultyLabel.setText(newLevel.getDifficulty().getName());
		levelModeLabel.setText(newLevel.getMode().toString());
		levelDifficultyStyle.fontColor = newLevel.getDifficulty().getColor();
	}

	private void showMenu() {
		TD.tracking.sendScreen("MainMenuScreen");
		difficultySelectionTable.setVisible(false);
		levelSelectionTable.setVisible(false);
		creditsTable.setVisible(false);
		achievementsTable.setVisible(false);
		donationTable.setVisible(false);

		versionLabel.setVisible(true);
		creditsButton.setVisible(true);
		upgradesTable.setVisible(false);
		setMainMenuVisible(true);
	}

	private void showDifficultySelection() {
		difficultySelectionTable.setVisible(true);
		levelSelectionTable.setVisible(false);

		versionLabel.setVisible(false);
		creditsButton.setVisible(false);
		activeGameLabelWarningDifficulty.setVisible(gameScreen != null);

		setMainMenuVisible(false);
	}

	private void showLevelSelection() {
		difficultySelectionTable.setVisible(false);
		levelSelectionTable.setVisible(true);

		versionLabel.setVisible(false);
		creditsButton.setVisible(false);
		activeGameLabelWarningLevel.setVisible(gameScreen != null);
		setMainMenuVisible(false);
	}

	private void setMainMenuVisible(boolean visible) {
		gameTitleLabel.setVisible(visible);
		continueButton.setVisible(visible);
		newGameButton.setVisible(visible);
		upgradesButton.setVisible(visible);
		achievementsButton.setVisible(visible);
		exitButton.setVisible(visible);
		creditsButton.setVisible(visible);
		donateButton.setVisible(visible);
	}

	public void clearGameScreen() {
		gameScreen = null;
	}

	@Override
	public void show() {
		super.show();

		showMenu();

		mainTable.clear();
		mainTable.setFillParent(true);

		final Table menuTable = new Table();

		if (gameScreen != null) {
			menuTable.add(continueButton).spaceBottom(15).expandX();
			menuTable.row();
		} else {
			try {
				final GameWorld world = laodWorld();

				if (world != null) {
					world.rebuildScene();

					gameScreen = new GameScreen(towerdefenseGame, world);

					menuTable.add(continueButton).spaceBottom(15).expandX();
					menuTable.row();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		menuTable.add(newGameButton).spaceBottom(15).expandX();
		menuTable.row();
		menuTable.add(upgradesButton).spaceBottom(15).expandX();
		menuTable.row();
		menuTable.add(achievementsButton).spaceBottom(15).expandX();
		menuTable.row();
		menuTable.add(exitButton).expandX();

		mainTable.add(menuTable).expand().colspan(3);
		mainTable.row();

		mainTable.add(creditsButton).bottom().left();
		mainTable.add(donateButton).bottom().left().expandX();
		mainTable.add(versionLabel).bottom();

	}

	private GameWorld laodWorld() {
		final DefaultReferenceStore referenceStore = new DefaultReferenceStore();
		final ReferenceJSonSerializer referenceJSonSerializer = new ReferenceJSonSerializer(referenceStore);

		// current version
		final FileHandle saveGame_2_0_0 = Gdx.files.local(Assets.CURRENT_SAVE_GAME_NAME);
		try {
			if (saveGame_2_0_0.exists()) {
				final Object fromJson = referenceJSonSerializer.fromJson(null, saveGame_2_0_0);

				return (GameWorld) fromJson;
			}
		} catch (Exception e) {

		}

		return null;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		backgroundLevel.applyCamera(stageCamera);
	}

	@Override
	protected boolean handleBack() {
		return true;
	}

	@Override
	protected void onBack() {
		if (upgradesTable.isVisible()) {
			showMenu();
		} else if (achievementsTable.isVisible()) {
			showMenu();
		} else if (difficultySelectionTable.isVisible()) {
			showMenu();
		} else if (levelSelectionTable.isVisible()) {
			showDifficultySelection();
		} else if (creditsTable.isVisible()) {
			showMenu();
		} else if (donationTable.isVisible()) {
			showMenu();
		}
	}

	/**
	 * Removes the current {@link GameScreen} if the game has ended.
	 */
	public void validateGameScreen() {
		if (gameScreen != null && gameScreen.gameHasEnded()) {
			gameScreen = null;
		}
	}

	public abstract static class BackgroundLevel extends AbstractWorld {

		private float spawnTime = 0;
		private static final float SPAWN_TIME = 2.5f;

		public BackgroundLevel(String mapPath, OrthographicCamera camera) {
			super(mapPath);

			setDrawGrid(false);
			setCamera(camera);
		}

		protected abstract void applyCamera(OrthographicCamera camera);

		@Override
		public void act(float delta) {
			super.act(delta);

			// spawn enemies in a regular interval
			spawnTime += delta;
			if (spawnTime > SPAWN_TIME) {

				final Vector2 initialPos = getPathNodes().get(0).getPosition();

				final AbstractEnemy abstractEnemy = new GroundEnemy(initialPos.x, initialPos.y, new Vector2(), ArmorType.MEDIUM, getPathNodes(), Rand.selectRandom(
						Assets.GENERIC_TANK_2,
						Assets.GENERIC_TANK_1),
						this, 100,
						(float) (Math.random() * 10 + 20), 0, 0);
				abstractEnemy.setRenderHealthPoints(false);
				abstractEnemy.centerAroundNodePosition();

				addEnemy(abstractEnemy);
				spawnTime = 0;
			}
		}

	}

	private static class GrassBackground extends BackgroundLevel {

		public GrassBackground(OrthographicCamera camera) {
			super("levels/grass.tmx", camera);

			addTower(new TurretTower(0, 0, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));
			addTower(new TurretTower(2 * 60, 2 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));

		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	private static class WoodsBackground extends BackgroundLevel {

		public WoodsBackground(OrthographicCamera camera) {
			super("levels/woods.tmx", camera);

			addTower(new TurretTower(0, 1 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));
			addTower(new TurretTower(2 * 60, 2 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));

			getPathNodes().clear();
			getPathNodes().add(new PathNode(new Vector2(3 * TILE_SIZE, -3 * TILE_SIZE), 3 * TILE_SIZE, 3 * TILE_SIZE, 0));
			getPathNodes().add(new PathNode(new Vector2(3 * TILE_SIZE, 5 * TILE_SIZE), 3 * TILE_SIZE, 3 * TILE_SIZE, 1));
			getPathNodes().add(new PathNode(new Vector2(27 * TILE_SIZE, 24 * TILE_SIZE), 3 * TILE_SIZE, 3 * TILE_SIZE, 2));
		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	private static class SnowBackground extends BackgroundLevel {

		public SnowBackground(OrthographicCamera camera) {
			super("levels/snow.tmx", camera);

			addTower(new TurretTower(0, 5 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));
			addTower(new TurretTower(4 * 60, 4 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));

			addTower(new ArtilleryTower(6 * 60, 6 * 60, null, Assets.TURRET_ICON, 500, 1.5f, 12, this));
		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.position.x, getHeight() - camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	private static class DesertBackground extends BackgroundLevel {

		public DesertBackground(OrthographicCamera camera) {
			super("levels/desert.tmx", camera);

			addTower(new TurretTower(1 * 60, 2 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 6, this));
			addTower(new TurretTower(6 * 60, 3 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 6, this));

		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2 + 60, 0);
			camera.update();
		}

	}

	private static class BeachBackground extends BackgroundLevel {

		public BeachBackground(OrthographicCamera camera) {
			super("levels/beach.tmx", camera);

			addTower(new TurretTower(2 * 60, 2 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));
			addTower(new TurretTower(0 * 60, 2 * 60, null, Assets.TURRET_ICON, 200, 0.25f, 7, this));

		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	private static class VulcanoBackground extends BackgroundLevel {

		public VulcanoBackground(OrthographicCamera camera) {
			super("levels/vulcano.tmx", camera);

			addTower(new FireTower(1 * GRID_SIZE, 2 * GRID_SIZE, null, Assets.FIRE_ICON, 75, 0f, 60, this));

			getPathNodes().clear();
			getPathNodes().add(new PathNode(new Vector2(-1 * GRID_SIZE, 1 * GRID_SIZE), GRID_SIZE, GRID_SIZE, 0));
			getPathNodes().add(new PathNode(new Vector2(0 * GRID_SIZE, 1 * GRID_SIZE), GRID_SIZE, GRID_SIZE, 1));
			getPathNodes().add(new PathNode(new Vector2(6 * GRID_SIZE, 1 * GRID_SIZE), GRID_SIZE, GRID_SIZE, 1));
			getPathNodes().add(new PathNode(new Vector2(6 * GRID_SIZE, -1 * GRID_SIZE), GRID_SIZE, GRID_SIZE, 1));
		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	private static class LavaBackground extends BackgroundLevel {

		public LavaBackground(OrthographicCamera camera) {
			super("levels/lava.tmx", camera);

			addTower(new FireTower(0 * 60, 2 * 60, null, Assets.FIRE_ICON, 75, 0f, 60, this));
		}

		@Override
		protected void applyCamera(OrthographicCamera camera) {
			camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
			camera.update();
		}

	}

	public static enum Difficulty {
		EASY("Easy", 0, Assets.COLOR_GREEN), MEDIUM("Medium", 0.15f, Assets.COLOR_YELLOW), HARD("Hard", 0.3f, Assets.COLOR_RED);

		private String name;
		private final float bonusPoints;
		private final Color color;

		private Difficulty(String name, float bonusPoints, Color color) {
			this.name = name;
			this.bonusPoints = bonusPoints;
			this.color = color;
		}

		public float getBonusPoints() {
			return bonusPoints;
		}

		public Color getColor() {
			return color;
		}

		public String getName() {
			return name;
		}
	}

	private static abstract class Level {
		private final String name;
		private final String levelPath;
		private boolean mapOfTheDay;
		private BackgroundLevel backgroundLevel;
		private Difficulty difficulty;
		private GameMode mode;

		public Level(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty, GameMode mode) {
			super();
			this.name = name;
			this.levelPath = levelPath;
			this.backgroundLevel = backgroundLevel;
			this.difficulty = difficulty;
			this.mode = mode;
		}

		public abstract GameWorld createWorld();

		public void setMapOfTheDay(boolean mapOfTheDay) {
			this.mapOfTheDay = mapOfTheDay;
		}

		public boolean isMapOfTheDay() {
			return mapOfTheDay;
		}

		public String getLevelPath() {
			return levelPath;
		}

		public String getName() {
			return name;
		}

		public BackgroundLevel getBackgroundLevel() {
			return backgroundLevel;
		}

		public Difficulty getDifficulty() {
			return difficulty;
		}

		public GameMode getMode() {
			return mode;
		}

	}

	private static class VulcanoLevel extends Level {

		public VulcanoLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.NO_ROADS);

			backgroundLevel.addActor(new EruptionSpawner(backgroundLevel.getMap(), backgroundLevel, 0.25f, "isLava"));
		}

		@Override
		public GameWorld createWorld() {
			return new NoRoadsLavaGameWorld(getLevelPath());
		}
	}

	private static class LavalLevel extends Level {

		public LavalLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.CLASSIC);

			backgroundLevel.addActor(new EruptionSpawner(backgroundLevel.getMap(), backgroundLevel, 0.25f, "isLava"));
		}

		@Override
		public GameWorld createWorld() {
			return new ClassicLavaGameWorld(getLevelPath());
		}
	}

	private static class SnowLevel extends Level {

		public SnowLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.CLASSIC);
		}

		@Override
		public GameWorld createWorld() {
			return new ClassicGameWorld(getLevelPath());
		}
	}

	private static class DesertLevel extends Level {

		public DesertLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.CLASSIC);
		}

		@Override
		public GameWorld createWorld() {
			return new ClassicGameWorld(getLevelPath());
		}
	}

	private static class BeachLevel extends Level {

		public BeachLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.NO_ROADS);
		}

		@Override
		public GameWorld createWorld() {
			return new NoRoadsGameWorld(getLevelPath());
		}
	}

	private static class GrassLevel extends Level {

		public GrassLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.CLASSIC);
		}

		@Override
		public GameWorld createWorld() {
			return new ClassicGameWorld(getLevelPath());
		}
	}

	private static class WoodsLevel extends Level {

		public WoodsLevel(String name, String levelPath, BackgroundLevel backgroundLevel, Difficulty difficulty) {
			super(name, levelPath, backgroundLevel, difficulty, GameMode.NO_ROADS);
		}

		@Override
		public GameWorld createWorld() {
			return new NoRoadsGameWorld(getLevelPath());
		}
	}
}
