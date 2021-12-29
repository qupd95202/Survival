package game.scene;

import game.Menu.*;
import game.Menu.Label;
import game.controllers.AudioResourceController;
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
import game.graphic.PropsAnimation;
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
import java.util.HashMap;
import java.util.List;


public class SingleSurvivalGameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener, GameOver {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    //留意畫的順序
    private Player mainPlayer;
    private ArrayList<ComputerPlayer> computerPlayers;
    private ArrayList<TransformObstacle> transformObstacles;
    private ArrayList<MapObject> unPassMapObjects;
    private ArrayList<Props> propsArrayList;
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
    private Delay timeDelay = new Delay(60);
    private int gameTime;
    private int chooseTime; //選擇的遊戲時間
    private int lastTime;
    private GameTime printGameTime;
    private Image imgClock;


    //左下角的方格
    Animation runner;
    Animation changeBody;
    Animation background321;


    //提示訊息(畫面上所有的文字處理)
    private ArrayList<Label> labels;
    private Label transFormCDLabel;

    //道具吃到的動畫
    private ObjectArr objectArr;
    private HashMap<Props.Type, PropsAnimation> allPropsAnimation;
    private Props mainPlayerCollisionProps;

    //暫停
    private PauseWindow pauseWindow;
    private boolean isWin;

    //音樂
    private String currentSound;

    //321動畫
    private Label label321;

    //遊戲結束前提醒顯示
    private game.core.Point point;

