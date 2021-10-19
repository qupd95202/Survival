package game.gameObj;

import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;

import java.awt.*;
import java.util.ArrayList;

public class Props extends GameObject {

    //設定道具落在哪
    public enum Type {
        addSpeed,
        teleportation,//瞬間移動
        trap; //陷阱
    }

    private Animation animation;
    private Type propsType;

    //先預設道具出現為隨機
    //之後再用enum設定
    public Props() {
        super(Global.random(0, Global.MAP_PIXEL_WIDTH), Global.random(0, Global.MAP_PIXEL_HEIGHT), Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
//        super(300,300, Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
        this.animation = AllImages.questionBox;
        this.propsType = genRandomType();
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
    }

    public Props(Type type) {
        super(Global.random(0, Global.MAP_PIXEL_WIDTH), Global.random(0, Global.MAP_PIXEL_HEIGHT), Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
//        super(300,300, Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
        this.animation = AllImages.questionBox;
        this.propsType = type;
    }

    @Override
    public void paintComponent(Graphics g) {
        animation.paint(painter().left(), painter().top(), painter().width(), painter().height(), g);
    }

    @Override
    public void update() {
        animation.update();
    }

    public Type getPropsType() {
        return propsType;
    }

    public Type genRandomType() {
        int randomNum = Global.random(1, 3);
        switch (randomNum) {
            case 1 -> {
                return Type.addSpeed;
            }
            case 2 -> {
                return Type.teleportation;
            }
            default -> {
                return Type.trap;
            }
        }
    }

}
