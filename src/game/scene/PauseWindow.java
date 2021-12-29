package game.scene;

import game.Menu.*;
import game.Menu.Button;
import game.Menu.Label;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.utils.CommandSolver;
import game.utils.GameKernel;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class PauseWindow implements GameKernel.GameInterface, CommandSolver.MouseCommandListener {
    private boolean isPause;
    private ArrayList<Button> buttons;
    private ArrayList<Label> labels;
    private Image pauseImg;
    private String soundsPath;
    private GameOver scene;
    private ArrayList<Button> buttonTouch;

    public PauseWindow(String soundsPath, GameOver currentScene) {
        this.soundsPath = soundsPath;
        scene = currentScene;
        pauseImg = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Button().button());
        labels = new ArrayList<Label>();
        buttons = new ArrayList<Button>();
        buttonTouch=new ArrayList<>();
        //文字
        labels = new ArrayList<game.Menu.Label>();
        labels.add(new game.Menu.Label(Global.SCREEN_X / 3 + 30, Global.SCREEN_Y / 4 - 30, "Pause", FontLoader.Blocks(100)));
        labels.add(new game.Menu.Label(Global.SCREEN_X / 3 + 80, labels.get(0).painter().bottom() + 200, "   CONTINUE ", FontLoader.cuteChinese(36), Color.white));
        labels.add(new game.Menu.Label(Global.SCREEN_X / 3 + 80, labels.get(1).painter().bottom() + 100, "     EXIT ", FontLoader.cuteChinese(36), Color.white));


        //按鈕
        buttons = new ArrayList<Button>();
        buttons.add(new Button(labels.get(1).painter().left(), labels.get(1).painter().top() - 40, 360, 40, new Animation(AllImages.pauseLabel)));
        buttons.add(new Button(labels.get(2).painter().left(), labels.get(2).painter().top() - 40, 360, 40, new Animation(AllImages.pauseLabel)));

        //觸碰按鈕後
        buttonTouch.add(new Button(buttons.get(0).collider().left()-40,buttons.get(0).collider().top(),buttons.get(0).collider().width(),buttons.get(0).collider().height()+10,new Animation(AllImages.inputButton)));
        buttonTouch.add(new Button(buttons.get(1).collider().left()-40,buttons.get(1).collider().top(),buttons.get(1).collider().width(),buttons.get(1).collider().height()+10,new Animation(AllImages.inputButton)));
    }

    @Override
    public void paint(Graphics g) {
        if (isPause) {
            g.drawImage(pauseImg, Global.SCREEN_X / 3 - 35, Global.SCREEN_Y / 4 , 500, 400, null);
//            buttons.forEach(button -> button.paint(g));
            if(Global.mouse.isCollision(buttons.get(0))){
                buttonTouch.get(0).paint(g);
            }
            if(Global.mouse.isCollision(buttons.get(1))){
                buttonTouch.get(1).paint(g);
            }
            labels.forEach(label -> label.paint(g));
        }
    }

    @Override
    public void update() {

    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.PRESSED && isPause) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                setPause(false);
                AudioResourceController.getInstance().play(soundsPath);
            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                AudioResourceController.getInstance().stop(soundsPath);
                scene.gameOver();
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }
}
