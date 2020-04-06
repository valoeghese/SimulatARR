package simulator.maths;

public class Vec3M extends Vec3 implements Cloneable {
	public Vec3M(double x, double y, double z) {
		super(x, y, z);
	}

	public void increase(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void mixProportionally(int thisSize, int otherSize, Vec3M other) {
		double totalProportion = thisSize + otherSize;
		double thisProportion = thisSize / totalProportion;
		double otherProportion = otherSize / totalProportion;

		this.x = (thisProportion * this.x) + (otherProportion * other.x);
		this.y = (thisProportion * this.y) + (otherProportion * other.y);
		this.z = (thisProportion * this.z) + (otherProportion * other.z);
	}

	@Override
	public Vec3M clone() {
		return new Vec3M(this.x, this.y, this.z);
	}
}
