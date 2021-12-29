package game.scene;

import game.Menu.CountPointScene;
import game.Menu.FontLoader;
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
import game.gameObj.players.ComputerPlayer;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.PropsAnimation;
import game.map.GameMap;
import game.map.ObjectArr;
import game.network.Client.ClientClass;
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

import static game.gameObj.Pact.*;

public class ConnectPointGameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    //留意畫的順序
    private ConnectTool connectTool;
    private Player mainPlayer;
    //    private ArrayList<ComputerPlayer> computerPlayers;
    private final ArrayList<TransformObstacle> transformObstacles = ObjectArr.transformObstaclList1;
    private ArrayList<MapObject> unPassMapObjects;
    private ArrayList<Props> propsArrayList;
    private Camera camera;
    private SmallMap smallMap;
    private GameMap gameMap;

    private Image imgForest;
    private Image imgWinter;
    private Image imgVolcano;
    private Image imgVillage;

    //道具生成、消失與碰撞
    private Delay propsReProduce;
    private Delay propsRemove;
    private Delay propsCollision;

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
    Animation imgWarning;
    Animation no;//當玩家為獵人時變身格會放
    Animation background321;

    //321動畫
    private Label label321;

    //提示訊息(畫面上所有的文字處理)
    private ArrayList<game.Menu.Label> labels;
    private game.Menu.Label transFormCDLabel;

    //積分動畫顯示
    private game.core.Point point;
    private Image imgPoint;

    //連線
    private Delay bumpDelay;

    //道具吃到的動畫
    private ObjectArr objectArr;
    private HashMap<Props.Type, PropsAnimation> allPropsAnimation;
    private Props mainPlayerCollisionProps;

    //顯示積分
    private ArrayList<Player> playersPoint;
    private Image winnerImg;
    private Label playersPointTitle;

    //獵人爆走cd
    private Label hunterCDLabel;

    public ConnectPointGameScene() {
        connectTool = ConnectTool.instance();
//        computerPlayers = connectTool.getObjectArr().getComputerPlayersConnectPoint();
        propsArrayList = connectTool.getObjectArr().getPropsArrConnectPoint();

    }

    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().loop(new Path().sound().background().mainscene(), -1);
        //連線，六十秒同步一次資料
        bumpDelay = new Delay(60);
        bumpDelay.play();

        //遊戲時間
        startTime = System.nanoTime();
        chooseTime = 184; //單位：秒
        //初始ArrayList
        gameObjectList = new ArrayList<>();
        labels = new ArrayList<game.Menu.Label>();

        //道具相關
        propsReProduce = new Delay(180);
        propsRemove = new Delay(600);
        propsCollision = new Delay(1);
        propsCollision.play();
        propsCollision.loop();
        propsRemove.play();
        propsRemove.loop();
        propsReProduce.play();
        propsReProduce.loop();

        //主角
        mainPlayer = connectTool.getSelf();

        //畫面上相關
        runner = new Animation(AllImages.runnerDark);
        changeBody = new Animation(AllImages.changeBody);
        imgWarning = new Animation(AllImages.WARNING);
        no = new Animation(AllImages.no);
        transFormCDLabel = new game.Menu.Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.transformCDTime()), FontLoader.Future(20));
        labels.add(new game.Menu.Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85, "F", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85, "R", FontLoader.Future(20)));
        labels.add(transFormCDLabel);
        //獵人cd資訊
        hunterCDLabel = new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.getOutrageCd()), FontLoader.Future(20));

        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        //電腦玩家 拉出來update
