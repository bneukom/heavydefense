package net.benjaminneukom.heavydefense.tower.builders;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.tower.SlowTower;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SlowTowerBuilder extends TowerBuilder {

	private final float slowFactor;
	private final float slowDuration;
	private final AbstractWorld abstractWorld;

	public SlowTowerBuilder(String iconPath, String smallIcon, String smallIconSelected, String name, String description, float damage, float range, float cost, float cooldown,
			float slowFactor,
			float slowDuration,
			final AbstractWorld abstractWorld) {
		super(iconPath, smallIcon, smallIconSelected, name, description, damage, range, cost, cooldown);
		this.slowFactor = slowFactor;
		this.slowDuration = slowDuration;
		this.abstractWorld = abstractWorld;
	}

	public SlowTower createTower(float x, float y) {
		return new SlowTower(x, y, name, iconPath, range, slowFactor, slowDuration, abstractWorld);
	}

	@Override
	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle sizeLabelStyle, LabelStyle costLabelStyle) {

		final LabelStyle valueLabelStyle = new LabelStyle(infoLabelStyle);
		valueLabelStyle.fontColor = Assets.COLOR_YELLOW;

		final Label costDescriptionLabel = new Label("Cost: " + (int) cost, costLabelStyle);
		final Label rangeDescriptionLabel = new Label("Range: ", infoLabelStyle);
		final Label rangeLabel = new Label(String.valueOf((int) range), valueLabelStyle);
		final Label slowFactorDescriptionLabel = new Label("SlowFactor: ", infoLabelStyle);
		final Label slowFactorLabel = new Label((int) ((1 - slowFactor) * 100) + "%", valueLabelStyle);

		info.row();
		info.add(costDescriptionLabel).left();
		info.row();

		final Table rangeTable = new Table();
		rangeTable.add(rangeDescriptionLabel).left();
		rangeTable.add(rangeLabel).left();
		info.add(rangeTable).expandX().left();
		info.row();

		final Table slowTable = new Table();
		slowTable.add(slowFactorDescriptionLabel).left();
		slowTable.add(slowFactorLabel).left();
		info.add(slowTable).expandX().left();
		info.row();
	}

}
