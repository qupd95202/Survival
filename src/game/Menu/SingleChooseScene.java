package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
import game.scene.SinglePointGameScene;
import game.scene.SingleSurvivalGameScene;
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

//    //滑鼠
//    private Mouse mouse;

    //動畫
    ArrayList<Animation> animations;

    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene9());


        //文字
        labels = new ArrayList<Label>();
        labels.add(new Label(Global.SCREEN_X / 3 + 30, Global.SCREEN_Y / 4 + 80, "     POINT MODE  ", FontLoader.Blocks(40)));
        labels.add(new Label(Global.SCREEN_X / 3 + 30, labels.get(0).painter().bottom() + 200, " SURVIVAL MODE ", FontLoader.Blocks(40)));

        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(labels.get(0).painter().left(), labels.get(0).painter().top() - 40, 380, 40));
        buttons.add(new Button(labels.get(1).painter().left(), labels.get(1).painter().top() - 40, 380, 40));
        buttons.add(new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT));

//        mouse=new Mouse(0,0,50,50);

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
        AudioResourceController.getInstance().stop(new Path().sound().background().lovelyflower());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);

        for (int i = 0; i < animations.size() - 1; i++) {
            animations.get(i).paint(Global.SCREEN_X / 4, Global.SCREEN_Y / 4 + i * 200, Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, g);
        }

        animations.get(2).paint(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).paint(g);
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
        if (state == CommandSolver.MouseState.CLICKED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                sceneEnd();
                SceneController.getInstance().change(new SinglePointGameScene());
            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                sceneEnd();
                SceneController.getInstance().change(new SingleSurvivalGameScene());
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                SceneController.getInstance().change(new MenuScene());
            }

        }
    }
}
