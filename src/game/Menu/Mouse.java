package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.net.URL;

public class Mouse extends GameObject implements CommandSolver.MouseCommandListener {
    private Image img;

    public static Image magicWand = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Button().magicWand());
    public static Image teleportationMouse = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Button().teleportationMouse());

    public Mouse(int x, int y, int width, int height) {
        super(x, y, width, height);
        img = magicWand;
    }


    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, painter().left(), painter().top(), painter().width(), painter().height(), null);
    }

    @Override
    public void update() {

    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.MOVED) {
            e.setSource(img);
            painter().setCenter(e.getX(), e.getY());
            collider().setCenter(e.getX(), e.getY());
        }

    }

    public void setImg(Image image) {
        this.img = image;
    }
}
