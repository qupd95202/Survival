package game.scene;


import game.map.GameMap;
import game.utils.CommandSolver;

import java.awt.*;

public abstract class Scene {
    //加入要畫的背景地圖 (每個場景要有自己的地圖)
    protected GameMap gameMap;

    public abstract void sceneBegin();

    public abstract void sceneEnd();

    public abstract void paint(Graphics g);

    public abstract void update();

    public abstract CommandSolver.MouseCommandListener mouseListener();

    public abstract CommandSolver.KeyListener keyListener();

}
