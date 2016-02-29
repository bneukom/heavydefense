package net.benjaminneukom.heavydefense.screens;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.util.Rand;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.tablelayout.Value;

public class SplashScreen extends AbstractScreen {

	private final Label loadingLabel;

	public static final String REMEMBER_KEY = "rememberSkip";

	private static final String[] TIPPS_OF_THE_DAY = new String[] {
			"There are medium and heavy armored enemies, use different towers for bonuses against these enemy types.",
			"Use Rocket Towers to defeat air enemies. Or upgrade the Tesla Tower to make it able to kill air enemies.",
			"Turrets should not be underestimated. Fully upgraded and good positioned they can deal high amounts of damage.",
			"You should play on a difficulty level where you can finish the map. Most of the points are earned from the last waves and from winning."
	};

	private int TIP_2_TRESHOLD = 5;
	private static final String[] TIPPS_OF_THE_DAY_2 = new String[TIPPS_OF_THE_DAY.length + 1];

	static {
		for (int tippIndex = 0; tippIndex < TIPPS_OF_THE_DAY.length; ++tippIndex) {
			TIPPS_OF_THE_DAY_2[tippIndex] = TIPPS_OF_THE_DAY[tippIndex];
		}
		TIPPS_OF_THE_DAY_2[TIPPS_OF_THE_DAY.length] = "If you like the game, consider giving a good rating or a donation :)";
	}

	private Button continueButton;

	private CheckBox rememberCheckbox;

	public SplashScreen(final HeavyDefenseGame game) {
		final Table mainTable = new Table();
		mainTable.setFillParent(true);

		final LabelStyle loadingLabelStyle = new LabelStyle();
		loadingLabelStyle.fontColor = Color.WHITE;
		loadingLabelStyle.font = Assets.TITLE_FONT_64;

		loadingLabel = new Label("Loading", loadingLabelStyle);
		mainTable.add(loadingLabel).expand().bottom().colspan(2);
		mainTable.row();

		final LabelStyle tippOfTheDayLabelStyle = new LabelStyle();
		tippOfTheDayLabelStyle.fontColor = Color.WHITE;
		tippOfTheDayLabelStyle.font = Assets.TEXT_FONT;

		final Label tippOfTheDay = new Label(Rand.selectRandom(TD.getPlayed() >= TIP_2_TRESHOLD ? TIPPS_OF_THE_DAY_2 : TIPPS_OF_THE_DAY), tippOfTheDayLabelStyle);
		tippOfTheDay.setWrap(true);
		tippOfTheDay.setAlignment(Align.center);

		mainTable.add(tippOfTheDay).expandX().minWidth(Value.percentWidth(0.9f)).colspan(2);
		mainTable.row();

		final CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
		checkBoxStyle.font = Assets.TEXT_FONT;
		checkBoxStyle.fontColor = Color.WHITE;
		checkBoxStyle.checkboxOn = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/checkboxchecked.png")));
		checkBoxStyle.checkboxOff = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/checkbox.png")));
		checkBoxStyle.checkboxOver = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/checkboxpressed.png")));

		rememberCheckbox = new CheckBox("Always Skip Tip", checkBoxStyle);
		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		rememberCheckbox.setChecked(preferences.contains(REMEMBER_KEY) ? preferences.getBoolean(REMEMBER_KEY) : false);
		rememberCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!rememberCheckbox.isChecked()) {
					game.disableSkipTipp();
				}
			}
		});

		mainTable.add(rememberCheckbox).expand().bottom().left();

		final ButtonStyle continueButtonStyle = new ButtonStyle();
		continueButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/continuebuttonsmall.png")))));
		continueButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/continuebuttonsmallpressed.png")))));
		continueButtonStyle.disabled = new TextureRegionDrawable(new TextureRegion(
				new TextureRegion(new Texture(Gdx.files.internal("sprites/hud/continuebuttonsmalldisabled.png")))));

		continueButton = new Button(continueButtonStyle);
		continueButton.setDisabled(true);
		continueButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.switchToMenu();
				persistRemember();
			}
		});

		mainTable.add(continueButton).bottom().right().padRight(3).padBottom(3);

		ui.addActor(mainTable);
	}

	@Override
	protected boolean handleBack() {
		return false;
	}

	public void setLoadingPercentage(final float percentage) {
		continueButton.setDisabled(percentage < 1);
		loadingLabel.setText("Loading (" + (int) (percentage * 100) + "%)");
	}

	public void persistRemember() {
		final Preferences preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		preferences.putBoolean(REMEMBER_KEY, rememberCheckbox.isChecked());
		preferences.flush();
	}

}
