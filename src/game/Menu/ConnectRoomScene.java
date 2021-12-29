package game.Menu;

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

public class ConnectRoomScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    //背景圖片
    private Image img;

    //按鈕
    private ArrayList<Button> buttons;

    //文字
    private ArrayList<Label> labels;

    //輸入文字
    private ArrayList<EditText> editTexts;


    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene5());

        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4, 360, 70, new Animation(AllImages.inputButton)));
        buttons.add(new Button(buttons.get(0).painter().left(), buttons.get(0).painter().bottom() + 40, 360, 70, new Animation(AllImages.inputButton)));
        buttons.add(new Button(buttons.get(1).painter().left(), buttons.get(1).painter().bottom() + 40, 360, 70, new Animation(AllImages.inputButton)));
        buttons.add(new Button(buttons.get(2).painter().left() - 30, buttons.get(2).painter().bottom() + 40, buttons.get(0).painter().width() / 2, buttons.get(0).painter().height() / 2, new Animation(AllImages.inputButton)));
        buttons.add(new Button(buttons.get(3).painter().right() + 50, buttons.get(3).painter().top(), buttons.get(0).painter().width() / 2, buttons.get(0).painter().height() / 2, new Animation(AllImages.inputButton)));

        //文字
        labels = new ArrayList<Label>();
        labels.add(new Label(buttons.get(0).painter().left(), buttons.get(0).painter().top() - 10, "Please Enter Your Name:", FontLoader.cuteChinese(20)));
        labels.add(new Label(buttons.get(1).painter().left(), buttons.get(1).painter().top() - 10, "Please Enter Your IP:", FontLoader.cuteChinese(20)));
        labels.add(new Label(buttons.get(2).painter().left(), buttons.get(2).painter().top() - 10, "Please Enter Your Port:", FontLoader.cuteChinese(20)));
        labels.add(new Label(buttons.get(3).collider().left() + 40, buttons.get(3).collider().top() + 25, "   BACK ", FontLoader.cuteChinese(20)));
        labels.add(new Label(buttons.get(4).collider().left() + 40, buttons.get(4).collider().top() + 25, " CONNECT ", FontLoader.cuteChinese(20)));
        labels.add(new Label(Global.SCREEN_X-300, 50, " Max People:8 ", FontLoader.cuteChinese(20)));

        //輸入文字
        editTexts = new ArrayList<EditText>();
        editTexts.add(new EditText(buttons.get(0).collider().left() + 10, buttons.get(0).collider().top() + 45, "NAME(MaxWords:10)"));
        editTexts.get(0).setEditLimit(10);
        editTexts.add(new EditText(buttons.get(1).collider().left() + 10, buttons.get(1).collider().top() + 45, "IP"));
        editTexts.add(new EditText(buttons.get(2).collider().left() + 10, buttons.get(2).collider().top() + 45, "PORT"));


    }

    @Override
    public void sceneEnd() {
        img = null;
        labels = null;
        editTexts = null;

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


        for (int i = 0; i < editTexts.size(); i++) {
            editTexts.get(i).paint(g);

        }

        Global.mouse.paint(g);
    }

    @Override
    public void update() {
        for (int i = 0; i < editTexts.size(); i++) {
            editTexts.get(i).update();

        }
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
        if (state == CommandSolver.MouseState.MOVED) {
            Global.mouse.mouseTrig(e, state, trigTime);
        }
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(buttons.get(4))) {
                if (inputError()) {
                    labels.add(new Label(buttons.get(3).collider().left() - 20, buttons.get(2).collider().bottom() + 30, " Please Enter Correct Information ", FontLoader.Future(20)));
                    return;
                }
                SceneController.getInstance().change(new ChooseRoleScene(getIp(), getPlayerName(), Integer.parseInt(getPort())));
            }
            if (Global.mouse.isCollision(buttons.get(3))) {
                AudioResourceController.getInstance().pause(new Path().sound().background().lovelyflower());
                SceneController.getInstance().change(new MenuScene());
            }
            if (Global.mouse.isCollision(buttons.get(0))) {
                editTexts.get(0).setIsEditable(true);
                editTexts.get(1).setIsEditable(false);
                editTexts.get(2).setIsEditable(false);
            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                editTexts.get(1).setIsEditable(true);
                editTexts.get(0).setIsEditable(false);
                editTexts.get(2).setIsEditable(false);
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                editTexts.get(2).setIsEditable(true);
                editTexts.get(0).setIsEditable(false);
                editTexts.get(1).setIsEditable(false);
            }
        }
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {

    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {

    }

    @Override
    public void keyTyped(char c, long trigTime) {
        if (editTexts.get(0).getIsEditable()) {
            editTexts.get(0).keyTyped(c, trigTime);
        }
        if (editTexts.get(1).getIsEditable()) {
            editTexts.get(1).keyTyped(c, trigTime);
        }
        if (editTexts.get(2).getIsEditable()) {
            editTexts.get(2).keyTyped(c, trigTime);
        }
    }

    public String getPlayerName() {
        return editTexts.get(0).getEditText();
    }

    public String getIp() {
        return editTexts.get(1).getEditText();
    }

    public String getPort() {
        return editTexts.get(2).getEditText();
    }

    public boolean inputError() {
        return getPlayerName() == "" || getIp() == "" || getPort() == "";
    }
}
