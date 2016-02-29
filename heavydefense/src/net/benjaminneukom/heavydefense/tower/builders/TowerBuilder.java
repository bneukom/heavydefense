package net.benjaminneukom.heavydefense.tower.builders;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class TowerBuilder {

	protected final String name;
	protected final String description;
	protected String iconPath;
	protected String smallIcon;
	protected String smallIconSelected;

	protected final float damage;
	protected final float range;
	protected final float cost;
	protected final float cooldown;

	public TowerBuilder(String iconPath, String smallIcon, String smallIconSelected, String name, String description, float damage, float range, float cost, float cooldown) {
		super();
		this.iconPath = iconPath;
		this.smallIcon = smallIcon;
		this.smallIconSelected = smallIconSelected;
		this.name = name;
		this.description = description;
		this.damage = damage;
		this.range = range;
		this.cost = cost;
		this.cooldown = cooldown;
	}

	public String getName() {
		return name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getDescription() {
		return description;
	}

	public float getRange() {
		return range;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getCost() {
		return cost;
	}

	public float getDamage() {
		return damage;
	}

	public String getSmallIcon() {
		return smallIcon;
	}

	public String getSmallIconSelected() {
		return smallIconSelected;
	}

	public void additionalDescription(Table info, LabelStyle infoLabelStyle) {

	}

	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle sizeLabelStyle, LabelStyle costLabelStyle) {
		final LabelStyle valueLabelStyle = new LabelStyle(infoLabelStyle);
		valueLabelStyle.fontColor = Assets.COLOR_YELLOW;

		final Label costDescriptionLabel = new Label("Cost: " + (int) cost, costLabelStyle);
		final Label damageDescriptionLabel = new Label("Damage: ", infoLabelStyle);
		final Label damageLabel = new Label(String.valueOf((int) damage), valueLabelStyle);
		final Label rangeDescriptionLabel = new Label("Range: ", infoLabelStyle);
		final Label rangeLabel = new Label(String.valueOf((int) range), valueLabelStyle);
		final Label cooldownDescriptionLabel = new Label("Cooldown: ", infoLabelStyle);
		final Label cooldownLabel = new Label(String.valueOf((int) (cooldown * 100f) / 100f) + " Sec", valueLabelStyle);

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

		final Table cooldownTable = new Table();
		cooldownTable.add(cooldownDescriptionLabel).left();
		cooldownTable.add(cooldownLabel).left();
		info.add(cooldownTable).expandX().left();
		info.row();
	}

	public abstract AbstractTower createTower(float x, float y);

}
