package simulator.maths;

public class Vec2d {
	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	private final double x;
	private final double y;

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
}
