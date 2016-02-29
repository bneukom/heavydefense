package net.benjaminneukom.heavydefense.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RemoveMargin {
	public static void main(String[] args) {
		final File graphicsFolder = new File("C:/Users/bneukom/workspace-towerdefense-git/towerdefense/towerdefense-android/resources/tiles");
		final File[] graphicsFiles = graphicsFolder.listFiles();
		for (File graphicFile : graphicsFiles) {
			try {
				BufferedImage original = ImageIO.read(graphicFile);
				BufferedImage removedMargin = new BufferedImage(original.getWidth() - 1, original.getHeight() - 1, BufferedImage.TYPE_INT_RGB);
				removedMargin.getGraphics().drawImage(original, 0, 0, removedMargin.getWidth(), removedMargin.getHeight(), 0, 1, original.getWidth() - 1, original.getHeight(),
						null);

				// removedMargin.getGraphics().drawImage(original, 0, 0, null);

				final File graphicPngFile = new File(graphicFile.getParent(), graphicFile.getName().substring(0, graphicFile.getName().indexOf(".")) + ".png");
				ImageIO.write(removedMargin, "png", graphicPngFile);

				graphicFile.renameTo(graphicPngFile);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
