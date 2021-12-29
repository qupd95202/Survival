package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.network.Client.ClientClass;
import game.network.Server.Server;
import game.scene.ConnectTool;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CountPointScene extends Scene implements CommandSolver.MouseCommandListener {
    private ArrayList<Player> players;
    private ArrayList<Label> labels;
    private ArrayList<Animation> animations;
    private Button button;
    private Label title;
    private Button buttonTouch;


    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().play(new Path().sound().background().countScene());
        labels = new ArrayList<>();
        animations = new ArrayList<>();
        title = new Label(Global.SCREEN_X / 3 - 80, Global.SCREEN_Y / 4 - 20, "S C O R E", FontLoader.Blocks(100));
        setLabels();
        button = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.cross));
        buttonTouch = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.inputButton));

    }

    @Override
    public void sceneEnd() {
        players = null;
        labels = null;
        animations = null;
        title = null;
        ConnectTool.instance().disconnect();
        Server.instance().close();
        ConnectTool.reset();
        AudioResourceController.getInstance().stop(new Path().sound().background().countScene());
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(169, 209, 142));
        g.fillRect(0, 0, Global.SCREEN_X, Global.SCREEN_Y);
        g.setFont(FontLoader.Blocks(18));
        title.paint(g);
        if (players.size() <= 4) {
            for (int i = 0; i < players.size(); i++) {
                animations.get(i).paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 3 + i * 100 - 30, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);
            }
        } else {
            for (int i = 0; i < players.size(); i++) {
                if (i <= 3) {
                    animations.get(i).paint(Global.SCREEN_X / 5 - 100, Global.SCREEN_Y / 3 + i * 100 - 30, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);
                } else {
                    int interX = 400;
                    animations.get(i).paint(Global.SCREEN_X / 5 - 100 + interX, Global.SCREEN_Y / 3 + (i - 4) * 100 - 30, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);

                }
            }
        }

        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }

        if (Global.mouse.isCollision(buttonTouch)) {
            buttonTouch.paint(g);
        }
        button.paint(g);
        Global.mouse.paint(g);
    }

    @Override
    public void update() {
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

    public void setPlayerPoint(ArrayList<Player> players) {
        this.players = players;
    }

    public void sort() {
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = 0; j < players.size() - i; j++) {
                if (players.get(i).getPoint() < players.get(i + 1).getPoint()) {
                    Player tmp = players.get(i);
                    players.set(i, players.get(i + 1));
                    players.set(i + 1, tmp);
                }
            }
        }
    }

    public void setLabels() {
        sort();
        for (int i = 0; i < players.size(); i++) {
            animations.add(players.get(i).getOriginalAnimation());
        }
        if (players.size() <= 4) {
            for (int i = 0; i < players.size(); i++) {
                labels.add(new Label(Global.SCREEN_X / 3 + 80, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getName()), FontLoader.Future(20)));
                labels.add(new Label(Global.SCREEN_X / 3 + 80 + 200, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getPoint()), FontLoader.Future(20)));
            }
        } else {
            for (int i = 0; i < players.size(); i++) {
                if (i <= 3) {
                    labels.add(new Label(Global.SCREEN_X / 5, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getName()), FontLoader.Future(20)));
                    labels.add(new Label(Global.SCREEN_X / 5 + 200, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getPoint()), FontLoader.Future(20)));
                } else {
                    int interX = 400;
                    labels.add(new Label(Global.SCREEN_X / 5 + interX, Global.SCREEN_Y / 3 + (i - 4) * 100, String.valueOf(players.get(i).getName()), FontLoader.Future(20)));
                    labels.add(new Label(Global.SCREEN_X / 5 + 200 + interX, Global.SCREEN_Y / 3 + (i - 4) * 100, String.valueOf(players.get(i).getPoint()), FontLoader.Future(20)));
                }
            }
        }

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
