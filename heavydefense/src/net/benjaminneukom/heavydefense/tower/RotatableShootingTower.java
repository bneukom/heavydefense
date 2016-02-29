package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.Assets;
import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class RotatableShootingTower extends AbstractTower {

	private transient TextureRegion[] sprites;
	private transient TextureRegion[] shootingSprites;

	private String towerTextureName;
	private String shootingTextureName;

	private float hasShotTime;
	private static final float SHOOTING_TIME = 0.125f;

	protected float currentCooldown;
	protected float cooldown;
	protected float damage;

	protected double angle;
	protected int spriteIndex = 0;

	// temporary vectors
	private transient Vector2 a = new Vector2();
	private transient Vector2 b = new Vector2();

	public RotatableShootingTower() {
	}

	public RotatableShootingTower(float x, float y, String name, String iconPath, float range, float cooldown, float damage, String towerTextureName,
			String shootingTextureName, AbstractWorld abstractWorld) {
		super(x, y, name, iconPath, range, abstractWorld);

		this.towerTextureName = towerTextureName;
		this.shootingTextureName = shootingTextureName;
		this.cooldown = cooldown;
		this.damage = damage;
	}

	@Override
	public void postSerialized() {
		super.postSerialized();

		final AtlasRegion towerTextureRegion = towerTextureAtlas.findRegion(towerTextureName);
		final AtlasRegion shootingTextureRegion = towerTextureAtlas.findRegion(shootingTextureName);

		this.sprites = Assets.getOrientationSprites(towerTextureRegion, getSpriteWidth(), getSpriteHeight());
		this.shootingSprites = Assets.getOrientationSprites(shootingTextureRegion, getSpriteWidth(), getSpriteHeight());

		this.currentSprite = sprites[0];

	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// rotate
		if (target != null) {
			a.set(getX() + getRenderOffsetX() + getSpriteWidth() / 2, getY() + getRenderOffsetY() + getSpriteHeight() / 2);
			b.set(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2);

			final Vector2 direction = b.sub(a).nor();

			final double rotation = Math.atan2(direction.y, direction.x);
			angle = Math.toDegrees(rotation);

			setRotation((float) rotation);

			int newSpriteIndex = -1;

			if (angle > -180 && angle <= -157.5) {
				newSpriteIndex = 3;
			} else if (angle > -157.5 && angle <= -112.5) {
				newSpriteIndex = 0;
			} else if (angle > -112.5 && angle <= -67.5) {
				newSpriteIndex = 1;
			} else if (angle > -67.5 && angle <= -22.5) {
				newSpriteIndex = 2;
			} else if (angle > -22.5 && angle <= 22.5) {
				newSpriteIndex = 4;
			} else if (angle > 22.5 && angle <= 67.5) {
				newSpriteIndex = 7;
			} else if (angle > 67.5 && angle <= 112.5) {
				newSpriteIndex = 6;
			} else if (angle > 112.5 && angle <= 157.5) {
				newSpriteIndex = 5;
			} else if (angle > 157.5 && angle <= 180) {
				newSpriteIndex = 3;
			}

			if (newSpriteIndex != spriteIndex) {
				spriteIndex = newSpriteIndex;
				directionChanged(newSpriteIndex);
			}

			// shoot
			if (currentCooldown > 0) {
				currentCooldown -= delta;
			} else {
				currentCooldown = cooldown;

				shoot();
				hasShotTime = SHOOTING_TIME;
			}

		}

		if (hasShotTime > 0) {
			currentSprite = shootingSprites[spriteIndex];
			hasShotTime = Math.max(0, hasShotTime - delta);
		} else {
			currentSprite = sprites[spriteIndex];
		}
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

	protected float getDamageUpgrade(int level) {
		return 10;
	}

	protected float getRangeUpgrade(int level) {
		return 10;
	}

	@Override
	protected void doUpgrade(int level) {
		super.doUpgrade(level);

		damage += getDamageUpgrade(level);
		range += getRangeUpgrade(level);
	}

	public void shoot() {

	}

	protected void directionChanged(int newSpriteIndex) {

	}

}
