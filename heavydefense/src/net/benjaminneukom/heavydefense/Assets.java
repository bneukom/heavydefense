package net.benjaminneukom.heavydefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class Assets {

	public static final String IMAGES_DIR = "effects";

	// textures
	public static final String PLAY_BUTTON_UP = "play.png";
	public static final String PLAY_BUTTON_DOWN = "play.png";
	public static final String PAUSE_BUTTON_UP = "pause.png";
	public static final String PAUSE_BUTTON_DOWN = "pause.png";
	public static final String BACKGROUND = "background.png";
	public static final String BACKGROUND_SMALL = "backgroundsmall.png";
	public static final String V_SCROLL = "sprites/hud/vscroll.png";
	public static final String V_SCROLL_KNOB = "sprites/hud/vscrollknob.png";
	public static final String H_SCROLL = "sprites/hud/hscroll.png";
	public static final String H_SCROLL_KNOB = "sprites/hud/hscrollknob.png";
	public static final String FAST_FORWARD_UP = "fastforward.png";
	public static final String FAST_FORWARD_DOWN = "fastforward.png";
	public static final String FAST_FORWARD_CHECKED = "fastforwardchecked.png";
	public static final String TURRET_ICON = "sprites/towers/turretIcon.png";
	public static final String TURRET_ICON_SMALL = "sprites/towers/turretIconSmall.png";
	public static final String TURRET_ICON_SMALL_SELECTED = "sprites/towers/turretIconSmallSelected.png";
	public static final String SLOW_ICON = "sprites/towers/slowIcon.png";
	public static final String SLOW_ICON_SMALL = "sprites/towers/slowIconSmall.png";
	public static final String SLOW_ICON_SMALL_SELECTED = "sprites/towers/slowIconSmallSelected.png";
	public static final String ARTILLERY_ICON = "sprites/towers/artilleryIcon.png";
	public static final String ARTILLERY_ICON_SMALL = "sprites/towers/artilleryIconSmall.png";
	public static final String ARTILLERY_ICON_SMALL_SELECTED = "sprites/towers/artilleryIconSmallSelected.png";
	public static final String TESLA_ICON = "sprites/towers/teslaIcon.png";
	public static final String TESLA_ICON_SMALL = "sprites/towers/teslaIconSmall.png";
	public static final String TESLA_ICON_SMALL_SELECTED = "sprites/towers/teslaIconSmallSelected.png";
	public static final String ROCKET_ICON = "sprites/towers/rocketIcon.png";
	public static final String ROCKET_ICON_SMALL = "sprites/towers/rocketIconSmall.png";
	public static final String ROCKET_ICON_SMALL_SELECTED = "sprites/towers/rocketIconSmallSelected.png";
	public static final String FIRE_ICON = "sprites/towers/fireIcon.png";
	public static final String FIRE_ICON_SMALL = "sprites/towers/fireIconSmall.png";
	public static final String FIRE_ICON_SMALL_SELECTED = "sprites/towers/fireIconSmallSelected.png";
	public static final String EMPTY_ICON = "sprites/hud/emptyicon.png";
	public static final String ACHIEVEMENT_UNLOCKED_ICON = "sprites/hud/achievementunlocked.png";
	public static final String ACHIEVEMENT_ICON = "sprites/hud/achievement.png";

	public static final String LEVEL4 = "level4.png";
	public static final String LEVEL3 = "level3.png";
	public static final String LEVEL2 = "level2.png";
	public static final String LEVEL1 = "level1.png";
	public static final String INCREASE_BUTTON = "sprites/hud/increasebutton.png";
	public static final String INCREASE_BUTTON_PRESSED = "sprites/hud/increasebuttonpressed.png";
	public static final String DECREASE_BUTTON = "sprites/hud/decreasebutton.png";
	public static final String DECREASE_BUTTON_PRESSED = "sprites/hud/decreasebuttonpressed.png";
	public static final String BACK_BUTTON = "sprites/hud/backbutton.png";
	public static final String BACK_BUTTON_PRESSED = "sprites/hud/backbuttonpressed.png";

	// particle effects
	public static final String ROCKET_SMOKE = "effects/rocketsmoke.p";
	public static final String BURNING = "effects/burning.p";
	public static final String FIRE_EFFECT = "effects/fire2.p";
	public static final String FROST_EFFECT = "effects/ice2.p";

	// fonts
	public static BitmapFont TEXT_FONT;
	public static BitmapFont TITLE_FONT_38;
	public static BitmapFont TITLE_FONT_64;
	public static BitmapFont TITLE_FONT_90;

	// colors
	public static final Color COLOR_RED = new Color(180f / 255f, 42f / 255f, 42f / 255f, 1);
	public static final Color COLOR_GREEN = new Color(0f / 255f, 81f / 255f, 44f / 255f, 1);
	public static final Color COLOR_YELLOW = new Color(212f / 255f, 207f / 255f, 17f / 255f, 1);

	// files
	public static final String SAVE_GAME_NAME = "heavydefense.save";
	public static final String SAVE_GAME_NAME_2_0_0 = "heavydefense2_0_0.save";
	public static final String GLOBAL_PREF_NAME = "towerdefense.pref";
	public static final String CURRENT_SAVE_GAME_NAME = SAVE_GAME_NAME_2_0_0;

	// atlases
	public static final String TOWER_ATLAS = "sprites/towers/towers.pack";
	public static final String FIRE_TOWER = "firetower";
	public static final String TURRET_TOWER = "turrettower";
	public static final String TURRET_TOWER_MUZZLE = "turrettower_muzzle";
	public static final String ROCKET_TOWER = "rockettower";
	public static final String SLOW_TOWER = "slowtower";
	public static final String ARTILLERY_TOWER = "artillerytower";
	public static final String ARTILLERY_TOWER_SHOOTING = "artillerytower_shooting";
	public static final String TESLA_TOWER = "teslatower";
	public static final String TESLA_TOWER_SHOOTING = "teslatower_shooting";

	public static final String ENEMY_ATLAS = "sprites/enemies/enemies.pack";
	public static final String FAST_TANK = "fastTank";
	public static final String END_BOSS = "endboss";
	public static final String HEAVY_TANK = "heavyTank";
	public static final String GENERIC_TANK_1 = "genericTank1";
	public static final String GENERIC_TANK_2 = "genericTank2";
	public static final String COPTER = "copter";
	public static final String COPTER_SHADOW = "coptershadow";

	public static final String OVERLAY_ATLAS = "sprites/overlay/overlay.pack";
	public static final String TURRET_BULLET = "turretBullet2";
	public static final String EXPLOSION = "explosion";
	public static final String EXPLOSION_BIG = "explosionbig";
	public static final String ROCKET = "rocket2";
	public static final String LAVA_ERUPTION = "lavaeruption";

	private static AssetManager assetManager = null;

	static {
		final Serializer<Array> arraySerializer = new Serializer<Array>() {
			{
				setAcceptsNull(true);
			}

			private Class genericType;

			public void setGenerics(Kryo kryo, Class[] generics) {
				if (kryo.isFinal(generics[0]))
					genericType = generics[0];
			}

			public void write(Kryo kryo, Output output, Array array) {
				int length = array.size;
				output.writeInt(length, true);
				if (length == 0)
					return;
				if (genericType != null) {
					Serializer serializer = kryo.getSerializer(genericType);
					genericType = null;
					for (Object element : array)
						kryo.writeObjectOrNull(output, element, serializer);
				} else {
					for (Object element : array)
						kryo.writeClassAndObject(output, element);
				}
			}

			public Array read(Kryo kryo, Input input, Class<Array> type) {
				Array array = new Array();
				kryo.reference(array);
				int length = input.readInt(true);
				array.ensureCapacity(length);
				if (genericType != null) {
					Class elementClass = genericType;
					Serializer serializer = kryo.getSerializer(genericType);
					genericType = null;
					for (int i = 0; i < length; i++)
						array.add(kryo.readObjectOrNull(input, elementClass, serializer));
				} else {
					for (int i = 0; i < length; i++)
						array.add(kryo.readClassAndObject(input));
				}
				return array;
			}

		};

		Serializer<Color> colorSerializer = new Serializer<Color>() {
			public Color read(Kryo kryo, Input input, Class<Color> type) {
				Color color = new Color();
				Color.rgba8888ToColor(color, input.readInt());
				return color;
			}

			public void write(Kryo kryo, Output output, Color color) {
				output.writeInt(Color.rgba8888(color));
			}
		};

	}

	public static void setAssetManager(AssetManager assetManager) {
		Assets.assetManager = assetManager;
	}

	public static Texture getTexture(String path) {
		return assetManager.get(path);
	}

	public static ParticleEffect getParticleEffect(String path) {
		return assetManager.get(path);
	}

	public static void queueAssets() {
		// load textures

		assetManager.load(PLAY_BUTTON_UP, Texture.class);
		assetManager.load(PLAY_BUTTON_DOWN, Texture.class);
		assetManager.load(PAUSE_BUTTON_UP, Texture.class);
		assetManager.load(PAUSE_BUTTON_DOWN, Texture.class);
		assetManager.load(BACKGROUND, Texture.class);
		assetManager.load(BACKGROUND_SMALL, Texture.class);
		assetManager.load(V_SCROLL, Texture.class);
		assetManager.load(V_SCROLL_KNOB, Texture.class);
		assetManager.load(H_SCROLL, Texture.class);
		assetManager.load(H_SCROLL_KNOB, Texture.class);
		assetManager.load(FAST_FORWARD_UP, Texture.class);
		assetManager.load(FAST_FORWARD_DOWN, Texture.class);
		assetManager.load(FAST_FORWARD_CHECKED, Texture.class);
		assetManager.load(TURRET_ICON, Texture.class);
		assetManager.load(TURRET_ICON_SMALL, Texture.class);
		assetManager.load(TURRET_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(SLOW_ICON, Texture.class);
		assetManager.load(SLOW_ICON_SMALL, Texture.class);
		assetManager.load(SLOW_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(ARTILLERY_ICON, Texture.class);
		assetManager.load(ARTILLERY_ICON_SMALL, Texture.class);
		assetManager.load(ARTILLERY_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(TESLA_ICON, Texture.class);
		assetManager.load(TESLA_ICON_SMALL, Texture.class);
		assetManager.load(TESLA_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(ROCKET_ICON, Texture.class);
		assetManager.load(ROCKET_ICON_SMALL, Texture.class);
		assetManager.load(ROCKET_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(FIRE_ICON, Texture.class);
		assetManager.load(FIRE_ICON_SMALL, Texture.class);
		assetManager.load(FIRE_ICON_SMALL_SELECTED, Texture.class);
		assetManager.load(EMPTY_ICON, Texture.class);
		assetManager.load(LEVEL1, Texture.class);
		assetManager.load(LEVEL2, Texture.class);
		assetManager.load(LEVEL3, Texture.class);
		assetManager.load(LEVEL4, Texture.class);
		assetManager.load(INCREASE_BUTTON, Texture.class);
		assetManager.load(INCREASE_BUTTON_PRESSED, Texture.class);
		assetManager.load(DECREASE_BUTTON, Texture.class);
		assetManager.load(DECREASE_BUTTON_PRESSED, Texture.class);
		assetManager.load(BACK_BUTTON, Texture.class);
		assetManager.load(BACK_BUTTON_PRESSED, Texture.class);
		assetManager.load(ACHIEVEMENT_ICON, Texture.class);
		assetManager.load(ACHIEVEMENT_UNLOCKED_ICON, Texture.class);

		// load particle effects
		final ParticleEffectLoaderParameters parameters = new ParticleEffectLoaderParameters(IMAGES_DIR);

		assetManager.load(ROCKET_SMOKE, ParticleEffect.class, parameters);
		assetManager.load(BURNING, ParticleEffect.class, parameters);
		assetManager.load(FIRE_EFFECT, ParticleEffect.class, parameters);
		assetManager.load(FROST_EFFECT, ParticleEffect.class, parameters);

		// load atlasses
		assetManager.load(TOWER_ATLAS, TextureAtlas.class);
		assetManager.load(ENEMY_ATLAS, TextureAtlas.class);
		assetManager.load(OVERLAY_ATLAS, TextureAtlas.class);
	}

	public static void loadFonts() {
		final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixelfont.ttf"));
		TEXT_FONT = generator.generateFont(26);
		TITLE_FONT_38 = generator.generateFont(38);
		TITLE_FONT_64 = generator.generateFont(64);
		TITLE_FONT_90 = generator.generateFont(90);

		generator.dispose();
	}

	public static TextureAtlas getTextureAtlas(String id) {
		return assetManager.get(id);
	}

	public static TextureRegion[] getOrientationSprites(TextureRegion sheet, int spriteWidth, int spriteHeight) {
		final TextureRegion[] regions = new TextureRegion[8];
		int x = 0;
		int y = 0;
		for (int i = 0; i < 8; ++i) {

			// ignore center
			if (x == spriteWidth && y == spriteHeight) {
				x += spriteWidth;
			}

			regions[i] = new TextureRegion(sheet, x, y, spriteWidth, spriteHeight);
			regions[i].flip(false, true);

			x += spriteWidth;
			if (x == sheet.getRegionWidth()) {
				x = 0;
				y += spriteHeight;
			}

		}

		return regions;

	}

	public static TextureRegion[] getOrientationSprites(Texture sheet, int spriteWidth, int spriteHeight) {
		final TextureRegion[] regions = new TextureRegion[8];
		int x = 0;
		int y = 0;
		for (int i = 0; i < 8; ++i) {

			// ignore center
			if (x == spriteWidth && y == spriteHeight) {
				x += spriteWidth;
			}

			regions[i] = new TextureRegion(sheet, x, y, spriteWidth, spriteHeight);
			regions[i].flip(false, true);

			x += spriteWidth;
			if (x == sheet.getWidth()) {
				x = 0;
				y += spriteHeight;
			}

		}

		return regions;
	}

	public static class ParticleEffectLoader extends SynchronousAssetLoader<ParticleEffect, ParticleEffectLoaderParameters> {

		public ParticleEffectLoader(FileHandleResolver resolver) {
			super(resolver);
		}

		@Override
		public ParticleEffect load(AssetManager assetManager, String fileName, FileHandle file, ParticleEffectLoaderParameters parameter) {
			final ParticleEffect particleEffect = new ParticleEffect();
			particleEffect.load(Gdx.files.internal(fileName), Gdx.files.internal(parameter.imagesDir));
			return particleEffect;
		}

		@Override
		public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ParticleEffectLoaderParameters parameter) {
			return null;
		}

	}

	public static class ParticleEffectLoaderParameters extends AssetLoaderParameters<ParticleEffect> {
		public final String imagesDir;

		private ParticleEffectLoaderParameters(String imagesDir) {
			super();
			this.imagesDir = imagesDir;
		}

	}

}
