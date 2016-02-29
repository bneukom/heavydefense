package net.benjaminneukom.heavydefense.game.worlds;

import net.benjaminneukom.heavydefense.game.EruptionSpawner;

public class NoRoadsLavaGameWorld extends NoRoadsGameWorld {

	private transient EruptionSpawner eruptionSpawner;

	public NoRoadsLavaGameWorld() {
		super();
	}

	public NoRoadsLavaGameWorld(String mapPath) {
		super(mapPath);

		createEruptionSpawner();

	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		createEruptionSpawner();
	}

	private void createEruptionSpawner() {
		eruptionSpawner = new EruptionSpawner(getMap(), this, 0.35f, "isLava");
		addActor(eruptionSpawner);
	}
}
