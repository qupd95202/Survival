package game.gameObj;

import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.PropsAnimation;

import java.awt.*;
import java.util.ArrayList;

public class Props extends GameObject {

    //設定道具落在哪
    public enum Type {
        addSpeed,
        teleportation,//瞬間移動
        trap, //陷阱
        addPoint, //加分
        thunder, //打雷讓所有獵人永久速度減少1
        superStar,//無敵星星
        timeStop, //時間暫停
        gameTimeDecrease, // 遊戲時間減少20
        hunterWatcher; //透視獵人位置

    }

    private Animation animation; //道具本身動畫
    private Type propsType;
    private boolean isGotByPlayer;

    PropsAnimation propsAnimation; //吃到道具時的特效動畫


    //先預設道具出現為隨機
    //之後再用enum設定
    public Props() {
        super(Global.random(0, Global.MAP_PIXEL_WIDTH), Global.random(0, Global.MAP_PIXEL_HEIGHT), Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
//        super(300,300, Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
        this.animation = new Animation(AllImages.questionBox);
        this.propsType = genRandomType();
        isGotByPlayer = false;
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());

    }

    public Props(int forSurvivalMode) {
        super(Global.random(0, Global.MAP_PIXEL_WIDTH), Global.random(0, Global.MAP_PIXEL_HEIGHT), Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
        this.animation = new Animation(AllImages.questionBox);
        this.propsType = genRandomTypeInSurvivalMode();
        isGotByPlayer = false;
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
    }

    public Props(int x, int y, Type type) {
        super(x, y, Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
        this.animation = new Animation(AllImages.questionBox);
        this.propsType = type;
    }

    @Override
    public void paintComponent(Graphics g) {
        animation.paint(painter().left(), painter().top(), painter().width(), painter().height(), g);
//        propsAnimation.paint(g);
    }

    @Override
    public void update() {
        animation.update();
//        propsAnimation.update();
    }

    public Type getPropsType() {
        return propsType;
    }

    public Type genRandomType() {
        int randomNum = Global.random(1, 4);
        switch (randomNum) {
            case 1 -> {
                return Type.addSpeed;
            }
            case 2 -> {
                return Type.teleportation;
            }
            case 3 -> {
                return Type.trap;
            }
            default -> {
                return Type.addPoint;
            }
        }
    }

    public Type genRandomTypeInSurvivalMode() {
        int randomNum = Global.random(1, 8);
        switch (randomNum) {
            case 1 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.addSpeed;
            }
            case 2 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.teleportation;
            }
            case 3 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.trap;
            }
            case 4 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.gameTimeDecrease;
            }
            case 5 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.superStar;
            }
            case 6 -> {//道具動畫（閃電）
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.thunder;
            }
            case 7 -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.hunterWatcher;
            }
            default -> {
                propsAnimation = new PropsAnimation(0, 0, 1100, 700, AllImages.lightning, 20, 120);
                return Type.timeStop;
            }
        }
    }

    public boolean isGotByPlayer() {
        return isGotByPlayer;
    }

    public void setGotByPlayer(boolean gotByPlayer) {
        isGotByPlayer = gotByPlayer;
    }

    public PropsAnimation getPropsAnimation() {
        return propsAnimation;
    }
}
