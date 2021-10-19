package game.scene;

import game.controllers.SceneController;
import game.core.Global;
import game.core.Position;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.MovingObstacle;
import game.gameObj.obstacle.Obstacle;
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


public class GameScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {
    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    private ArrayList<Props> propsArrayList = ObjectArr.propsArr;
    //留意畫的順序
    private Player mainPlayer;
    //    private ComputerPlayer cp;
//    private ComputerPlayer cp2;
    private ArrayList<Player> players;
    private ArrayList<TransformObstacle> transformObstacles;
    private ArrayList<MapObject> unPassMapObjects;
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

    //關閉的區域（在裡面扣分）
    private boolean inclosedArea;
    private Image imgWarning;

    //左下角的方格
    Animation runner;
    Animation changeBody;

    @Override
    public void sceneBegin() {
        //遊戲時間
        startTime = System.nanoTime();
        chooseTime = 300; //單位：秒


        gameObjectList = new ArrayList<>();//初始ArrayList
        transformObstacles = new ArrayList<>();
        players = new ArrayList<>();
        propsReProduce = new Delay(900);
        propsRemove = new Delay(1800);
        propsRemove.play();
        propsRemove.loop();
        propsReProduce.play();
        propsReProduce.loop();

        //先將要畫的物件初始
        mainPlayer = new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.beige, Player.RoleState.HUNTER);
        transformObstacles.add(new TransformObstacle(400, 500, AllImages.bunny1));
        transformObstacles.add(new MovingObstacle(300, 300, AllImages.bee));
        players.add(mainPlayer);
        players.add(new ComputerPlayer(0, 0, AllImages.blue, Player.RoleState.PREY));
        players.add(new ComputerPlayer(500, 500, AllImages.blue, Player.RoleState.PREY));
        runner = AllImages.runnerDark;
        changeBody = AllImages.changeBody;


        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        players.forEach(player -> gameObjectList.addAll(List.of(player)));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(List.of(transformObstacle)));

        gameMap = new GameMap(Global.MAP_WIDTH, Global.MAP_HEIGHT);
        unPassMapObjects = gameMap.getMapObjects();
        unPassMapObjects.forEach(mapObject -> gameObjectList.addAll(List.of(mapObject)));
        camera = new Camera(gameMap.getWidth() + 5, gameMap.getHeight() + 5);
        camera.setTarget(mainPlayer);
        smallMap = new SmallMap(0, 0, Global.MAP_WIDTH, Global.MAP_HEIGHT, 0.05, 0.05);

        imgForest = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().forest());
        imgWinter = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().winter());
        imgVolcano = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().volcano());
        imgVillage = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().village());


        imgWarning = SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().warningLabel());

    }


    @Override
    public void sceneEnd() {

    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        gameTime = (System.nanoTime() - startTime) / 1000000000;
        lastTime = chooseTime - gameTime;
        camera.startCamera(g);
        g.drawImage(imgForest, 0, 0, 1920, 1920, null);
        g.drawImage(imgVolcano, 1920, 0, 1920, 1920, null);
        g.drawImage(imgWinter, 0, 1920, 1920, 1920, null);
        g.drawImage(imgVillage, 1920, 1920, 1920, 1920, null);
        gameMap.paint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        propsPaint(g);

        //跟著鏡頭的在這之後paint
        camera.paint(g);
        camera.endCamera(g);

        //顯示遊戲剩餘時間
        paintLastGameTime(g);
        //顯示警告
        paintWarning(g);
        //顯示積分
        paintPoint(g);

        //判斷有沒有道具
        if (mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runner = AllImages.runnerNormal;
        } else if (mainPlayer.isCanUseTeleportation() && mainPlayer.isUseTeleportation()) {
            runner = AllImages.runnerLight;
        } else {
            runner = AllImages.runnerDark;
        }
        runner.paint(0, Global.SCREEN_Y - 100, 100, 100, g);

        //變身格
        changeBody.paint(105, Global.SCREEN_Y - 100, 100, 100, g);
        if (mainPlayer.getStoredTransformAnimation() != null) {
            mainPlayer.getStoredTransformAnimation().paint(125, Global.SCREEN_Y - 80, 60, 60, g);
        }

        //要畫在小地圖的要加在下方
        smallMap.start(g);
        gameMap.paint(g);
        smallMap.paint(g, mainPlayer, Color.red, 100, 100);//小地圖的需要另外再paint一次
        for (int i = 1; i < players.size(); i++) {
            smallMap.paint(g, players.get(i), Color.YELLOW, 100, 100);
        }
//        smallMap.paint(g, cp, Color.YELLOW, 100, 100);

        camera.paint(g);
    }

    @Override
    public void update() {
        mapAreaClosing();

        //為了解決player與npc重疊時 畫面物件顯示先後順序問題
        propsGenUpdate();
        sortObjectByPosition();
        //用forEach將ArrayList中每個gameObject去update()
        keepNotPass(unPassMapObjects);
        allPropsUpdate();
        gameObjectList.forEach(gameObject -> gameObject.update());
        cPlayerCheckOthersUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        camera.update();
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

    public void playerCollisionCheckUpdate() {
        players.forEach(player -> {
            players.forEach(player1 -> {
                if (player != player1) {
                    player.exchangeRole(player1);
                }
            });
        });
    }

    public void paintPoint(Graphics g) {
        g.setColor(Color.RED);
        g.drawString("你的積分:" + mainPlayer.getPoint(), 600, 30);
        g.setColor(Color.BLACK);
    }

    private void paintLastGameTime(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString(String.format("剩餘時間 %s 秒", lastTime), Global.SCREEN_X - 100, 30);
        g.setColor(Color.BLACK);
    }

    /**
     * 讓角色無法穿過該物件
     */
    public void keepNotPass(ArrayList<? extends GameObject> gameObjects) {
        for (Player player : players) {
            player.setNothingBlock(true);
            for (int i = 0; i < gameObjects.size(); i++) {
                if (player.isCollisionForMovement(gameObjects.get(i))) {
                    player.notMove();
                }
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
                    player.collideProps(props);
                    propsArrayList.remove(i--);
                }
            }
        }
    }

    private void mapAreaClosing() {
        if (gameTime > 30 && gameTime <= 60) {
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST) {
                inclosedArea = true;
            } else {
                inclosedArea = false;
            }
        } else if (gameTime > 60 && gameTime <= 120) {
            if (mainPlayer.getPositionType() == Global.MapAreaType.FOREST ||
                    mainPlayer.getPositionType() == Global.MapAreaType.ICEFIELD) {
                inclosedArea = true;
            } else {
                inclosedArea = false;
            }
        } else if (gameTime > 120) {
            if (mainPlayer.getPositionType() != Global.MapAreaType.VILLAGE) {
                inclosedArea = true;
            } else {
                inclosedArea = false;
            }
        }
    }

    private void checkPlayerInClosedArea() {
        if (mainPlayer.painter().left() < closedArea.getX() && mainPlayer.painter().top() < closedArea.getY()) {
            mainPlayer.setInclosedArea(true);
        }else {
            mainPlayer.setInclosedArea(false);;
        }
    }

    private void paintWarning(Graphics g) {
        if (mainPlayer.inclosedArea()) {
            g.drawImage(
                    imgWarning,
                    Global.SCREEN_X / 2 - 100 ,
                    0,
                    200,
                    37,
                    null
            );
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
        for (Props props : ObjectArr.propsArr) {
            props.paint(g);
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
    }


}
//    public void computerUpdate() {
//        cp.whoIsNear(player);
//        cp.update();
//    }
//    public void nearUpdate() {
//
//    }


//    public void computerUpdate() {
//        cp.whoIsNear(player);
//        cp.update();
//    }
//}
