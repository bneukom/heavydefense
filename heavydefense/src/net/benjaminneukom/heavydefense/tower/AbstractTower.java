package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.HeavyDefenseGame;
import net.benjaminneukom.heavydefense.PostSerialization;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.util.MoreMath;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class AbstractTower extends Actor implements Comparable<AbstractTower>, PostSerialization {

	protected transient TextureAtlas towerTextureAtlas;
	protected transient TextureRegion currentSprite;

	protected String towerName;

	protected transient Texture icon;
	protected String iconPath;

	protected float range;
	protected AbstractWorld abstractWorld;

	protected AbstractEnemy target;

	protected int level = 0;

	private transient float timeSinceLastSearch;
	private static final float SEARCH_TARGET_INTERVALL = 0.75f;

	// debug information
	private boolean isTargeting = false;
	private float activeTime = 0.0f;
	private float totalDamageDone = 0.0f;

	// TODO static
	private transient TextureRegion[] levelTextures = new TextureRegion[4];

	public AbstractTower() {
	}

	public AbstractTower(float x, float y, String name, String iconPath, float range, AbstractWorld abstractWorld) {
		super();
		this.towerName = name;
		this.iconPath = iconPath;
		this.range = range;
		this.abstractWorld = abstractWorld;

		setPosition(x, y);
	}

	@Override
	public void postSerialized() {
		levelTextures[0] = new TextureRegion(Assets.getTexture(Assets.LEVEL1));
		levelTextures[1] = new TextureRegion(Assets.getTexture(Assets.LEVEL2));
		levelTextures[2] = new TextureRegion(Assets.getTexture(Assets.LEVEL3));
		levelTextures[3] = new TextureRegion(Assets.getTexture(Assets.LEVEL4));

		levelTextures[0].flip(false, true);
		levelTextures[1].flip(false, true);
		levelTextures[2].flip(false, true);
		levelTextures[3].flip(false, true);

		icon = Assets.getTexture(iconPath);

		setBounds(getX(), getY(), 3 * HeavyDefenseGame.TILE_SIZE, 3 * HeavyDefenseGame.TILE_SIZE);

		towerTextureAtlas = Assets.getTextureAtlas(Assets.TOWER_ATLAS);
	}

	public void setWorld(AbstractWorld abstractWorld) {
		this.abstractWorld = abstractWorld;
	}

	public String getTowerName() {
		return towerName;
	}

	public Texture getIcon() {
		return icon;
	}

	public String getIconPath() {
		return iconPath;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (currentSprite != null)
			batch.draw(currentSprite, getX() + getRenderOffsetX(), getY() + getRenderOffsetY());
	}

	public float getUpgradeOffsetX() {
		return 0;
	}

	public float getUpgradeOffsetY() {
		return 0;
	}

	@Override
	public int getZIndex() {
		return (int) getY();
	}

	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle upgradeLabelStyle) {

	}

	public float getUpgradeCost(int level) {
		return level * 600;
	}

	public final float getUpgradeCost() {
		return getUpgradeCost(level);
	}

	public final float getNextUpgradeCost() {
		return getUpgradeCost(level + 1);
	}

	/**
	 * Sets the level to the given level.
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		if (level > getMaxLevel() || level < 0) {
			throw new IllegalArgumentException();
		}
		this.level = level;
	}

	public final void upgrade() {
		level++;
		doUpgrade(level);
	}

	public int getMaxLevel() {
		return 3;
	}

	@Override
	public void act(float delta) {
		timeSinceLastSearch += delta;

		// search for target
		if (target == null) {
			searchNewTarget();
		}

		if (target != null) {
			// check if still in range or dead/at the end
			if (!isInRange(target) || target.isDead() || target.hasReachedEnd()) {

				target = null;

				searchNewTarget();

				if (target == null) {
					targetLost();
				}
			}

		}

		if (isTargeting) {
			activeTime += delta;
		}
	}

	protected float distanceTo(AbstractEnemy target) {
		int middleWidth = 3 * HeavyDefenseGame.TILE_SIZE / 2;
		int middleHeight = 3 * HeavyDefenseGame.TILE_SIZE / 2;
		return MoreMath.dist(getX() + middleWidth, getY() + middleHeight, target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2);
	}

	protected boolean isInRange(AbstractEnemy target) {
		int middleWidth = 3 * HeavyDefenseGame.TILE_SIZE / 2;
		int middleHeight = 3 * HeavyDefenseGame.TILE_SIZE / 2;
		return MoreMath.dist(getX() + middleWidth, getY() + middleHeight, target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2) < range;
	}

	private void searchNewTarget() {
		if (timeSinceLastSearch > SEARCH_TARGET_INTERVALL) {
			for (int enemyIndex = 0; enemyIndex < abstractWorld.getEnemies().size; ++enemyIndex) {
				final AbstractEnemy abstractEnemy = abstractWorld.getEnemies().get(enemyIndex);
				if (isInRange(abstractEnemy) && canTarget(abstractEnemy)) {
					target = abstractEnemy;
					targetAcquired();
					break;
				}
			}

			timeSinceLastSearch = 0;
		}
	}

	public void resetDpsStats() {
		totalDamageDone = 0;
		activeTime = 0;
	}

	public void registerDamage(float amount) {
		totalDamageDone += amount;
	}

	protected void targetLost() {
		isTargeting = false;
	}

	protected void targetAcquired() {
		isTargeting = true;

	}

	protected void doUpgrade(int level) {

	}

	public float getRange() {
		return range;
	}

	public int getLevel() {
		return level;
	}

	public float getSellValue() {
		return 200f + 50 * level;
	}

	@Override
	public int compareTo(AbstractTower o) {
		return (int) (getY() - o.getY());
	}

	public final float getTotalDamageDone() {
		return totalDamageDone;
	}

	public final float getDps() {
		return totalDamageDone / activeTime;
	}

	public float getActiveTime() {
		return activeTime;
	}

	public TextureRegion[] getLevelTextures() {
		return levelTextures;
	}

	/**
	 * Returns if this tower can target the given enemy (for example, only rocket towers can target air units).
	 * 
	 * @param abstractEnemy
	 * @return
	 */
	public abstract boolean canTarget(AbstractEnemy abstractEnemy);

	/**
	 * Returns the x offset at which the tower will be rendered in the cell (60 * 60).
	 * 
	 * @return
	 */
	public abstract float getRenderOffsetX();

	/**
	 * Returns the y offset at which the tower will be rendered in the cell (60 * 60).
	 * 
	 * @return
	 */
	public abstract float getRenderOffsetY();

	/**
	 * Returns the width of one sprite
	 * 
	 * @return
	 */
	public abstract int getSpriteWidth();

	/**
	 * Returns the height of one sprite
	 * 
	 * @return
	 */
	public abstract int getSpriteHeight();

	public void removed() {

	}

}