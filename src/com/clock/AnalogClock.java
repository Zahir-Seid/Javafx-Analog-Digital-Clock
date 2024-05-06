package com.clock;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Circle;
import javafx.scene.Group;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.Parent;
import javafx.scene.Node;
import java.time.LocalDateTime;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
public class AnalogClock {

    private static final double START_RADIUS = 350;
    private static final int NO_HOUR_TICKS = 12;
    private static final int NO_MINUTE_TICKS = 60;
    private final AnalogClockwork clockwork;

    public AnalogClock(AnalogClockwork clockwork) {
        this.clockwork = clockwork;
    }

    private Circle circle;
    private RadialGradient gradient; // Declare gradient as a class member

    public Parent createClock(Stage stage) {
        circle = new Circle(START_RADIUS); // Initialize circle
        circle.setCenterX(START_RADIUS);
        circle.setCenterY(START_RADIUS);

        gradient = createRadialGradient();
        Parent root = new Group(
                clockDial(),
                minuteTickMarks(),
                hourTickMarks(),
                hourHand(),
                minuteHand(),
                secondsHand()
        );
        setUpMouseForScaleAndMove(stage, root);
        return root;
    }

    private Node clockDial() {
        // Create the analog clock dial
        circle.setFill(gradient);

        // Create the digital clock text
        Text digitalClock = new Text();
        digitalClock.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        digitalClock.setFill(Color.BLACK);

        // Create a timeline to update the clock every second
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {

                    digitalClock.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));

                    // Position the digital clock inside the circle based on current time
                    double digitalClockOffsetY = LocalDateTime.now().getMinute() >= 28 && LocalDateTime.now().getMinute() <= 33
                            || LocalDateTime.now().getHour() % 12 >= 2 && LocalDateTime.now().getHour() % 12 <= 3
                            ? -0.4 * START_RADIUS
                            : 0.4 * START_RADIUS;
                    double digitalClockWidth = digitalClock.getBoundsInLocal().getWidth() + 20; // Add padding
                    double digitalClockHeight = digitalClock.getBoundsInLocal().getHeight() + 10; // Add padding
                    double boxLayoutX = START_RADIUS - digitalClockWidth / 2;
                    double boxLayoutY = START_RADIUS + digitalClockOffsetY;
                    digitalClock.setLayoutX(boxLayoutX + 10); // Add padding
                    digitalClock.setLayoutY(boxLayoutY + digitalClockHeight / 2);

                    // Adjust the position of the digital clock text
                    digitalClock.relocate(boxLayoutX + 10, boxLayoutY + digitalClockHeight / 2);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Create a Group to hold both the analog clock dial and the digital clock
        Group clockGroup = new Group(circle, digitalClock);

