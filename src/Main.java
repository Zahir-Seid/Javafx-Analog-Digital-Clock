import com.clock.AnalogClock;
import com.clock.AnalogClockwork;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private final AnalogClockwork clockwork = new AnalogClockwork();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(final Stage stage) throws Exception {
        AnalogClock analogClock = new AnalogClock(clockwork);
        final Parent root = analogClock.createClock(stage);

        Scene scene = transparentScene((Parent) root);
        showTransparentStage(stage, scene);
    }


    private Scene transparentScene(Parent root) {
        return new Scene(root, Color.TRANSPARENT);
    }

    private void showTransparentStage(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
