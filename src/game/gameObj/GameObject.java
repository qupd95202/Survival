package game.gameObj;


import game.core.Global;
import game.core.Position;
import game.core.Size;
import game.utils.GameKernel;

import java.awt.*;

public abstract class GameObject implements GameKernel.GameInterface {

    //碰撞用（方塊）
    private Rect collider;
    private Rect painter;

    //位置 與 大小
    protected Position position;
    protected Size size;

    protected boolean canPass; //可不可以穿過


    public GameObject(int x, int y, int width, int height) {
        collider = new Rect(x, y, width, height);
        painter = new Rect(x, y, width, height);
        position = new Position(x, y);
        size = new Size(width, height);
    }

    public GameObject(int x, int y, int width, int height, int x2, int y2, int width2, int height2) {
        collider = new Rect(x, y, width, height);
        painter = new Rect(x2, y2, width2, height2);
        painter.setCenter(collider.centerX(), collider.centerY()); //置中
        position = new Position(x, y);
        size = new Size(width, height);
    }

    public GameObject(Rect rect) {
        painter = rect.clone();
        collider = rect.clone();
    }

    public GameObject(Rect rect, Rect rect2) {
        painter = rect.clone();
        collider = rect2.clone();
        painter.setCenter(collider.centerX(), collider.centerY());
    }


    public final void translate(int x, int y) {
        collider.translate(x, y);
        painter.translate(x, y);
    }

    public final void translateX(int x) {
        collider.translateX(x);
        painter.translateX(x);
    }

    public final void translateY(int y) {
        collider.translateY(y);
        painter.translateY(y);
    }

    public boolean isCollision(GameObject object) {
        return collider.overlap(object.collider);
    }

    public final Rect collider() {
        return collider;
    }

    public final Rect painter() {
        return painter;
    }

    public boolean touchTop() {
        return collider.top() <= 0;
    }

    public boolean touchBottom() {
        return collider.bottom() >= Global.MAP_PIXEL_HEIGHT;
    }

    public boolean touchLeft() {
        return collider.left() <= 0;
    }

    public boolean touchRight() {
        return collider.right() >= Global.MAP_PIXEL_WIDTH;
    }

    public void setXY(int x, int y) {
        collider.setXY(x, y);
        painter.setXY(x, y);
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
    }


    public Position getPosition() {
        return position;
    }

    public Size getSize() {
        return size;
    }


    @Override
    public final void paint(Graphics g) {
        paintComponent(g);
        if (Global.IS_DEBUG) {
            g.setColor(Color.RED);
            collider.paint(g);
            g.setColor(Color.GREEN);
            painter.paint(g);
            g.setColor(Color.black);
        }
    }

    public abstract void paintComponent(Graphics g);
}
