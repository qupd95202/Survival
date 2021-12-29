package game.Menu;

import game.Teach.SurvivalPropsRuleScene;
import game.Teach.TeachPointGameScene;
import game.Teach.TeachScene;
import game.Teach.TeachSurvivalGameScene;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
import game.scene.SinglePointGameScene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SingleChooseScene extends Scene implements CommandSolver.MouseCommandListener {
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

//        Font font=new Font("",Font.BOLD,40);
        Font font=FontLoader.cuteChinese(40);
        //文字
        labels = new ArrayList<Label>();
        labels.add(new Label(Global.SCREEN_X / 3 + 30, Global.SCREEN_Y / 4 + 80, "    POINT MODE  ",font));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(0).painter().bottom() + 200, "   SURVIVAL MODE ", font));

        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(labels.get(0).painter().left(), labels.get(0).painter().top() - 40, 380, 40));
        buttons.add(new Button(labels.get(1).painter().left(), labels.get(1).painter().top() - 40, 380, 40));
        buttons.add(new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT));
        buttons.add(new Button(labels.get(0).painter().left(), labels.get(0).painter().top() - 40, 380, 40, new Animation(AllImages.inputButton)));
        buttons.add(new Button(labels.get(1).painter().left(), labels.get(1).painter().top() - 40, 380, 40, new Animation(AllImages.inputButton)));
        buttons.add(new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.inputButton)));


        //動畫
        animations = new ArrayList<>();
        animations.add(new Animation(AllImages.beige));
        animations.add(new Animation(AllImages.blue));
        animations.add(new Animation(AllImages.cross));


    }

    @Override
    public void sceneEnd() {
        this.labels = null;
        this.img = null;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);

        for (int i = 0; i < animations.size() - 1; i++) {
            animations.get(i).paint(Global.SCREEN_X / 4, Global.SCREEN_Y / 4 + i * 200, Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, g);
        }

        animations.get(2).paint(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);

        for (int i = 0; i < buttons.size(); i++) {
            if (i == 3) {
                if (Global.mouse.isCollision(buttons.get(i))) {
                    buttons.get(i).paint(g);
                }

            } else if (i == 4) {
                if (Global.mouse.isCollision(buttons.get(i))) {
                    buttons.get(i).paint(g);
                }
            } else if (i == 5) {
                if (Global.mouse.isCollision(buttons.get(i))) {
                    buttons.get(i).paint(g);
                }
            } else {
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
        if (state == CommandSolver.MouseState.MOVED) {
            Global.mouse.mouseTrig(e, state, trigTime);
        }
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                AudioResourceController.getInstance().pause(new Path().sound().background().lovelyflower());
                SceneController.getInstance().change(new TeachPointGameScene());
            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                AudioResourceController.getInstance().pause(new Path().sound().background().lovelyflower());
                SceneController.getInstance().change(new TeachSurvivalGameScene());
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                AudioResourceController.getInstance().pause(new Path().sound().background().lovelyflower());
                SceneController.getInstance().change(new MenuScene());
            }
        }
    }
}
