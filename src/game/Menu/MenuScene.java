package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MenuScene extends Scene  implements CommandSolver.MouseCommandListener {
    private Image img;
    private Button singleButton;//單機遊戲按鍵
    private Button createRoomButton;//創建房間按鍵
    private Button connectButton;//進入房間
    private Button teachButton;//教學按鍵
    private Mouse mouse;
    private Label gameName;
    private Label labelSingle;
    private Label labelCreateRoom;
    private Label labelConnect;
    private Label labelTeach;


    @Override
    public void sceneBegin() {
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene2());
//        img=
        //按鈕
        singleButton = new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 50, 360, 70);
        createRoomButton = new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 130, 360, 70);
        connectButton = new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 210, 360, 70);
        teachButton = new Button(Global.SCREEN_X / 3 - 50, Global.SCREEN_Y / 4 + 290, 360, 70);

        //文字
        gameName=new Label(Global.SCREEN_X / 3 -70,Global.SCREEN_Y / 4 ,"Survival",100);
        labelSingle=new Label(singleButton.collider().left()+15,singleButton.collider().top()+50,"  SINGLE GAME ",40);
        labelCreateRoom=new Label(createRoomButton.collider().left()+15,createRoomButton.collider().top()+50," CREATE ROOM ",40);
        labelConnect=new Label(connectButton.collider().left()+15,connectButton.collider().top()+50,"CONNECT ROOM",40);
        labelTeach=new Label(teachButton.collider().left()+15,teachButton.collider().top()+50,"   TEACH  GAME ",40);

        mouse=new Mouse(0,0,50,50);
    }

    @Override
    public void sceneEnd() {

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        singleButton.paint(g);
        createRoomButton.paint(g);
        connectButton.paint(g);
        teachButton.paint(g);

        gameName.paint(g);
        labelSingle.paint(g);
        labelCreateRoom.paint(g);
        labelConnect.paint(g);
        labelTeach.paint(g);

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
//        if (state == CommandSolver.MouseState.MOVED) {
//            System.out.println(e.getX());
//        }
        mouse.mouseTrig(e,state,trigTime);

    }
}
