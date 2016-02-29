package net.benjaminneukom.heavydefense.pathfinding;

import java.util.HashMap;
import java.util.PriorityQueue;

import net.benjaminneukom.heavydefense.util.MoreMath;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AStar {

	private static HashMap<String, AStarNode> nodes = new HashMap<String, AStarNode>();

	private static final Array<Vector2> DIRECTIONS = new Array<Vector2>(4);

	private static boolean[][] map;
	private static int endX;
	private static int endY;

	static {
		DIRECTIONS.add(new Vector2(-1, 0));
		DIRECTIONS.add(new Vector2(1, 0));
		DIRECTIONS.add(new Vector2(0, 1));
		DIRECTIONS.add(new Vector2(0, -1));
	}

	public static AStarPath createPath(int startX, int startY, int endX, int endY, boolean[][] map) {

		AStar.map = map;
		AStar.endX = endX;
		AStar.endY = endY;

		nodes.clear();

		final AStarNode start = createNode(startX, startY);
		final AStarNode goal = createNode(endX, endY);

		if (startX == endX && startY == endY) return new AStarPath(true);

		final PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>();
		queue.add(start);

		AStarPath bestPath = null;
		while (queue.size() != 0) {
			AStarNode current = queue.poll();
			if (current.visited) continue;

			if (current == goal) {
				bestPath = reconstructPath(goal);
				break;
			}

			addNeighbors(current);

			current.visited = true;

			for (AStarNode neighbor : current.getNeighbors()) {
				if (neighbor.visited) continue;
				if (!canMove(neighbor.x, neighbor.y)) continue;

				final double distance = current.pathDistance + MoreMath.dist(current.x, current.y, neighbor.x, neighbor.y);

				if (neighbor.parent != null && distance >= neighbor.pathDistance) continue;

				neighbor.pathDistance = distance;

				neighbor.heuristicDistance = MoreMath.dist(neighbor.x, neighbor.y, goal.x, goal.y) + distance;
				if (neighbor.parent == null) {
					neighbor.priority = neighbor.heuristicDistance;
					queue.add(neighbor);
				} else
					neighbor.priority = neighbor.heuristicDistance;

				neighbor.parent = current;
			}
		}

		return bestPath == null ? new AStarPath(false) : bestPath;
	}

	private static boolean canMove(int x, int y) {
		// can move to the target node even it is outside of the map
		if (isTargetNode(x, y)) return true;

		// outside of the map
		if (x < 0 || y < 0 || x >= map.length || y >= map[x].length) return false;

		// check map if it is movable
		return !map[x][y];
	}

	private static boolean isTargetNode(int x, int y) {
		return x == endX && y == endY;
	}

	private static void addNeighbors(AStarNode n) {
		final int nodeX = n.x;
		final int nodeY = n.y;
		for (Vector2 d : DIRECTIONS) {
			final int newX = nodeX + (int) d.x;
			final int newY = nodeY + (int) d.y;
			if (canMove(newX, newY)) {
				n.addNeighbor(getNode(newX, newY));
			}
		}
	}

	private static AStarNode getNode(int x, int y) {
		final String hash = AStarNode.createHash(x, y);
		final AStarNode n = nodes.get(hash);
		return n == null ? createNode(x, y) : n;
	}

	private static AStarNode createNode(int x, int y) {
		final AStarNode n = new AStarNode(x, y);
		nodes.put(AStarNode.createHash(x, y), n);
		return n;
	}

	private static AStarPath reconstructPath(AStarNode goalNode) {
		final AStarPath path = new AStarPath(true);

		AStarNode node = goalNode;
		while (node != null) {
			path.addNodeFront(node);
			node = node.parent;
		}

		return path;
	}

}
