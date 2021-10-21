package game.gameObj.obstacle;

import game.controllers.SceneController;
import game.core.Global;
import game.core.Movement;
import game.gameObj.GameObject;
import game.gameObj.Transformation;
import game.gameObj.mapObj.MapObject;
import game.graphic.Animation;

import game.graphic.ImgArrAndType;
import game.utils.Delay;

import game.utils.Path;

import java.awt.*;
import java.util.ArrayList;

//這個應該是要不可移動但可變身的物件
public class MovingObstacle extends TransformObstacle {

    //動畫處理部分拉出
    private Animation animation;

    //給能變身物件也能移動
    private Movement movement;
    private Delay randomMoveDelay;
    private int iniMoveOnX;
    private int iniMoveOnY;


    public MovingObstacle(int x, int y, ImgArrAndType animation) {
        super(x, y, animation);
        this.animation = new Animation(animation);
        this.canPass = false;

        //給能變身物件也能移動
        movement = new Movement(1);//一般角色移動
        randomMoveDelay = new Delay(180);
        randomMoveDelay.play();
        randomMoveDelay.loop();
        iniMoveOnX = Global.random(-2, 1);
        iniMoveOnY = Global.random(-2, 1);
    }

    private void move() {
        if (randomMoveDelay.count()) {
            iniMoveOnX = Global.random(-2, 1);
            iniMoveOnY = Global.random(-2, 1);
            if (Global.getProbability(80)) {
                iniMoveOnX = 0;
                iniMoveOnY = 0;
            }
        }
        movement.move(iniMoveOnX, iniMoveOnY);
        translate(movement.getVector2D().getX(), movement.getVector2D().getY());
        keepInMap();
    }

    public void keepInMap() {
        if (touchBottom() || touchLeft() || touchRight() || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        animation.paint(
                painter().left(),
                painter().top(),
                painter().width(),
                painter().height(),
                g);
    }

    @Override
    public void update() {
        animation.update();
        move();
    }
}
