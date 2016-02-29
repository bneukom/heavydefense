package net.benjaminneukom.heavydefense.tower;

import net.benjaminneukom.heavydefense.util.MoreMath;
import net.benjaminneukom.heavydefense.util.ShapeRendererExt;
import net.benjaminneukom.heavydefense.util.ShapeRendererExt.LineEnd;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Lightning {
	private static final float MAX_TIME = 0.4f;

	private float time = MAX_TIME;

	private Array<LightningSegment> segments;
	private transient float[] lightningBuffer = new float[500];

	private boolean weak;

	private static float OFFSET = 35;

	private static final Vector2 MIDPOINT = new Vector2();
	private static final Vector2 NORMAL = new Vector2();

	public Lightning() {
	}

	public Lightning(float startX, float startY, float endX, float endY, float towerRange, boolean weak) {
		this.weak = weak;

		float dist = MoreMath.dist(startX, startY, endX, endY);
		float offset = OFFSET / towerRange * dist;
		offset = Math.max(15, offset);

		if (dist < 30) {
			this.segments = generateLightning(new Array<LightningSegment>(new LightningSegment[] { new LightningSegment(startX, startY, endX, endY) }), 1, offset);
		} else if (dist < 55) {
			this.segments = generateLightning(new Array<LightningSegment>(new LightningSegment[] { new LightningSegment(startX, startY, endX, endY) }), 2, offset);
		} else if (dist < 75) {
			this.segments = generateLightning(new Array<LightningSegment>(new LightningSegment[] { new LightningSegment(startX, startY, endX, endY) }), 3, offset);
		} else if (dist < 135) {
			this.segments = generateLightning(new Array<LightningSegment>(new LightningSegment[] { new LightningSegment(startX, startY, endX, endY) }), 4, offset);
		} else {
			this.segments = generateLightning(new Array<LightningSegment>(new LightningSegment[] { new LightningSegment(startX, startY, endX, endY) }), 5, offset);
		}
	}

	public void act(float delta) {

		time -= delta;
		time = Math.max(0, time);
	}

	public void draw(ShapeRenderer renderer) {

		if (!weak) {
			drawLightning(renderer, segments, 10, 20f / 255f, false);
			drawLightning(renderer, segments, 7, 50f / 255f, false);
			drawLightning(renderer, segments, 4, 65f / 255f, false);
			drawLightning(renderer, segments, 2, 85f / 255f, false);
			drawLightning(renderer, segments, 1, 110f / 255f, false);

			//			drawLightning(shapeRenderer, segments, 12, 30f / 255f, false);
			//			drawLightning(shapeRenderer, segments, 8, 50f / 255f, false);
			//			drawLightning(shapeRenderer, segments, 4, 80f / 255f, false);
			//			drawLightning(shapeRenderer, segments, 2, 125f / 255f, false);
			//			drawLightning(shapeRenderer, segments, 1, 145f / 255f, false);
		} else {
			drawLightning(renderer, segments, 2, 255 / 255f, false);
		}
	}

	private void drawLightning(ShapeRenderer shapeRenderer, Array<LightningSegment> segments, int size, float alpha, boolean ignoreAlphaChange) {
		final float changedAlpha = ignoreAlphaChange ? alpha : Math.min((float) (alpha * time / MAX_TIME), 1f);
		shapeRenderer.setColor(200f / 255f, 232f / 255f, 255f / 255f, changedAlpha);

		for (int lightningIndex = 0; lightningIndex < segments.size; ++lightningIndex) {
			LightningSegment lightningSegment = segments.get(lightningIndex);
			lightningBuffer[lightningIndex * 2 + 0] = lightningSegment.startX;
			lightningBuffer[lightningIndex * 2 + 1] = lightningSegment.startY;

			if (lightningIndex == segments.size - 1) {
				lightningBuffer[lightningIndex * 2 + 2] = lightningSegment.endX;
				lightningBuffer[lightningIndex * 2 + 3] = lightningSegment.endY;
			}
		}

		ShapeRendererExt.consecutiveLines(shapeRenderer, lightningBuffer, segments.size + 1, size, LineEnd.ROUND);
	}

	public Array<LightningSegment> generateLightning(Array<LightningSegment> segments, int generations, float offset) {
		if (generations <= 0)
			return segments;

		final Array<LightningSegment> newSegments = new Array<LightningSegment>();
		for (LightningSegment lightningSegment : segments) {
			final float startX = lightningSegment.startX;
			final float startY = lightningSegment.startY;
			final float endX = lightningSegment.endX;
			final float endY = lightningSegment.endY;
			final Vector2 midPoint = midPoint(endX, endY, startX, startY);

			NORMAL.set(-(endY - startY), endX - startX).nor();

			float rand = (float) (-offset + Math.random() * 2 * offset);
			midPoint.add(NORMAL.scl(rand));

			newSegments.add(new LightningSegment(startX, startY, midPoint.x, midPoint.y));
			newSegments.add(new LightningSegment(midPoint.x, midPoint.y, endX, endY));

			// add an offshoot with a certian change
			//			if (Math.random() < 0.3f) {
			//				float directionX = midPoint.x - startX;
			//				float directionY = midPoint.y - startY;
			//
			//				final float theta = (float) Math.toRadians(Math.random() * 10);
			//				directionX = (float) (directionX * Math.cos(theta) - directionY * Math.sin(theta));
			//				directionY = (float) (directionX * Math.sin(theta) + directionY * Math.cos(theta));
			//
			//				final float splitEndX = directionX * 0.9f + midPoint.x;
			//				final float splitEndY = directionY * 0.9f + midPoint.y;
			//
			//				newSegments.add(new LightningSegment(midPoint.x, midPoint.y, splitEndX, splitEndY));
			//			}
		}

		return generateLightning(newSegments, generations - 1, offset / 2);

	}

	private Vector2 midPoint(float ax, float ay, float bx, float by) {
		return MIDPOINT.set((ax + bx) / 2, (ay + by) / 2);
	}

	public float getTime() {
		return time;
	}

	private static class LightningSegment {
		public float startX;
		public float startY;
		public float endX;
		public float endY;

		@SuppressWarnings("unused")
		public LightningSegment() {
		}

		public LightningSegment(float startX, float startY, float endX, float endY) {
			super();
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}

	}

}
