package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OpenScene extends Scene implements CommandSolver.MouseCommandListener {
    private Image img;
    private ArrayList<Label> labels;
    //    private Mouse mouse;
    private Delay time;
    private int count;
    private Label title;
    private GameObject touchObj;

    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().loop(new Path().sound().background().openScene(),-1);
        labels = new ArrayList<>();
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene4());
        title = new Label(Global.SCREEN_X / 5 - 150, Global.SCREEN_Y / 5 - 30, "Survival", FontLoader.Blocks(80), Color.white);
        labels.add(new Label(Global.SCREEN_X / 3 + 50, Global.SCREEN_Y / 5 * 4 - 40, "TAP TO STAR", FontLoader.Blocks(35)));
        labels.add(new Label(Global.SCREEN_X / 3 + 50, Global.SCREEN_Y / 5 * 4 - 40, "", FontLoader.Blocks(40)));
//        mouse=new Mouse(0,0,50,50);
        time = new Delay(60);
        time.play();
        time.loop();
        count = 0;
        touchObj = new Button(Global.SCREEN_X / 3 + 50, Global.SCREEN_Y / 5 * 4 - 75, 260, 40);
    }

    @Override
    public void sceneEnd() {
        img = null;
        labels = null;
        title = null;
        time = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().openScene());
        SceneController.getInstance().change(new MenuScene());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        title.paint(g);
        labels.get(count).paint(g);
        Global.mouse.paint(g);
        touchObj.paint(g);
    }

    @Override
    public void update() {
        if (time.count()) {
            count = (count + 1) % 2;
        }

        Global.mouse.update();
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
        if (state == CommandSolver.MouseState.CLICKED) {
            if (Global.mouse.isCollision(touchObj)) {
                sceneEnd();
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }
}