//        computerPlayers.forEach(player -> gameObjectList.addAll(java.util.List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(java.util.List.of(transformObstacle)));
        connectTool.getMainPlayers().forEach(player -> gameObjectList.addAll(List.of(player)));

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

        //顯示積分
        playersPoint = connectTool.getMainPlayers();
        winnerImg = SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().crown());
        playersPointTitle = new Label(Global.SCREEN_X - 170, 70, "===排行榜===", FontLoader.cuteChinese(25));


        //隨機一個為獵人
        randomHunter();
    }


    @Override
    public void sceneEnd() {
        AudioResourceController.getInstance().stop(new Path().sound().background().normalgamebehind30final());
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        gameTime = (System.nanoTime() - startTime) / 1000000000;
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
        //畫滑鼠
        Global.mouse.paint(g);
        //碰撞道具時播放動畫
        if (mainPlayerCollisionProps != null) {
            allPropsAnimation.get(mainPlayerCollisionProps.getPropsType()).paint(g);
        }
        //所有玩家積分排序顯示
        paintSortedPoint(g);
        playersPointTitle.setColor(Color.YELLOW);
        playersPointTitle.paint(g);

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
        if (mainPlayer.isInOutrage) {
            for (Player player : connectTool.getMainPlayers()) {
                if (player != mainPlayer) {
                    smallMap.paint(g, player, Color.YELLOW, 100, 100);
                }
            }
        }
        camera.paint(g);
    }

    @Override
    public void update() {
        if (!isCanMove()) {
            camera.update();
            return;
        }
        //無法穿越部分物件
        keepNotPass(unPassMapObjects);
        //區域封閉
        mapAreaClosing();
        //道具生成與更新
        propsGenUpdate();
        allPropsUpdate();
        //狂暴化效果更新
        outrageEffect();
        //為了解決player與npc重疊時 畫面物件顯示先後順序問題
        sortObjectByPosition();
        //用forEach將ArrayList中每個gameObject去update()
        gameObjectList.forEach(gameObject -> gameObject.update());
//        cPlayerCheckOthersUpdate();
//        cPlayerCheckPropsUpdate();  //連線不追蹤道具了
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        imgWarning.update();
        camera.update();
        //cd時間顯示之資料
        transFormCDLabel.setWords(String.valueOf(mainPlayer.transformCDTime()));
        hunterCDLabel.setWords(String.valueOf(mainPlayer.getOutrageCd()));

        connectTool.consume();
        positionUpdate();
        timeUP();
        //碰撞道具時播放動畫的更新
        if (mainPlayerCollisionProps != null) {
            allPropsAnimation.get(mainPlayerCollisionProps.getPropsType()).update();
        }
        //所有玩家積分排序更新
        sortPlayersPoint();
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

//    public void cPlayerCheckOthersUpdate() {
//        for (int i = 0; i < computerPlayers.size(); i++) {
//            ComputerPlayer computerPlayer = (ComputerPlayer) computerPlayers.get(i);
//            if (ClientClass.getInstance().getID() == 100) {
//                ClientClass.getInstance().sent(COMPUTER_MAINPLAYER_WHOISNEAR, bale(Integer.toString(i)));
//            }
//            for (int j = 0; j < computerPlayers.size(); j++) {
//                Player player = computerPlayers.get(j);
//                if (computerPlayer != player) {
//                    if (ClientClass.getInstance().getID() == 100) {
//                        ClientClass.getInstance().sent(COMPUTER_WHOISNEAR, bale(Integer.toString(i), Integer.toString(j)));
//                    }
//                }
//            }
//        }
//    }

//    public void cPlayerCheckPropsUpdate() {
//        for (int i = 0; i < computerPlayers.size(); i++) {
//            ComputerPlayer computerPlayer = (ComputerPlayer) computerPlayers.get(i);
//            for (int j = 0; j < propsArrayList.size(); j++) {
//                if (ClientClass.getInstance().getID() == 100) {
//                    ClientClass.getInstance().sent(COMPUTER_WHICHISNEAR, bale(Integer.toString(i), Integer.toString(j)));
//                }
//            }
//        }
//    }

    /**
     * 碰撞檢查
     */
    public void playerCollisionCheckUpdate() {
        for (int i = 0; i < connectTool.getMainPlayers().size(); i++) {
            Player player1 = connectTool.getMainPlayers().get(i);
            for (int j = 0; j < connectTool.getMainPlayers().size(); j++) {
                Player player2 = connectTool.getMainPlayers().get(j);
                if (player1 != player2 && player1.isCollision(player2)) {
                    if (player1.roleState != player2.roleState && player1.roleState != Player.RoleState.BUMPING && player2.roleState != Player.RoleState.BUMPING) {
                        if (player1.currentAnimation != player1.bumpAnimation && player2.currentAnimation != player2.bumpAnimation)
                            if (ClientClass.getInstance().getID() == 100) {
                                if (bumpDelay.count()) {
                                    ClientClass.getInstance().sent(PLAYER_COLLISION_PLAYER, bale(Integer.toString(player1.ID()), Integer.toString(player2.ID())));
                                    bumpDelay.play();
                                }
                            }
                    }
                }
            }
        }
////
//        for (int i = 0; i < computerPlayers.size(); i++) {
//            for (int j = 0; j < computerPlayers.size(); j++) {
//                if (computerPlayers.get(i) != computerPlayers.get(j)) {
//                    if (ClientClass.getInstance().getID() == 100) {
//                        ClientClass.getInstance().sent(COMPUTER_COLLISION_COMPUTER, bale(Integer.toString(i), Integer.toString(j)));
//                    }
//                }
//            }
//        }

//        for (int i = 0; i < computerPlayers.size(); i++) {
//            ClientClass.getInstance().sent(PLAYER_COLLISION_COMPUTER, bale(Integer.toString(i)));
//        }

    }

    //積分顯示動畫
    public void paintPoint(Graphics g) {
//        g.setColor(Color.RED);
//        g.drawString("你的積分:" + mainPlayer.getPoint(), 700, 30);
//        g.setColor(Color.BLACK);
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
        for (GameObject gameObject : gameObjects) {
            mainPlayer.isCollisionForMovement(gameObject);
        }
//        for (Player player : computerPlayers) {
//            for (GameObject gameObject : gameObjects) {
//                player.isCollisionForMovement(gameObject);
//            }
//        }
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
        for (int i = 0; i < propsArrayList.size(); i++) {
            Props props = propsArrayList.get(i);
            if (mainPlayer.isCollision(props)) {
                mainPlayerCollisionProps = propsArrayList.get(i);
                allPropsAnimation.get(props.getPropsType()).setPlayPropsAnimation(true);//將此道具的動畫設為開啟
                if (propsCollision.count()) {
                    ClientClass.getInstance().sent(PLAYER_COLLISION_PROPS, bale(Integer.toString(i)));
                }
            }
        }

//        for (int i = 0; i < computerPlayers.size(); i++) {
//            ComputerPlayer computerPlayer = computerPlayers.get(i);
//            for (int j = 0; j < propsArrayList.size(); j++) {
//                Props props = propsArrayList.get(i);
//                if (computerPlayer.isCollision(props)) {
//                    if (ClientClass.getInstance().getID() == 100) {
//                        ClientClass.getInstance().sent(COMPUTER_COLLISION_PROPS, bale(Integer.toString(i),Integer.toString(j)));
//                    }
//                    computerPlayer.collideProps(props);
//                    props.setGotByPlayer(true);
//                    propsArrayList.remove(i--);

    }

    /**
     * 隨時間部分區域變成扣分區
     */
    private void mapAreaClosing() {
        if (gameTime > 50 && gameTime <= 100) {
            AudioResourceController.getInstance().stop(new Path().sound().background().mainscene());
            AudioResourceController.getInstance().play(new Path().sound().background().gameFirst());
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        } else if (gameTime > 100 && gameTime <= 150) {
            AudioResourceController.getInstance().stop(new Path().sound().background().gameFirst());
            AudioResourceController.getInstance().play(new Path().sound().background().normalgamebehind30());
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST ||
                    mainPlayer.getPositionType() == Global.MapAreaType.ICEFIELD) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        } else if (gameTime > 150) {
            AudioResourceController.getInstance().stop(new Path().sound().background().normalgamebehind30());
            AudioResourceController.getInstance().play(new Path().sound().background().normalgamebehind30final());
            if (mainPlayer.getPositionType() != Global.MapAreaType.VILLAGE) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        }
    }

    /**
     * 區域關閉前提醒
     *
     * @param g 繪圖
     */
    private void beforeClosdTip(Graphics g) {
        if (gameTime > 40 && gameTime < 50) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，森林草原區變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((50 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 90 && gameTime < 100) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，冰原雪地區也變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((100 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 140 && gameTime < 150) {
            Label labelTip = new Label(Global.SCREEN_X / 2 - 150, 75, "秒後，荒原紅土區也變成扣分區！", FontLoader.cuteChinese(30));
            labelTip.setColor(Color.BLACK);
            g.drawImage(point.imgDigits((150 - (int) gameTime) % 10),
                    Global.SCREEN_X / 2 - 180,
                    50,
                    30,
                    30,
                    null);
            labelTip.paint(g);
        } else if (gameTime > 174 && gameTime < 184) {
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
            if (propsArrayList.size() >= 10 * connectTool.getMainPlayers().size()) {
                return;
            }
            if (ClientClass.getInstance().getID() == 100) {
                ClientClass.getInstance().sent(PROPS_GEN, bale(String.valueOf(Global.random(0, Global.MAP_PIXEL_WIDTH)), String.valueOf(Global.random(0, Global.MAP_PIXEL_HEIGHT)), Props.genRandomType().toString()));
            }
        }
    }

    /**
     * 畫面同步
     */
    public void positionUpdate() {
        ClientClass.getInstance().sent(UPDATE_POSITION, bale(Integer.toString(mainPlayer.collider().getX()), Integer.toString(mainPlayer.collider().getY())));
        ClientClass.getInstance().sent(POINT_UPDATE, bale(Integer.toString(mainPlayer.getPoint())));
        if (ClientClass.getInstance().getID() == 100) {
//            for (int i = 0; i < computerPlayers.size(); i++) {
//                ComputerPlayer computerPlayer = computerPlayers.get(i);
//                ClientClass.getInstance().sent(COMPUTER_UPDATE_POSITION, bale(Integer.toString(i), Integer.toString(computerPlayer.collider().getX()), Integer.toString(computerPlayer.collider().getY())));
//            }
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
            ClientClass.getInstance().sent(DECREASE_SPEED, bale(""));
            mainPlayer.startOutrage = false;
        }
    }

    /**
     * 時間到
     */
    public void timeUP() {
        if (chooseTime == gameTime) {
            CountPointScene countPointScene = new CountPointScene();
            countPointScene.setPlayerPoint(connectTool.getMainPlayers());
            SceneController.getInstance().change(countPointScene);
        }
    }

    /**
     * 顯示玩家目前分數
     *
     * @param player 要畫出分數的角色
     * @param x      分數顯示位置x軸
     * @param y      分數顯示位置Y軸
     * @param g      繪圖
     */
    private void paintPlayersPoint(Player player, int x, int y, Graphics g) {
        g.drawImage(point.imgHundreds(player.getPoint()),
                x,
                y,
                10,
                15,
                null);
        g.drawImage(point.imgTens(player.getPoint()),
                x + 10,
                y,
                10,
                15,
                null);
        g.drawImage(point.imgDigits(player.getPoint()),
                x + 20,
                y,
                10,
                15,
                null);


    }

    /**
     * 顯示所有玩家排序積分
     *
     * @param g 繪圖
     */
    private void paintSortedPoint(Graphics g) {
        g.drawImage(winnerImg, Global.SCREEN_X - 200, 73, 40, 40, null);
        for (int i = 0; i < playersPoint.size(); i++) {
            g.setColor(Color.BLACK);
            Font font = new Font("", Font.BOLD, 15);
            g.setFont(font);
            g.drawString(playersPoint.get(i).getName(), Global.SCREEN_X - 132, 100 + i * 20);
            paintPlayersPoint(playersPoint.get(i), Global.SCREEN_X - 165, 87 + i * 22, g);
        }
    }

    /**
     * 排序所有玩家積分
     */
    private void sortPlayersPoint() {
        for (int i = 0; i < playersPoint.size() - 1; i++) {
            for (int j = 0; j < playersPoint.size() - i; j++) {
                if (playersPoint.get(i).getPoint() < playersPoint.get(i + 1).getPoint()) {
                    Player tmp = playersPoint.get(i);
                    playersPoint.set(i, playersPoint.get(i + 1));
                    playersPoint.set(i + 1, tmp);
                }
            }
        }
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {
        if (isCanMove()) {
            mainPlayer.keyPressed(commandCode, trigTime);

//            if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
////                if (mainPlayer.roleState == Player.RoleState.HUNTER) {
////                    ClientClass.getInstance().sent(OUTRAGE, bale(String.valueOf(trigTime)));
////                }
//                ClientClass.getInstance().sent(TRANSFORM, bale(String.valueOf(trigTime)));
//            }
//            if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
//                ClientClass.getInstance().sent(TELEPORTATION, bale(String.valueOf(trigTime)));
//            }
        }
    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (isCanMove()) {
            mainPlayer.keyReleased(commandCode, trigTime);
        }
        if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
//                if (mainPlayer.roleState == Player.RoleState.HUNTER) {
//                    ClientClass.getInstance().sent(OUTRAGE, bale(String.valueOf(trigTime)));
//                }
            ClientClass.getInstance().sent(TRANSFORM, bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
            ClientClass.getInstance().sent(TELEPORTATION, bale(String.valueOf(trigTime)));
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        if (isCanMove()) {
            mainPlayer.mouseTrig(e, state, trigTime, unPassMapObjects, transformObstacles, camera, Global.mouse);
        }
    }

    private boolean isCanMove() {
        return gameTime > 3;
    }

    private void randomHunter() {
        int number = Global.random(0, ConnectTool.instance().getMainPlayers().size() - 1);
        if (ClientClass.getInstance().getID() == 100) {
            int id = connectTool.getMainPlayers().get(number).ID();
            ClientClass.getInstance().sent(RANDOM_HUNTER, bale(Integer.toString(id)));
        }
    }
}
