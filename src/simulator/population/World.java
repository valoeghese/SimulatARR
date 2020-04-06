package simulator.population;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import simulator.environment.Environment;
import simulator.maths.noise.OpenSimplexNoise;

public final class World {
	public World(Random rand, int radius) {
		OpenSimplexNoise noise = new OpenSimplexNoise(rand);

		for (int x = -radius; x <= radius; ++x) {
			for (int y = -radius; y <= radius; ++y) {
				long position = Population.hashCoord(x, y);
				double temperature = noise.sample(x / 14D, y / 14D);
				double humidity = noise.sample(x / 6D, y / 6D);

				if (temperature < -0.3) {
					ENVIRONMENTS.put(position, Environment.ICE);
				} else if (temperature > 0.3) {
					if (humidity > 0.3) {
						ENVIRONMENTS.put(position, Environment.TROPICAL_RAINFOREST);
					} else if (humidity < 0.3) {
						ENVIRONMENTS.put(position, Environment.DESERT);
					} else {
						ENVIRONMENTS.put(position, Environment.TROPICAL_GRASSLANDS);
					}
				} else {
					if (humidity > 0.5) {
						ENVIRONMENTS.put(position, Environment.FOREST);
					} else {
						ENVIRONMENTS.put(position, Environment.GRASSLANDS);
					}
				}
			}
		}
	}

	public void addPopulation(long position, Population population) {
		POPULATIONS.computeIfAbsent(position, l -> new ArrayList<>()).add(population);
	}

	public void removePopulation(Population population) {
		long loc = population.position;

		if (POPULATIONS.containsKey(loc)) {
			POPULATIONS.get(loc).remove(population);
		}
	}

	public List<Population> getPopulations(long position) {
		return POPULATIONS.get(position);
	}

	public Environment getEnvironment(long position) {
		return ENVIRONMENTS.getOrDefault(position, Environment.GRASSLANDS);
	}

	private static final Map<Long, List<Population>> POPULATIONS = new HashMap<>();
	private static final Map<Long, Environment> ENVIRONMENTS = new HashMap<>();
}
