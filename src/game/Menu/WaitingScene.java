package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.ImgArrAndType;
import game.network.Client.ClientClass;
import game.scene.ConnectPointGameScene;
import game.scene.ConnectTool;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import static game.gameObj.Pact.*;

public class WaitingScene extends Scene implements CommandSolver.MouseCommandListener {
    private Image img;
    private Button buttons;
    //文字(標題)
    //文字(star)
    private Label start;
    private String IP;
    private String name;
    private ImgArrAndType imgArrAndType;
    private int port;

    public WaitingScene(String IP, String name, ImgArrAndType imgArrAndType, int port) {
        this.name = name;
        this.imgArrAndType = imgArrAndType;
        ConnectTool.instance().setMainPlayer(new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, this.imgArrAndType, Player.RoleState.PREY, this.name));
        this.IP = IP;
        this.port = port;
    }


    @Override
    public void sceneBegin() {
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene9());
        start = new Label(Global.SCREEN_X / 4, Global.SCREEN_Y / 2 + 100, "START", FontLoader.Blocks(40));
        buttons = new Button(start.collider().right() + 40 + 20, start.collider().bottom(), Global.UNIT_WIDTH * 2 + 30, Global.UNIT_HEIGHT, start);
        try {
            ConnectTool.instance().connect(IP, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sceneEnd() {
        img = null;
        start = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().lovelyflower());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        buttons.paint(g);
        Global.mouse.paint(g);
        g.setColor(Color.RED);
        g.drawString("當前人數:" + (ConnectTool.instance().getMainPlayers().size()), 50, 50);
        g.setColor(Color.BLACK);
    }

    @Override
    public void update() {
        ClientClass.getInstance().sent(CONNECT, bale(Integer.toString(ChooseRoleScene.imgArrAndTypeParse(imgArrAndType)), name));
        buttons.update();
        ConnectTool.instance().consume();
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
            if (Global.mouse.isCollision(buttons)) {
                if (ClientClass.getInstance().getID() == 100) {
                    sceneEnd();
                    ClientClass.getInstance().sent(START_GAME, bale());
                }
            }
        }
    }
}
