package net.benjaminneukom.heavydefense.ui.towers.achievements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Value;

public class AchievementListWidget extends Table {
	private List<AchievementWidget> achievementWidgets = new ArrayList<AchievementWidget>();

	public AchievementListWidget(Collection<Achievement> collection) {
		for (Achievement upgrade : collection) {
			addUpgrade(upgrade);
		}

	}

	public void update() {
		for (AchievementWidget achievementWidget : achievementWidgets) {
			achievementWidget.update();
		}
	}

	private void addUpgrade(final Achievement achievement) {
		final AchievementWidget widget = new AchievementWidget(achievement, this);
		achievementWidgets.add(widget);

		add(widget).left().expandX().minWidth(Value.percentWidth(0.8f));
		row();
	}

}