        return clockGroup;
    }


    private RadialGradient createRadialGradient() {
        Stop stops[] = {
                new Stop(0.92, Color.WHITE),
                new Stop(0.98, Color.BLACK),
                new Stop(1.0, Color.BLACK)
        };
        return new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);
    }

    private Node hourHand() {
        double distanceFromRim = START_RADIUS * 0.5;
        Rotate rotate = handRotation(clockwork.hourHandAngle());
        return hourOrMinuteHand(distanceFromRim, Color.BLACK, rotate);
    }

    private Node minuteHand() {
        double distanceFromRim = START_RADIUS * 0.75;
        Rotate rotate = handRotation(clockwork.minuteHandAngle());
        return hourOrMinuteHand(distanceFromRim, Color.BLACK, rotate);
    }

    private Node secondsHand() {
        double distanceFromRim = START_RADIUS * 0.7;
        Color handColor = Color.RED;
        Rotate rotate = handRotation(clockwork.secondsHandAngle());

        Group group = new Group();
        group.getChildren().addAll(
                secondsHandLine(distanceFromRim, handColor),
                secondsHandTip(distanceFromRim, handColor),
                centerPoint(handColor)
        );
        group.getTransforms().add(rotate);

        return group;
    }


    private Node secondsHandTip(double distanceFromRim, Color handColor) {
        double handTipRadius = START_RADIUS * 0.07;

        Circle circle = new Circle();
        circle.setCenterX(START_RADIUS);
        circle.setCenterY(START_RADIUS - distanceFromRim);
        circle.setFill(handColor);
        circle.setRadius(handTipRadius);

        return circle;
    }


    private Node secondsHandLine(double distanceFromRim, Paint handColor) {
        double handCenterExtension = START_RADIUS * 0.15;
        double handWidth = START_RADIUS * 0.02;

        Line line = new Line();
        line.setStartX(START_RADIUS);
        line.setStartY(START_RADIUS - distanceFromRim);
        line.setEndX(START_RADIUS);
        line.setEndY(START_RADIUS + handCenterExtension);
        line.setStrokeWidth(handWidth);
        line.setStroke(handColor);

        return line;
    }


    private Rotate handRotation(ObservableDoubleValue handAngle) {
        Rotate handRotation = new Rotate();
        handRotation.setPivotX(START_RADIUS);
        handRotation.setPivotY(START_RADIUS);
        handRotation.angleProperty().bind(handAngle);
        return handRotation;
    }


    private Node hourOrMinuteHand(double distanceFromRim, Color color, Rotate rotate) {
        double handBaseWidth = START_RADIUS * 0.05;
        double handTipWidth = START_RADIUS * 0.03;
        double handCenterExtension = START_RADIUS * 0.15;
        double leftBaseCornerX = START_RADIUS - handBaseWidth;
        double baseY = START_RADIUS + handCenterExtension;
        double tipY = START_RADIUS - distanceFromRim;
        double leftTipCornerX = START_RADIUS - handTipWidth;
        double rightTipCornerX = START_RADIUS + handTipWidth;
        double rightCornerBaseX = START_RADIUS + handBaseWidth;
        Path path = new Path();
        path.setFill(color);
        path.setStroke(Color.TRANSPARENT);
        path.getElements().addAll(
                new MoveTo(leftBaseCornerX, baseY),
                new LineTo(leftTipCornerX, tipY),
                new LineTo(rightTipCornerX, tipY),
                new LineTo(rightCornerBaseX, baseY),
                new LineTo(leftBaseCornerX, baseY)
        );
        path.getTransforms().add(rotate);
        return path;
    }


    private Node minuteTickMarks() {
        Group tickMarkGroup = new Group();
        int noTicks = NO_MINUTE_TICKS;
        for (int n = 0; n < noTicks; n++) {
            tickMarkGroup.getChildren().add(tickMark(n, 1, noTicks));
        }
        return tickMarkGroup;
    }

    private Node hourTickMarks() {
        Group tickMarkGroup = new Group();
        int noTicks = NO_HOUR_TICKS;
        for (int n = 0; n < noTicks; n++) {
            tickMarkGroup.getChildren().add(tickMark(n, 6, noTicks));
        }
        return tickMarkGroup;
    }

    private Node tickMark(int n, double width, int noTicks) {
        Line line = new Line();
        line.setStartX(START_RADIUS);
        line.setStartY(START_RADIUS * 0.12);
        line.setEndX(START_RADIUS);
        line.setEndY(START_RADIUS * 0.2 + width * 2);

        Rotate rotate = new Rotate();
        rotate.setPivotX(START_RADIUS);
        rotate.setPivotY(START_RADIUS);
        rotate.setAngle(360 / noTicks * n);

        line.getTransforms().add(rotate);

        line.setStrokeWidth(width);

        return line;
    }


    private Node centerPoint(Color color) {
        Circle circle = new Circle();
        circle.setFill(color);
        circle.setRadius(0.03 * START_RADIUS);
        circle.setCenterX(START_RADIUS);
        circle.setCenterY(START_RADIUS);
        return circle;
    }


    private void setUpMouseForScaleAndMove(final Stage stage, final Parent root) {
        SimpleDoubleProperty mouseStartX = new SimpleDoubleProperty(0);
        SimpleDoubleProperty mouseStartY = new SimpleDoubleProperty(0);
        root.setOnMousePressed(setMouseStartPoint(mouseStartX, mouseStartY));
        root.setOnMouseDragged(moveWhenDragging(stage, mouseStartX, mouseStartY));
        root.onScrollProperty().set(scaleWhenScrolling(stage, root));
    }

    private EventHandler<? super MouseEvent> setMouseStartPoint(final SimpleDoubleProperty mouseStartX, final SimpleDoubleProperty mouseStartY) {
        return new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                mouseStartX.set(mouseEvent.getX());
                mouseStartY.set(mouseEvent.getY());
            }
        };
    }

    private EventHandler<MouseEvent> moveWhenDragging(final Stage stage, final SimpleDoubleProperty mouseStartX, final SimpleDoubleProperty mouseStartY) {
        return new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                stage.setX(stage.getX() + mouseEvent.getX() - mouseStartX.doubleValue());
                stage.setY(stage.getY() + mouseEvent.getY() - mouseStartY.doubleValue());
            }
        };
    }

    private EventHandler<ScrollEvent> scaleWhenScrolling(final Stage stage, final Parent root) {
        return new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent scrollEvent) {
                double scroll = scrollEvent.getDeltaY();
                root.setScaleX(root.getScaleX() + scroll / 100);
                root.setScaleY(root.getScaleY() + scroll / 100);
                root.setTranslateX(root.getTranslateX() + scroll);
                root.setTranslateY(root.getTranslateY() + scroll);
                stage.sizeToScene();
            }
        };
    }



}