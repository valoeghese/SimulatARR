package simulator.maths;

public final class MathsUtils {
	public static double clampMap(double value, double min, double max, double newmin, double newmax) {
		value -= min;
		value /= (max - min);
		value = newmin + value * (newmax - newmin);

		if (value > newmax) {
			return newmax;
		} else if (value < newmin) {
			return newmin;
		} else {
			return value;
		}
	}

	public static Vec2i directionTowards(int startX, int startY, int endX, int endY) {
		if (endX > startX) {
			if (endY > startY) {
				return new Vec2i(1, 1);
			} else if (endY < startY) {
				return new Vec2i(1, -1);
			} else {
				return Vec2i.RIGHT;
			}
		} else if (endX < startX) {
			if (endY > startY) {
				return new Vec2i(-1, 1);
			} else if (endY < startY) {
				return new Vec2i(-1, -1);
			} else {
				return Vec2i.LEFT;
			}
		}

		if (endY > startY) {
			return Vec2i.UP;
		} else if (endY < startY) {
			return Vec2i.DOWN;
		}

		return Vec2i.ZERO;
	}
}
