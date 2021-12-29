package game.Menu;

import game.Teach.TeachScene;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.scene.Scene;
import game.scene.SingleSurvivalGameScene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class SurvivalPropsRuleScene extends Scene implements CommandSolver.MouseCommandListener {
    private Image img;
    private ArrayList<Button> buttons;
    private ArrayList<Label> labels;
    private int inter;//間距和大小

    @Override
    public void sceneBegin() {
        //初始化
        buttons=new ArrayList<>();
        labels=new ArrayList<>();
        inter=Global.UNIT_HEIGHT*2;
        //主選單背景圖
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene8());
        labels.add(new Label(Global.SCREEN_X -200, 80,"SKIP",FontLoader.Rocket(50)));
        buttons.add(new Button(labels.get(0), 135, 40));
        //左邊的圖(會動的)
        buttons.add(new Button(Global.SCREEN_X/10,Global.SCREEN_Y/8,inter,inter,new Animation(AllImages.addSpeed,1)));
        buttons.add(new Button(buttons.get(1).collider().left(),buttons.get(1).collider().bottom(),inter,inter,new Animation(AllImages.gameTimeDecrease,10)));
        buttons.add(new Button(buttons.get(2).collider().left(),buttons.get(2).collider().bottom(),inter,inter,new Animation(AllImages.star,10)));
        buttons.add(new Button(buttons.get(3).collider().left(),buttons.get(3).collider().bottom(),inter,inter,new Animation(AllImages.hunterWatcher,10)));
        buttons.add(new Button(buttons.get(4).collider().left(),buttons.get(4).collider().bottom(),inter,inter,new Animation(AllImages.lightning,5)));
        //右邊的圖(不會動的)
        buttons.add(new Button(Global.SCREEN_X/2+20,Global.SCREEN_Y/8,inter,inter,new Animation(AllImages.addPoint,999)));
        buttons.add(new Button(buttons.get(6).collider().left(),buttons.get(6).collider().bottom(),inter,inter,new Animation(AllImages.timeStop,999)));
        buttons.add(new Button(buttons.get(7).collider().left(),buttons.get(7).collider().bottom(),inter,inter,new Animation(AllImages.teleportation,999)));
        buttons.add(new Button(buttons.get(8).collider().left(),buttons.get(8).collider().bottom(),inter,inter,new Animation(AllImages.trap,999)));

        int xNum=-20;
        int yNum=80;

        Font font=FontLoader.Mini_Square(35);
        //label(道具文字)
        labels.add(new Label(buttons.get(1).collider().right()+inter/3+xNum,buttons.get(1).collider().top()+yNum,"移動速度+1(永久)", FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(2).collider().right()+inter/3+xNum,buttons.get(2).collider().top()+yNum,"剩餘時間減少20秒", FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(3).collider().right()+inter/3+xNum,buttons.get(3).collider().top()+yNum,"無敵且極速維持10秒",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(4).collider().right()+inter/3+xNum,buttons.get(4).collider().top()+yNum,"小地圖可以看到所有獵人位置",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(5).collider().right()+inter/3+xNum,buttons.get(5).collider().top()+yNum,"所有獵人移動速度-1",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(6).collider().right()+inter/3+xNum,buttons.get(6).collider().top()+yNum,"分數+10",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(7).collider().right()+inter/3+xNum,buttons.get(7).collider().top()+yNum,"獵人暫時無法移動3秒",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(8).collider().right()+inter/3+xNum,buttons.get(8).collider().top()+yNum,"瞬間移動",FontLoader.cuteChinese(25)));
        labels.add(new Label(buttons.get(9).collider().right()+inter/3+xNum,buttons.get(9).collider().top()+yNum,"無法移動2秒",FontLoader.cuteChinese(25)));

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
        for (int i = 1; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }
        Global.mouse.paint(g);
    }

    @Override
    public void update() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).update();
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
        if (state == CommandSolver.MouseState.PRESSED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                SceneController.getInstance().change(new SingleSurvivalGameScene());
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }
}
