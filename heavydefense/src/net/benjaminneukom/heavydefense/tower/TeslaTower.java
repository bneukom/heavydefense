package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.TD;
import net.benjaminneukom.heavydefense.enemies.AbstractEnemy;
import net.benjaminneukom.heavydefense.enemies.AirEnemy;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.enemies.debuffs.SlowDebuff;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;
import net.benjaminneukom.heavydefense.ui.upgrade.Upgrade.UpgradeType;
import net.benjaminneukom.heavydefense.util.MoreMath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

// TODO some stuff is duplicate with rotatableShootingTower
// TODO lots of unnecessary object creations
// TODO pool  lightnings???
// TODO lightnings should always be rendered on top (of everything)...
public class TeslaTower extends AbstractTower {

	private static final float SHOOT_ANIMATION_FRAME_TIME = 0.045f;

	private transient TextureRegion towerTexture;

	private transient Animation shootingAnimation;
	private transient TextureRegion[] frames;
	private transient TextureRegion currentFrame;
	private float stateTime;

	private boolean renderShootingAnimation = false;

	protected float currentCooldown;
	protected float cooldown;
	protected float damage;

	private static final int COLUMNS = 11;
	private float shootingAnimationTime = SHOOT_ANIMATION_FRAME_TIME * COLUMNS;

	private Glow glow;

	private transient ShapeRenderer shapeRenderer = new ShapeRenderer();

	private final static float SHOOT_OFFSET_X = 30;
	private final static float SHOOT_OFFSET_Y = 15;

	public TeslaTower() {
	}

