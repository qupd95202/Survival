package game.scene;

import game.Menu.Label;
import game.Menu.MenuScene;
import game.Menu.Mouse;
import game.controllers.SceneController;
import game.core.GameTime;
import game.core.Global;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.TransformObstacle;
import game.gameObj.players.Player;
import game.gameObj.players.ComputerPlayer;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.map.GameMap;
import game.map.ObjectArr;
import game.scene_process.Camera;
import game.scene_process.SmallMap;
import game.utils.CommandSolver;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 獵人吃到道具不會加速，道具多點，道具功能增加
 */
public class SingleSurvivalGameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    //留意畫的順序
    private Player mainPlayer;
    private ArrayList<ComputerPlayer> computerPlayers;
    private ArrayList<TransformObstacle> transformObstacles = ObjectArr.transformObstaclList1;
    private ArrayList<MapObject> unPassMapObjects;
    private ArrayList<Props> propsArrayList = ObjectArr.propsArrSurvivalGame;
    private Camera camera;
    private SmallMap smallMap;
    private GameMap gameMap;

    private Image imgForest;
    private Image imgWinter;
    private Image imgVolcano;
    private Image imgVillage;

    //道具生成與消失
    private Delay propsReProduce;
    private Delay propsRemove;
    private Delay propAnimationDelay;//計時吃到道具動畫播放時間

    //時間計算
    private long startTime;
    private long gameTime;
    private long chooseTime; //選擇的遊戲時間
    private long lastTime;
    private GameTime printGameTime;
    private Image imgClock;


    //左下角的方格
    Animation runner;
    Animation changeBody;

    //滑鼠
    private Mouse mouse;

    //提示訊息(畫面上所有的文字處理)
    private ArrayList<Label> labels;
    private Label transFormCDLabel;


    @Override
    public void sceneBegin() {
        //遊戲時間
        startTime = System.nanoTime();
        chooseTime = 300; //單位：秒
        //初始ArrayList
        gameObjectList = new ArrayList<>();
        computerPlayers = new ArrayList<>();
        labels = new ArrayList<Label>();

        //道具相關
        propsReProduce = new Delay(150);
        propsRemove = new Delay(300);
        propsRemove.play();
        propsRemove.loop();
        propsReProduce.play();
        propsReProduce.loop();
        propAnimationDelay = new Delay(19);

        //主角
        mainPlayer = new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.blue, Player.RoleState.PREY);
        //新增電腦玩家

        computerPlayers.add(new ComputerPlayer(100, 100, AllImages.beige, Player.RoleState.HUNTER));
        computerPlayers.add(new ComputerPlayer(900, 3200, AllImages.beige, Player.RoleState.HUNTER));
        computerPlayers.add(new ComputerPlayer(2300, 3000, AllImages.beige, Player.RoleState.HUNTER));
        computerPlayers.add(new ComputerPlayer(100, 2300, AllImages.beige, Player.RoleState.HUNTER));
        computerPlayers.add(new ComputerPlayer(1599, 500, AllImages.beige, Player.RoleState.HUNTER));
        computerPlayers.add(new ComputerPlayer(100, 2500, AllImages.beige, Player.RoleState.HUNTER));

        //預設難度一
        for (ComputerPlayer computerPlayer : computerPlayers) {
            computerPlayer.AILevel1();
        }
        //畫面上相關
        runner = new Animation(AllImages.runnerDark);
        changeBody = new Animation(AllImages.changeBody);

        transFormCDLabel = new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.transformCDTime()), 20);
        labels.add(new Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85, "F", 20));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85, "R", 20));
        labels.add(transFormCDLabel);

        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        computerPlayers.forEach(player -> gameObjectList.addAll(List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(List.of(transformObstacle)));

        //地圖與鏡頭相關
        gameMap = new GameMap(Global.MAP_WIDTH, Global.MAP_HEIGHT);
        unPassMapObjects = gameMap.getMapObjects();
        unPassMapObjects.forEach(mapObject -> gameObjectList.addAll(List.of(mapObject)));
        camera = new Camera(gameMap.getWidth() + 5, gameMap.getHeight() + 5);
        camera.setTarget(mainPlayer);
        smallMap = new SmallMap(0, 0, Global.MAP_WIDTH, Global.MAP_HEIGHT, 0.05, 0.05);

        //背景地圖
        imgForest = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().forest());
        imgWinter = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().winter());
        imgVolcano = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().volcano());
        imgVillage = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().village());

        //滑鼠
        mouse = new Mouse(0, 0, 50, 50);

        printGameTime = new GameTime();
        imgClock = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().clock());

    }


    @Override
    public void sceneEnd() {
        SceneController.getInstance().change(new MenuScene());
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        gameTime = (System.nanoTime() - startTime) / 1000000000;
        lastTime = chooseTime - gameTime;
        camera.startCamera(g);
        mapPaint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        mainPlayer.paint(g);
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        propsPaint(g);

        //跟著鏡頭的在這之後paint
        camera.paint(g);
        camera.endCamera(g);

        //顯示遊戲時間
        paintTime(g);
        //顯示技能
        skillPaint(g);
        //畫滑鼠
        mouse.paint(g);

//        //碰撞道具時播放動畫
//        for (int i = 0; i < propsArrayList.size(); i++) {
//            Props prop = propsArrayList.get(i);
//            if (prop.getPropsAnimation().isPlayPropsAnimation()) {
//                System.out.println("吃到");
//                prop.getPropsAnimation().paint(g);
//                propsArrayList.remove(i--);
//            }
//        }


        //要畫在小地圖的要加在下方
        smallMap.start(g);
        gameMap.paint(g);
        smallMap.paint(g, mainPlayer, Color.red, 100, 100);//小地圖的需要另外再paint一次
        if (Global.IS_DEBUG || mainPlayer.isHunterWatcher) {
            for (int i = 1; i < computerPlayers.size(); i++) {
                smallMap.paint(g, computerPlayers.get(i), Color.YELLOW, 100, 100);
            }
        }
        camera.paint(g);

    }

    @Override
    public void update() {
        //道具生成與更新
        propsGenUpdate();
        allPropsUpdate();
        propsEffectUpdate();
        //難度更新
        levelUpdate();
        //為了解決player與npc重疊時 畫面物件顯示先後順序問題
        sortObjectByPosition();
        //無法穿越部分物件
        keepNotPass(unPassMapObjects);
        //用forEach將ArrayList中每個gameObject去update()
        mainPlayer.update();
        gameObjectList.forEach(gameObject -> gameObject.update());
        cPlayerCheckOthersUpdate();
        cPlayerCheckPropsUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        camera.update();
        //cd時間顯示之資料
        transFormCDLabel.setWords(String.valueOf(mainPlayer.transformCDTime()));
    }

    @Override
    public CommandSolver.MouseCommandListener mouseListener() {
        return this;
    }


    @Override
    public CommandSolver.KeyListener keyListener() {
        return this;
    }

    /**
     * 為了解決player與npc重疊時 畫面物件顯示先後順序問題 所以加物件存進ArrayList後進行排序
     */
    private void sortObjectByPosition() {
        gameObjectList.sort(Comparator.comparing(gameObject -> gameObject.painter().bottom()));
    }

    /**
     * 電腦玩家偵查玩家
     */
    public void cPlayerCheckOthersUpdate() {
        for (ComputerPlayer computerPlayer : computerPlayers) {
            computerPlayer.whoIsNearInSurvivalGame(mainPlayer);
        }
    }

    /**
     * 電腦玩家偵查道具
     */
    public void cPlayerCheckPropsUpdate() {
        for (int i = 1; i < computerPlayers.size(); i++) {
            ComputerPlayer computerPlayer = computerPlayers.get(i);
            for (int j = 0; j < propsArrayList.size(); j++) {
                computerPlayer.whichPropIsNear(propsArrayList.get(j));
            }
        }
    }

    /**
     * 遊戲結束
     */
    public void playerCollisionCheckUpdate() {
        computerPlayers.forEach(player -> {
            if (player.isCollision(mainPlayer)) {
                if (!mainPlayer.isSuperStar) {
                    sceneEnd();
                }
            }
        });
    }

    private void paintTime(Graphics g) {
        g.drawImage(imgClock,
                Global.SCREEN_X - 150,
                -5,
                60,
                60,
                null);
        g.drawImage(printGameTime.imgHundreds(lastTime),
                Global.SCREEN_X - 100,
                10,
                30,
                30,
                null);
        g.drawImage(printGameTime.imgTens(lastTime),
                Global.SCREEN_X - 80,
                10,
                30,
                30,
                null);
        g.drawImage(printGameTime.imgDigits(lastTime),
                Global.SCREEN_X - 60,
                10,
                30,
                30,
                null);
    }

    public void mapPaint(Graphics g) {
        g.drawImage(imgForest, 0, 0, 1920, 1920, null);
        g.drawImage(imgVolcano, 1920, 0, 1920, 1920, null);
        g.drawImage(imgWinter, 0, 1920, 1920, 1920, null);
        g.drawImage(imgVillage, 1920, 1920, 1920, 1920, null);
        gameMap.paint(g);
    }

    public void skillPaint(Graphics g) {
        if (mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runner.setImg(AllImages.runnerNormal);
        } else if (mainPlayer.isCanUseTeleportation() && mainPlayer.isUseTeleportation()) {
            runner.setImg(AllImages.runnerLight);
        } else {
            runner.setImg(AllImages.runnerDark);
        }
        runner.paint(0, Global.SCREEN_Y - 100, 100, 100, g);
        changeBody.paint(105, Global.SCREEN_Y - 100, 100, 100, g);
        if (mainPlayer.getStoredTransformAnimation() != null) {
            mainPlayer.getStoredTransformAnimation().paint(125, Global.SCREEN_Y - 80, 60, 60, g);
        }
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }
    }

    /**
     * 讓角色無法穿過該物件
     */
    public void keepNotPass(ArrayList<? extends GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            mainPlayer.isCollisionForMovement(gameObject);
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.isCollisionForMovement(gameObject);
            }
        }
    }

    /**
     * 道具的update
     */
    public void allPropsUpdate() {
        for (Props props : propsArrayList) {
            props.update();
        }
    }

    /**
     * 碰撞道具的update
     */
    public void propsCollisionCheckUpdate() {
        for (int i = 0; i < propsArrayList.size(); i++) {
            Props props = propsArrayList.get(i);
            if (mainPlayer.isCollision(props)) {
                mainPlayer.collidePropsInSurvivalMode(props);

                props.getPropsAnimation().setPlayPropsAnimation(true);

                props.setGotByPlayer(true);
                propsArrayList.remove(i--);
                continue;
            }
            for (ComputerPlayer computerPlayer : computerPlayers) {
                if (computerPlayer.isCollision(props)) {
                    computerPlayer.collidePropsInSurvivalMode(props);
                    props.setGotByPlayer(true);
                    propsArrayList.remove(i--);
                    break;
                }
            }
        }
    }

    /**
     * 道具產生更新
     */
    public void propsGenUpdate() {
        if (propsRemove.count()) {
            if (propsArrayList.size() > 0) {
                propsArrayList.remove(0);
            }
        }
        if (propsReProduce.count()) {
            if (propsArrayList.size() >= Global.PROPS_AMOUNT_MAX_SURVIVAL_GAME) {
                return;
            }
            propsArrayList.add(new Props(1));
        }
    }

    /**
     * 道具畫面更新
     *
     * @param g
     */
    public void propsPaint(Graphics g) {
        for (Props props : propsArrayList) {
            props.paint(g);
        }
    }

    public void levelUpdate() {
        if ((int) lastTime == 150) {
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.AILevel2();
            }
        }
        if ((int) lastTime == 50) {
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.AILevel3();
            }
        }
    }

    public void propsEffectUpdate() {
        if (mainPlayer.isThunder) {
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.decreaseSpeed();
            }

            mainPlayer.isThunder = false;
        }
        if (mainPlayer.isHunterStop) {
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.propsStopTimeDelay.play();
                computerPlayer.setCanMove(false);
            }
            mainPlayer.isHunterStop = false;
        }

        if (mainPlayer.isDecreaseGameTime) {
            chooseTime -= 20;
            mainPlayer.isDecreaseGameTime = false;
        }
    }


    @Override
    public void keyPressed(int commandCode, long trigTime) {
        mainPlayer.keyPressed(commandCode, trigTime);
    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {
        mainPlayer.keyReleased(commandCode, trigTime);
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (state == CommandSolver.MouseState.CLICKED) {
            int mouseX = e.getX() + camera.painter().left();
            int mouseY = e.getY() + camera.painter().top();
            for (TransformObstacle transformObstacle : transformObstacles) {
                if (transformObstacle.isXYin(mouseX, mouseY)) {
                    mainPlayer.chooseTransformObject(transformObstacle);
                }
            }
            for (MapObject mapObject : unPassMapObjects) {
                if (mapObject.isXYin(mouseX, mouseY)) {
                    return;
                }
            }
            mainPlayer.useTeleportation(mouseX, mouseY);
        }
        mouse.mouseTrig(e, state, trigTime);
    }
}
