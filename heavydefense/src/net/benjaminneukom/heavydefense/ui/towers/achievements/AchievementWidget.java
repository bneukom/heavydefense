package net.benjaminneukom.heavydefense.ui.towers.achievements;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.util.StringUtil;
import net.benjaminneukom.heavydefense.util.TextureDrawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Value;

public class AchievementWidget extends Table {

	private Image achievementIcon;
	private Label progressLabel;
	private Achievement achievement;

	public AchievementWidget(final Achievement achievement, Table parent) {
		this.achievement = achievement;
		this.achievementIcon = new Image();

		add(achievementIcon).left().align(BaseTableLayout.LEFT);

		LabelStyle infoLabelStyle = new LabelStyle();
		infoLabelStyle.font = Assets.TEXT_FONT;
		infoLabelStyle.fontColor = Color.BLACK;

		LabelStyle headerLabelStyle = new LabelStyle();
		headerLabelStyle.font = Assets.TITLE_FONT_38;
		headerLabelStyle.fontColor = Color.BLACK;

		LabelStyle scoreLabelStyle = new LabelStyle();
		scoreLabelStyle.font = Assets.TEXT_FONT;
		scoreLabelStyle.fontColor = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);

		final Table info = new Table();

		final Label nameLabel = new Label(achievement.getName(), headerLabelStyle);

		final Label descriptionLabel = new Label(achievement.getDescription(), infoLabelStyle);
		descriptionLabel.setWrap(true);

		progressLabel = new Label("Progress: " + StringUtil.numberSepeartor(achievement.getCurrentStep()) + "/" + StringUtil.numberSepeartor(achievement.getTotalSteps()),
				infoLabelStyle);

		final Label pointsLabelText = new Label("Worth: ", infoLabelStyle);
		final Label pointsLabel = new Label(StringUtil.numberSepeartor((int) achievement.getPoints()), scoreLabelStyle);

		info.add(nameLabel).expandX().left();
		info.row();
		info.add(descriptionLabel).expandX().left().minWidth(Value.percentWidth(0.8f, parent));
		info.row();
		info.add(progressLabel).expandX().left();
		info.row();

		final Table costTable = new Table();
		costTable.add(pointsLabelText).left();
		costTable.add(pointsLabel).left().padRight(10);

		info.add(costTable).left();
		info.row();

		add(info).expandX().left();

		update();

	}

	public void update() {
		if (achievement.isUnlocked()) {
			achievementIcon.setDrawable(new TextureDrawable(Assets.getTexture(Assets.ACHIEVEMENT_UNLOCKED_ICON)));
		} else {
			achievementIcon.setDrawable(new TextureDrawable(Assets.getTexture(Assets.ACHIEVEMENT_ICON)));
		}

		progressLabel.setText("Progress: " + StringUtil.numberSepeartor(achievement.getCurrentStep()) + "/" + StringUtil.numberSepeartor(achievement.getTotalSteps())
				);
	}

}
