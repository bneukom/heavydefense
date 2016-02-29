package net.benjaminneukom.heavydefense.game.worlds;

import static net.benjaminneukom.heavydefense.HeavyDefenseGame.GRID_SIZE;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.pathfinding.AStar;
import net.benjaminneukom.heavydefense.pathfinding.AStarPath;
import net.benjaminneukom.heavydefense.tower.AbstractTower;

import com.badlogic.gdx.utils.Array;

public class NoRoadsGameWorld extends GameWorld {

	private transient AStarPath fullPath;
	private transient Array<PathNode> fullPathNodes;

	public NoRoadsGameWorld() {
		super();
	}

	public NoRoadsGameWorld(String mapPath) {
		super(mapPath);

		createFullPath();
	}

	@Override
	public void addEnemy(AbstractEnemy abstractEnemy) {
		super.addEnemy(abstractEnemy);

		abstractEnemy.setPathNodes(fullPathNodes);
		abstractEnemy.setTargetIndex(0);
	}

	@Override
	public void addTower(AbstractTower tower) {
		super.addTower(tower);

		createFullPath();
		updateEnemyPaths();

	}

	@Override
	public void removeTower(AbstractTower tower) {
		super.removeTower(tower);

		updateEnemyPaths();
	}

	/**
	 * Updates the {@link PathNode} targets for each enemy currently on the map.
	 */
	private void updateEnemyPaths() {
		for (AbstractEnemy enemy : getEnemies()) {
			enemy.updatePathNodes(this);
		}
	}

	@Override
	public boolean canBuildTower(float x, float y) {
		// check for default world intersections
		final boolean canBuildTower = super.canBuildTower(x, y);
		if (!canBuildTower) return false;

		// check if on a cell which contains an enemy
		final int cellX = (int) (x / GRID_SIZE);
		final int cellY = (int) (y / GRID_SIZE);
		final int realCellX = cellX * GRID_SIZE;
		final int realCellY = cellY * GRID_SIZE;
		for (AbstractEnemy enemy : getEnemies()) {
			final float enemyX = enemy.getX();
			final float enemyY = enemy.getY();
			final float enemyWidth = enemy.getWidth();
			final float enemyHeight = enemy.getHeight();

			// x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y
			if (enemyX < realCellX + GRID_SIZE && enemyX + enemyWidth > realCellX && enemyY < realCellY + GRID_SIZE && enemyY + enemyHeight > realCellY) {
				return false;
			}
		}

		// check if this cell would prohibit enemies from reaching the end
		// Note that a free road map ONLY EVER has to path nodes (start and end)
		final PathNode startNode = pathNodes.get(0);
		final PathNode targetNode = pathNodes.get(1);
		final int startX = (int) (startNode.getX() / GRID_SIZE);
		final int startY = (int) (startNode.getY() / GRID_SIZE);
		final int endX = (int) (targetNode.getX() / GRID_SIZE);
		final int endY = (int) (targetNode.getY() / GRID_SIZE);

		// check if path is still finishable with new cell occupied
		final boolean pathFinishable = canFinishPath(cellX, cellY, startX, startY, endX, endY);

		if (!pathFinishable) return false;

		// check if all enemies on the grid can finish the map with this new tower set
		if (!canAllEnemiesFinish(cellX, cellY, endX, endY)) return false;

		// tower can be built
		return true;
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		createFullPath();
		// find new path
		for (AbstractEnemy enemy : getEnemies()) {
			enemy.updatePathNodes(this, false);

			if (enemy.getPathNodes().size > 0) {
				// find the serialized pathNode in the new node list and update the target
				matchTargetPathNode(enemy);
			} // otherwise there is no path (enemy is already on target, so just move to the target)

		}
	}

	/**
	 * Returns <code>true</code> if and only if the given cell does not disrupt the path from the given start to end point. Meaning a path from start to end will still exist even
	 * if the given cell is occupied.
	 * 
	 * @param cellX
	 * @param cellY
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 */
	private boolean canFinishPath(final int cellX, final int cellY, final int startX, final int startY, final int endX, final int endY) {
		final boolean[][] collision = getCollisionMap();
		collision[cellX][cellY] = true;

		final boolean pathFinishable = AStar.createPath(startX, startY, endX, endY, collision).isFinished;

		collision[cellX][cellY] = false;
		return pathFinishable;
	}

	/**
	 * Returns <code>true</code> if and only if all enemies can reach the given finish point with the given cell occupied.
	 * 
	 * @param cellX
	 * @param cellY
	 * @param endX
	 * @param endY
	 * @return
	 */
	private boolean canAllEnemiesFinish(final int cellX, final int cellY, final int endX, final int endY) {
		for (AbstractEnemy enemy : getEnemies()) {
			final int startX = (int) (enemy.getX() / GRID_SIZE);
			final int startY = (int) (enemy.getY() / GRID_SIZE);
			if (!canFinishPath(cellX, cellY, startX, startY, endX, endY)) {
				return false;
			}
		}
		return true;
	}

	private void createFullPath() {
		final PathNode startNode = pathNodes.get(0);
		final PathNode targetNode = pathNodes.get(1);
		final int startX = (int) (startNode.getX() / GRID_SIZE);
		final int startY = (int) (startNode.getY() / GRID_SIZE);
		final int endX = (int) (targetNode.getX() / GRID_SIZE);
		final int endY = (int) (targetNode.getY() / GRID_SIZE);

		fullPath = AStar.createPath(startX, startY, endX, endY, getCollisionMap());
		fullPathNodes = fullPath.toPathNodes();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.NO_ROADS;
	}
}
