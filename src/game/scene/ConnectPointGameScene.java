package game.scene;

import game.Menu.Label;
import game.Menu.Mouse;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static game.gameObj.Pact.CONNECT;
import static game.gameObj.Pact.bale;

public class ConnectPointGameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    //留意畫的順序
    private ConnectTool connectTool;
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
    //滑鼠
    private Mouse mouse;

    //提示訊息(畫面上所有的文字處理)
    private ArrayList<game.Menu.Label> labels;
    private game.Menu.Label transFormCDLabel;

    //積分動畫顯示
    private game.core.Point point;
    private Image imgPoint;

    public ConnectPointGameScene() {
        connectTool = new ConnectTool();
        connectTool.setMainPlayer(new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.blue, Player.RoleState.PREY));
        connectTool.createRoom(5550);
    }

    @Override
    public void sceneBegin() {
        try {
            connectTool.connect("127.0.0.1", 5550);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientClass.getInstance().sent(CONNECT, bale(""));
        connectTool.consume();
        //遊戲時間
        startTime = System.nanoTime();
        chooseTime = 300; //單位：秒
        //初始ArrayList
        gameObjectList = new ArrayList<>();
        players = new ArrayList<>();
        labels = new ArrayList<game.Menu.Label>();

        //道具相關
        propsReProduce = new Delay(900);
        propsRemove = new Delay(1800);
        propsRemove.play();
        propsRemove.loop();
        propsReProduce.play();
        propsReProduce.loop();

        //主角
        mainPlayer = connectTool.getSelf();
//        mainPlayer.setID(1);

        //新增玩家
//        players.add(mainPlayer);
        //電腦
        players.add(new ComputerPlayer(100, 100, AllImages.beige, Player.RoleState.HUNTER));
        players.add(new ComputerPlayer(3000, 100, AllImages.blue, Player.RoleState.PREY));
        players.add(new ComputerPlayer(100, 3000, AllImages.blue, Player.RoleState.PREY));

        //畫面上相關
        runner = new Animation(AllImages.runnerDark);
        changeBody = new Animation(AllImages.changeBody);
        imgWarning = new Animation(AllImages.WARNING);
        no = new Animation(AllImages.no);
        transFormCDLabel = new game.Menu.Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30, String.valueOf(mainPlayer.transformCDTime()), 20);
        labels.add(new game.Menu.Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85, "F", 20));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85, "R", 20));
        labels.add(transFormCDLabel);

        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        players.forEach(player -> gameObjectList.addAll(java.util.List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(java.util.List.of(transformObstacle)));

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

        point = new Point();
        imgPoint = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().coin());

        printGameTime = new GameTime();
        imgClock = SceneController.getInstance().imageController().tryGetImage(new Path().img().numbers().clock());
    }


    @Override
    public void sceneEnd() {

    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        gameTime = (System.nanoTime() - startTime) / 1000000000;
        lastTime = chooseTime - gameTime;
        camera.startCamera(g);
        mapPaint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        connectTool.paint(g);
        propsPaint(g);

        //跟著鏡頭的在這之後paint
        camera.paint(g);
        camera.endCamera(g);

        //顯示遊戲時間
        paintTime(g);
        //顯示警告
        paintWarning(g);
        //顯示積分
        paintPoint(g);
        //顯示技能
        skillPaint(g);
        //畫滑鼠
        mouse.paint(g);

        //要畫在小地圖的要加在下方
        smallMap.start(g);
        gameMap.paint(g);
        smallMap.paint(g, mainPlayer, Color.red, 100, 100);//小地圖的需要另外再paint一次
        if (Global.IS_DEBUG) {
            for (int i = 0; i < players.size(); i++) {
                smallMap.paint(g, players.get(i), Color.YELLOW, 100, 100);
            }
        }
        camera.paint(g);
    }

    @Override
    public void update() {
        connectTool.update();
        //區域封閉
        mapAreaClosing();
        //道具生成與更新
        propsGenUpdate();
        allPropsUpdate();
        //為了解決player與npc重疊時 畫面物件顯示先後順序問題
        sortObjectByPosition();
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
        connectTool.consume();
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
        for (int i = 0; i < players.size(); i++) {
            ComputerPlayer computerPlayer = (ComputerPlayer) players.get(i);
            computerPlayer.whoIsNear(mainPlayer);
            for (int j = 0; j < players.size(); j++) {
                Player player = players.get(j);
                if (computerPlayer != player) {
                    computerPlayer.whoIsNear(player);
                }
            }
        }
    }

    public void cPlayerCheckPropsUpdate() {
        for (int i = 0; i < players.size(); i++) {
            ComputerPlayer computerPlayer = (ComputerPlayer) players.get(i);
            for (int j = 0; j < propsArrayList.size(); j++) {
                computerPlayer.whichPropIsNear(propsArrayList.get(j));
            }
        }
    }

    public void playerCollisionCheckUpdate() {
        players.forEach(player -> {
            player.exchangeRole(mainPlayer);
            players.forEach(player1 -> {
                if (player != player1) {
                    player.exchangeRole(player1);
                }
            });
        });
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
//        g.setColor(Color.WHITE);
//        g.drawString(String.format("剩餘時間 %s 秒", lastTime), Global.SCREEN_X - 100, 30);
//        g.setColor(Color.BLACK);


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
        if (mainPlayer.roleState == Player.RoleState.HUNTER) {
            no.paint(105, Global.SCREEN_Y - 100, 100, 100, g);
        }
    }

    /**
     * 讓角色無法穿過該物件
     */
    public void keepNotPass(ArrayList<? extends GameObject> gameObjects) {

        for (GameObject gameObject : gameObjects) {
            mainPlayer.isCollisionForMovement(gameObject);
        }
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

        for (int i = 0; i < propsArrayList.size(); i++) {
            Props props = propsArrayList.get(i);
            if (mainPlayer.isCollision(props)) {
                mainPlayer.collideProps(props);
                props.setGotByPlayer(true);
                propsArrayList.remove(i--);
            }
        }

        for (Player player : players) {
            for (int i = 0; i < propsArrayList.size(); i++) {
                Props props = propsArrayList.get(i);
                if (player.isCollision(props)) {
                    player.collideProps(props);
                    props.setGotByPlayer(true);
                    propsArrayList.remove(i--);
                }
            }
        }
    }

    private void mapAreaClosing() {
        if (gameTime > 100 && gameTime <= 180) {
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        } else if (gameTime > 180 && gameTime <= 270) {
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST ||
                    mainPlayer.getPositionType() == Global.MapAreaType.ICEFIELD) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        } else if (gameTime > 270) {
            if (mainPlayer.getPositionType() != Global.MapAreaType.VILLAGE) {
                mainPlayer.setInClosedArea(true);
            } else {
                mainPlayer.setInClosedArea(false);
            }
        }
    }

    private void paintWarning(Graphics g) {
        if (mainPlayer.isInClosedArea()) {
            g.setColor(Color.RED);
            imgWarning.paint(
                    Global.SCREEN_X / 2 - 50,
                    100,
                    120,
                    50,
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

    @Override
    public void keyPressed(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.UP.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.DOWN.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.DOWN.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.LEFT.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.LEFT.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.RIGHT.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.RIGHT.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.TRANSFORM.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.TELEPORTATION.getValue(), bale(String.valueOf(trigTime)));
        }
    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.UP.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.DOWN.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.DOWN.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.LEFT.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.LEFT.getValue(), bale(String.valueOf(trigTime)));
        }
        if (commandCode == Global.KeyCommand.RIGHT.getValue()) {
            ClientClass.getInstance().sent(Global.KeyCommand.RIGHT.getValue(), bale(String.valueOf(trigTime)));
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }

    @Override
    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
        mainPlayer.mouseTrig(e, state, trigTime, unPassMapObjects, transformObstacles, camera, mouse);
    }
}
