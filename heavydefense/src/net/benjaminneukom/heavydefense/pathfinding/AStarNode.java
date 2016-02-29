package net.benjaminneukom.heavydefense.pathfinding;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Pool.Poolable;

public class AStarNode implements Comparable<AStarNode>, Poolable {
	public int x;
	public int y;

	public boolean visited = false;
	public double pathDistance = 0;
	public double heuristicDistance = Double.MAX_VALUE;
	public AStarNode parent = null;
	public double priority = 0;

	public List<AStarNode> neighbors = new ArrayList<AStarNode>();

	public AStarNode() {
	}

	public AStarNode(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void addNeighbor(AStarNode n) {
		neighbors.add(n);
	}

	public List<AStarNode> getNeighbors() {
		return neighbors;
	}

	public int compare(AStarNode n1, AStarNode n2) {
		if (n1.priority == n2.priority) return 0;
		return n1.priority < n2.priority ? -1 : 1;
	}

	public int compareTo(AStarNode o) {
		return compare(this, o);
	}

	public static String createHash(int x, int y) {
		return (int) x + "_" + (int) y;
	}

	@Override
	public void reset() {
		this.x = 0;
		this.y = 0;
	}

}
