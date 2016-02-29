package net.benjaminneukom.heavydefense.ui.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.TD.ScoreListener;


import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Value;

public class UpgradesListWidget extends Table {
	private List<UpgradeWidget> upgradeWidgets = new ArrayList<UpgradeWidget>();

	public UpgradesListWidget(Collection<Upgrade> collection) {
		for (Upgrade upgrade : collection) {
			addUpgrade(upgrade);
		}

		TD.addScoreChangedListener(new ScoreListener() {

			@Override
			public void scoreChanged(float newScore) {
				updateUpgradeWidgets(newScore);
			}

			private void updateUpgradeWidgets(float newScore) {
				for (UpgradeWidget upgradeWidget : upgradeWidgets) {
					upgradeWidget.updateEnablement(newScore);
				}
			}
		});

	}

	private void addUpgrade(final Upgrade upgrade) {
		final UpgradeWidget widget = new UpgradeWidget(upgrade);
		upgradeWidgets.add(widget);

		add(widget).left().expandX().minWidth(Value.percentWidth(0.8f));
		row();
	}

}
