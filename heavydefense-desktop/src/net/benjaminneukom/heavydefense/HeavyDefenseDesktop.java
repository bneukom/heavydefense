package net.benjaminneukom.heavydefense;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class HeavyDefenseDesktop {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Heavy Defense";
		cfg.useGL20 = true;
		//		cfg.addIcon("icon32_2.png", FileType.Internal);
		cfg.width = 1920 / 2;
		cfg.height = 1080 / 2;
		//		cfg.resizable = false;
		//		cfg.fullscreen = true;

		// cfg.width = 1024;
		// cfg.height = 768;
		//		cfg.fullscreen = true;

		new LwjglApplication(new HeavyDefenseGame(new DesktopBilling(), new DesktopTracking(), new DesktopAppVersion()), cfg);
	}
}
