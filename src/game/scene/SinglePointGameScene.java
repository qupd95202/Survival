package game.scene;

import game.Menu.*;
import game.Menu.Label;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.GameTime;
import game.core.Global;
import game.core.Point;
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


public class SinglePointGameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener, GameOver {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來

    private Player mainPlayer;
    private ArrayList<Player> players;
    private final ArrayList<TransformObstacle> transformObstacles = ObjectArr.transformObstaclList1;
    private ArrayList<MapObject> unPassMapObjects;
    private final ArrayList<Props> propsArrayList = ObjectArr.propsArr;
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
    Animation imgWarning;
    Animation no;//當玩家為獵人時變身格會放
    Animation background321;


    //提示訊息(畫面上所有的文字處理)
    private ArrayList<Label> labels;
    private Label transFormCDLabel;

    //積分動畫顯示
    private Point point;
    private Image imgPoint;

    //道具吃到的動畫
    private ObjectArr objectArr;
    private HashMap<Props.Type, PropsAnimation> allPropsAnimation;
    private Props mainPlayerCollisionProps;

    //暫停
    private PauseWindow pauseWindow;

    //音樂
    private String currentSound;

    //321動畫
    private Label label321;

    //獵人爆走cd
    private Label hunterCDLabel;


    public SinglePointGameScene() {
        currentSound = new Path().sound().background().mainscene();
        pauseWindow = new PauseWindow(currentSound, this);
    }


    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().loop(currentSound, -1);
        //遊戲時間
        timeDelay.play();
        timeDelay.loop();
        chooseTime = 154; //單位：秒
        //初始ArrayList
        gameObjectList = new ArrayList<>();
        players = new ArrayList<>();
        labels = new ArrayList<>();


        //道具相關
        propsReProduce = new Delay(900);
        propsRemove = new Delay(1800);
        propsRemove.play();
        propsRemove.loop();
        propsReProduce.play();
        propsReProduce.loop();

