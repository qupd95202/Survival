package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
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
    //滑鼠
    private Mouse mouse;
    //按鈕
    private ArrayList<Button> buttons;
    //文字
    private ArrayList<Label> labels;

    @Override
    public void sceneBegin() {
        //單人背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene7());


        //按鈕
        buttons=new ArrayList<Button>();
        buttons.add(new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4+80 , 360, 70));
        buttons.add(new Button(buttons.get(0).painter().left(), buttons.get(0).painter().bottom()+50, 360, 70));
        buttons.add(new Button(buttons.get(0).painter().left()+80, buttons.get(1).painter().bottom()+30, buttons.get(0).painter().width()/2, buttons.get(0).painter().height()/2));

        //文字
        labels =new ArrayList<Label>();
        labels.add(new Label(buttons.get(0).painter().left()+20,buttons.get(0).painter().top()+45 ,"SINGLE POINT GAME",30));
        labels.add(new Label(buttons.get(1).painter().left()+20,buttons.get(1).painter().top()+45 ,"          SURVIVAL   ",30));
        labels.add(new Label(buttons.get(2).collider().left()+50,buttons.get(2).collider().top()+25,"  BACK ",20));


        //滑鼠
        mouse=new Mouse(0,0,50,50);

    }

    @Override
    public void sceneEnd() {
        this.labels=null;
        this.buttons=null;
        this.img=null;
        this.mouse=null;
        AudioResourceController.getInstance().stop(new Path().sound().background().lovelyflower());
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
        return null;
    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if(state== CommandSolver.MouseState.MOVED){
            mouse.mouseTrig(e,state,trigTime);
        }
        if(state== CommandSolver.MouseState.CLICKED){
            if(mouse.isCollision(buttons.get(0))){
                SceneController.getInstance().change(new SinglePointGameScene());
            }
            if(mouse.isCollision(buttons.get(1))){
                SceneController.getInstance().change(new SingleSurvivalGameScene());
            }
            if(mouse.isCollision(buttons.get(2))){
                SceneController.getInstance().change(new MenuScene());
            }

        }
    }
}
