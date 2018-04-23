package InternetTetris;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StopWatch");
        primaryStage.setScene(new Scene(getPane(), 400, 400));
        primaryStage.show();
    }


    private BorderPane getPane() {
        BorderPane pane = new BorderPane();

        ClockUI clockUI = new ClockUI();
        clockUI.setMinSize(200, 200);
        pane.setCenter(clockUI);

        ButtonBar buttonBar = new ButtonBar();
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> clockUI.startClock());
        Button pauseButton = new Button("Stop");
        pauseButton.setOnAction(e -> clockUI.stopClock());
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> clockUI.resetClock());
        buttonBar.getButtons().addAll(startButton, pauseButton, resetButton);
        pane.setBottom(buttonBar);

        return pane;
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }
}