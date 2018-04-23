package InternetTetris;

import javafx.animation.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Created by subhranil on 23/6/17.
 */
public class ClockUI extends StackPane {

    private final Rotate hourRotate;
    private final Rotate minuteRotate;
    private final Rotate secondRotate;
    private final Timeline hourTimeline;
    private final Timeline minuteTimeline;
    private final Timeline secondTimeline;
    private final ParallelTransition clockTransition;

    public ClockUI() {
        super();

        Line hourHand = getHand(80, Color.WHITE);
        hourRotate = getRotate(hourHand);
        hourTimeline = createRotateTimeline(Duration.hours(12), hourRotate);

        Line minuteHand = getHand(100, Color.WHITE);
        minuteRotate = getRotate(minuteHand);
        minuteTimeline = createRotateTimeline(Duration.minutes(60), minuteRotate);

        Line secondHand = getHand(90, Color.WHITE);
        secondRotate = getRotate(secondHand);
        secondTimeline = createRotateTimeline(Duration.seconds(60), secondRotate);

        clockTransition = new ParallelTransition(hourTimeline, minuteTimeline, secondTimeline);

        Circle back = new Circle(120);
        back.centerXProperty().bind(widthProperty().divide(2));
        back.centerYProperty().bind(heightProperty().divide(2));
        back.setStyle("-fx-fill: #555555");
        setStyle("-fx-background-color: #333333;");

        getChildren().addAll(back, hourHand, minuteHand, secondHand);
    }

    private Timeline createRotateTimeline(Duration duration, Rotate rotate) {
        Timeline timeline = new Timeline(30);
        timeline.getKeyFrames().add(new KeyFrame(duration, new KeyValue(rotate.angleProperty(), 360)));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public void startClock() {
        if (clockTransition.getStatus() != Animation.Status.RUNNING) {
            clockTransition.play();
        }
    }

    public void stopClock() {
        if (clockTransition.getStatus() == Animation.Status.RUNNING) {
            clockTransition.pause();
        }
    }

    public void resetClock() {
        stopClock();
        clockTransition.stop();
    }

    private Rotate getRotate(Line line){
        Rotate r = new Rotate(0);
        r.pivotXProperty().bind(line.startXProperty());
        r.pivotYProperty().bind(line.startYProperty());
        line.getTransforms().add(r);
        return r;
    }

    private Line getHand(int size, Paint color) {
        Line hand = new Line();
        hand.startXProperty().bind(widthProperty().divide(2));
        hand.startYProperty().bind(heightProperty().divide(2));
        hand.endXProperty().bind(widthProperty().divide(2));
        hand.endYProperty().bind(heightProperty().divide(2).subtract(size));
        hand.setStroke(color);
        hand.setStrokeWidth(3);

        return hand;
    }

}