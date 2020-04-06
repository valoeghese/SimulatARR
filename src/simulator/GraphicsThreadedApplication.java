package simulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class GraphicsThreadedApplication extends Application {
	protected static Settings settings = new Settings();

	@Override
	public void start(Stage stage) throws Exception {
		Pane pane = new Pane();
		Canvas canvas = new Canvas(settings.width, settings.height);
		pane.getChildren().add(canvas);
		stage.setScene(new Scene(pane));
		stage.setWidth(settings.width);
		stage.setHeight(settings.height + 39);

		this.start(canvas.getGraphicsContext2D(), settings.width, settings.height);
		stage.setOnCloseRequest(this::onClose);
		stage.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onClick);
		stage.show();
	}

	protected abstract void start(GraphicsContext gc, int width, int height);

	protected void onClose(WindowEvent event) {
	}

	protected void onClick(MouseEvent event) {
	}

	protected static class Settings {
		public int width = 600;
		public int height = 600;
	}

	protected static void newUpdateThread(Runnable subscriber, long delay) {
		Thread t = new Thread(() -> {
			while (true) {
				subscriber.run();

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		t.setDaemon(true);
		t.start();
	}
}
