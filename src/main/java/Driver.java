import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is the driver class of the whole pixel art program. This class extends the Application class.
 */
public class Driver extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        mainStage.setTitle("Pixeler");
        mainStage.setScene(PixelScene.initPixelScene());
        mainStage.show();
    }
}
