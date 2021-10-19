package game.core;

import game.core.Global.KeyCommand;
import game.utils.CommandSolver;

//將物件移動的處理拉出來
public class Movement implements CommandSolver.KeyListener {
    //Vector:向量 2D移動
    private Vector2D vector2D;
    private int speed;

    public Movement(int speed) {
        this.vector2D = new Vector2D(0, 0); //移動向量一開始是0
        this.speed = speed;
    }

    public void move(double moveOnX, double moveOnY) {
        int deltaX = 0;
        int deltaY = 0;
        if (moveOnX < 0) {
            deltaX--;
        } else if (moveOnX > 0) {
            deltaX++;
        }

        if (moveOnY < 0) {
            deltaY--;
        } else if (moveOnY > 0) {
            deltaY++;
        }
        vector2D = new Vector2D(deltaX, deltaY);
        //方向 * 速度
        vector2D.multply(speed);
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {
        int deltaX = 0;
        int deltaY = 0;
        if (commandCode == Global.KeyCommand.UP.getValue()) {
            deltaY--;
        }
        if (commandCode == KeyCommand.DOWN.getValue()) {
            deltaY++;
        }
        if (commandCode == Global.KeyCommand.LEFT.getValue()) {
            deltaX--;
        }
        if (commandCode == Global.KeyCommand.RIGHT.getValue()) {
            deltaX++;
        }
//        System.out.println(deltaX);
//        System.out.println(deltaY);
        vector2D = new Vector2D(deltaX, deltaY);
        //方向 * 速度
        vector2D.multply(speed);
    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {

    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }

    //取得移動變化
    public Vector2D getVector2D() {
        return vector2D;
    }

    //是否有在移動
    public boolean isMoving() {
        return vector2D.length() > 0;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }
    public void addSpeed(int addSpeed){
        this.speed+=addSpeed;
    }
}
