package simulator;

import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import simulator.maths.MathsUtils;
import simulator.maths.Vec2d;
import simulator.population.Population;
import simulator.population.World;

public class Simulator extends GraphicsThreadedApplication {
	private static final int SECTION_WIDTH = 10;
	private static int WORLD_DIAMETER;

	public static void main(String[] args) {
		WORLD_DIAMETER = Population.WORLD_RADIUS * 2 + 1;
		settings.height = WORLD_DIAMETER * SECTION_WIDTH;
		settings.width = WORLD_DIAMETER * SECTION_WIDTH;
		Population.world = new World(RAND, WORLD_DIAMETER);
		launch(new String[0]);
	}

	private PixelWriter pwr;
	private int width, height;

	@Override
	protected void start(GraphicsContext gc, int width, int height) {
		this.pwr = gc.getPixelWriter();
		this.width = width;
		this.height = height;
		new Population(RAND, 50);
		newUpdateThread(this::update, 100);
	}

	@Override
	protected void onClose(WindowEvent event) {
		System.out.println("==== Final Populations ====");
		for (Population p : Population.getAllPopulations()) {
			System.out.println(p.toString());
			System.out.println();
		}
	}

	@Override
	protected void onClick(MouseEvent event) {
		System.out.println("==== Populations ====");
		for (Population p : Population.getAllPopulations()) {
			System.out.println(p.toString());
			System.out.println();
		}
	}

	private void update() {
		Population.tickPopulations(RAND);
		Platform.runLater(this::draw);
	}

	private void draw() {
		for (int x = 0; x < this.width; ++x) {
			int worldX = (x / SECTION_WIDTH) - Population.WORLD_RADIUS;

			for (int y = 0; y < this.height; ++y) {
				int drawY = this.height - y - 1;
				int worldY = (y / SECTION_WIDTH) - Population.WORLD_RADIUS;

				long hashCoord = Population.hashCoord(worldX, worldY);

				List<Population> populations = Population.world.getPopulations(hashCoord);
				Vec2d climate = Population.world.getEnvironment(hashCoord).getClimate();

				int size = populations == null ? 0 : populations.size();
				Color colour = new Color(climate.getX() * 0.5, MathsUtils.clampMap(size, 0.0, 5.0, 0.0, 1.0), climate.getY() * 0.5, 1.0);
				this.pwr.setColor(x, drawY, colour);
			}
		}
	}

	private static final Random RAND = new Random();
}
