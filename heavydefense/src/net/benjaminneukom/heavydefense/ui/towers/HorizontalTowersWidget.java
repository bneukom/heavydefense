package net.benjaminneukom.heavydefense.ui.towers;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Player;
import net.benjaminneukom.heavydefense.game.worlds.GameWorld;
import net.benjaminneukom.heavydefense.screens.GameScreen;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.tower.builders.TowerBuilder;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.tablelayout.Value;

// Left to right icons of towers. when clicked show tooltip. attached to the top
public class HorizontalTowersWidget extends Table {

	private ObjectMap<TowerBuilder, Button> builderToButtonMap = new ObjectMap<TowerBuilder, Button>(10);
	private Table towersTable;
	private GameScreen gameScreen;

	private Stack buyTowerStack;
	private BuyTowerWidget buyTowerWidget;
	private Label tipLabel;

	public HorizontalTowersWidget(GameScreen gameScreen, TowerBuilder... builders) {

		this.gameScreen = gameScreen;

		this.buyTowerWidget = new BuyTowerWidget();
		this.buyTowerWidget.setVisible(false);

		final LabelStyle tipLabelStyle = new LabelStyle();
		tipLabelStyle.font = Assets.TEXT_FONT;
		tipLabelStyle.fontColor = Color.BLACK;
		this.tipLabel = new Label("Select Tower from below then tap the tower again if you want to build it.", tipLabelStyle);
		this.tipLabel.setWrap(true);
		this.tipLabel.setAlignment(Align.center);

		final Table tipLabelTable = new Table();

		tipLabelTable.add(tipLabel).bottom().expand().minWidth(Value.percentWidth(0.9f));

		this.buyTowerStack = new Stack();
		this.buyTowerStack.add(buyTowerWidget);
		this.buyTowerStack.add(tipLabelTable);

		add(buyTowerStack).colspan(8).expandX().top().minWidth(Value.percentWidth(1f)).spaceBottom(4);

		row();

		this.towersTable = new Table();
		this.towersTable.setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND_SMALL)));

		for (TowerBuilder towerBuilder : builders) {
			addTowerButton(towerBuilder);
		}

		add(towersTable).expand().bottom().minWidth(Value.percentWidth(1));
	}

	private void addTowerButton(final TowerBuilder towerBuilder) {
		final ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureDrawable(Assets.getTexture(towerBuilder.getSmallIcon()));
		buttonStyle.checked = new TextureDrawable(Assets.getTexture(towerBuilder.getSmallIconSelected()));
		buttonStyle.checkedOver = new TextureDrawable(Assets.getTexture(towerBuilder.getSmallIconSelected()));

		final Button towerButton = new Button(buttonStyle);

		towerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				if (towerButton.isDisabled()) return;

				if (!towerButton.isChecked()) {
					// buy tower
					final GameWorld gameWorld = gameScreen.getGameWorld();
					final AbstractTower tower = buyTowerWidget.getTowerBuilder().createTower(gameWorld.getBuyTouchX(), gameWorld.getBuyTouchY());

					gameWorld.getPlayer().increaseMoney(-buyTowerWidget.getTowerCost());

					gameWorld.addTower(tower);

					// restore state
					gameScreen.hideBuyTowerDialog();
					towerButton.setChecked(false);
					buyTowerWidget.setVisible(false);
					tipLabel.setVisible(true);

					updateEnablement();
				} else {

					buyTowerWidget.setTowerBuilder(towerBuilder);
					buyTowerWidget.setVisible(true);
					tipLabel.setVisible(false);

					for (Button button : builderToButtonMap.values()) {
						if (button != towerButton) {
							button.setChecked(false);
						}
					}
				}
			}
		});

		towersTable.add(towerButton).left();
		builderToButtonMap.put(towerBuilder, towerButton);
	}

	public void updateEnablement() {
		final Player player = gameScreen.getGameWorld().getPlayer();
		final Entries<TowerBuilder, Button> entries = builderToButtonMap.entries();
		for (Entry<TowerBuilder, Button> entry : entries) {

			final boolean isDisabled = player.getMoney() < entry.key.getCost();
			final Button towerButton = entry.value;
			towerButton.setDisabled(isDisabled);

			if (isDisabled) {
				towerButton.setColor(0.3f, 0.3f, 0.3f, 0.3f);
			} else {
				towerButton.setColor(1, 1, 1, 1);
			}
		}

	}
}
