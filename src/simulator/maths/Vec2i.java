package simulator.maths;

public class Vec2i {
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	private final int x;
	private final int y;

	public Vec2i mirror() {
		return new Vec2i(-this.x, -this.y);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public static final Vec2i ZERO = new Vec2i(0, 0);
	public static final Vec2i UP = new Vec2i(0, 1);
	public static final Vec2i DOWN = new Vec2i(0, -1);
	public static final Vec2i LEFT = new Vec2i(-1, 0);
	public static final Vec2i RIGHT = new Vec2i(1, 0);
}
