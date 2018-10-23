package application;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

class Animation {

    static void animateNode(Node node){
        TranslateTransition transition = new TranslateTransition(Duration.millis(50),node);
        transition.setFromX(0);
        transition.setFromY(0);
        transition.setToX(2.5);
        transition.setToY(2.5);
        transition.setCycleCount(6);
        transition.setAutoReverse(true);
        transition.playFromStart();
    }
}
