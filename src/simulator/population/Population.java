package simulator.population;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import simulator.environment.Environment;
import simulator.maths.MathsUtils;
import simulator.maths.UnaryTypePair;
import simulator.maths.Vec2i;
import simulator.maths.Vec3M;

public class Population {
	/**
	 * Abstract representation of language similarities and differences.
	 * Two languages are said to be distinct unintelligible languages if the a language has a distance of 1 from another
	 */
	private Vec3M language;
	/**
	 * Abstract Intelligence, Build, Strength. Each from [-1,1]
	 */
	private final Vec3M stats;

	private int x, y;
	long position;
	private int size;
	private boolean toAlter = false;
	private boolean settled = false;
	private Environment settledIn;

	public Population(Random rand, int size) {
		this.language = new Vec3M(rand.nextDouble() * 20 - 10, rand.nextDouble() * 20 - 10, rand.nextDouble() * 20 - 10);
		this.stats = new Vec3M(rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1);
		this.size = size;
		this.positionInfo(0, 0);

		POPULATIONS.add(this);
		world.addPopulation(this.position, this);
	}

	private Population(Population old, double proportion) {
		this.language = old.language.clone();
		this.stats = old.stats.clone();
		this.size = (int) (old.size * proportion);
		old.size -= this.size;
		this.positionInfo(old.x, old.y);

		POPULATIONS.add(this);
		world.addPopulation(this.position, this);
	}

	private void ensureStatsAreValid() {
		// x
		double stat = this.stats.getX();
		if (stat > 1) this.stats.setX(1);
		else if (stat < -1) this.stats.setX(-1);
		// y
		stat = this.stats.getY();
		if (stat > 1) this.stats.setY(1);
		else if (stat < -1) this.stats.setY(-1);
		// z
		stat = this.stats.getZ();
		if (stat > 1) this.stats.setZ(1);
		else if (stat < -1) this.stats.setZ(-1);
	}

	private void setPosition(int x, int y) {
		world.removePopulation(this);
		this.positionInfo(x, y);
		world.addPopulation(this.position, this);
	}

	private void positionInfo(int x, int y) {
		// world loops back on itself
		if (x > WORLD_RADIUS) x = -WORLD_RADIUS;
		else if (x < -WORLD_RADIUS) x = WORLD_RADIUS;
		if (y > WORLD_RADIUS) y = -WORLD_RADIUS;
		else if (y < -WORLD_RADIUS) y = WORLD_RADIUS;

		this.x = x;
		this.y = y;
		this.position = hashCoord(x, y);
	}

	private void tick(Random rand) {
		// alter stats and language of population
		this.language.increase((rand.nextDouble() - 0.5) * 0.02, (rand.nextDouble() - 0.5) * 0.02, (rand.nextDouble() - 0.5) * 0.02);
		this.stats.increase((rand.nextDouble() - 0.5) * 0.005, (rand.nextDouble() - 0.5) * 0.005, (rand.nextDouble() - 0.5) * 0.005);
		Environment env = world.getEnvironment(this.position);
		env.modify(this.stats);
		this.ensureStatsAreValid();

		// move
		this.move(rand);

		List<Population> populationsAtPos = world.getPopulations(this.position);

		// split some populations, if there's not too many at one position
		if (populationsAtPos.size() < 3) {
			// can only split if the size is worth it
			if (this.size > 20) {
				if (POPULATIONS.size() < MAX_POPULATION_COUNT) {
					if (rand.nextInt(this.independence()) == 0) {
						TO_SPLIT.add(this);
						this.toAlter = true;
					}
				}
			}
		}

		// might merge populations if there are exactly two
		merge: do {
			if (populationsAtPos.size() == 2) {
				Population p0 = populationsAtPos.get(0);
				Population p1 = populationsAtPos.get(1);

				// if none will alter in any way already
				if (p0.toAlter || p1.toAlter) {
					break merge;
				}

				// must be of similar build
				double buildDifference = Math.abs(p0.stats.getY() - p1.stats.getY());

				if (differentRace(buildDifference)) {
					break merge;
				}

				// merge if properties allow it
				double linguisticDifference = p0.language.distanceTo(p1.language);

				if (linguisticDifference > 2) { // difficult language difference. someone needs to be familiar with the language already
					boolean p1IsDominant = p0.dominance() < p1.dominance();

					// less dominant party needs intelligence greater than 0.15 to merge
					if (p1IsDominant) {
						if (p0.stats.getX() > 0.15) {
							if (rand.nextBoolean()) {
								prepareMerge(p0, p1);
							}
						}
					} else {
						if (p1.stats.getX() > 0.15) {
							if (rand.nextBoolean()) {
								prepareMerge(p0, p1);
							}
						}
					}
				} else if (linguisticDifference > 1) { // language difference. someone needs to be adept at learning similar languages.
					if (rand.nextInt(5) == 0) {
						prepareMerge(p0, p1);
					}
				} else if (linguisticDifference > 0.6) { // major dialectal difference
					if (rand.nextInt(3) == 0) {
						prepareMerge(p0, p1);
					}
				} else { // no significant difference
					if (rand.nextBoolean()) {
						prepareMerge(p0, p1);
					}
				}
			}
		} while (false);

		if (this.size < 500) {
			this.size *= 1.5;
		}

		this.size /= 1.11 + 0.1 * this.stats.getZ();

		if (this.size <= 0) {
			this.die();
		}
	}

