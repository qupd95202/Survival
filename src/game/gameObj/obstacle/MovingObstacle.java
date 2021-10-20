package game.gameObj.obstacle;

import game.controllers.SceneController;
import game.core.Global;
import game.core.Movement;
import game.gameObj.GameObject;
import game.gameObj.Transformation;
import game.gameObj.mapObj.MapObject;
import game.graphic.Animation;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.util.ArrayList;

//這個應該是要不可移動但可變身的物件
public class MovingObstacle extends TransformObstacle {
    private ArrayList<Image> img;

    //動畫處理部分拉出
    private Animation animation;

    //給能變身物件也能移動
    private Movement movement;
    private Delay movementDeley; //每隔一小段時間再動


    public MovingObstacle(int x, int y,Animation animation) {
        super(x, y,animation);
        img = Global.bumpImg;

        this.animation = animation;
        this.canPass = false;

        //給能變身物件也能移動
        movement = new Movement(1);
        movementDeley =  new Delay(120);
    }

    private void move() {
        
        translate(Global.random(-1, 1), Global.random(-1, 1));
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

    @Override
    public Animation getAnimation() {
        return animation;
    }
}
