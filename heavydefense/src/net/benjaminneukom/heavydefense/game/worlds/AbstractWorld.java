package net.benjaminneukom.heavydefense.game.worlds;

import java.util.Comparator;
import java.util.Iterator;

import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.PostSerialization;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.tower.Lightning;
import net.benjaminneukom.heavydefense.util.ShapeRendererExt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * Contains the background map, enemies and towers for the game.
 */
public abstract class AbstractWorld extends Group implements PostSerialization {

	private transient OrthogonalTiledMapRenderer mapRenderer;
	private transient ShapeRenderer shapeRenderer;
	private transient TiledMap map;
	private String mapPath;

	private transient Group mapGroup = new Group();
	private transient Group enemiesGroup = new Group();
	private transient Group towersGroup = new Group();
	private transient Group overlayGroup = new Group();

	private Array<Actor> mapActors = new Array<Actor>();
	private Array<AbstractEnemy> enemies = new Array<AbstractEnemy>();
	private Array<AbstractTower> towers = new Array<AbstractTower>();
	private Array<Actor> overlayActors = new Array<Actor>();

	private Array<Lightning> lightnings = new Array<Lightning>();

	private boolean drawGrid = true;

	protected transient Array<PathNode> pathNodes = new Array<PathNode>();

	private transient ObjectSet<ParticleEffect> particleEffects = new ObjectSet<ParticleEffect>();
	private transient ObjectSet<ParticleEffect> removeAfterCompletion = new ObjectSet<ParticleEffect>();

	private transient OrthographicCamera camera;

	private transient static final int MAX_PARTICLE_EFFECTS = 5;
	private transient static final int MAX_LIGHTNING_DRAWING = 3;

	public AbstractWorld() {
		particleEffects = new ObjectSet<ParticleEffect>();
		removeAfterCompletion = new ObjectSet<ParticleEffect>();
	}

	public AbstractWorld(String mapPath) {
		this.mapPath = mapPath;

		loadMap(mapPath);

		this.mapRenderer = new OrthogonalTiledMapRenderer(map);
		this.shapeRenderer = new ShapeRenderer(7500);

		setHeight(map.getProperties().get("height", Integer.class) * HeavyDefenseGame.TILE_SIZE);
		setWidth(map.getProperties().get("width", Integer.class) * HeavyDefenseGame.TILE_SIZE);

		addActor(mapGroup);
		addActor(towersGroup);
		addActor(enemiesGroup);
		addActor(overlayGroup);
	}

	/**
	 * The camera is set after the world has been created
	 * 
	 * @param camera
	 */
	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	public void addEnemy(AbstractEnemy abstractEnemy) {
		enemiesGroup.addActor(abstractEnemy);
		enemies.add(abstractEnemy);

		enemiesGroup.getChildren().sort();
	}

	public TiledMap getMap() {
		return map;
	}

	public Array<PathNode> getPathNodes() {
		return pathNodes;
	}

	private void loadMap(String mapPath) {
		TmxMapLoader tmxMapLoader = new TmxMapLoader();

		final Parameters parameters = new TmxMapLoader.Parameters();
		parameters.yUp = false;

		// load the map synchronous
		this.map = tmxMapLoader.load(mapPath, parameters);

		// get path nodes and collisions
		for (MapLayer mapLayer : map.getLayers()) {
			if (mapLayer.getName().equals("Objects")) {
				final MapObjects objects = mapLayer.getObjects();

				final Array<Rectangle> collisions = new Array<Rectangle>(objects.getCount());

				loadMapObject(objects);

				for (MapObject mapObject : objects) {
					final int x = mapObject.getProperties().get("x", Integer.class);
					final int y = mapObject.getProperties().get("y", Integer.class);

					final RectangleMapObject collisionObject = (RectangleMapObject) mapObject;
					final Rectangle rectangle = collisionObject.getRectangle();

					if (mapObject.getProperties().get("type").equals("Collision")) {

						collisions.add(new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));

					} else if (mapObject.getProperties().get("type").equals("PathNode")) {
						final int nodeId = Integer.parseInt(mapObject.getProperties().get("nodeId", String.class));
						final PathNode node = new PathNode(new Vector2(x, y), rectangle.getWidth(), rectangle.getHeight(), nodeId);

						pathNodes.add(node);
					}
				}
			}
		}

