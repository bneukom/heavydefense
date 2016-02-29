package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.game.worlds.AbstractWorld;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

// TODO probably pool?
/**
 * Animated Actor which will remove itself from the owner {@link Group} after the animation is finished.
 */
public class AnimatedActor extends Actor implements PostSerialization {
	protected AbstractWorld abstractWorld;

	private transient Animation explosionAnimation;
	private transient TextureRegion[] frames;
	private transient TextureRegion currentFrame;

	private float stateTime;
	private float lifeTime;

	private int columns;
	private int rows;

	private String sheetName;
	private String atlasPath;
	private float delay;

	public AnimatedActor() {
	}

	public AnimatedActor(float x, float y, String atlasPath, String sheetPath, int columns, int rows, float frameTime, AbstractWorld owner) {
		this(x, y, atlasPath, sheetPath, columns, rows, frameTime, 0, owner);
	}

	public AnimatedActor(float x, float y, String atlasPath, String sheetPath, int columns, int rows, float frameTime, float delay, AbstractWorld owner) {
		this.atlasPath = atlasPath;
		this.sheetName = sheetPath;
		this.columns = columns;
		this.rows = rows;
		this.delay = delay;
		this.abstractWorld = owner;

		postSerialized();

		explosionAnimation = new Animation(frameTime, frames);
		stateTime = 0f;
		currentFrame = explosionAnimation.getKeyFrame(stateTime, false);

		setPosition(x, y);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		lifeTime += delta;
		if (lifeTime < delay) return;

		if (explosionAnimation.isAnimationFinished(stateTime)) {
			done();
		}

		stateTime += delta;
		currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
	}

	protected void done() {
		abstractWorld.removeMapActor(this);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (lifeTime < delay) return;

		super.draw(batch, parentAlpha);
		batch.draw(currentFrame, getX() + getRenderOffsetX(), getY() + getRenderOffsetY());

	}

	protected float getRenderOffsetX() {
		return 0;
	}

	protected float getRenderOffsetY() {
		return 0;
	}

	@Override
	public void postSerialized() {
		final TextureAtlas textureAtlas = Assets.getTextureAtlas(atlasPath);
		final AtlasRegion sheet = textureAtlas.findRegion(sheetName);

		final TextureRegion[][] tmp = sheet.split(sheet.getRegionWidth() / columns, sheet.getRegionHeight() / rows);
		frames = new TextureRegion[columns * rows];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				TextureRegion sprite = new TextureRegion(tmp[i][j]);
				sprite.flip(false, true);
				frames[index++] = sprite;

			}
		}
		explosionAnimation = new Animation(0.045f, frames);
		currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
	}

}
