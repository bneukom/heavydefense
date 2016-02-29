package net.benjaminneukom.heavydefense.enemies;

import static net.benjaminneukom.heavydefense.HeavyDefenseGame.GRID_SIZE;

import java.util.Comparator;
import java.util.Iterator;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.PostSerialization;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner.KillListener;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner.ReachedEndListener;
import net.benjaminneukom.heavydefense.enemies.debuffs.Debuff;
import net.benjaminneukom.heavydefense.enemies.debuffs.FireDebuff;
import net.benjaminneukom.heavydefense.game.PathNode;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.game.worlds.GameWorld;
import net.benjaminneukom.heavydefense.pathfinding.AStar;
import net.benjaminneukom.heavydefense.pathfinding.AStarPath;
import net.benjaminneukom.heavydefense.tower.AbstractTower;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractEnemy extends Group implements Comparable<AbstractEnemy>, PostSerialization {

	private float speed;
	private float currentSpeed;
	private Vector2 velocity;
	private int wave;

	protected transient TextureAtlas enemyAtlas;
	private String textureSheetName;
	private transient TextureRegion[] sprites;
	private transient TextureRegion currentSprite;
	private int textureWidth;
	private int textureHeight;

	private int targetIndex = 0;
	private PathNode currentTarget;
	private transient Array<PathNode> pathNodes;
	private transient PathNode end;

	private float maxHealthPoints;
	private float healthPoints;
	private boolean renderHealthPoints = true;
	protected AbstractWorld abstractWorld;

	private boolean reachedEnd = false;
	private boolean dead = false;
	private float money;

	private Array<Debuff> debuffs = new Array<Debuff>();
	private transient Comparator<Debuff> layerComparator = new Comparator<Debuff>() {

		@Override
		public int compare(Debuff o1, Debuff o2) {
			return o1.getRenderPriority() - o2.getRenderPriority();
		}
	};

	private transient Array<KillListener> killListeners = new Array<KillListener>();
	private transient Array<ReachedEndListener> reachedEndListeners = new Array<ReachedEndListener>();

	private EnemyType enemyType;
	private ArmorType armorType;

	public AbstractEnemy() {
		killListeners = new Array<KillListener>();
		reachedEndListeners = new Array<ReachedEndListener>();
		setTouchable(Touchable.disabled);
	}

	public AbstractEnemy(float x, float y, Vector2 velocity, EnemyType enemyType, ArmorType armorType, Array<PathNode> nodes, String textureSheetName, AbstractWorld abstractWorld,
			float healthPoints, float speed, int wave, float money) {
		this.enemyType = enemyType;
		this.armorType = armorType;
		this.textureSheetName = textureSheetName;
		this.wave = wave;
		this.abstractWorld = abstractWorld;
		this.maxHealthPoints = healthPoints;
		this.healthPoints = healthPoints;
		this.money = money;
		this.speed = speed;
		this.velocity = velocity;
		this.targetIndex = 0;

		// copy nodes
		this.pathNodes = new Array<PathNode>(nodes.size);
		for (PathNode pathNode : nodes) {
			this.pathNodes.add(pathNode.copy());
		}

		this.currentTarget = pathNodes.get(++targetIndex);
		this.end = pathNodes.get(pathNodes.size - 1);

		setPosition(x + currentTarget.getWidth() / 2, y + currentTarget.getHeight() / 2);
		setTouchable(Touchable.disabled);
	}

	/**
	 * Updates the {@link PathNode}. Call this method if the {@link GameWorld#getCollisionMap()} of the world has changed.
	 * 
	 * @param world
	 */
	public void updatePathNodes(final GameWorld world) {
		updatePathNodes(world, true);

	}

	/**
	 * Updates the {@link PathNode}. Call this method if the {@link GameWorld#getCollisionMap()} of the world has changed.
	 * 
	 * @param world
	 * @param updateTargetIndex
	 *            whether to update (reset) the target and the targetIndex.
	 */
	public void updatePathNodes(final GameWorld world, boolean updateTarget) {

		final boolean[][] collisionMap = world.getCollisionMap();

		final int startX = (int) ((getX() + getWidth() / 2) / GRID_SIZE);
		final int startY = (int) ((getY() + getHeight() / 2) / GRID_SIZE);
		final int endX = (int) (end.getX() / GRID_SIZE);
		final int endY = (int) (end.getY() / GRID_SIZE);
		final AStarPath path = AStar.createPath(startX, startY, endX, endY, collisionMap);

		pathNodes.clear();

		final Array<PathNode> newPathNodes = path.toPathNodes();
		pathNodes.addAll(newPathNodes);

		if (updateTarget) {
			final int currentTargetIndex = newPathNodes.indexOf(currentTarget, false);
			if (currentTargetIndex != -1) {
				targetIndex = currentTargetIndex;
			} else {
				targetIndex = 0;
			}

			if (pathNodes.size > targetIndex)
				currentTarget = pathNodes.get(targetIndex);
		}
	}

	/**
	 * Callback after a new tower has been added to the world
	 * 
	 * @param tower
	 *            the tower which has been added
	 */
	public final void newTower(AbstractTower tower) {
		// new tower intersects current path node
		if (tower.getX() < currentTarget.getX() + currentTarget.getWidth() && tower.getX() + tower.getWidth() > currentTarget.getX()
				&& tower.getY() < currentTarget.getY() + currentTarget.getHeight() && tower.getY() + tower.getHeight() > currentTarget.getY()) {
			// follow the usual path in this case
			setTargetIndex(0);
		}
	}

	/**
	 * Sets the {@link #targetIndex} and current target to the given <code>index</code>.
	 * 
	 * @param index
	 */
	public void setTargetIndex(int index) {
		currentTarget = pathNodes.get(index);
		targetIndex = index;
	}

	@Override
	public void postSerialized() {

		enemyAtlas = Assets.getTextureAtlas(Assets.ENEMY_ATLAS);

		final AtlasRegion enemySheet = enemyAtlas.findRegion(textureSheetName);

		this.textureWidth = enemySheet.getRegionWidth() / 3;
		this.textureHeight = enemySheet.getRegionHeight() / 3;
		this.sprites = Assets.getOrientationSprites(enemySheet, textureWidth, textureHeight);
		this.currentSprite = sprites[0];

		// add the debuffs
		for (Debuff debuff : debuffs) {
			addActor(debuff);
		}

		// set initial position
		setSize(textureWidth, textureHeight);
	}

	public Array<Debuff> getDebuffs() {
		return debuffs;
	}

	public Array<PathNode> getPathNodes() {
		return pathNodes;
	}

	public PathNode getCurrentTarget() {
		return currentTarget;
	}

	public PathNode getCurrentTarget() {
		return currentTarget;
	}

	public void removed() {
		for (Debuff debuff : debuffs) {
			debuff.end(this);
		}

		debuffs.clear();
		killListeners.clear();
		reachedEndListeners.clear();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		currentSpeed = speed;

		// update and remove buffs
		final Iterator<Debuff> iterator = debuffs.iterator();
		while (iterator.hasNext()) {
			final Debuff debuff = iterator.next();
			debuff.applyDebuff(this);
			if (debuff.ended()) {
				iterator.remove();
				debuff.end(this);
				removeActor(debuff);
			}
		}

		// move
		float targetX = currentTarget.getPosition().x - textureWidth / 2 + currentTarget.getWidth() / 2;
		float targetY = currentTarget.getPosition().y - textureHeight / 2 + currentTarget.getHeight() / 2;
		velocity.set(targetX - getX(), targetY - getY());
		velocity.nor().scl(currentSpeed);

		translate(velocity.x * delta, velocity.y * delta);

		float dx = getX() - targetX;
		float dy = getY() - targetY;

		// reached target node
		if (Math.hypot(dx, dy) < 3) {
			if (targetIndex < pathNodes.size - 1) {
				currentTarget = pathNodes.get(++targetIndex);
			} else {
				// at the end
				if (!reachedEnd) {
					fireReachedEndEvent();
				}
				reachedEnd = true;
				abstractWorld.removeEnemy(this);

			}
		}

		// update rotation
		updateRotation();
	}

	protected void updateRotation() {
		double angle = Math.toDegrees(Math.atan2(velocity.y, velocity.x));
		if (angle > -180 && angle <= -157.5) {
			// 3
			changeSprite(3);
		} else if (angle > -157.5 && angle <= -112.5) {
			// 0
			changeSprite(0);
		} else if (angle > -112.5 && angle <= -67.5) {
			// 1
			changeSprite(1);
		} else if (angle > -67.5 && angle <= -22.5) {
			// 2
			changeSprite(2);
		} else if (angle > -22.5 && angle <= 22.5) {
			// 4
			changeSprite(4);
		} else if (angle > 22.5 && angle <= 67.5) {
			// 7
			changeSprite(7);
		} else if (angle > 67.5 && angle <= 112.5) {
			// 6
			changeSprite(6);
		} else if (angle > 112.5 && angle <= 157.5) {
			// 5
			changeSprite(5);
		} else if (angle > 157.5 && angle <= 180) {
			// 3
			changeSprite(3);
		}
	}

	protected void changeSprite(int index) {
		currentSprite = sprites[index];
	}

	//	private final transient ShapeRenderer shapeRenderer = new ShapeRenderer();
	//
	//	private final boolean almostEqual(float a, float b) {
	//		return Math.abs(a - b) < 1f;
	//	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		for (Debuff debuff : debuffs) {
			debuff.beforeRender(batch);
		}

		batch.draw(currentSprite, getX(), getY());

		for (Debuff debuff : debuffs) {
			debuff.afterRender(batch);
		}

		// draw target
		//		batch.end();
		//		Gdx.gl.glEnable(GL10.GL_BLEND);
		//		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//		Gdx.gl.glLineWidth(1);
		//
		//		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		//		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		//		shapeRenderer.begin(ShapeType.Filled);
		//		for (PathNode node : pathNodes) {
		//			shapeRenderer.setColor(1, 0, 0, 0.5f);
		//			if (currentTarget != null && node == currentTarget
		//					|| almostEqual(node.getX(), currentTarget.getX()) && almostEqual(node.getY(), currentTarget.getY())) {
		//				shapeRenderer.setColor(0, 1, 0, 0.5f);
		//			}
		//			shapeRenderer.rect(node.getX(), node.getY(), node.getWidth(), node.getHeight());
		//		}
		//		shapeRenderer.end();
		//		Gdx.gl.glDisable(GL10.GL_BLEND);
		//		batch.begin();

	}

	@Override
	public int getZIndex() {
		return (int) getY();
	}

	public void setRenderHealthPoints(boolean renderHealthPoints) {
		this.renderHealthPoints = renderHealthPoints;
	}

	public void addKillListener(KillListener killListener) {
		this.killListeners.add(killListener);
	}

	public void addKillListeners(Array<KillListener> killListeners) {
		this.killListeners.addAll(killListeners);
	}

	public void addReachedEndListeners(Array<ReachedEndListener> reachedEndListeners) {
		this.reachedEndListeners.addAll(reachedEndListeners);
	}

	public void addReachedEndListener(ReachedEndListener reachedEndListeners) {
		this.reachedEndListeners.add(reachedEndListeners);
	}

	private void fireReachedEndEvent() {
		for (ReachedEndListener reachedEndListener : reachedEndListeners) {
			reachedEndListener.reachedEnd(this);
		}
	}

	public void doDamage(float damage) {
		if (dead)
			return;

		// increase damage if burning
		float upgradeValue = TD.getUpgradeValue(UpgradeType.FIRE_BURN_DEBUFF);
		if (containsDebuffType(FireDebuff.class)) {
			damage += damage * upgradeValue;
		}

		healthPoints -= damage;
		healthPoints = Math.max(0, healthPoints);
		if (healthPoints == 0) {
			onKilled();

			dead = true;

			// fire killed event
			for (KillListener killListener : killListeners) {
				killListener.killed(this, wave);
			}

			killListeners.clear();
			abstractWorld.removeEnemy(this);
		}
	}

	protected void onKilled() {
		abstractWorld.addOverlayActor(new Explosion(getX(), getY(), Assets.OVERLAY_ATLAS, Assets.EXPLOSION, 9, 1, abstractWorld));
	}

	public boolean isDead() {
		return healthPoints <= 0;
	}

	public boolean hasReachedEnd() {
		return reachedEnd;
	}

	public void multiplySpeed(float value) {
		currentSpeed *= value;
	}

	public float getMoney() {
		return money;
	}

	public void addDebuff(Debuff debuff) {
		// no duplicates
		if (debuff.isUnique() && debuffs.contains(debuff, false)) {
			final int oldDebuffIndex = debuffs.indexOf(debuff, false);
			final Debuff oldDebuff = debuffs.get(oldDebuffIndex);

			// if the new debuff is worse or equal to the old one, just return
			if (debuff.compareTo(oldDebuff) <= 0) {
				return;
			} else {
				// in case the new debuff is better, remove the old one and add the new
				final Debuff removed = debuffs.removeIndex(oldDebuffIndex);
				removeActor(removed);
			}
		}

		addActor(debuff);
		debuffs.add(debuff);
		debuffs.sort(layerComparator);
		debuff.start(this);
	}

	/**
	 * Returns the first {@link Debuff} with the given type or <code>null</code> if none found.
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Debuff> T getDebuff(Class<T> type) {
		for (Debuff debuff : debuffs) {
			if (debuff.getClass() == type) {
				return (T) debuff;
			}
		}

		return null;
	}

	public boolean containsDebuffType(Class<? extends Debuff> type) {
		for (Debuff debuff : debuffs) {
			if (debuff.getClass() == type) {
				return true;
			}
		}

		return false;
	}

	public ArmorType getArmorType() {
		return armorType;
	}

	public EnemyType getEnemyType() {
		return enemyType;
	}

	public String getTextureSheetPath() {
		return textureSheetName;
	}

	public int getTextureWidth() {
		return textureWidth;
	}

	public int getTextureHeight() {
		return textureHeight;
	}

	public float getSpeed() {
		return speed;
	}

	public float getHealthPoints() {
		return healthPoints;
	}

	public int getWave() {
		return wave;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public boolean isRenderHealthPoints() {
		return renderHealthPoints;
	}

	public float getMaxHealthPoints() {
		return maxHealthPoints;
	}

	@Override
	public int compareTo(AbstractEnemy o) {
		if (o instanceof AirEnemy)
			return -1;

		return (int) (getY() - o.getY());
	}

	public void centerAroundNodePosition() {
		translate(-textureWidth / 2, -textureHeight / 2);
	}

	public void setPathNodes(Array<PathNode> pathNodes) {
		// copy nodes
		this.pathNodes = new Array<PathNode>(pathNodes.size);
		for (PathNode pathNode : pathNodes) {
			this.pathNodes.add(pathNode.copy());
		}
		this.end = this.pathNodes.get(pathNodes.size - 1);
	}

}
