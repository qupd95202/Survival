package game.gameObj.obstacle;

import game.gameObj.GameObject;
import game.gameObj.Transformation;
import game.gameObj.mapObj.MapObject;

import java.awt.*;

//單純路障
public class Obstacle extends GameObject {

    public Obstacle(int x, int y, int width, int height) {
        super(x, y, width, height, x, y, width, height);
        this.canPass = false;
    }


    @Override
    public void paintComponent(Graphics g) {

    }

    @Override
    public void update() {

    }

}
