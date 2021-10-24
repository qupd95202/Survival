package game.Menu;

import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.Animation;
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


    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().play(new Path().sound().background().countScene());
        labels = new ArrayList<>();
        animations = new ArrayList<>();
        title = new Label(Global.SCREEN_X / 3 - 80, Global.SCREEN_Y / 4 - 20, "S C O R E", FontLoader.Blocks(100));
        setLabels();
        button = new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.cross));

    }

    @Override
    public void sceneEnd() {
        players = null;
        labels = null;
        animations = null;
        title = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().countScene());
        SceneController.getInstance().change(new MenuScene());
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(169, 209, 142));
        g.fillRect(0, 0, Global.SCREEN_X, Global.SCREEN_Y);
        g.setFont(FontLoader.Blocks(18));
        g.drawString("hahaha", 100, 100);
        title.paint(g);
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 3 + i * 100 - 30, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, g);
        }
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
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
            animations.add(players.get(i).getCurrentAnimation());
        }
        for (int i = 0; i < players.size(); i++) {
            labels.add(new Label(Global.SCREEN_X / 3 + 80, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getName()), FontLoader.Future(20)));
            labels.add(new Label(Global.SCREEN_X / 3 + 80 + 200, Global.SCREEN_Y / 3 + i * 100, String.valueOf(players.get(i).getPoint()), FontLoader.Future(20)));
        }
    }


    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.MOVED) {
            Global.mouse.mouseTrig(e, state, trigTime);
        }
        if (state == CommandSolver.MouseState.CLICKED) {
            if (Global.mouse.isCollision(button)) {
                sceneEnd();
            }

        }
    }

}