		pathNodes.sort(new Comparator<PathNode>() {

			@Override
			public int compare(PathNode o1, PathNode o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	protected void loadMapObject(MapObjects objects) {

	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// update particle effects
		for (ParticleEffect effect : particleEffects) {
			effect.update(delta);
		}

		// remove lightning
		final Iterator<Lightning> lightningIterator = lightnings.iterator();
		while (lightningIterator.hasNext()) {
			final Lightning next = lightningIterator.next();
			if (next.getTime() <= 0) {
				lightningIterator.remove();
			} else {
				next.act(delta);
			}
		}

		// remove particle
		final Iterator<ParticleEffect> iterator = removeAfterCompletion.iterator();
		while (iterator.hasNext()) {
			final ParticleEffect next = iterator.next();
			if (next.isComplete()) {
				particleEffects.remove(next);
				iterator.remove();
			}
		}

	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// update the alpha value of the spritebatch
		final Color batchColor = batch.getColor();
		batch.setColor(batchColor.a, batch.getColor().g, batch.getColor().b, getColor().a);
		final Color mapBatchColor = mapRenderer.getSpriteBatch().getColor();
		mapRenderer.getSpriteBatch().setColor(mapBatchColor.a, mapRenderer.getSpriteBatch().getColor().g, mapRenderer.getSpriteBatch().getColor().b,
				getColor().a);

		// draw map
		mapRenderer.setView(camera);
		mapRenderer.render();
		batch.end();

		// after map render
		renderAfterMap(batch, shapeRenderer);

		if (drawGrid) {
			// render lines
			shapeRenderer.begin(ShapeType.Filled);

			Gdx.gl.glEnable(GL10.GL_BLEND);
			Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			Gdx.gl.glLineWidth(1);

			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setColor(35f / 255f, 35f / 255f, 35f / 255f, 0.13f);

			for (int x = 0; x < map.getProperties().get("width", Integer.class) * HeavyDefenseGame.TILE_SIZE; x += 60) {
				ShapeRendererExt.line(shapeRenderer, x, 0, x, map.getProperties().get("width", Integer.class) * HeavyDefenseGame.TILE_SIZE, 1);
			}
			for (int y = 0; y < map.getProperties().get("height", Integer.class) * HeavyDefenseGame.TILE_SIZE; y += 60) {
				ShapeRendererExt.line(shapeRenderer, 0, y, map.getProperties().get("width", Integer.class) * HeavyDefenseGame.TILE_SIZE, y, 1);
			}
			shapeRenderer.end();
		}

		batch.begin();

		// draw all actors (eg. towers and enemies)
		super.draw(batch, parentAlpha);

		//		 draw tower upgrades
		for (AbstractTower tower : towers) {
			if (tower.getLevel() >= 1) {
				final float xGrid = tower.getX() - tower.getX() % HeavyDefenseGame.GRID_SIZE;
				final float yGrid = tower.getY() - tower.getY() % HeavyDefenseGame.GRID_SIZE;
				batch.draw(tower.getLevelTextures()[tower.getLevel() - 1], xGrid, yGrid);
			}
		}

		// draw particle effects
		for (ParticleEffect particleEffect : particleEffects) {
			particleEffect.draw(batch);
		}

		batch.end();

		// draw lightnings
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		for (int lightningIndex = 0; lightningIndex < lightnings.size; ++lightningIndex) {
			if (lightningIndex >= MAX_LIGHTNING_DRAWING)
				break;

			final Lightning lightning = lightnings.get(lightningIndex);
			lightning.draw(shapeRenderer);
		}

		shapeRenderer.end();

		//		shapeRenderer.begin(ShapeType.Filled);
		//		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		//		shapeRenderer.setColor(1, 0, 0, 0.5f);
		//		ShapeRendererExt.consecutiveLines(shapeRenderer, new float[] { 100, 50, 300, 50, 400, 75, 450, 300 }, 15);
		//		shapeRenderer.end();

		Gdx.gl.glDisable(GL10.GL_BLEND);

		// draw enemy health
		for (AbstractEnemy enemy : enemies) {
			if (enemy.isRenderHealthPoints()) {

				Gdx.gl.glEnable(GL10.GL_BLEND);
				Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

				shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(4f / 255f, 184f / 255f, 31 / 255f, 0.4f);
				shapeRenderer.rect(enemy.getX(), enemy.getY() - 8, enemy.getWidth() * (enemy.getHealthPoints() / enemy.getMaxHealthPoints()), 4);
				shapeRenderer.end();

				Gdx.gl.glDisable(GL10.GL_BLEND);

			}
		}

		batch.begin();

		overlayRender(batch, shapeRenderer);

		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	protected void overlayRender(SpriteBatch batch, ShapeRenderer shapeRenderer2) {

	}

	protected void renderAfterMap(SpriteBatch batch, ShapeRenderer shapeRenderer2) {

	}

	public Array<AbstractTower> getTowers() {
		return towers;
	}

	public void setDrawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}

	public Array<AbstractEnemy> getEnemies() {
		return enemies;
	}

	public void removeTower(AbstractTower tower) {
		towersGroup.removeActor(tower);
		towers.removeValue(tower, false);

		tower.removed();
	}

	public void addTower(AbstractTower tower) {
		// add the new tower
		towersGroup.addActor(tower);
		towers.add(tower);

		towersGroup.getChildren().sort();
	}

	public void removeEnemy(AbstractEnemy abstractEnemy) {
		abstractEnemy.removed();
		enemies.removeValue(abstractEnemy, false);
		enemiesGroup.removeActor(abstractEnemy);
	}

	/**
	 * Adds an {@link Actor} which will be drawn right after the map has been drawn
	 * 
	 * @param actor
	 */
	public void addMapActor(Actor actor) {
		mapGroup.addActor(actor);
		mapActors.add(actor);
	}

	public void removeMapActor(Actor actor) {
		mapGroup.removeActor(actor);
		mapActors.removeValue(actor, false);
	}

	/**
	 * Adds an overlay {@link Actor} which will be drawn at the very end (for example for an explosion).
	 * 
	 * @param actor
	 */
	public void addOverlayActor(Actor actor) {
		overlayGroup.addActor(actor);
		overlayActors.add(actor);
	}

	public void removeOverlayActor(Actor actor) {
		overlayGroup.removeActor(actor);
		overlayActors.removeValue(actor, false);
	}

	public Array<Actor> getTowerActors() {
		return towersGroup.getChildren();
	}

	public int getActiveEnemyCount() {
		int activeEnemies = 0;
		for (AbstractEnemy abstractEnemy : enemies) {
			if (!abstractEnemy.isDead() && !abstractEnemy.hasReachedEnd()) {
				activeEnemies++;
			}
		}

		return activeEnemies;
	}

	public void removeParticleEffectAfterCompletion(ParticleEffect effect) {
		removeAfterCompletion.add(effect);
	}

	public void addParticleEffect(ParticleEffect effect, boolean treshholdAffected) {
		if (treshholdAffected && particleEffects.size > MAX_PARTICLE_EFFECTS) {
			return;
		}
		particleEffects.add(effect);
	}

	public void addParticleEffect(ParticleEffect effect) {
		particleEffects.add(effect);
	}

	public void removeParticleEffect(ParticleEffect effect) {
		particleEffects.remove(effect);
	}

	public void cleanUpParticleEffects() {
		final Iterator<ParticleEffect> iterator = particleEffects.iterator();

		while (iterator.hasNext()) {
			ParticleEffect next = iterator.next();
			next.allowCompletion();
			iterator.remove();
		}

		removeAfterCompletion.clear();
	}

	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}

	public String getMapPath() {
		return mapPath;
	}

	public void addLightning(Lightning lightning) {
		this.lightnings.add(lightning);
	}

	@Override
	public void postSerialized() {
		loadMap(mapPath);

		this.mapRenderer = new OrthogonalTiledMapRenderer(map);
		this.shapeRenderer = new ShapeRenderer(7500);

	}

	/**
	 * Adds all {@link Actor} and {@link Group}s to the {@link Stage}. This method should be called after deserializing a world. Subclasses must call the base class
	 * {@link #rebuildScene()} to add all actors and groups.
	 */
	public void rebuildScene() {
		addActor(mapGroup);
		addActor(towersGroup);
		addActor(enemiesGroup);
		addActor(overlayGroup);

		for (AbstractEnemy abstractEnemy : enemies) {
			enemiesGroup.addActor(abstractEnemy);
		}

		for (AbstractTower tower : towers) {
			towersGroup.addActor(tower);
		}

		for (Actor overlayActor : overlayActors) {
			overlayGroup.addActor(overlayActor);
		}

		for (Actor mapActor : mapActors) {
			mapGroup.addActor(mapActor);
		}

		enemiesGroup.getChildren().sort();

	}

}
