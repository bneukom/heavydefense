package net.benjaminneukom.heavydefense.util;

import com.badlogic.gdx.math.Vector2;

public class MoreMath {
	public static float dist(Vector2 a, Vector2 b) {
		return dist(a.x, a.y, b.x, b.y);
	}

	public static float dist(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;

		return (float) Math.hypot(dx, dy);
	}

	public static double angle(float x1, float y1, float x2, float y2) {
		float deltaX = x2 - x1;
		float deltaY = y2 - y1;

		return Math.atan2(deltaY, deltaX);
	}
}
