package net.benjaminneukom.heavydefense.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

// TODO try keeping original image structure
public class CreateAtlas {
	public static void main(String[] args) {
		try {
			// createAtlas(new File("C:/Users/bneukom/workspace-towerdefense/towerdefense-android/assets/tiles"), 20, new Color(0, 0, 0, 0), Color.BLACK, new Color(0, 138, 118));
			createAtlas2(new File("C:/Users/bneukom/workspace-towerdefense-git/towerdefense/towerdefense-android/resources/tiles"), 20, new Color(0, 0, 0, 0), Color.BLACK,
					new Color(0, 138, 118), Color.WHITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createAtlas2(File input, int tileSize, Color... ignoreColors) throws IOException {
		final File[] listFiles = input.listFiles();
		final List<BufferedImage> cleanedTileSheets = new ArrayList<BufferedImage>();

		for (File file : listFiles) {
			if (file.getName().endsWith(".png")) {
				System.out.println("processing file " + file.getPath());

				cleanedTileSheets.add(removeEmpty(ImageIO.read(file), tileSize, tileSize, ignoreColors));
			} else {
				System.out.println("ignore " + file.getPath());
			}
		}

		Collections.sort(cleanedTileSheets, new Comparator<BufferedImage>() {
			@Override
			public int compare(BufferedImage o1, BufferedImage o2) {
				return o1.getHeight() - o2.getHeight();
			}
		});

		final int maxWidth = 512;
		int width = 0;
		int height = 0;

		int newWidth = 0;
		int maxHeight = 0;

		for (int cleanTileIndex = 0; cleanTileIndex < cleanedTileSheets.size(); ++cleanTileIndex) {
			final BufferedImage bufferedImage = cleanedTileSheets.get(cleanTileIndex);

			if (newWidth + bufferedImage.getWidth() <= maxWidth) {
				newWidth += bufferedImage.getWidth();
				maxHeight = Math.max(bufferedImage.getHeight(), maxHeight);

				if (cleanTileIndex == cleanedTileSheets.size() - 1) {
					height += maxHeight;
					width = Math.max(newWidth, width);
				}

			} else {
				height += maxHeight;

				newWidth = 0;
				maxHeight = 0;
			}

			width = Math.max(newWidth, width);
		}

		maxHeight = 0;

		final BufferedImage atlas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D atlasGraphics = atlas.createGraphics();
		int x = 0;
		int y = 0;
		for (BufferedImage sheetImage : cleanedTileSheets) {
			if (x + sheetImage.getWidth() > atlas.getWidth()) {
				x = 0;
				y += maxHeight;
				maxHeight = 0;
			}

			atlasGraphics.drawImage(sheetImage, null, x, y);
			x += sheetImage.getWidth();

			maxHeight = Math.max(maxHeight, sheetImage.getHeight());
		}

		final File atlasFile = new File(input, "atlas.png");
		ImageIO.write(atlas, "png", atlasFile);

		System.out.println("done writing atlas");

	}

	// TODO only render tiles which are not empty
	// TODO create atlas exactly the size of all tiles (example 300 tiles sqrt(300) * sqrt(300))
	public static void createAtlas(File input, int tileSize, Color... ignoreColors) throws IOException {
		final File[] listFiles = input.listFiles();

		final List<BufferedImage> tiles = new ArrayList<BufferedImage>(listFiles.length * 20);

		for (File file : listFiles) {
			if (file.getName().endsWith(".png")) {
				System.out.println("processing file " + file.getPath());
				final BufferedImage image = ImageIO.read(file);
				tiles.addAll(split(image, tileSize, tileSize, ignoreColors));
			} else {
				System.out.println("ignore " + file.getPath());
			}
		}

		System.out.println("total number of tiles: " + tiles.size());
		final int size = (int) (Math.ceil(Math.sqrt(tiles.size())) * tileSize);
		System.out.println("atlas size: " + size);
		final BufferedImage atlas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = (Graphics2D) atlas.getGraphics();
		int x = 0;
		int y = 0;
		for (BufferedImage tile : tiles) {
			if (x >= size) {
				x = 0;
				y += tileSize;
			}
			graphics.drawImage(tile, x, y, null);
			x += tileSize;
		}

		File atlasFile = new File(input, "atlas.png");
		ImageIO.write(atlas, "png", atlasFile);
		System.out.println("done writing atlas");

	}

	public static BufferedImage removeEmpty(BufferedImage image, int tileWidth, int tileHeight, Color... ignoredColors) {
		if (image.getWidth() % tileWidth != 0 || image.getHeight() % tileHeight != 0) {
			throw new IllegalArgumentException();
		}

		class Line {
			final List<BufferedImage> images = new ArrayList<BufferedImage>();
		}

		int width = 0;
		int height = 0;
		final List<Line> lines = new ArrayList<Line>();

		for (int y = 0; y < image.getHeight(); y += tileHeight) {
			int newWidth = 0;
			final Line line = new Line();

			for (int x = 0; x < image.getWidth(); x += tileWidth) {
				final BufferedImage subimage = image.getSubimage(x, y, tileWidth, tileHeight);

				// ignore
				if (isUnicolored(subimage, ignoredColors)) {
					continue;
				}

				newWidth += tileWidth;

				line.images.add(subimage);
			}

			if (line.images.size() > 0) {
				lines.add(line);
				height += tileHeight;
			}

			width = Math.max(newWidth, width);

		}

		final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = result.createGraphics();

		int y = 0;
		for (Line line : lines) {
			int x = 0;
			for (BufferedImage tile : line.images) {
				graphics.drawImage(tile, null, x, y);
				x += tileWidth;
			}
			y += tileHeight;
		}

		graphics.dispose();

		return result;
	}

	public static List<BufferedImage> split(BufferedImage image, int width, int height, Color... ignoreColors) {
		if (image.getWidth() % width != 0 || image.getHeight() % height != 0) {
			throw new IllegalArgumentException();
		}

		final List<BufferedImage> images = new ArrayList<BufferedImage>();

		for (int y = 0; y < image.getHeight(); y += height) {
			for (int x = 0; x < image.getWidth(); x += width) {
				final BufferedImage subimage = image.getSubimage(x, y, width, height);

				// ignore
				if (isUnicolored(subimage, ignoreColors)) {
					continue;
				}

				images.add(subimage);
			}
		}

		return images;

	}

	private static boolean isUnicolored(BufferedImage image, Color... colors) {
		Color: for (Color color : colors) {
			for (int x = 0; x < image.getWidth(); ++x) {
				for (int y = 0; y < image.getHeight(); ++y) {
					int rgb = new Color(image.getRGB(x, y), true).getRGB();

					if (rgb != color.getRGB()) {
						continue Color;
					}
				}
			}

			return true;
		}

		return false;
	}
}
