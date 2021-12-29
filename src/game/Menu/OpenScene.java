package game.Menu;

import game.Teach.TeachScene;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OpenScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    private Image img;
    private ArrayList<Label> labels;
    private Delay time;
    private int count;
    private Label title;
    private ArrayList<Button> buttons;


    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().loop(new Path().sound().background().openScene(), -1);
        labels = new ArrayList<>();
        buttons = new ArrayList<>();
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene4());
        title = new Label(Global.SCREEN_X / 5 - 150, Global.SCREEN_Y / 5 - 30, "SURVIVAL", FontLoader.cuteChinese(80), Color.white);
        labels.add(new Label(Global.SCREEN_X / 3 - 40, Global.SCREEN_Y / 5 * 4 - 40, "   CLICK HERE TO START", FontLoader.cuteChinese(35)));
        labels.add(new Label(Global.SCREEN_X / 3 + 50, Global.SCREEN_Y / 5 * 4 - 40, "", FontLoader.cuteChinese(40)));
        time = new Delay(60);
        time.play();
        time.loop();
        count = 0;
        buttons.add(new Button(Global.SCREEN_X / 3 - 45, Global.SCREEN_Y / 5 * 4 - 75, 460, 40));
        buttons.add(new Button(Global.SCREEN_X / 3 - 45, Global.SCREEN_Y / 5 * 4 - 75, 460, 40, new Animation(AllImages.inputButton)));
    }

    @Override
    public void sceneEnd() {
        img = null;
        labels = null;
        title = null;
        time = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().openScene());

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        title.paint(g);
        for (int i = 0; i < buttons.size(); i++) {
            if (Global.mouse.isCollision(buttons.get(1))) {
                buttons.get(1).paint(g);
            }
            buttons.get(0).paint(g);
        }
        labels.get(count).paint(g);
        Global.mouse.paint(g);
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
        return this;
    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                SceneController.getInstance().change(new TeachScene());
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }


    @Override
    public void keyPressed(int commandCode, long trigTime) {

    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.ENTER.getValue()) {
            SceneController.getInstance().change(new TeachScene());
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }
}
