package net.benjaminneukom.heavydefense.tools;

// 
//  GifSequenceWriter.java
//  
//  Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifSequenceWriter {
	protected ImageWriter gifWriter;
	protected ImageWriteParam imageWriteParam;
	protected IIOMetadata imageMetaData;

	/**
	 * Creates a new GifSequenceWriter
	 * 
	 * @param outputStream
	 *            the ImageOutputStream to be written to
	 * @param imageType
	 *            one of the imageTypes specified in BufferedImage
	 * @param timeBetweenFramesMS
	 *            the time between frames in miliseconds
	 * @param loopContinuously
	 *            wether the gif should loop repeatedly
	 * @throws IIOException
	 *             if no gif ImageWriters are found
	 * 
	 * @author Elliot Kroo (elliot[at]kroo[dot]net)
	 */
	public GifSequenceWriter(
			ImageOutputStream outputStream,
			int imageType,
			int timeBetweenFramesMS,
			boolean loopContinuously) throws IIOException, IOException {
		// my method to create a writer
		gifWriter = getWriter();
		imageWriteParam = gifWriter.getDefaultWriteParam();
		ImageTypeSpecifier imageTypeSpecifier =
				ImageTypeSpecifier.createFromBufferedImageType(imageType);

		imageMetaData =
				gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
						imageWriteParam);

		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode)
				imageMetaData.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(
				root,
				"GraphicControlExtension");

		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute(
				"transparentColorFlag",
				"FALSE");
		graphicsControlExtensionNode.setAttribute(
				"delayTime",
				Integer.toString(timeBetweenFramesMS / 10));
		graphicsControlExtensionNode.setAttribute(
				"transparentColorIndex",
				"0");

		IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Created by MAH");

		IIOMetadataNode appEntensionsNode = getNode(
				root,
				"ApplicationExtensions");

		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");

		int loop = loopContinuously ? 0 : 1;

		child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte)
				((loop >> 8) & 0xFF) });
		appEntensionsNode.appendChild(child);

		imageMetaData.setFromTree(metaFormatName, root);

		gifWriter.setOutput(outputStream);

		gifWriter.prepareWriteSequence(null);
	}

	public void writeToSequence(RenderedImage img) throws IOException {
		gifWriter.writeToSequence(
				new IIOImage(
						img,
						null,
						imageMetaData),
				imageWriteParam);
	}

	/**
	 * Close this GifSequenceWriter object. This does not close the underlying stream, just finishes off the GIF.
	 */
	public void close() throws IOException {
		gifWriter.endWriteSequence();
	}

	/**
	 * Returns the first available GIF ImageWriter using ImageIO.getImageWritersBySuffix("gif").
	 * 
	 * @return a GIF ImageWriter object
	 * @throws IIOException
	 *             if no GIF image writers are returned
	 */
	private static ImageWriter getWriter() throws IIOException {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
		if (!iter.hasNext()) {
			throw new IIOException("No GIF Image Writers Exist");
		} else {
			return iter.next();
		}
	}

	/**
	 * Returns an existing child node, or creates and returns a new child node (if the requested node does not exist).
	 * 
	 * @param rootNode
	 *            the <tt>IIOMetadataNode</tt> to search for the child node.
	 * @param nodeName
	 *            the name of the child node.
	 * 
	 * @return the child node, if found or a new node created with the given name.
	 */
	private static IIOMetadataNode getNode(
			IIOMetadataNode rootNode,
			String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
				== 0) {
				return ((IIOMetadataNode) rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return (node);
	}

	/**
	 * public GifSequenceWriter( BufferedOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously) {
	 */
	public static void main(String[] args) throws Exception {
		reduceImageQuality("C:/Users/bneukom/HeavyDefense/movie");
		//		createGif("C:/Users/bneukom/HeavyDefense/movie", "C:/Users/bneukom/HeavyDefense/movie/movie.gif");

		//		BufferedImage read = ImageIO.read(new File("C:/Users/bneukom/HeavyDefense/movie/frame0048.png.png"));
		//		System.out.println(read.getType());
	}

	private static void reduceImageQuality(String imagesFolder) throws IOException {
		final File imagesFolderFile = new File(imagesFolder);
		int frameCount = 0;
		for (File imageFile : imagesFolderFile.listFiles()) {
			if (imageFile.isFile()) {
				System.out.println("reduce: " + imageFile.getName());
				BufferedImage originalImage = ImageIO.read(imageFile);
				BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = newImage.createGraphics();
				g.drawImage(originalImage, 0, 0, null);
				g.dispose();

				ImageIO.write(newImage, "gif", new File(imageFile.getParent(), "frame" + frameCount++ + ".gif"));
				imageFile.delete();
			}
		}
	}

	/**
	 * Write a JPEG file setting the compression quality.
	 * 
	 * @param image
	 *            a BufferedImage to be saved
	 * @param destFile
	 *            destination file (absolute or relative path)
	 * @param quality
	 *            a float between 0 and 1, where 1 means uncompressed.
	 * @throws IOException
	 *             in case of problems writing the file
	 */
	private static void writeJpeg(BufferedImage image, File destFile, float quality)
			throws IOException {
		ImageWriter writer = null;
		FileImageOutputStream output = null;
		try {
			writer = ImageIO.getImageWritersByFormatName("jpeg").next();

			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			output = new FileImageOutputStream(destFile);
			writer.setOutput(output);
			IIOImage iioImage = new IIOImage(image, null, null);
			writer.write(null, iioImage, param);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (writer != null) writer.dispose();
			if (output != null) output.close();
		}
	}

	private static void createGif(String imagesFolder, final String gifName) throws IOException, FileNotFoundException, IIOException {
		final File imagesFolderFile = new File(imagesFolder);

		// grab the output image type from the first image in the sequence
		BufferedImage firstImage = ImageIO.read(imagesFolderFile.listFiles()[0]);

		// create a new BufferedOutputStream with the last argument
		ImageOutputStream output =
				new FileImageOutputStream(new File(gifName));

		// create a gif sequence with the type of the first image
		GifSequenceWriter writer =
				new GifSequenceWriter(output, firstImage.getType(), 10, false);

		// write out the first image to our sequence...
		writer.writeToSequence(firstImage);

		for (File imageFile : imagesFolderFile.listFiles()) {
			if (imageFile.isFile()) {
				System.out.println("append: " + imageFile.getName());
				BufferedImage nextImage = ImageIO.read(imageFile);
				writer.writeToSequence(nextImage);
			}
		}

		writer.close();
		output.close();
	}
}