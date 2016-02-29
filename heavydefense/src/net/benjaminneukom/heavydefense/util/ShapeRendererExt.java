package net.benjaminneukom.heavydefense.util;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShapeRendererExt {
	private static float[] EDGES_BUFFER = new float[8];
	private static float[] INTERSECTION_BUFFER = new float[2];

	private final static double EPSILON = 0.0001;

	public enum LineEnd {
		FLAT, ROUND;
	}

	public static void consecutiveLines(final ShapeRenderer renderer, float[] points, int numPoints, float width, LineEnd lineEnd) {

		// initialize first edges
		final float[] edges = calculateEdges(points[0], points[1], points[2], points[3], width);
		float currentEdge1X = edges[0];
		float currentEdge1Y = edges[1];
		float currentEdge2X = edges[2];
		float currentEdge2Y = edges[3];
		float currentEdge3X = edges[4];
		float currentEdge3Y = edges[5];
		float currentEdge4X = edges[6];
		float currentEdge4Y = edges[7];

		float start1X = currentEdge1X;
		float start1Y = currentEdge1Y;
		float start2X = currentEdge2X;
		float start2Y = currentEdge2Y;

		// render all lines
		for (int point = 1; point < numPoints; ++point) {
			float end1X;
			float end1Y;
			float end2X;
			float end2Y;
			float nextEdge1X = 0;
			float nextEdge1Y = 0;
			float nextEdge2X = 0;
			float nextEdge2Y = 0;
			float nextEdge3X = 0;
			float nextEdge3Y = 0;
			float nextEdge4X = 0;
			float nextEdge4Y = 0;

			// if there is a next segment intersect boundary lines
			if (point + 1 < numPoints) {
				final float[] nextEdges = calculateEdges(points[0 + 2 * point], points[1 + 2 * point], points[2 + 2 * point], points[3 + 2 * point], width);
				nextEdge1X = nextEdges[0];
				nextEdge1Y = nextEdges[1];
				nextEdge2X = nextEdges[2];
				nextEdge2Y = nextEdges[3];
				nextEdge3X = nextEdges[4];
				nextEdge3Y = nextEdges[5];
				nextEdge4X = nextEdges[6];
				nextEdge4Y = nextEdges[7];

				final float[] end1 = intersection(currentEdge1X, currentEdge1Y, currentEdge4X, currentEdge4Y, nextEdge1X, nextEdge1Y, nextEdge4X, nextEdge4Y);
				if (end1 != null) {
					end1X = end1[0];
					end1Y = end1[1];
				} else {
					end1X = currentEdge4X;
					end1Y = currentEdge4Y;
				}

				final float[] end2 = intersection(currentEdge2X, currentEdge2Y, currentEdge3X, currentEdge3Y, nextEdge2X, nextEdge2Y, nextEdge3X, nextEdge3Y);
				if (end2 != null) {
					end2X = end2[0];
					end2Y = end2[1];
				} else {
					end2X = currentEdge3X;
					end2Y = currentEdge3Y;
				}

			} else {
				end1X = currentEdge4X;
				end1Y = currentEdge4Y;
				end2X = currentEdge3X;
				end2Y = currentEdge3Y;
			}

			// render line
			renderer.triangle(start2X, start2Y, end2X, end2Y, end1X, end1Y);
			renderer.triangle(end1X, end1Y, start1X, start1Y, start2X, start2Y);

			// prepare for next line
			start1X = end1X;
			start1Y = end1Y;
			start2X = end2X;
			start2Y = end2Y;

			currentEdge1X = nextEdge1X;
			currentEdge1Y = nextEdge1Y;
			currentEdge2X = nextEdge2X;
			currentEdge2Y = nextEdge2Y;
			currentEdge3X = nextEdge3X;
			currentEdge3Y = nextEdge3Y;
			currentEdge4X = nextEdge4X;
			currentEdge4Y = nextEdge4Y;
		}

		if (lineEnd == LineEnd.ROUND) {
			renderer.circle(points[(numPoints - 1) * 2], points[(numPoints - 1) * 2 + 1], Math.max(2, width * 0.9f));
		}
	}

	/**
	 * Returns the intersection of the two lines by the given points. Returns <code>null</code> if the lines are parallel.
	 * 
	 * @return
	 */
	private static float[] intersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		if (isAlmostZero(det) || Float.isNaN(det)) return null;

		float x = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / det;
		float y = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / det;
		INTERSECTION_BUFFER[0] = x;
		INTERSECTION_BUFFER[1] = y;

		return INTERSECTION_BUFFER;
	}

	private static boolean isAlmostZero(float val) {
		return Math.abs(val) < EPSILON;
	}

	private static float[] calculateEdges(float x1, float y1, float x2, float y2, float width) {
		final float lineX = x2 - x1;
		final float lineY = y2 - y1;
		//		final float lineLength = Math.max(1f, (float) Math.hypot(lineX, lineY)); // fix for really short lines
		final float lineLength = (float) Math.hypot(lineX, lineY);

		final float perpendicularWidthOffsetX = -lineY / lineLength * width / 2;
		final float perpendicularWidthOffsetY = lineX / lineLength * width / 2;

		EDGES_BUFFER[0] = x1 - perpendicularWidthOffsetX;
		EDGES_BUFFER[1] = y1 - perpendicularWidthOffsetY;
		EDGES_BUFFER[2] = x1 + perpendicularWidthOffsetX;
		EDGES_BUFFER[3] = y1 + perpendicularWidthOffsetY;
		EDGES_BUFFER[4] = x2 + perpendicularWidthOffsetX;
		EDGES_BUFFER[5] = y2 + perpendicularWidthOffsetY;
		EDGES_BUFFER[6] = x2 - perpendicularWidthOffsetX;
		EDGES_BUFFER[7] = y2 - perpendicularWidthOffsetY;

		return EDGES_BUFFER;
	}

	public static void line(ShapeRenderer renderer, float x1, float y1, float x2, float y2, float width) {
		final float startX = x1;
		final float startY = y1;
		float endX = x2;
		float endY = y2;
		final float lineX = x2 - x1;
		final float lineY = y2 - y1;
		final float lineLength = (float) Math.hypot(lineX, lineY);

		final float perpendicularWidthOffsetX = -lineY / lineLength * width / 2;
		final float perpendicularWidthOffsetY = lineX / lineLength * width / 2;

		endX += lineX / lineLength * width;
		endY += lineY / lineLength * width;

		final float edge1X = startX - perpendicularWidthOffsetX;
		final float edge1Y = startY - perpendicularWidthOffsetY;
		final float edge2X = endX - perpendicularWidthOffsetX;
		final float edge2Y = endY - perpendicularWidthOffsetY;
		final float edge3X = endX + perpendicularWidthOffsetX;
		final float edge3Y = endY + perpendicularWidthOffsetY;
		final float edge4X = startX + perpendicularWidthOffsetX;
		final float edge4Y = startY + perpendicularWidthOffsetY;

		renderer.triangle(edge1X, edge1Y, edge2X, edge2Y, edge3X, edge3Y);
		renderer.triangle(edge1X, edge1Y, edge3X, edge3Y, edge4X, edge4Y);
	}
}
