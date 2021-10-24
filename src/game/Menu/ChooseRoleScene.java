package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;
import game.network.Client.ClientClass;
import game.scene.ConnectTool;
import game.scene.Scene;
import game.utils.CommandSolver;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ChooseRoleScene extends Scene implements CommandSolver.MouseCommandListener {
    //背景圖片
    private Image img;
    //選好後儲存
    private Animation currentAnimation;
    //判斷碰撞用的
    private ArrayList<Button> buttons;
    //角色間距(之後拉去global)
    private int inter;
    //文字(標題)
    private Label title;
    //文字(star)
    private Label create;
    //名字
    private String mainPlayerName;

    private String IP;

    private int port;


    public ChooseRoleScene(String IP, String name, int port) {
        mainPlayerName = name;
        this.IP = IP;
        this.port = port;
    }

    @Override
    public void sceneBegin() {
        //背景圖片
        img = SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Scene().scene9());
        //間距
        inter = 40;
        //初始為藍色
        currentAnimation = new Animation(AllImages.blue);
        //角色按鈕
        buttons = new ArrayList<>();
        buttons.add(new Button(Global.SCREEN_X / 4, Global.SCREEN_Y / 2 + 100, Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, new Animation(AllImages.yellow)));
        buttons.add(new Button(buttons.get(0).collider().right() + inter, buttons.get(0).collider().top(), Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, new Animation(AllImages.pink)));
        buttons.add(new Button(buttons.get(1).collider().right() + inter, buttons.get(0).collider().top(), Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, new Animation(AllImages.green)));
        buttons.add(new Button(buttons.get(2).collider().right() + inter, buttons.get(0).collider().top(), Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, new Animation(AllImages.blue)));
        buttons.add(new Button(buttons.get(1).collider().right() - Global.UNIT_WIDTH / 2, buttons.get(0).collider().top() - 200, Global.UNIT_WIDTH * 2, Global.UNIT_HEIGHT * 2, currentAnimation));
        //文字
        title = new Label(Global.SCREEN_X / 2 - 325, 150, "CHOOSE YOUR ROLE", FontLoader.Blocks(60));
        create = new Label(buttons.get(3).collider().right() + inter + 20, buttons.get(3).collider().bottom() + 40, "CREATE", FontLoader.Blocks(40));
        buttons.add(new Button(buttons.get(3).collider().right() + inter + 20, buttons.get(3).collider().bottom(), Global.UNIT_WIDTH * 2 + 30, Global.UNIT_HEIGHT, create));
    }

    @Override
    public void sceneEnd() {
        img = null;
        currentAnimation = null;
        title = null;
        create = null;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, Global.SCREEN_X, Global.SCREEN_Y, null);
        title.paint(g);
        //角色
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).paint(g);
        }

        Global.mouse.paint(g);
    }

    @Override
    public void update() {
        //角色
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
        if (state == CommandSolver.MouseState.MOVED) {
            Global.mouse.mouseTrig(e, state, trigTime);
        }
        if (state == CommandSolver.MouseState.CLICKED) {
            if (Global.mouse.isCollision(buttons.get(0))) {
                currentAnimation = buttons.get(0).getAnimation();
                buttons.get(4).setAnimation(currentAnimation);

            }
            if (Global.mouse.isCollision(buttons.get(1))) {
                currentAnimation = buttons.get(1).getAnimation();
                buttons.get(4).setAnimation(currentAnimation);
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                currentAnimation = buttons.get(2).getAnimation();
                buttons.get(4).setAnimation(currentAnimation);
            }
            if (Global.mouse.isCollision(buttons.get(3))) {
                currentAnimation = buttons.get(3).getAnimation();
                buttons.get(4).setAnimation(currentAnimation);
            }
            if (Global.mouse.isCollision(buttons.get(5))) {
                SceneController.getInstance().change(new WaitingScene(IP, mainPlayerName, getCurrentImgArrAndType(),port));
            }

        }
    }

    private ImgArrAndType getCurrentImgArrAndType() {
        return this.currentAnimation.getImg();
    }

    public static int imgArrAndTypeParse(ImgArrAndType imgArrAndType) {
        if (AllImages.yellow.equals(imgArrAndType)) {
            return 1;
        } else if (AllImages.pink.equals(imgArrAndType)) {
            return 2;
        } else if (AllImages.green.equals(imgArrAndType)) {
            return 3;
        } else {
            return 4;
        }
    }

    public static ImgArrAndType imgArrAndTypeParseInt(int number) {
        switch (number) {
            case 1:
                return AllImages.yellow;
            case 2:
                return AllImages.pink;
            case 3:
                return AllImages.green;
            default:
                return AllImages.blue;
        }
    }
}