	private void move(Random rand) {
		if (this.settled) {
			// if too large or too dumb cannot settle in one location
			if (this.inelligableForSettling() || rand.nextInt(this.settledIn.isSettleable() ? 25 : 15) == 0) {
				this.settled = false;
			} else {
				return;
			}
		}

		int i = rand.nextInt(4);

		// find nearby populations, and alter movement accordingly
		double intelligence = this.stats.getX();
		boolean lowIntelligence = intelligence < -0.2;
		Vec2i directionTowards = null;

		if (lowIntelligence || intelligence > 0.2) {
			outerCheckLoop: for (int xo = -2; xo <= 2; ++xo) {
				int totalX = this.x + xo;

				if (totalX > WORLD_RADIUS || totalX < -WORLD_RADIUS) {
					continue;
				}

				for (int yo = -2; yo <= 2; ++yo) {
					if (xo == 0 && yo == 0) {
						continue;
					}

					int totalY = this.y + yo;

					if (totalY > WORLD_RADIUS || totalY < -WORLD_RADIUS) {
						continue;
					}

					long pos = hashCoord(totalX, totalY);
					List<Population> populations = world.getPopulations(pos);

					if (populations != null) {
						if (!populations.isEmpty()) {
							Population primary = populations.get(0);
							// if intelligence is low, keep away! best survival strat. ug
							directionTowards = MathsUtils.directionTowards(this.x, this.y, primary.x, primary.y);

							if (lowIntelligence) {
								directionTowards = directionTowards.mirror();
							} else {
								double buildDifference = Math.abs(this.stats.getY() - primary.stats.getY());

								if (differentRace(buildDifference)) {
									directionTowards = directionTowards.mirror();
								} else if (this.ignorePopulation(rand, buildDifference)) {
									break outerCheckLoop;
								}
							}

							i = 4;
							break outerCheckLoop;
						}
					}
				}
			}
		}

		switch (i) {
		case 0:
			this.setPosition(this.x, this.y + 1);
			break;
		case 1:
			this.setPosition(this.x + 1, this.y);
			break;
		case 2:
			this.setPosition(this.x, this.y - 1);
			break;
		case 3:
			this.setPosition(this.x - 1, this.y);
			break;
		case 4:
			this.setPosition(this.x + directionTowards.getX(), this.y + directionTowards.getY());
			break;
		}

		this.trySettle(rand);
	}

	private void trySettle(Random rand) {
		if (this.elligableForSettling()) {
			Environment env = world.getEnvironment(this.position);

			if (rand.nextInt(env.isSettleable() ? 10 : 30) == 0) {
				this.settled = true;
				this.settledIn = env;
			}
		}
	}

	private boolean inelligableForSettling() {
		return this.size > 1120 || this.stats.getX() < -0.4;
	}

	private boolean elligableForSettling() {
		return this.size <= 1000 && this.stats.getX() > 0.4;
	}

	private boolean ignorePopulation(Random rand, double buildDiff) {
		return buildDiff > 0.4 || rand.nextInt(this.independence()) > 5;
	}

	private int independence() {
		return 2 + (int) ((8 - this.stats.getX() * 8) + (20 - MathsUtils.clampMap(this.dominance(), 0, 100, 0, 20)));
	}

	private double dominance() {
		return this.size * this.stats.getZ();
	}

	private void merge(Population other) {
		POPULATIONS.remove(other);
		world.removePopulation(other);

		// who is dominant?
		if (other.dominance() > this.dominance()) {
			this.language = other.language;
		}

		this.stats.mixProportionally(this.size, other.size, other.stats);

		this.size += other.size;
	}

	private void die() {
		TO_DIE.add(this);
	}

	private void split(double proportion) {
		new Population(this, proportion);
		this.toAlter = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Population:\n");
		sb.append("\tSize:\t").append(this.size).append("\n");
		sb.append("\tIntelligence:\t").append(this.stats.getX()).append("\n");
		sb.append("\tBuild:\t").append(this.stats.getY()).append("\n");
		sb.append("\tStrength:\t").append(this.stats.getZ()).append("\n");
		sb.append("\tAbstract Language Info:\t").append(this.language.getX() + "/" + this.language.getY() + "/" + this.language.getZ()).append("\n");
		sb.append("\tPosition:\t").append(this.x + "/" + this.y).append("\tIn Environment:\t").append(world.getEnvironment(position)).append("\n");
		sb.append("\tSettled:\t").append(this.settled);
		return sb.toString();
	}

	public static void tickPopulations(Random rand) {
		TO_MERGE.clear();
		TO_SPLIT.clear();
		TO_DIE.clear();
		POPULATIONS.forEach(p -> p.tick(rand));
		TO_MERGE.forEach(pair -> pair.a.merge(pair.b));
		TO_SPLIT.forEach(p -> p.split(rand.nextDouble()));
		TO_DIE.forEach(p -> kill(p));
	}

	private static void kill(Population p) {
		POPULATIONS.remove(p);
		world.removePopulation(p);
	}

	private static void prepareMerge(Population p0, Population p1) {
		TO_MERGE.add(new UnaryTypePair<>(p0, p1));
		p0.toAlter = true;
		p1.toAlter = true;
	}

	private static boolean differentRace(double buildDiff) {
		return buildDiff > 0.8;
	}

	public static long hashCoord(int x, int y) {
		return ((long) x & 0xFFFFFFFFL) | (((long) y & 0xFFFFFFFFL) << 32);
	}

	public static Population[] getAllPopulations() {
		return POPULATIONS.toArray(new Population[0]);
	}

	private static final List<Population> POPULATIONS = new ArrayList<>();
	public static final int WORLD_RADIUS = 20;
	public static World world;
	private static final int MAX_POPULATION_COUNT = 20;

	private static final List<UnaryTypePair<Population>> TO_MERGE = new ArrayList<>();
	private static final List<Population> TO_SPLIT = new ArrayList<>();
	private static final List<Population> TO_DIE = new ArrayList<>();
}
