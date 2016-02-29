package net.benjaminneukom.heavydefense;

import java.nio.ByteBuffer;

import net.benjaminneukom.heavydefense.Assets.ParticleEffectLoader;
import net.benjaminneukom.heavydefense.screens.GameScreen;
import net.benjaminneukom.heavydefense.screens.MenuScreen;
import net.benjaminneukom.heavydefense.screens.SplashScreen;
import net.benjaminneukom.heavydefense.system.AppVersion;
import net.benjaminneukom.heavydefense.system.Billing;
import net.benjaminneukom.heavydefense.system.Tracking;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.ScreenUtils;

public class HeavyDefenseGame extends Game {

	private AssetManager assetManager = new AssetManager();
	private MenuScreen menuScreen;
	private SplashScreen splashScreen;

	private Preferences preferences;

	private static long frame;
	private long start;
	private long count = 0;
	private long startTime = System.currentTimeMillis();
	private boolean skipTipp;

	public static final int TILE_SIZE = 20;
	public static final int GRID_SIZE = 3 * TILE_SIZE;

	/**
	 * Whether recording and screen capturing is enabled or not.
	 */
	private static boolean recordEnabled = false;

	/**
	 * Whether the current FPS should get logged every second.
	 */
	public static boolean logFps = false;

	/**
	 * Whether the frame rate should be limited
	 */
	public static boolean limitFps = false;

	/**
	 * Record the game by capturing every frame as PNG, slow operation.
	 */
	private static boolean recordGame = false;

	/**
	 * If <code>true</code> the next frame will be captured and written into the user folder.
	 */
	private static boolean caputreScreen;

	private Billing billing;
	private Tracking tracking;
	private AppVersion version;

	public HeavyDefenseGame(Billing billing, Tracking tracking, AppVersion version) {
		this.billing = billing;
		this.tracking = tracking;
		this.version = version;
		start = System.currentTimeMillis();

	}

	@Override
	public void create() {

		billing.initlaize();
		tracking.initialize();

		TD.load();

		TD.billing = billing;
		TD.tracking = tracking;
		TD.appVersion = version.getVersion();

		TD.increasePlayed();

		Gdx.gl.glBlendFunc(GL20.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//		Gdx.gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		preferences = Gdx.app.getPreferences(Assets.GLOBAL_PREF_NAME);
		skipTipp = preferences.contains(SplashScreen.REMEMBER_KEY) && preferences.getBoolean(SplashScreen.REMEMBER_KEY);

		assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));

		Texture.setAssetManager(assetManager);
		Assets.setAssetManager(assetManager);

		Assets.loadFonts();
		Assets.queueAssets();

		splashScreen = new SplashScreen(this);

	}

	public void switchToMenu() {
		menuScreen.validateGameScreen();

		setScreen(menuScreen);

	}

	public void disableSkipTipp() {
		skipTipp = false;
	}

	@Override
	public void render() {

		// limit fps
		if (limitFps) {
			long endTime = System.currentTimeMillis();
			long dt = endTime - startTime;
			if (dt < 100)
				try {
					Thread.sleep(100 - dt);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			startTime = System.currentTimeMillis();
		}

		// log fps
		if (logFps) {
			count++;
			if (System.currentTimeMillis() - start > 1000) {
				float fps = (float) count / (float) (System.currentTimeMillis() - start) * 1000f;
				Gdx.app.log("heavydefense", "fps: " + (int) fps);
				count = 0;
				start = System.currentTimeMillis();
			}
		}

		if (assetManager.update()) {
			// instantiate if not already here
			if (menuScreen == null) {
				menuScreen = new MenuScreen(this);
			}

			splashScreen.setLoadingPercentage(1);

			// automatic change change to menu screen
			if (skipTipp) {
				if (getScreen() == splashScreen) {
					setScreen(menuScreen);
				}
			}

		} else {
			if (getScreen() == null) {
				setScreen(splashScreen);
			}
			splashScreen.setLoadingPercentage(assetManager.getProgress());
		}

		final float deltaT;
		if (!limitFps)
			// render with maximum time delta of 0.25 seconds. this delta might be higher if for example the in App billing was open
			deltaT = Math.min(0.25f, Gdx.graphics.getDeltaTime());
		else
			deltaT = 1f / 60f;

		if (getScreen() != null) getScreen().render(deltaT);

		if (recordGame || caputreScreen) {
			writeFrame(caputreScreen && !recordGame);
			caputreScreen = false;
		}

	}

	public void writeFrame(boolean isMovie) {
		final Pixmap pixmap = getFrameBufferPixmap();
		String name = isMovie ? "screenshots/heavydefense_" + System.nanoTime() : "movie/frame" + String.format("%05d", frame++);
		PixmapIO.writePNG(Gdx.files.external("HeavyDefense/" + name + ".png"), pixmap);
		pixmap.dispose();
	}

	public void writeFrame() {
		writeFrame(false);
	}

	public static Pixmap getFrameBufferPixmap() {
		final byte[] frameBufferPixels = ScreenUtils.getFrameBufferPixels(true);
		final Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		pixels.clear();
		pixels.put(frameBufferPixels);
		pixels.position(0);
		return pixmap;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		TD.persist();

		final Screen currentScreen = getScreen();
		if (currentScreen != null && currentScreen instanceof GameScreen) {
			final GameScreen gameScreen = (GameScreen) currentScreen;

			gameScreen.persistGame();

			if (!gameScreen.gameHasEnded() && Gdx.app.getType() != ApplicationType.Desktop)
				gameScreen.setPaused(true);
		}
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		tracking.dispose();
	}

	public static void captureScreen() {
		if (!recordEnabled) return;
		caputreScreen = true;
	}

	public static void toggleRecording() {
		if (!recordEnabled) return;
		recordGame = !recordGame;
		limitFps = recordGame;
		logFps = recordGame;
		frame = 0;
	}
}
