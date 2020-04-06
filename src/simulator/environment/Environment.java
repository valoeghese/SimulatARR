package simulator.environment;

import simulator.maths.Vec2d;
import simulator.maths.Vec3;
import simulator.maths.Vec3M;

public class Environment {
	public Environment(String name, Vec2d climate, Vec3 statsEffect, boolean settleable) {
		this.name = name;
		this.climate = climate;
		this.statsEffect = statsEffect;
		this.settleable = settleable;
	}

	private final String name;
	private final Vec2d climate;
	private final Vec3 statsEffect;
	private final boolean settleable;

	public Vec2d getClimate() {
		return this.climate;
	}

	public boolean isSettleable() {
		return this.settleable;
	}

	public void modify(Vec3M stats) {
		stats.increase(this.statsEffect.getX(), this.statsEffect.getY(), this.statsEffect.getZ());
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static final Environment GRASSLANDS = new Environment("grasslands", new Vec2d(0.5, 0.5), new Vec3(0, -0.01, 0), true);
	public static final Environment FOREST = new Environment("forest", new Vec2d(0.5, 1.0), new Vec3(0, -0.01, 0.01), true);
	public static final Environment TROPICAL_RAINFOREST = new Environment("tropical_rainforest", new Vec2d(1.0, 1.0), new Vec3(0, -0.01, 0.02), false);
	public static final Environment TROPICAL_GRASSLANDS = new Environment("tropical_grasslands", new Vec2d(1.0, 0.5), new Vec3(0, -0.01, 0.01), true);
	public static final Environment DESERT = new Environment("desert", new Vec2d(1.0, 0.0), new Vec3(0, -0.02, -0.01), false);
	public static final Environment ICE = new Environment("ice", new Vec2d(0.0, 0.5), new Vec3(0, 0.01, -0.01), false);
}
