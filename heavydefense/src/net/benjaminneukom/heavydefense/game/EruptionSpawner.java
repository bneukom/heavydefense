package net.benjaminneukom.heavydefense.game;

import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Spawns lava eruptions on tiles marked with the given <code>key</code> property on tiles.
 * 
 */
public class EruptionSpawner extends Actor {

	private float currentTime;
	private float spawnTime;
	private String key;
	private AbstractWorld abstractWorld;

	private transient Array<Vector2> overlayPositioins = new Array<Vector2>();

	public EruptionSpawner() {
	}

	public EruptionSpawner(TiledMap map, final AbstractWorld abstractWorld, float spawnTime, String key) {
		this.abstractWorld = abstractWorld;
		this.spawnTime = spawnTime;
		this.key = key;

		init(map);
	}

	public void init(TiledMap map) {
		final MapLayers layers = map.getLayers();
		final int width = map.getProperties().get("width", Integer.class);
		final int height = map.getProperties().get("height", Integer.class);
		for (int layerIndex = 0; layerIndex < layers.getCount(); ++layerIndex) {
			final MapLayer layer = layers.get(layerIndex);
			if (layer instanceof TiledMapTileLayer) {
				final TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
				for (int x = 0; x < width; ++x) {
					for (int y = 0; y < height; ++y) {
						final Cell cell = tiledLayer.getCell(x, y);
						if (cell != null) {
							final TiledMapTile tile = cell.getTile();
							if (tile != null) {
								final boolean isLava = tile.getProperties().containsKey(key);

								if (isLava) {
									overlayPositioins.add(new Vector2(x, y));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		currentTime += delta;

		if (currentTime >= spawnTime) {
			if (overlayPositioins.size > 0) {
				final int randomIndex = (int) (Math.random() * overlayPositioins.size);
				final Vector2 position = overlayPositioins.get(randomIndex);
				abstractWorld.addMapActor(new LavaEruption(position.x * HeavyDefenseGame.TILE_SIZE, position.y * HeavyDefenseGame.TILE_SIZE, abstractWorld));
			}
			currentTime = 0;
		}

	}

}
