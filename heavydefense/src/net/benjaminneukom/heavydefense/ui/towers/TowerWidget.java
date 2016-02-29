package net.benjaminneukom.heavydefense.ui.towers;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.tablelayout.BaseTableLayout;

public class TowerWidget extends Table {
	private LabelStyle infoLabelStyle;
	private LabelStyle headerLabelStyle;
	private LabelStyle upgradeLabelStyle;

	private Image towerIcon;
	private Label nameLabel;
	private Table towerInfoTable;
	private TextButton upgradeButton;
	private TextButton sellButton;
	private TextButtonStyle upgradeButtonStyle;
	private AbstractTower currentTower;

	private static final Color DISABLED_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.7f);

	private boolean maxLevel = false;
	private boolean notEnoughMoney = false;

	public TowerWidget() {
		towerIcon = new Image();

		addListener(new ClickListener());

		final Stack mainStack = new Stack();
		mainStack.setFillParent(true);
		add(mainStack);

		final Table mainTable = new Table();
		mainTable.setFillParent(true);

		mainTable.add(towerIcon).left().align(BaseTableLayout.LEFT);

		setBackground(new TextureRegionDrawable(new TextureRegion(Assets.getTexture(Assets.BACKGROUND))));

		padRight(5);

		infoLabelStyle = new LabelStyle();
		infoLabelStyle.font = Assets.TEXT_FONT;
		infoLabelStyle.fontColor = Color.BLACK;

		upgradeLabelStyle = new LabelStyle();
		upgradeLabelStyle.font = Assets.TEXT_FONT;
		upgradeLabelStyle.fontColor = Assets.COLOR_GREEN;

		headerLabelStyle = new LabelStyle();
		headerLabelStyle.font = Assets.TITLE_FONT_38;
		headerLabelStyle.fontColor = Color.BLACK;

		Table info = new Table();

		nameLabel = new Label("", headerLabelStyle);

		// needs to be in a single cell
		final Table nameTable = new Table();
		nameTable.add(nameLabel).left().padRight(10);

		info.add(nameTable).left();
		info.row();

		towerInfoTable = new Table();
		info.add(towerInfoTable).left();

		info.row();

		upgradeButtonStyle = new TextButtonStyle();
		upgradeButtonStyle.font = Assets.TITLE_FONT_38;
		upgradeButtonStyle.fontColor = Color.WHITE;
		upgradeButtonStyle.overFontColor = Color.BLACK;
		upgradeButton = new TextButton("Upgrade", upgradeButtonStyle);

		info.add(upgradeButton).left().spaceTop(15);
		info.row();

		final TextButtonStyle sellbuttonStyle = new TextButtonStyle();
		sellbuttonStyle.font = Assets.TITLE_FONT_38;
		sellbuttonStyle.overFontColor = Color.BLACK;
		sellbuttonStyle.fontColor = Color.WHITE;
		sellButton = new TextButton("Sell", sellbuttonStyle);
		info.add(sellButton).left().spaceTop(15);

		mainTable.add(info).right();

		mainStack.add(mainTable);

		final Table overlayTable = new Table();
		overlayTable.setFillParent(true);
		// overlayTable.add(new Image(new Texture(Gdx.files.internal("close2.png")))).expand().top().right().space(50);

		mainStack.add(overlayTable);

	}

	public AbstractTower getCurrentTower() {
		return currentTower;
	}

	public void addSellClickListener(ClickListener sellClickListener) {
		sellButton.addListener(sellClickListener);
	}

	public void addUpgradeClickListener(ChangeListener upgradeListener) {
		upgradeButton.addListener(upgradeListener);
	}

	public void updateUpgradeButtonState() {
		if (currentTower == null)
			return;

		if (currentTower.getLevel() >= currentTower.getMaxLevel()) {
			upgradeButton.setDisabled(true);
			upgradeButtonStyle.disabledFontColor = DISABLED_COLOR;
			maxLevel = true;
		} else {
			if (!notEnoughMoney) {
				upgradeButton.setDisabled(false);
			}
			maxLevel = false;
		}
	}

	public void updateMoney(float currentMoney) {
		if (currentTower == null)
			return;

		if (currentMoney < currentTower.getNextUpgradeCost()) {
			// max level disabled color has priority
			if (!maxLevel) {
				upgradeButtonStyle.disabledFontColor = Assets.COLOR_RED;
			}
			upgradeButton.setDisabled(true);

			notEnoughMoney = true;
		} else {
			if (!maxLevel) {
				upgradeButton.setDisabled(false);
			}

			notEnoughMoney = false;
		}

	}

	public void setTower(AbstractTower tower, float currentMoney) {
		this.currentTower = tower;

		fill(tower);

		notEnoughMoney = false;
		maxLevel = false;

		// default
		upgradeButton.setDisabled(false);

		updateMoney(currentMoney);
		updateUpgradeButtonState();
	}

	public void refresh() {
		updateUpgradeButtonState();

		fill(currentTower);
	}

	private void fill(AbstractTower tower) {
		nameLabel.setText(tower.getTowerName());
		towerIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(tower.getIcon())));

		upgradeButton.setText("Upgrade (" + (int) tower.getUpgradeCost(currentTower.getLevel() + 1) + ")");
		sellButton.setText("Sell (" + (int) tower.getSellValue() + ")");

		towerInfoTable.clear();
		towerInfoTable.add(new Label("Level: " + tower.getLevel() + "/" + tower.getMaxLevel(), infoLabelStyle)).left();
		towerInfoTable.row();

		tower.fillInfoTable(towerInfoTable, infoLabelStyle, headerLabelStyle, upgradeLabelStyle);
	}

}
