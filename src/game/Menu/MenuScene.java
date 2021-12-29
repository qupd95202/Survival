package game.Menu;

import game.Teach.TeachScene;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
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

    //動畫
    ArrayList<Animation> animations;

    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene9());
        AudioResourceController.getInstance().loop(new Path().sound().background().lovelyflower(), -1);

        Font font=FontLoader.cuteChinese(40);

        labels = new ArrayList<Label>();
        labels.add(new Label(Global.SCREEN_X / 3 +30, Global.SCREEN_Y / 4 - 30, "MENU", FontLoader.cuteChinese(130)));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(0).painter().bottom() + 100, "   SINGLE GAME ", font));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(1).painter().bottom() + 100, "   CREATE ROOM ", font));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(2).painter().bottom() + 100, "  CONNECT ROOM", font));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(3).painter().bottom() + 100, "   TEACH  GAME ", font));


        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(labels.get(1).painter().left() - 5, labels.get(1).painter().top() - 45, 370, 50, new Animation(AllImages.inputButton)));
        buttons.add(new Button(labels.get(2).painter().left() - 5, labels.get(2).painter().top() - 45, 370, 50, new Animation(AllImages.inputButton)));
        buttons.add(new Button(labels.get(3).painter().left() - 5, labels.get(3).painter().top() - 45, 370, 50, new Animation(AllImages.inputButton)));
        buttons.add(new Button(labels.get(4).painter().left() - 5, labels.get(4).painter().top() - 45, 370, 50, new Animation(AllImages.inputButton)));


        //動畫
        animations = new ArrayList<>();
        animations.add(new Animation(AllImages.beige));
        animations.add(new Animation(AllImages.blue));
        animations.add(new Animation(AllImages.pink));
        animations.add(new Animation(AllImages.yellow));


    }

    @Override
    public void sceneEnd() {
        this.labels = null;
        this.img = null;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).paint(Global.SCREEN_X / 4, Global.SCREEN_Y / 4 + i * 100, Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, g);
        }


        for (int i = 0; i < buttons.size(); i++) {
            if (Global.mouse.isCollision(buttons.get(i))) {
                buttons.get(i).paint(g);
            }
        }

        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }


        Global.mouse.paint(g);

    }

    @Override
    public void update() {
        Global.mouse.update();
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).update();
        }

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
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                SceneController.getInstance().change(new SingleChooseScene());
            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                SceneController.getInstance().change(new CreateRoomScene());
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                SceneController.getInstance().change(new ConnectRoomScene());
            }
            if (Global.mouse.isCollision(buttons.get(3))) {
                AudioResourceController.getInstance().pause(new Path().sound().background().lovelyflower());
                SceneController.getInstance().change(new TeachScene());
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }
}
