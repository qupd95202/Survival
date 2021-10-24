package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;

public class GameOverScene extends Scene implements CommandSolver.MouseCommandListener {
    private Image img;
    private Button button;
    private Animation animation;

    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().play(new Path().sound().background().gameover());
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene9());

        button = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.cross));
        animation = new Animation(AllImages.gameOver);
    }

    @Override
    public void sceneEnd() {
        this.button = null;
        this.animation = null;
        this.img = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().gameover());
        SceneController.getInstance().change(new MenuScene());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        button.paint(g);
        animation.paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 2 - 50, 350, 150, g);
        Global.mouse.paint(g);
    }

    @Override
    public void update() {

    }

    @Override
    public CommandSolver.MouseCommandListener mouseListener() {
        return this;
    }

    @Override
    public CommandSolver.KeyListener keyListener() {
        return null;
    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.MOVED) {
            Global.mouse.mouseTrig(e, state, trigTime);
        }
        if (state == CommandSolver.MouseState.CLICKED) {
            if (Global.mouse.isCollision(button)) {
                sceneEnd();
            }
        }
    }

}
