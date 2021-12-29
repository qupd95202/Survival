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
import java.util.ArrayList;

public class WinScene extends Scene implements CommandSolver.MouseCommandListener {
    private Image img;
    private Button button;
    private Label label;
    private Label label2;
    private Label label3;
    private ArrayList<Animation> animations;
    private Button touchButton;


    @Override
    public void sceneBegin() {
        animations = new ArrayList<>();
        AudioResourceController.getInstance().loop(new Path().sound().background().winScene(), -1);
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene2());
        button = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.cross));
        label = new Label(Global.SCREEN_X / 3 - 80, Global.SCREEN_Y / 2, "Too easy?", FontLoader.cuteChinese(100),Color.RED);
        label2 = new Label(Global.SCREEN_X / 4 - 120, Global.SCREEN_Y / 2 + 100, "Let's Battle in Connect Mode!!", FontLoader.dotChinese(50), Color.black);
        label3 = new Label(Global.SCREEN_X - 500, Global.SCREEN_Y - 50, "P.S.   You Have to Find Some Friends First. QQ ", FontLoader.Mini_Square(20), Color.pink);
        Animation animation = new Animation(AllImages.star);
        animation.setDelay(5);
        animations.add(animation);
        animations.add(animation);
        animations.add(animation);
        touchButton = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.inputButton));

    }

    @Override
    public void sceneEnd() {
        this.img = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().winScene());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        animations.get(0).paint(60, -150, 500, 500, g);
        animations.get(1).paint(30, Global.SCREEN_Y / 5 + 100, 500, 500, g);
        animations.get(2).paint(Global.SCREEN_X - 300, 200, 500, 500, g);
        if(Global.mouse.isCollision(button)){
            touchButton.paint(g);
        }
        button.paint(g);
        label.paint(g);
        label2.paint(g);
        label3.paint(g);
        Global.mouse.paint(g);
    }

    @Override
    public void update() {
        animations.get(0).update();
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
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(button)) {
                SceneController.getInstance().change(new MenuScene());
            }
        }
    }
}
