package game.core;

import game.controllers.SceneController;
import game.utils.Path;

import java.awt.*;

public class Point {
    private int point;
    private Image img0;
    private Image img1;
    private Image img2;
    private Image img3;
    private Image img4;
    private Image img5;
    private Image img6;
    private Image img7;
    private Image img8;
    private Image img9;

    public Point() {
        point = 0;

        img0 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number0());
        img1 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number1());
        img2 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number2());
        img3 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number3());
        img4 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number4());
        img5 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number5());
        img6 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number6());
        img7 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number7());
        img8 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number8());
        img9 = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().number9());

    }

    public Image imgDigits(int inputPoint) {
        point = inputPoint;
        int digits = point % 10;
        if (digits == 0) {
            return img0;
        }
        if (digits == 1) {
            return img1;
        }
        if (digits == 2) {
            return img2;
        }
        if (digits == 3) {
            return img3;
        }
        if (digits == 4) {
            return img4;
        }
        if (digits == 5) {
            return img5;
        }
        if (digits == 6) {
            return img6;
        }
        if (digits == 7) {
            return img7;
        }
        if (digits == 8) {
            return img8;
        }
        if (digits == 9) {
            return img9;
        }
        else return img0;
    }

    public Image imgTens(int inputPoint) {
        point = inputPoint;
        int tens = (point / 10) % 10;

        if (tens == 0) {
            return img0;
        }
        if (tens == 1) {
            return img1;
        }
        if (tens == 2) {
            return img2;
        }
        if (tens == 3) {
            return img3;
        }
        if (tens == 4) {
            return img4;
        }
        if (tens == 5) {
            return img5;
        }
        if (tens == 6) {
            return img6;
        }
        if (tens == 7) {
            return img7;
        }
        if (tens == 8) {
            return img8;
        }
        if (tens == 9) {
            return img9;
        }
        else return img0;
    }

    public Image imgHundreds(int inputPoint) {
        point = inputPoint;
        int hundreds = point / 100;

        if (hundreds == 0) {
            return img0;
        }
        if (hundreds == 1) {
            return img1;
        }
        if (hundreds == 2) {
            return img2;
        }
        if (hundreds == 3) {
            return img3;
        }
        if (hundreds == 4) {
            return img4;
        }
        if (hundreds == 5) {
            return img5;
        }
        if (hundreds == 6) {
            return img6;
        }
        if (hundreds == 7) {
            return img7;
        }
        if (hundreds == 8) {
            return img8;
        }
        if (hundreds == 9) {
            return img9;
        }
        else return img0;
    }

}