        //主角
        mainPlayer = new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.beige, Player.RoleState.HUNTER, "Player 1");
        //新增玩家
        players.add(mainPlayer);
        //電腦
        players.add(new ComputerPlayer(100, 100, AllImages.yellow, Player.RoleState.PREY, "1"));
        players.add(new ComputerPlayer(2634, 2718, AllImages.blue, Player.RoleState.PREY, "2"));
        players.add(new ComputerPlayer(100, 3000, AllImages.pink, Player.RoleState.PREY, "3"));
        players.add(new ComputerPlayer(1680, 2850, AllImages.yellow, Player.RoleState.PREY, "4"));
        players.add(new ComputerPlayer(1700, 300, AllImages.blue, Player.RoleState.PREY, "5"));
        players.add(new ComputerPlayer(980, 2500, AllImages.green, Player.RoleState.PREY, "6"));
        players.add(new ComputerPlayer(3000, 3000, AllImages.green, Player.RoleState.PREY, "7"));

        //畫面上相關
        runner = new Animation(AllImages.runnerDark);
        changeBody = new Animation(AllImages.changeBody);
        imgWarning = new Animation(AllImages.WARNING);
        no = new Animation(AllImages.no);

        //獵物cd資訊
        transFormCDLabel = new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.transformCDTime()), FontLoader.Future(20));
        labels.add(new Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85, "F", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85, "R", FontLoader.Future(20)));
        labels.add(transFormCDLabel);

        //獵人cd資訊
        hunterCDLabel = new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.getOutrageCd()), FontLoader.Future(20));


        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        players.forEach(player -> gameObjectList.addAll(List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(List.of(transformObstacle)));

        //地圖與鏡頭相關
        gameMap = new GameMap(Global.MAP_WIDTH, Global.MAP_HEIGHT, new Path().img().map().bmp(), new Path().img().map().txt());
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


        point = new Point();
        imgPoint = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().coin());

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
        AudioResourceController.getInstance().stop(currentSound);
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序


        lastTime = chooseTime - gameTime;
        camera.startCamera(g);
        mapPaint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        propsPaint(g);

        //跟著鏡頭的在這之後paint
        camera.paint(g);
        camera.endCamera(g);

        //顯示遊戲時間
        paintTime(g);
        //區域關閉前提醒
        beforeClosdTip(g);
        //顯示警告
        paintWarning(g);
        //顯示積分
        paintPoint(g);
        //顯示技能
        skillPaint(g);
        //碰撞道具時播放動畫
        if (mainPlayerCollisionProps != null) {
            allPropsAnimation.get(mainPlayerCollisionProps.getPropsType()).paint(g);
        }
        pauseWindow.paint(g);
        //畫滑鼠
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
        if (Global.IS_DEBUG || mainPlayer.isInOutrage) {
            for (int i = 1; i < players.size(); i++) {
                smallMap.paint(g, players.get(i), Color.YELLOW, 100, 100);
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
        //區域封閉
        mapAreaClosing();
        //道具生成與更新
        propsGenUpdate();
        allPropsUpdate();
        //為了解決player與npc重疊時 畫面物件顯示先後順序問題
        sortObjectByPosition();
        //狂暴化效果更新
        outrageEffect();
        //無法穿越部分物件
        keepNotPass(unPassMapObjects);
        //用forEach將ArrayList中每個gameObject去update()
        gameObjectList.forEach(gameObject -> gameObject.update());
        cPlayerCheckOthersUpdate();
        cPlayerCheckPropsUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        imgWarning.update();
        camera.update();
        //cd時間顯示之資料
        transFormCDLabel.setWords(String.valueOf(mainPlayer.transformCDTime()));
        hunterCDLabel.setWords(String.valueOf(mainPlayer.getOutrageCd()));
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

    public void cPlayerCheckOthersUpdate() {
        for (int i = 1; i < players.size(); i++) {
            ComputerPlayer computerPlayer = (ComputerPlayer) players.get(i);
            for (int j = 0; j < players.size(); j++) {
                Player player = players.get(j);
                if (computerPlayer != player) {
                    computerPlayer.whoIsNear(player);
                }
            }
        }
    }

    public void cPlayerCheckPropsUpdate() {
        for (int i = 1; i < players.size(); i++) {
            ComputerPlayer computerPlayer = (ComputerPlayer) players.get(i);
            for (int j = 0; j < propsArrayList.size(); j++) {
                computerPlayer.whichPropIsNear(propsArrayList.get(j));
            }
        }
    }

    public void playerCollisionCheckUpdate() {
        players.forEach(player -> {
            players.forEach(player1 -> {
                if (player != player1) {
                    player.exchangeRole(player1);
                }
            });
        });
    }

    //積分顯示動畫
    public void paintPoint(Graphics g) {
        g.drawImage(imgPoint,
                520,
                5,
                40,
                40,
                null);
        g.drawImage(point.imgHundreds(mainPlayer.getPoint()),
                560,
                10,
                20,
                30,
                null);
        g.drawImage(point.imgTens(mainPlayer.getPoint()),
                580,
                10,
                20,
                30,
                null);
        g.drawImage(point.imgDigits(mainPlayer.getPoint()),
                600,
                10,
                20,
                30,
                null);


    }

    //顯示剩餘時間動畫
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
        for (int i = 0; i < labels.size() - 1; i++) {
            labels.get(i).paint(g);
        }

        //獵人要印
        if (mainPlayer.roleState == Player.RoleState.HUNTER) {
            no.paint(120, Global.SCREEN_Y - 85, 70, 70, g);
            hunterCDLabel.paint(g);
        } else {
            if (mainPlayer.getStoredTransformAnimation() != null) {
                mainPlayer.getStoredTransformAnimation().paint(125, Global.SCREEN_Y - 80, 60, 60, g);
            }
            //prey要印的
            labels.get(2).paint(g);
        }
    }

    /**
     * 讓角色無法穿過該物件
     */
    public void keepNotPass(ArrayList<? extends GameObject> gameObjects) {
        for (Player player : players) {
            for (GameObject gameObject : gameObjects) {
                player.isCollisionForMovement(gameObject);
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
        //道具更新
        for (Player player : players) {
            for (int i = 0; i < propsArrayList.size(); i++) {
                Props props = propsArrayList.get(i);
                if (player.isCollision(props)) {
                    mainPlayerCollisionProps = propsArrayList.get(i);
                    allPropsAnimation.get(props.getPropsType()).setPlayPropsAnimation(true);//將此道具的動畫設為開啟
                    player.collideProps(props);
                    props.setGotByPlayer(true);
                    propsArrayList.remove(i--);
                }
            }
        }
    }

    /**
     * 隨時間部分區域變成扣分區
     */
    private void mapAreaClosing() {
        if (timeDelay.count()) {
            if (gameTime == 30) {
                AudioResourceController.getInstance().stop(currentSound);
                currentSound = new Path().sound().background().gameFirst();
                AudioResourceController.getInstance().loop(currentSound, -1);
            }
            if (gameTime == 70) {
                AudioResourceController.getInstance().stop(currentSound);
                currentSound = new Path().sound().background().gamebehind60();
                AudioResourceController.getInstance().loop(currentSound, -1);
            }
            if (gameTime == 120) {
                AudioResourceController.getInstance().stop(currentSound);
                currentSound = new Path().sound().background().normalgamebehind30final();
                AudioResourceController.getInstance().loop(currentSound, -1);
            }
            if (gameTime > 30 && gameTime <= 70) {
                if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST) {
                    mainPlayer.setInClosedArea(true);
                } else {
                    mainPlayer.setInClosedArea(false);
                }
            } else if (gameTime > 70 && gameTime <= 120) {
                if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST ||
                        mainPlayer.getPositionType() == Global.MapAreaType.ICEFIELD) {
                    mainPlayer.setInClosedArea(true);
                } else {
                    mainPlayer.setInClosedArea(false);
                }
            } else if (gameTime > 120) {
                if (mainPlayer.getPositionType() != Global.MapAreaType.VILLAGE) {
                    mainPlayer.setInClosedArea(true);
                } else {
                    mainPlayer.setInClosedArea(false);
                }
            }
        }
    }

    /**
     * 區域關閉前提醒
     *
     * @param g 繪圖
     */
    private void beforeClosdTip(Graphics g) {
        if (gameTime > 20 && gameTime < 30) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，森林草原區變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((30 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 60 && gameTime < 70) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，冰原雪地區也變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((70 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 110 && gameTime < 120) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，荒原紅土區也變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((120 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 144 && gameTime < 154) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，遊戲結束！！！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((154 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        }
    }

    /**
     * 進入扣分區的顯示警示
     *
     * @param g
     */
    private void paintWarning(Graphics g) {
        if (mainPlayer.isInClosedArea() && mainPlayer.roleState != Player.RoleState.HUNTER) {
            g.setColor(Color.RED);
            imgWarning.paint(
                    Global.SCREEN_X / 2 - 50,
                    100,
                    80,
                    80,
                    g);
            g.setColor(Color.BLACK);
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
            if (propsArrayList.size() >= Global.PROPS_AMOUNT_MAX) {
                return;
            }
            propsArrayList.add(new Props());
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

    /**
     * 獵人狂暴化效果
     */
    public void outrageEffect() {
        if (mainPlayer.startOutrage) {
            for (int i = 1; i < players.size(); i++) {
                Player player = players.get(i);
                if (player.getMovement().getSpeed() > Global.SPEED_MIN) {
                    player.getMovement().addSpeed(-1);
                }
            }
            mainPlayer.startOutrage = false;
        }
    }

    /**
     * 時間到
     */
    public void timeUP() {
        if (timeDelay.count()) {
            if (chooseTime <= gameTime) {
                gameOver();
            }
        }
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

    @Override
    public void gameOver() {
        CountPointScene countPointScene = new CountPointScene();
        countPointScene.setPlayerPoint(players);
        SceneController.getInstance().change(countPointScene);
    }

    private boolean isCanMove() {
        return gameTime > 3;
    }
}
