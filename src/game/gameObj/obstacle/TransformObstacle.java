package game.gameObj.obstacle;

import game.core.Global;
import game.core.Movement;
import game.gameObj.GameObject;
import game.gameObj.Transformation;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;

import java.awt.*;

//地圖第二層物件 可變身  //這個應該要是可移動(多一個方法)也可變身的物件
public class TransformObstacle extends GameObject implements Transformation {
    //動畫處理部分拉出
    private Animation animation;

    public TransformObstacle(int x, int y, ImgArrAndType imgArrAndType) {
        super(x, y, Global.OBSTACLE_WIDTH, Global.OBSTACLE_HEIGHT);
        this.animation = new Animation(imgArrAndType);
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());

    }


    public boolean isXYin(int x, int y) {
        if (x <= painter().right() && x >= painter().left() && y <= painter().bottom() && y >= painter().top()) {
            return true;
        }
        return false;
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


    }


    @Override
    public ImgArrAndType getImgArrAndType() {
        return animation.getImg();
    }
}
