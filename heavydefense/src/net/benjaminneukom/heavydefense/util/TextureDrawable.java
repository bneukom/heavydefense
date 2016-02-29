package net.benjaminneukom.heavydefense.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class TextureDrawable extends BaseDrawable{
	private Texture texture;

	/** Creates an unitialized TextureDrawable. The texture must be set before use. */
	public TextureDrawable () {
	}

	public TextureDrawable (Texture texture) {
		setTexture(texture);
	}

	public TextureDrawable (TextureDrawable drawable) {
		super(drawable);
		setTexture(drawable.texture);
	}

	public void draw (SpriteBatch batch, float x, float y, float width, float height) {
		batch.draw(texture, x, y, width, height);
	}

	public void setTexture (Texture texture) {
		this.texture = texture;
		setMinWidth(texture.getWidth());
		setMinHeight(texture.getHeight());
	}

	public Texture getTexture() {
		return texture;
	}
}
