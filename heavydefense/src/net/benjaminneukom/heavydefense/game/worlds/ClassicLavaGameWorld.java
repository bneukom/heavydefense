package net.benjaminneukom.heavydefense.game.worlds;

import net.benjaminneukom.heavydefense.game.EruptionSpawner;

public class ClassicLavaGameWorld extends ClassicGameWorld {

	private transient EruptionSpawner eruptionSpawner;

	public ClassicLavaGameWorld() {
		super();
	}

	public ClassicLavaGameWorld(String mapPath) {
		super(mapPath);

		createEruptionSpawner();

		boolean b = 3 + 1 + 1 == 4 + 5;
		System.out.println(b);

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
