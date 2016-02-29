package net.benjaminneukom.heavydefense.game.worlds;

import static net.benjaminneukom.heavydefense.HeavyDefenseGame.GRID_SIZE;

import java.util.Iterator;

import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.Player;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.screens.MenuScreen.Difficulty;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;
import net.benjaminneukom.heavydefense.util.ShapeRendererExt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class GameWorld extends AbstractWorld {

	private int difficulty;
	private transient AbstractTower currentTowerShow;
	private transient boolean isBuyingTower;
	private transient float buyTouchX;
	private transient float buyTouchY;
	private transient Array<InvalidPlaceHighlighter> invalidHighligters = new Array<InvalidPlaceHighlighter>();
	private Player player;
	private WaveSpawner waveSpawner;
	private Difficulty levelDifficulty;
	private boolean mapOfTheDay;
	private int totalTowersBuilt;
	private transient TextureRegion directionTexture;
	private transient boolean[][] collisionMap;
	private transient Array<Direction> directions;

	public GameWorld() {
		invalidHighligters = new Array<InvalidPlaceHighlighter>();
		directions = new Array<Direction>();
	}

	public GameWorld(String mapPath) {
		super(mapPath);

		// increase the number of games which this player has started
		TD.increaseGamesStarted();

		this.isBuyingTower = false;

		final int liveIncrease = (int) TD.getUpgrade(UpgradeType.INCREASED_LIVE).getValue();
		final float moneyIncrease = TD.getUpgrade(UpgradeType.INCREASED_START_MONEY).getValue();
		this.player = new Player(15 + liveIncrease, 800 + moneyIncrease);
		this.directionTexture = new TextureRegion(new Texture(Gdx.files.internal("direction.png")));
	}

	@Override
	public void addTower(AbstractTower tower) {
		super.addTower(tower);

		int collisionIndexX = (int) (tower.getX() / GRID_SIZE);
		int collisionIndexY = (int) (tower.getY() / GRID_SIZE);
		collisionMap[collisionIndexX][collisionIndexY] = true;

		totalTowersBuilt++;

		newTower(tower);
	}

	/**
	 * Notify enemies that a new tower has been added.
	 * 
	 * @param tower
	 *            the tower which has been added
	 */
	private void newTower(AbstractTower tower) {
		for (AbstractEnemy enemy : getEnemies()) {
			enemy.newTower(tower);
		}
	}

	@Override
	public void removeTower(AbstractTower tower) {
		super.removeTower(tower);

		int collisionIndexX = (int) (tower.getX() / GRID_SIZE);
		int collisionIndexY = (int) (tower.getY() / GRID_SIZE);
		collisionMap[collisionIndexX][collisionIndexY] = false;

	}

	public void setWaveSpawner(WaveSpawner spawner) {
		this.waveSpawner = spawner;
		addActor(waveSpawner);
	}

	public void setIsBuyingTower(float x, float y) {
		this.buyTouchX = x;
		this.buyTouchY = y;
		this.isBuyingTower = true;
	}

	public void setDisplayTower(AbstractTower tower) {
		currentTowerShow = tower;
	}

	public void stopBuyingTower() {
		isBuyingTower = false;
	}

	public float getBuyTouchX() {
		return buyTouchX;
	}

	public float getBuyTouchY() {
		return buyTouchY;
	}

	public Player getPlayer() {
		return player;
	}

	public WaveSpawner getWaveSpawner() {
		return waveSpawner;
	}

	/**
	 * Shows the user a red highlight for a short amount of time to indicate an invalid tower placement.
	 * 
	 * @param x
	 * @param y
	 */
	public void showInvalidClick(int x, int y) {
		for (InvalidPlaceHighlighter invalidPlaceHighlighter : invalidHighligters) {
			if (invalidPlaceHighlighter.x == x && invalidPlaceHighlighter.y == x) {
				invalidPlaceHighlighter.reset();
				return;
			}
		}

		invalidHighligters.add(new InvalidPlaceHighlighter(x, y));
	}

	public void setIsMapOfTheDay(boolean mapOfTheDay) {
		this.mapOfTheDay = mapOfTheDay;
	}

	public void setLevelDifficulyt(Difficulty difficulty) {
		levelDifficulty = difficulty;
	}

	public Difficulty getLevelDifficulty() {
		return levelDifficulty;
	}

	public boolean isMapOfTheDay() {
		return mapOfTheDay;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getTotalTowersBuilt() {
		return totalTowersBuilt;
	}

	@Override
	protected void renderAfterMap(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		super.renderAfterMap(batch, shapeRenderer);

		// render directions
		batch.begin();
		for (Direction direction : directions) {
			final Rectangle bounds = direction.bounds;
			final float x = bounds.x + bounds.width / 2 - directionTexture.getRegionWidth() / 2;
			final float y = bounds.y + bounds.height / 2 - directionTexture.getRegionHeight() / 2;
			batch.draw(directionTexture, x, y, bounds.width / 2, bounds.height / 2, bounds.width, bounds.height, 1, 1, direction.rotation, true);

		}

		batch.end();

		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(1);

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

		if (isBuyingTower) {
			shapeRenderer.begin(ShapeType.Filled);

			// draw tower placement
			shapeRenderer.setColor(27f / 255f, 224f / 255f, 57f / 255f, 0.35f);
			shapeRenderer.rect(buyTouchX, buyTouchY, HeavyDefenseGame.TILE_SIZE * 3, HeavyDefenseGame.TILE_SIZE * 3);

			// render collisions
			shapeRenderer.setColor(1f, 0f, 0f, 0.1f);
			for (int x = 0; x < collisionMap.length; ++x) {
				for (int y = 0; y < collisionMap[x].length; ++y) {
					if (collisionMap[x][y])
						shapeRenderer.rect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
				}
			}

			shapeRenderer.end();
		}
	}

	@Override
	protected void overlayRender(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		super.overlayRender(batch, shapeRenderer);

		// draw invalid highliter (if user clicked on a field with collision)
		if (invalidHighligters.size > 0) {
			batch.end();

			Gdx.gl.glEnable(GL10.GL_BLEND);
			Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);

			for (InvalidPlaceHighlighter invalidPlaceHighlighter : invalidHighligters) {
				invalidPlaceHighlighter.render(shapeRenderer);
			}

			shapeRenderer.end();
			batch.begin();
		}

		// draw range of current tower
		if (currentTowerShow != null) {
			batch.end();

			Gdx.gl.glEnable(GL10.GL_BLEND);
			Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

			shapeRenderer.begin(ShapeType.Filled);

			shapeRenderer.setColor(27f / 255f, 224f / 255f, 57f / 255f, 0.25f);

			float towerX = currentTowerShow.getX();
			float towerY = currentTowerShow.getY();

			shapeRenderer.circle(towerX + currentTowerShow.getWidth() / 2, towerY + currentTowerShow.getHeight() / 2,
					currentTowerShow.getRange());

			shapeRenderer.end();

			shapeRenderer.begin(ShapeType.Filled);

			shapeRenderer.setColor(1, 1, 1, 0.4f);

			float towerSize = 3 * 20;
			ShapeRendererExt.line(shapeRenderer, towerX + 2, towerY, towerX + 2, towerY + towerSize - 3, 3);
			ShapeRendererExt.line(shapeRenderer, towerX + towerSize - 2, towerY, towerX + towerSize - 2, towerY + towerSize - 3, 3);

			ShapeRendererExt.line(shapeRenderer, towerX + 3.5f, towerY + 1.5f, towerX + towerSize * 0.25f + 1.5f, towerY + 1.5f, 3);
			ShapeRendererExt.line(shapeRenderer, towerX + 3.5f, towerY + towerSize - 1.5f, towerX + towerSize * 0.25f + 1.5f, towerY + towerSize - 1.5f, 3);

			ShapeRendererExt.line(shapeRenderer, towerX + towerSize - 3.5f, towerY + 1.5f, towerX + towerSize - towerSize * 0.25f - 3.5f, towerY + 1.5f, 3);
			ShapeRendererExt.line(shapeRenderer, towerX + towerSize - 3.5f, towerY + towerSize - 1.5f, towerX + towerSize - towerSize * 0.25f - 3.5f, towerY + towerSize - 1.5f,
					3);

			shapeRenderer.end();

			batch.begin();
		}
	}

	@Override
	protected void loadMapObject(MapObjects objects) {
		super.loadMapObject(objects);

		this.directions = new Array<Direction>();

		final Array<Rectangle> collisions = new Array<Rectangle>(objects.getCount());
		for (MapObject mapObject : objects) {

			final RectangleMapObject collisionObject = (RectangleMapObject) mapObject;
			final Rectangle rectangle = collisionObject.getRectangle();

			final String objectType = mapObject.getProperties().get("type", String.class);
			if (objectType.equals("Collision")) {
				collisions.add(new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
			} else if (objectType.equals("Direction")) {
				final int rotation = Integer.parseInt(mapObject.getProperties().get("rotation", String.class));
				directions.add(new Direction(rectangle, rotation));
			}
		}

		// load
		final int mapWidth = getMap().getProperties().get("width", Integer.class);
		final int mapHeight = getMap().getProperties().get("height", Integer.class);

		collisionMap = new boolean[mapWidth / 3][mapHeight / 3];

		final Rectangle buffer = new Rectangle();
		for (int x = 0; x < collisionMap.length; ++x) {
			for (int y = 0; y < collisionMap[x].length; ++y) {
				buffer.x = x * GRID_SIZE;
				buffer.y = y * GRID_SIZE;
				buffer.width = GRID_SIZE;
				buffer.height = GRID_SIZE;

				for (Rectangle rectangle : collisions) {
					if (rectangle.overlaps(buffer)) {
						// if (intersects(rectangle, buffer)) {
						collisionMap[x][y] = true;
						break;
					}
				}
			}
		}

		// Print the amount of towers which can be built in this world.
		//		int numberOfPossibleTowers = 0;
		//		for (int x = 0; x < collisionMap.length; ++x) {
		//			for (int y = 0; y < collisionMap[x].length; ++y) {
		//				if (!collisionMap[x][y]) numberOfPossibleTowers++;
		//			}
		//		}
		//
		//		System.out.println("Possible Towers in " + getMapPath() + ": " + numberOfPossibleTowers);
	}

	public boolean[][] getCollisionMap() {
		return collisionMap;
	}

	@Override
	public void rebuildScene() {
		super.rebuildScene();

		waveSpawner.setAbstractWorld(this);
		addActor(waveSpawner);
	}

	/**
	 * Checks if the given (x, y) intersects the world somehow. For example if the map does not allow it or if the {@link GameMode} is {@link GameMode#NO_ROADS} and the tower would
	 * permit enemies to pass to the end.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canBuildTower(float x, float y) {
		final int gridX = (int) (x / GRID_SIZE);
		final int gridY = (int) (y / GRID_SIZE);

		// check out of bounds
		if (gridX < 0 || gridY < 0 || gridX > collisionMap.length || gridY > collisionMap[gridX].length) return false;

		return !collisionMap[gridX][gridY];
	}

	@Override
	public void act(float delta) {

		// TODO measure time it takes to act();
		long start = System.currentTimeMillis();
		super.act(delta);

		// update highlighters
		final Iterator<InvalidPlaceHighlighter> invalidHighlitIterator = invalidHighligters.iterator();
		while (invalidHighlitIterator.hasNext()) {
			final InvalidPlaceHighlighter next = invalidHighlitIterator.next();
			if (next.time <= 0) {
				invalidHighlitIterator.remove();
			} else {
				next.update(delta);
			}
		}

		System.out.println(System.currentTimeMillis() - start + "ms");
	}

	static class InvalidPlaceHighlighter {
		private static final float MAX_TIME = 0.35f;

		private float time = MAX_TIME;

		int x;
		int y;

		private InvalidPlaceHighlighter(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public void reset() {
			time = MAX_TIME;
		}

		public void update(float deltaT) {
			time -= deltaT;
			time = Math.max(0, time);
		}

		public void render(ShapeRenderer shapeRenderer) {
			if (time <= 0)
				return;

			drawRect(shapeRenderer, 0.3f);

		}

		private void drawRect(ShapeRenderer renderer, float alpha) {
			final float changedAlpha = Math.min((float) (alpha * time / MAX_TIME), 1f);
			renderer.setColor(1, 0, 0, changedAlpha);
			renderer.rect(x, y, GRID_SIZE, GRID_SIZE);
		}
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		this.directionTexture = new TextureRegion(new Texture(Gdx.files.internal("direction.png")));

		// re add collisions from towers
		for (AbstractTower tower : getTowers()) {
			int collisionX = (int) (tower.getX() / GRID_SIZE);
			int collisionY = (int) (tower.getY() / GRID_SIZE);
			collisionMap[collisionX][collisionY] = true;
		}

		// reset the path nodes for enemies currently in the world
		for (AbstractEnemy enemy : getEnemies()) {
			enemy.setPathNodes(getPathNodes());
		}
	}

	protected void matchTargetPathNode(final AbstractEnemy enemy) {
		Array<PathNode> enemyPathNodes = enemy.getPathNodes();

		int nodeIndex = 0;
		for (; nodeIndex < enemyPathNodes.size; ++nodeIndex) {
			PathNode pathNode = enemyPathNodes.get(nodeIndex);
			if (pathNode.equals(enemy.getCurrentTarget())) break;
		}

		if (nodeIndex < enemyPathNodes.size) {
			enemy.setTargetIndex(nodeIndex);
		} else {
			enemy.setTargetIndex(0);
		}
	}

	/**
	 * Returns the {@link GameMode} for this world.
	 * 
	 * @return
	 */
	public abstract GameMode getGameMode();

	private static class Direction {
		public final Rectangle bounds;
		public final float rotation;

		public Direction(Rectangle bounds, float rotation) {
			super();
			this.bounds = bounds;
			this.rotation = rotation;
		}

	}
}
