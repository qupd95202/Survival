package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.scene.SinglePointGameScene;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ConnectScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    //背景圖片
    private Image img;

    //按鈕
    private ArrayList<Button> buttons;

    //文字
    private ArrayList<Label> labels;

    //輸入文字
    private ArrayList<EditText> editTexts;

    //滑鼠
    private Mouse mouse;



    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene8());

        //按鈕
        buttons=new ArrayList<Button>();
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 , 360, 70));
        buttons.add(new Button(buttons.get(0).painter().left(), buttons.get(0).painter().bottom()+40, 360, 70));
        buttons.add(new Button(buttons.get(1).painter().left(), buttons.get(1).painter().bottom()+40, 360, 70));
        buttons.add(new Button(buttons.get(2).painter().left()-30, buttons.get(2).painter().bottom()+40, buttons.get(0).painter().width()/2, buttons.get(0).painter().height()/2));
        buttons.add(new Button(buttons.get(3).painter().right()+50, buttons.get(3).painter().top(), buttons.get(0).painter().width()/2, buttons.get(0).painter().height()/2));

        //文字
        labels =new ArrayList<Label>();
        labels.add(new Label(buttons.get(0).painter().left(),buttons.get(0).painter().top()-10 ,"Please Enter Your Name:",20));
        labels.add(new Label(buttons.get(1).painter().left(),buttons.get(1).painter().top()-10 ,"Please Enter Your IP:",20));
        labels.add(new Label(buttons.get(2).painter().left(),buttons.get(2).painter().top()-10 ,"Please Enter Your Pork:",20));
        labels.add(new Label(buttons.get(3).collider().left()+40,buttons.get(3).collider().top()+25,"   BACK ",20));
        labels.add(new Label(buttons.get(4).collider().left()+40,buttons.get(4).collider().top()+25," CONNECT ",20));

        //輸入文字
        editTexts=new ArrayList<EditText>();
        editTexts.add(new EditText(buttons.get(0).collider().left()+10,buttons.get(0).collider().top()+45,25,"NAME"));
        editTexts.add(new EditText(buttons.get(1).collider().left()+10,buttons.get(1).collider().top()+45,20,"IP"));
        editTexts.add(new EditText(buttons.get(2).collider().left()+10,buttons.get(2).collider().top()+45,20,"PORK"));

        mouse=new Mouse(0,0,50,50);
    }

    @Override
    public void sceneEnd() {

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

        mouse.paint(g);
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
        return this;
    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if(state== CommandSolver.MouseState.MOVED){
            mouse.mouseTrig(e,state,trigTime);
        }
        if(state== CommandSolver.MouseState.CLICKED){
            if(mouse.isCollision(buttons.get(4))){
                SceneController.getInstance().change(new SinglePointGameScene());
            }
            if(mouse.isCollision(buttons.get(3))){
                SceneController.getInstance().change(new MenuScene());
            }
            if(mouse.isCollision(buttons.get(0))){
                editTexts.get(0).setIsEditable(true);
                editTexts.get(1).setIsEditable(false);
                editTexts.get(2).setIsEditable(false);
            }
            if(mouse.isCollision(buttons.get(1))){
                editTexts.get(1).setIsEditable(true);
                editTexts.get(0).setIsEditable(false);
                editTexts.get(2).setIsEditable(false);
            }
            if(mouse.isCollision(buttons.get(2))){
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
        if(mouse.isCollision(buttons.get(0))){
            editTexts.get(0).keyTyped(c,trigTime);
        }
        if(mouse.isCollision(buttons.get(1))){
            editTexts.get(1).keyTyped(c,trigTime);
        }
        if(mouse.isCollision(buttons.get(2))){
            editTexts.get(2).keyTyped(c,trigTime);
        }
    }
}
