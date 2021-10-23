package game.Menu;

import game.Teach.TeachScene;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.scene.SinglePointGameScene;
import game.scene.Scene;
import game.scene.SingleSurvivalGameScene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuScene extends Scene implements CommandSolver.MouseCommandListener {
    //背景圖片
    private Image img;

    //按鈕
    private ArrayList<Button> buttons;

    //文字
    private ArrayList<Label> labels;


    //滑鼠
    private Mouse mouse;


    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene5());
        AudioResourceController.getInstance().loop(new Path().sound().background().lovelyflower(), -1);
        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 50, 360, 70));
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 130, 360, 70));
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 210, 360, 70));
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 290, 360, 70));

        //文字
        labels = new ArrayList<Label>();
        labels.add(new Label(Global.SCREEN_X / 3 - 70, Global.SCREEN_Y / 4, "Survival", 100));
        labels.add(new Label(buttons.get(0).collider().left() + 15, buttons.get(0).collider().top() + 50, "  SINGLE GAME ", 40));
        labels.add(new Label(buttons.get(1).collider().left() + 15, buttons.get(1).collider().top() + 50, " CREATE ROOM ", 40));
        labels.add(new Label(buttons.get(2).collider().left() + 15, buttons.get(2).collider().top() + 50, "CONNECT ROOM", 40));
        labels.add(new Label(buttons.get(3).collider().left() + 15, buttons.get(3).collider().top() + 50, "   TEACH  GAME ", 40));

        mouse = new Mouse(0, 0, 50, 50);
    }

    @Override
    public void sceneEnd() {
        this.labels=null;
        this.buttons=null;
        this.img=null;
        this.mouse=null;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).paint(g);
        }
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }


        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }

        mouse.paint(g);

    }

    @Override
    public void update() {
        mouse.update();
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
            if (mouse.isCollision(buttons.get(0))) {
                SceneController.getInstance().change(new SingleChooseScene());
            }
            if (mouse.isCollision(buttons.get(1))) {
                SceneController.getInstance().change(new SingleSurvivalGameScene());
            }
            if (mouse.isCollision(buttons.get(2))) {
                SceneController.getInstance().change(new ConnectScene());
            }
            if (mouse.isCollision(buttons.get(3))) {
                SceneController.getInstance().change(new TeachScene());
            }
        }
        mouse.mouseTrig(e, state, trigTime);

    }
}
