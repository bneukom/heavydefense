package net.benjaminneukom.heavydefense.game.worlds;

import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;

public class ClassicGameWorld extends GameWorld {

	public ClassicGameWorld() {
		super();
	}

	public ClassicGameWorld(String mapPath) {
		super(mapPath);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.CLASSIC;
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		// find the serialized pathNode target in the new nodes and update the target
		for (AbstractEnemy enemy : getEnemies()) {
			matchTargetPathNode(enemy);
		}
	}

}
