package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

public class Explosion extends AnimatedActor {

	public Explosion() {
	}

	public Explosion(float x, float y, String atlasPath, String sheetName, int columns, int rows, float delay, AbstractWorld abstractWorld) {
		super(x, y, atlasPath, sheetName, columns, rows, 0.045f, delay, abstractWorld);
	}

	public Explosion(float x, float y, String atlasPath, String sheetName, int columns, int rows, AbstractWorld abstractWorld) {
		this(x, y, atlasPath, sheetName, columns, rows, 0, abstractWorld);
	}

	@Override
	protected void done() {
		abstractWorld.removeOverlayActor(this);
	}

}
