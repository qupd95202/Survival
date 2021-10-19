package game.graphic;

import game.controllers.SceneController;
import game.core.Global;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Animation {

    private ArrayList<Image> img;
    private Global.MapAreaType mapAreaType;
    private int count;
    private Delay delay;

    public Animation(ArrayList<Image> image, Global.MapAreaType mapAreaType) {
        this.mapAreaType = mapAreaType;
        this.img = image;
        delay = new Delay(30);
        delay.loop();
        count = 0;
    }


    public void paint(int x, int y, int width, int height, Graphics g) {
        g.drawImage(img.get(count), x, y, width, height, null);
    }

    public void update() {
        if (delay.count()) {
            count = ++count % img.size();
        }
    }

    public Global.MapAreaType getMapAreaType() {
        return mapAreaType;
    }
}