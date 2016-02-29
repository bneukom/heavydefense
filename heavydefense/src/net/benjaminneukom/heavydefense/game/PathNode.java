package net.benjaminneukom.heavydefense.game;

import com.badlogic.gdx.math.Vector2;

public class PathNode {

	private Vector2 position;
	private int index;
	private float width;
	private float height;

	public PathNode() {
	}

	public PathNode(Vector2 position, float width, float height, int index) {
		super();
		this.position = position;
		this.index = index;
		this.width = width;
		this.height = height;
	}

	public Vector2 getPosition() {
		return position;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getIndex() {
		return index;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public PathNode copy() {
		return new PathNode(position, width, height, index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + index;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + Float.floatToIntBits(width);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PathNode other = (PathNode) obj;

		// epsilon equals due to float
		if (Math.abs(height - other.height) > 1f) return false;
		if (Math.abs(width - other.width) > 1f) return false;

		if (position == null) {
			if (other.position != null) return false;
		} else if (!position.epsilonEquals(other.position, 1f)) return false;

		return true;
	}

	public static void main(String[] args) {
		PathNode p = new PathNode(new Vector2(2f, 2.5f), 50f, 125.5f, 0);
		PathNode p2 = new PathNode(new Vector2(2.1f, 2.7f), 50f, 125.5f, 0);

		System.out.println(p.equals(p2));
	}
}