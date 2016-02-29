package net.benjaminneukom.heavydefense.screens;

import net.benjaminneukom.heavydefense.HeavyDefenseGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class AbstractScreen implements Screen {

	protected Stage stage;
	protected Stage ui;
	protected OrthographicCamera stageCamera;
	private boolean paused;
	private float speed = 1;

	public AbstractScreen() {
		super();

		final SpriteBatch stageBatch = new SpriteBatch(2048);

		this.stage = new Stage(480, 270, true);
		this.ui = new Stage(480, 270, true, stageBatch);
		this.stageCamera = (OrthographicCamera) stage.getCamera();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!paused) {
			stage.act(delta * speed);
		}
		stage.draw();

		ui.act(delta);
		ui.draw();

	}

	protected void renderPaused(float delta) {

	}

	@Override
	public void resize(int width, int height) {
		float gameWidth = width;
		float gameHeight = height;

		while (gameWidth >= 800) {
			gameWidth /= 2;
			gameHeight /= 2;
		}

		ui.setViewport(gameWidth, gameHeight, true);

		stage.setViewport(gameWidth, gameHeight, true);
		stageCamera.setToOrtho(true, gameWidth, gameHeight);

		//		int stageWidth = 480;
		//		int stageHeight = stageWidth * height / width;
		//
		//		stage.setViewport(stageWidth, stageHeight, true);
		//		stageCamera.setToOrtho(true, stageWidth, stageHeight);

		//		int newWidth = 480;
		//		int newHeight = 270;
		//		Vector2 size = Scaling.fit.apply(newWidth, newHeight, width, height);
		//		//		Vector2 size = new Vector2(newWidth, newHeight);
		//		int viewportX = (int) (width - size.x) / 2;
		//		int viewportY = (int) (height - size.y) / 2;
		//		int viewportWidth = (int) size.x;
		//		int viewportHeight = (int) size.y;
		//		Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
		//		ui.setViewport(newWidth, newHeight, true, viewportX, viewportY, viewportWidth, viewportHeight);
		//		stage.setViewport(newWidth, newHeight, true, viewportX, viewportY, viewportWidth, viewportHeight);

	}

	@Override
	public void show() {
		final InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(ui);
		multiplexer.addProcessor(createStageMultiplexer());

		InputProcessor backInputProcessor = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.P) {
					HeavyDefenseGame.captureScreen();
				}

				if (keycode == Keys.R) {
					HeavyDefenseGame.toggleRecording();
				}

				if (keycode == Keys.BACK) {
					if (handleBack()) {
						onBack();
					}
					return true;
				}

				return false;
			}
		};
		multiplexer.addProcessor(backInputProcessor);

		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);
	}

	protected void onBack() {

	}

	protected boolean handleBack() {
		return true;
	}

	protected InputMultiplexer createStageMultiplexer() {
		return new InputMultiplexer(stage);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		hide();
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

}