    public SingleSurvivalGameScene() {
        currentSound = new Path().sound().background().gamefirst2();
        pauseWindow = new PauseWindow(currentSound, this);
    }


    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().loop(currentSound, -1);
        //遊戲時間
        timeDelay.play();
        timeDelay.loop();
        chooseTime = 184; //單位：秒
        //初始ArrayList
        gameObjectList = new ArrayList<>();
        computerPlayers = new ArrayList<>();
        transformObstacles = ObjectArr.transformObstaclList1;
        propsArrayList = ObjectArr.propsArrSurvivalGame;
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
        mainPlayer = new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.blue, Player.RoleState.PREY, "Player 1");
        //新增電腦玩家

        computerPlayers.add(new ComputerPlayer(2500, 100, AllImages.beige, Player.RoleState.HUNTER, "1"));
        computerPlayers.add(new ComputerPlayer(900, 3200, AllImages.beige, Player.RoleState.HUNTER, "2"));
        computerPlayers.add(new ComputerPlayer(2300, 3000, AllImages.beige, Player.RoleState.HUNTER, "3"));
        computerPlayers.add(new ComputerPlayer(3000, 2300, AllImages.beige, Player.RoleState.HUNTER, "4"));
        computerPlayers.add(new ComputerPlayer(1599, 500, AllImages.beige, Player.RoleState.HUNTER, "5"));
        computerPlayers.add(new ComputerPlayer(100, 2500, AllImages.beige, Player.RoleState.HUNTER, "6"));

        //預設難度一
        for (ComputerPlayer computerPlayer : computerPlayers) {
            computerPlayer.AILevel1();
            computerPlayer.setMode(ComputerPlayer.Mode.SINGLE_SURVIVAL_GAME);
        }
        //畫面上相關
        runner = new Animation(AllImages.runnerDark);
        changeBody = new Animation(AllImages.changeBody);

        transFormCDLabel = new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.transformCDTime()), FontLoader.Future(20));
        labels.add(new Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85, "F", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85, "R", FontLoader.Future(20)));
        labels.add(transFormCDLabel);

        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        computerPlayers.forEach(player -> gameObjectList.addAll(List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(List.of(transformObstacle)));

        //地圖與鏡頭相關
        gameMap = new GameMap(Global.MAP_WIDTH, Global.MAP_HEIGHT, new Path().img().mapSurvival().bmp(), new Path().img().mapSurvival().txt());
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


        printGameTime = new GameTime();
        imgClock = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().clock());

        //吃到道具的動畫
        objectArr = new ObjectArr();
        allPropsAnimation = objectArr.genPropsAnimation();

        //321動畫
        background321 = new Animation(AllImages.inputButton);
        label321 = new Label(Global.SCREEN_X / 2 - 200, Global.SCREEN_Y / 2 + 40, "        3", FontLoader.Future(100));

    }


    @Override
    public void sceneEnd() {
        gameObjectList = null;
        this.gameMap = null;
        this.transformObstacles = null;
        Global.IS_NIGHTMARE = false;
        AudioResourceController.getInstance().stop(currentSound);
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
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
        //遊戲結束前提醒
        beforeTimeUp(g);
        //顯示技能
        skillPaint(g);
        //碰撞道具時播放動畫
        if (mainPlayerCollisionProps != null) {
            allPropsAnimation.get(mainPlayerCollisionProps.getPropsType()).paint(g);
        }
        pauseWindow.paint(g);
        Global.mouse.paint(g);
        if (!isCanMove()) {
            AudioResourceController.getInstance().play(new Path().sound().background().countdown());
            background321.paint(0, 0, Global.SCREEN_X, Global.SCREEN_Y, g);
            if (gameTime == 1) {
                label321.setWords("        2");
            }
            if (gameTime == 2) {
                label321.setWords("        1");
            }
            if (gameTime == 3) {
                label321.setWords("START");
            }
            label321.paint(g);
        }
        //要畫在小地圖的要加在下方
        smallMap.start(g);
        gameMap.paint(g);
        smallMap.paint(g, mainPlayer, Color.red, 100, 100);//小地圖的需要另外再paint一次
        if (Global.IS_DEBUG || mainPlayer.isHunterWatcher) {
            for (int i = 0; i < computerPlayers.size(); i++) {
                smallMap.paint(g, computerPlayers.get(i), Color.YELLOW, 100, 100);
            }
        }
        camera.paint(g);
    }

    @Override
    public void update() {
        if (timeDelay.count()) {
            gameTime++;
        }
        if (!isCanMove()) {
            return;
        }
        if (pauseWindow.isPause()) {
            return;
        }
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
//        cPlayerCheckPropsUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        camera.update();
        //cd時間顯示之資料
        transFormCDLabel.setWords(String.valueOf(mainPlayer.transformCDTime()));
        timeUP();
        //碰撞道具時播放動畫的更新
        if (mainPlayerCollisionProps != null) {
            allPropsAnimation.get(mainPlayerCollisionProps.getPropsType()).update();
        }
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

//    /**
//     * 電腦玩家偵查道具
//     */
//    public void cPlayerCheckPropsUpdate() {
//        for (int i = 0; i < computerPlayers.size(); i++) {
//            ComputerPlayer computerPlayer = computerPlayers.get(i);
//            for (int j = 0; j < propsArrayList.size(); j++) {
//                computerPlayer.whichPropIsNear(propsArrayList.get(j));
//            }
//        }
//    }

    /**
     * 遊戲結束
     */
    public void playerCollisionCheckUpdate() {
        computerPlayers.forEach(player -> {
            if (player.isCollision(mainPlayer)) {
                if (!mainPlayer.isSuperStar) {
                    gameOver();
                }
            }
        });
    }

    /**
     * 遊戲結束前提醒
     *
     * @param g 繪圖
     */
    private void beforeTimeUp(Graphics g) {
        if (gameTime > 174 && gameTime < 184) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，遊戲結束！！！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((184 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        }
    }

    private void paintTime(Graphics g) {
        if (isCanMove()) {
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
                mainPlayerCollisionProps = propsArrayList.get(i);
                allPropsAnimation.get(props.getPropsType()).setPlayPropsAnimation(true);//將此道具的動畫設為開啟
                mainPlayer.collidePropsInSurvivalMode(props);
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
     * 主角是獵人就顯示在畫面上
     */
    public void hunterPaint() {
        if (mainPlayer.roleState == Player.RoleState.HUNTER) {

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
        if (!Global.IS_NIGHTMARE) {
            if (timeDelay.count()) {
                if (lastTime == 140) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().manyhuterscencefirst();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel2();
                    }
                }
                if (lastTime == 100) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().manyhunterscenemedium();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel3();
                    }
                }
                if (lastTime == 50) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().gamebehindfinal();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel4();
                    }
                }
            }
        } else {
            if (timeDelay.count()) {
                if (lastTime == 140) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().manyhuterscencefirst();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    computerPlayers.add(new ComputerPlayer(2300, 3000, AllImages.beige, Player.RoleState.HUNTER, "7"));
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel3();
                    }
                }
                if (lastTime == 100) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().manyhunterscenemedium();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    computerPlayers.add(new ComputerPlayer(2300, 3000, AllImages.beige, Player.RoleState.HUNTER, "8"));
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel4();
                    }
                }
                if (lastTime == 50) {
                    AudioResourceController.getInstance().stop(currentSound);
                    currentSound = new Path().sound().background().gamebehindfinal();
                    AudioResourceController.getInstance().loop(currentSound, -1);
                    computerPlayers.add(new ComputerPlayer(2300, 3000, AllImages.beige, Player.RoleState.HUNTER, "9"));
                    for (ComputerPlayer computerPlayer : computerPlayers) {
                        computerPlayer.AILevel5();
                    }
                }
            }

        }
    }

    public void propsEffectUpdate() {
        if (mainPlayer.isThunder) {
            for (ComputerPlayer computerPlayer : computerPlayers) {
                computerPlayer.propsThunderDelay.play();
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
            chooseTime -= 10;
            mainPlayer.isDecreaseGameTime = false;
        }
    }

    /**
     * 時間到
     */
    public void timeUP() {
        if (timeDelay.count()) {
            if (chooseTime <= gameTime) {
                SceneController.getInstance().change(new WinScene());
            }
        }
    }

    public void gameOver() {
        SceneController.getInstance().change(new GameOverScene());
    }


    @Override
    public void keyPressed(int commandCode, long trigTime) {
        if (isCanMove()) {
            mainPlayer.keyPressed(commandCode, trigTime);
        }
    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (isCanMove()) {
            mainPlayer.keyReleased(commandCode, trigTime);
            if (commandCode == Global.KeyCommand.ESCAPE.getValue()) {
                AudioResourceController.getInstance().play(new Path().sound().background().pause());
                if (!pauseWindow.isPause()) {
                    pauseWindow.setPause(true);
                } else {
                    pauseWindow.setPause(false);
                }
            }
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (isCanMove()) {
            mainPlayer.mouseTrig(e, state, trigTime, unPassMapObjects, transformObstacles, camera, Global.mouse);
            pauseWindow.mouseTrig(e, state, trigTime);
        }
    }

    private boolean isCanMove() {
        return gameTime > 3;
    }
}
