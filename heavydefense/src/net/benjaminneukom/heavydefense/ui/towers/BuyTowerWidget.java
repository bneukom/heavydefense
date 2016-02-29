package net.benjaminneukom.heavydefense.ui.towers;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.tower.builders.TowerBuilder;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class BuyTowerWidget extends Table {

	//	private Image towerIcon;
	protected TowerBuilder builder;

	private LabelStyle sizeLabelStyle;
	private LabelStyle costLabelStyle;
	private LabelStyle infoLabelStyle;
	private LabelStyle headerLabelStyle;

	private Label nameLabel;
	private Table info;

	public BuyTowerWidget() {

		setBackground(new TextureDrawable(Assets.getTexture(Assets.BACKGROUND)));

		infoLabelStyle = new LabelStyle();
		infoLabelStyle.font = Assets.TEXT_FONT;
		infoLabelStyle.fontColor = Color.BLACK;

		headerLabelStyle = new LabelStyle();
		headerLabelStyle.font = Assets.TITLE_FONT_38;
		headerLabelStyle.fontColor = Color.BLACK;

		costLabelStyle = new LabelStyle();
		costLabelStyle.font = Assets.TEXT_FONT;
		costLabelStyle.fontColor = Color.BLACK;

		info = new Table();
		nameLabel = new Label("", headerLabelStyle);
		nameLabel.setAlignment(Align.center);
	}

	public void setTowerBuilder(TowerBuilder builder) {
		this.builder = builder;

		this.clear();
		this.info.clear();

		nameLabel.setText(builder.getName());
		info.add(nameLabel).center();
		info.row();

		builder.additionalDescription(info, infoLabelStyle);
		builder.fillInfoTable(info, infoLabelStyle, headerLabelStyle, sizeLabelStyle, costLabelStyle);

		add(info).right();
	}

	public float getTowerCost() {
		return builder.getCost();
	}

	public TowerBuilder getTowerBuilder() {
		return builder;
	}

}
