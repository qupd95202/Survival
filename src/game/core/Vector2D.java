package game.core;

//拉出2D移動的計算
public class Vector2D {
    private int x;
    private int y;


    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //方向 * 速度
    public void multply(double speed) {
        x *= speed;
        y *= speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }


    //向量變化
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

}
