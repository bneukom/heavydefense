package net.benjaminneukom.heavydefense.ui.upgrade;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.util.StringUtil;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Value;

public class UpgradeWidget extends Table {

	private Image towerIcon;
	private boolean enabled = true;
	private boolean maxLevelReached = false;

	private LabelStyle costLabelStyle;
	private Button upgradeButton;

	private Upgrade upgrade;

	private static final Color COST_COLOR = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);
	private static final Color MAX_LEVEL_COLOR = new Color(0.4f, 0.4f, 0.4f, 0.75f);

	private Label costLabel;
	private Label nameLabel;
	private Label descriptionLabel;
	private LabelStyle scoreLabelStyle;
	private LabelStyle headerLabelStyle;
	private LabelStyle infoLabelStyle;

	public UpgradeWidget(final Upgrade upgrade) {
		this.upgrade = upgrade;

		towerIcon = new Image(Assets.getTexture(upgrade.getIconPath()));

		add(towerIcon).left().align(BaseTableLayout.LEFT);

		infoLabelStyle = new LabelStyle();
		infoLabelStyle.font = Assets.TEXT_FONT;
		infoLabelStyle.fontColor = Color.BLACK;

		headerLabelStyle = new LabelStyle();
		headerLabelStyle.font = Assets.TITLE_FONT_38;
		headerLabelStyle.fontColor = Color.BLACK;

		scoreLabelStyle = new LabelStyle();
		scoreLabelStyle.font = Assets.TEXT_FONT;
		scoreLabelStyle.fontColor = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);

		costLabelStyle = new LabelStyle();
		costLabelStyle.font = Assets.TEXT_FONT;
		costLabelStyle.fontColor = COST_COLOR;

		final Table info = new Table();
		info.setFillParent(true);

		nameLabel = new Label(upgrade.getName() + " " + upgrade.getLevel() + "/" + upgrade.getMaxLevel(), headerLabelStyle);

		descriptionLabel = new Label(upgrade.getDescription(), infoLabelStyle);
		descriptionLabel.setWrap(true);
		final Label costLabelText = new Label("Cost: ", infoLabelStyle);
		costLabel = new Label(StringUtil.numberSepeartor((int) upgrade.getUpgradeCost()), costLabelStyle);

		final ButtonStyle upgradeButtontStyle = new ButtonStyle();
		upgradeButtontStyle.up = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/upgradebutton.png")));
		upgradeButtontStyle.down = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/upgradebuttonpressed.png")));
		upgradeButtontStyle.disabled = new TextureDrawable(new Texture(Gdx.files.internal("sprites/hud/upgradebuttondisabled.png")));
		upgradeButton = new Button(upgradeButtontStyle);

		upgradeButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TD.addScore(-upgrade.getUpgradeCost());

				upgrade.upgrade();

				updateText();
				updateEnablement(TD.getScore());

				if (upgrade.isMaxLevel()) {
					maxLevelReached = true;
					maxLevel();
				}
			}

		});
		info.add(nameLabel).expandX().left();
		info.row();
		info.add(descriptionLabel).expandX().left().minWidth(Value.percentWidth(0.8f));
		info.row();

		final Table costTable = new Table();
		costTable.add(costLabelText).left();
		costTable.add(costLabel).left().padRight(10);
		costTable.add(upgradeButton).left();

		info.add(costTable).left();
		info.row();

		add(info).expandX().left();

		if (upgrade.isMaxLevel()) {
			maxLevelReached = true;
			maxLevel();
		} else {
			updateEnablement(TD.getScore());
		}

	}

	public Button getUpgradeButton() {
		return upgradeButton;
	}

	public String getName() {
		return upgrade.getName();
	}

	public int getMaxLevel() {
		return upgrade.getMaxLevel();
	}

	public float getValueUpgrade() {
		return upgrade.getValueUpgrade();
	}

	public int getLevel() {
		return upgrade.getLevel();
	}

	public float getValue() {
		return upgrade.getValue();
	}

	public float getUpgradeCost() {
		return upgrade.getUpgradeCost();
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.upgradeButton.setDisabled(!enabled);

		if (enabled) {
			towerIcon.setColor(1f, 1f, 1f, 1f);
		} else {
			towerIcon.setColor(0.3f, 0.3f, 0.3f, 0.3f);
		}
	}

	private void maxLevel() {
		scoreLabelStyle.fontColor = MAX_LEVEL_COLOR;
		headerLabelStyle.fontColor = MAX_LEVEL_COLOR;
		infoLabelStyle.fontColor = MAX_LEVEL_COLOR;
		costLabelStyle.fontColor = MAX_LEVEL_COLOR;

		costLabel.setText("-");

		setEnabled(false);
	}

	private void markNotEnoughScore() {
		costLabelStyle.fontColor = Assets.COLOR_RED;
	}

	private void removeNotEnoughScoreMark() {
		costLabelStyle.fontColor = COST_COLOR;
	}

	public void updateEnablement(float newScore) {
		if (maxLevelReached)
			return;

		if (newScore < upgrade.getUpgradeCost()) {
			markNotEnoughScore();
			setEnabled(false);
		} else {
			removeNotEnoughScoreMark();
			setEnabled(true);
		}

	}

	private void updateText() {
		if (maxLevelReached)
			return;

		nameLabel.setText(upgrade.getName() + " " + upgrade.getLevel() + "/" + upgrade.getMaxLevel());
		costLabel.setText(StringUtil.numberSepeartor((int) upgrade.getUpgradeCost()));
		descriptionLabel.setText(upgrade.getDescription());
	}
}
