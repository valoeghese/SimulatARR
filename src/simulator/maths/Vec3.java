package simulator.maths;

public class Vec3 {
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	protected double x;
	protected double y;
	protected double z;

	public double distanceTo(Vec3 other) {
		double dx = Math.abs(other.x - this.x);
		double dy = Math.abs(other.y - this.y);
		double dz = Math.abs(other.z - this.z);

		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
}