	public TeslaTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, abstractWorld);
		this.cooldown = cooldown;
		this.damage = damage;
		this.glow = new Glow(x + SHOOT_OFFSET_X, y + SHOOT_OFFSET_Y);
		this.stateTime = 0f;

		postSerialized();
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		this.towerTexture = new TextureRegion(towerTextureAtlas.findRegion(Assets.TESLA_TOWER));
		this.towerTexture.flip(false, true);

		final TextureRegion shootingSheet = new TextureRegion(towerTextureAtlas.findRegion(Assets.TESLA_TOWER_SHOOTING));

		final int rows = 1;

		final TextureRegion[][] tmp = shootingSheet.split(shootingSheet.getRegionWidth() / COLUMNS, shootingSheet.getRegionHeight() / rows);

		frames = new TextureRegion[COLUMNS * rows];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				frames[index] = tmp[i][j];
				frames[index].flip(false, true);
				index++;
			}
		}

		shootingSheet.flip(false, true);

		shootingAnimation = new Animation(SHOOT_ANIMATION_FRAME_TIME, frames);

		currentFrame = shootingAnimation.getKeyFrame(stateTime, false);

		shapeRenderer = new ShapeRenderer();

		currentSprite = towerTexture;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// update glows
		glow.update(delta);

		if (renderShootingAnimation) {
			stateTime += delta;
			currentFrame = shootingAnimation.getKeyFrame(stateTime, false);

			currentSprite = currentFrame;
		} else {
			currentSprite = towerTexture;
		}

		if (shootingAnimation.isAnimationFinished(stateTime)) {
			stateTime = 0;
			renderShootingAnimation = false;
		}

		if (target != null) {
			// shoot
			if (currentCooldown > 0) {
				currentCooldown -= delta;

				// render the shooting animation before shooting
				if (currentCooldown < shootingAnimationTime) {
					renderShootingAnimation = true;
				}

			} else {
				currentCooldown = cooldown;

				shoot();

				stateTime = 0;
				renderShootingAnimation = false;
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();

		// render glow
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Filled);

		glow.render(shapeRenderer);

		shapeRenderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);

		batch.begin();

	}

	private void shoot() {
		shootAt(getX() + SHOOT_OFFSET_X, getY() + SHOOT_OFFSET_Y, target);

		if (TD.isUpgraded(UpgradeType.TESLA_OVERLOAD)) {
			float overloadChance = TD.getUpgradeValue(UpgradeType.TESLA_OVERLOAD);

			if (Math.random() < overloadChance) {
				for (AbstractEnemy enemy : abstractWorld.getEnemies()) {

					if (enemy != target && canTarget(enemy)) {
						final float distance = MoreMath.dist(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2, enemy.getX() + enemy.getWidth() / 2,
								enemy.getY() + enemy.getHeight() / 2);
						if (distance < 25) {
							// do not show lightnig
							doDamage(enemy);
						} else if (distance < 125) {
							shootAt(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2, enemy);
							break;
						}
					}
				}
			}
		}
	}

	private void shootAt(final float fromX, final float fromY, final AbstractEnemy target) {
		float targetWidth = target.getWidth();
		float targetHeight = target.getHeight();
		abstractWorld.addLightning(new Lightning(fromX, fromY, (float) (target.getX() + targetWidth / 2 + Math.random() * targetWidth / 5), (float) (target.getY()
				+ targetHeight / 2 + Math.random() * targetHeight / 5), range, false));
		glow.reset();

		doDamage(target);
	}

	private void doDamage(final AbstractEnemy target) {
		target.doDamage(damage);
		registerDamage(damage);

		target.addDebuff(new SlowDebuff(0.6f, 0.35f));
	}

	@Override
	public void fillInfoTable(Table info, LabelStyle infoLabelStyle, LabelStyle headerLabelStyle, LabelStyle upgradeLabelStyle) {
		final Label damageLabel = new Label("Damage: " + (int) damage, infoLabelStyle);
		final Label damageUpgrade = new Label("+" + (int) getDamageUpgrade(level + 1), upgradeLabelStyle);
		final Label rangeLabel = new Label("Range: " + (int) range, infoLabelStyle);
		final Label rangeUpgrade = new Label("+" + (int) getRangeUpgrade(level + 1), upgradeLabelStyle);
		final Label cooldownLabel = new Label("Cooldown: " + cooldown + " sec", infoLabelStyle);
		final Label cooldownUpgrade = new Label("", upgradeLabelStyle);

		final Table damageTable = new Table();
		damageTable.add(damageLabel).padRight(10);
		damageTable.add(damageUpgrade);
		info.add(damageTable).left();
		info.row();

		final Table rangeTable = new Table();
		rangeTable.add(rangeLabel).left().padRight(10);
		rangeTable.add(rangeUpgrade).right();
		info.add(rangeTable).left();
		info.row();

		final Table cooldownTable = new Table();
		cooldownTable.add(cooldownLabel).left().padRight(10);
		cooldownTable.add(cooldownUpgrade).right();
		info.add(cooldownTable).left();
		info.row();
	}

	@Override
	protected void doUpgrade(int level) {
		super.doUpgrade(level);

		damage += getDamageUpgrade(level);
		range += getRangeUpgrade(level);
	}

	protected float getDamageUpgrade(int level) {
		return 40;
	}

	protected float getRangeUpgrade(int level) {
		return 10;
	}

	@Override
	public float getUpgradeCost(int level) {
		float cost = (float) Math.ceil(level * 600 * (1 - TD.getUpgradeValue(UpgradeType.TESLA_CHEAPER_UPGRADE)));
		return cost;
	}

	private static class Glow {
		private static final float MAX_TIME = 0.45f;

		private float time = 0;

		private float x;
		private float y;

		@SuppressWarnings("unused")
		public Glow() {
		}

		private Glow(float x, float y) {
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

			drawGlow(shapeRenderer, 11, 60f / 255f);
			drawGlow(shapeRenderer, 7, 90f / 255f);
			drawGlow(shapeRenderer, 4, 150f / 255f);
			drawGlow(shapeRenderer, 2, 230f / 255f);

		}

		private void drawGlow(ShapeRenderer renderer, int size, float alpha) {
			final float changedAlpha = Math.min((float) (alpha * time / MAX_TIME), 1f);
			renderer.setColor(200f / 255f, 232f / 255f, 255f / 255f, changedAlpha);

			renderer.circle(x, y, size);
		}

	}

	@Override
	public boolean canTarget(AbstractEnemy abstractEnemy) {
		if (abstractEnemy instanceof AirEnemy && TD.isUpgraded(UpgradeType.TESLA_AIR))
			return true;

		return abstractEnemy instanceof GroundEnemy;
	}

	@Override
	public int getMaxLevel() {
		return TD.isUpgraded(UpgradeType.TESLA_TOWER_LEVEL_4) ? super.getMaxLevel() + 1 : super.getMaxLevel();
	}

	@Override
	public float getRenderOffsetX() {
		return 9;
	}

	@Override
	public float getRenderOffsetY() {
		return 7;
	}

	@Override
	public int getSpriteWidth() {
		return 40;
	}

	@Override
	public int getSpriteHeight() {
		return 45;
	}
}
