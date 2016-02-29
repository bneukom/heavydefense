package net.benjaminneukom.heavydefense.pathfinding;

import static net.benjaminneukom.heavydefense.HeavyDefenseGame.GRID_SIZE;

import java.util.ArrayList;
import java.util.List;

import net.benjaminneukom.heavydefense.game.PathNode;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AStarPath {

	public boolean isFinished;
	public List<AStarNode> nodes = new ArrayList<AStarNode>();
	public int index = 0;

	public AStarPath(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public void addNodeFront(AStarNode node) {
		nodes.add(0, node);
	}

	public boolean isDone() {
		return index >= nodes.size();
	}

	public AStarNode getCurrent() {
		return nodes.get(index);
	}

	public void next() {
		++index;
	}

	public int size() {
		return nodes.size();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Array<PathNode> toPathNodes() {
		final Array<PathNode> pathNodes = new Array<PathNode>(nodes.size());
		for (int nodeIndex = 0; nodeIndex < nodes.size(); ++nodeIndex) {
			AStarNode node = nodes.get(nodeIndex);
			pathNodes.add(new PathNode(new Vector2(node.x * GRID_SIZE, node.y * GRID_SIZE), GRID_SIZE, GRID_SIZE, nodeIndex));
		}
		return pathNodes;
	}

}
