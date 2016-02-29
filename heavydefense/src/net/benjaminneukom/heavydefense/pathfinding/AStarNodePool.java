package net.benjaminneukom.heavydefense.pathfinding;

import com.badlogic.gdx.utils.Pool;

public class AStarNodePool extends Pool<AStarNode> {

	public AStarNodePool() {
		super(64);
	}

	@Override
	protected AStarNode newObject() {
		return new AStarNode();
	}

}
