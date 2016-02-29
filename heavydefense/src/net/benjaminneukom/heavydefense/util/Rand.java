package net.benjaminneukom.heavydefense.util;

public class Rand {
	public static <T> T selectRandom(T... v) {
		return v[(int) (Math.random() * v.length)];
	}
}
