package game.Teach;


import game.Menu.*;
import game.Menu.Label;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.MovingObstacle;
import game.gameObj.obstacle.TransformObstacle;
import game.gameObj.players.ComputerPlayer;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.map.GameMap;
import game.scene.Scene;
import game.scene_process.Camera;
import game.utils.CommandSolver;
import game.utils.Delay;
import game.utils.Path;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeachScene extends Scene implements CommandSolver.MouseCommandListener, CommandSolver.KeyListener {

    private ArrayList<GameObject> gameObjectList; //將Game要畫的所有GameObject存起來
    private ArrayList<Props> propsArrayList;
    //留意畫的順序
    private TeachPlayer mainPlayer;
    private ArrayList<Player> players;
    private ArrayList<TransformObstacle> transformObstacles;
    private ArrayList<MapObject> unPassMapObjects;
    private Camera camera;
    private GameMap gameMap;

    private Image imgForest;

    //道具生成與消失
    private Delay propsReProduce;
    private Delay propsRemove;
    private Delay labelTime;


    //關閉的區域（在裡面扣分）
    private boolean inclosedArea;


    //左下角的方格
    Animation runnerLight;
    Animation runnerDark;
    Animation runnerNormal;
    Animation changeBody;
    Animation imgWarning;
    Animation no;//當玩家為獵人時變身格會放
//    //滑鼠
//    private Mouse mouse;

    //提示訊息(畫面上所有的文字處理)
    private ArrayList<Label> labels;
    private Label teachLabel;
    private ArrayList<String> teachStrings;
    private int teachCount;
    //避免一直進入迴圈
    private Boolean firstBee;
    private Boolean firstProps;
    private Boolean firstCongratulations;

    private Image img;
    private Animation button;

    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().play(new Path().sound().background().gameFirst());
        gameObjectList = new ArrayList<>();//初始ArrayList
        transformObstacles = new ArrayList<>();
        players = new ArrayList<>();
        labels = new ArrayList<Label>();
        teachStrings=new ArrayList<>();
        propsArrayList = new ArrayList<Props>();
        labelTime=new Delay(120);


        //先將要畫的物件初始
        producePlayer();

        //將要畫的物件存進ArrayList 為了要能在ArrayList取比較 重疊時畫的先後順序（y軸）
        players.forEach(player -> gameObjectList.addAll(List.of(player)));


        gameMap = new GameMap(Global.MAP_WIDTH, Global.MAP_HEIGHT, new Path().img().map().bmp(), new Path().img().map().txt());
        unPassMapObjects = gameMap.getMapObjects();
        unPassMapObjects.forEach(mapObject -> gameObjectList.addAll(List.of(mapObject)));
        camera = new Camera(gameMap.getWidth() + 5, gameMap.getHeight() + 5);
        camera.setTarget(mainPlayer);
        imgForest = SceneController.getInstance().imageController().tryGetImage(new Path().img().background().forest());
        setLabels();
        setAnimation();
        teachCount=0;

//        mouse = new Mouse(0, 0, 50, 50);
        firstBee=true;
        firstProps=true;
        firstCongratulations=true;
        button=new Animation(AllImages.button1);
    }


    @Override
    public void sceneEnd() {
        this.gameObjectList=null; //將Game要畫的所有GameObject存起來
        this.propsArrayList=null;
        this.mainPlayer=null;
        this.players=null;
        this.transformObstacles=null;
        this.unPassMapObjects=null;
        this.camera=null;
        this.gameMap=null;
        this.imgForest=null;
        this.propsReProduce=null;
        this.propsRemove=null;
        this.labelTime=null;
        this.runnerLight=null;
        this.runnerDark=null;
        this.runnerNormal=null;
        this.changeBody=null;
        this.imgWarning=null;
        this.no=null;
        this.labels=null;
        this.teachLabel=null;
        this.teachStrings=null;
        this.firstBee=null;
        this.firstProps=null;
        this.firstCongratulations=null;
        AudioResourceController.getInstance().stop(new Path().sound().background().gameFirst());
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        g.drawImage(imgForest, 0, 0, 1920, 1920, null);

        gameMap.paint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        propsPaint(g);

        button.paint(0,Global.SCREEN_Y-100,Global.SCREEN_X,100,g);
        //判斷有沒有道具
        if (!mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runnerDark.paint(Global.RUNNER_X, Global.RUNNER_Y-100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        } else if (mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runnerNormal.paint(Global.RUNNER_X, Global.RUNNER_Y-100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        } else {
            runnerLight.paint(Global.RUNNER_X, Global.RUNNER_Y-100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        }

        //變身格
        changeBody.paint(105, Global.SCREEN_Y - 200, 100, 100, g);
        if (mainPlayer.getStoredTransformAnimation() != null) {
            mainPlayer.getStoredTransformAnimation().paint(125, Global.SCREEN_Y - 180, 60, 60, g);
        }
        if (mainPlayer.roleState == Player.RoleState.HUNTER) {
            no.paint(105, Global.SCREEN_Y - 200, 100, 100, g);
        }

        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).paint(g);
        }

        teachLabel.paint(g);
        Global.mouse.paint(g);

    }

    @Override
    public void update() {


        //為了解決player與npc重疊時 畫面物件顯示先後順序問題

        sortObjectByPosition();
        //用forEach將ArrayList中每個gameObject去update()
        keepNotPass(transformObstacles);
        keepNotPass(unPassMapObjects);
        allPropsUpdate();

        gameObjectList.forEach(gameObject -> gameObject.update());
        cPlayerCheckOthersUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        camera.update();
        labels.get(2).setWords(String.valueOf(mainPlayer.transformCDTime()));

        if(teachLabel!=null){
            if(labelTime.count() && teachCount<teachStrings.size()-1){
                teachCount++;
                teachLabel.setWords(teachStrings.get(teachCount));
            }
        }


        if(mainPlayer.roleState== Player.RoleState.PREY && firstBee){
            if(labelTime.count()){
                produceBee();
                firstBee=false;
            }
        }
        if(mainPlayer.getCurrentAnimation().getImg()==AllImages.bee && firstProps){
            if(labelTime.count()) {
                firstProps = false;
                produceProps();
            }
        }
        if(mainPlayer.isCanUseTeleportation() && mainPlayer.isUseTeleportation() && firstCongratulations){
            firstCongratulations=false;
            produceCongratulations();
        }
        if(firstCongratulations==false && teachCount==teachStrings.size()-1){
            SceneController.getInstance().change(new MenuScene());
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

    public void playerCollisionCheckUpdate() {
        players.forEach(player -> {
            players.forEach(player1 -> {
                if (player != player1) {
                    player.exchangeRole(player1);
                }
            });
        });
    }




    /**
     * 讓角色無法穿過該物件
     */
    public void keepNotPass(ArrayList<? extends GameObject> gameObjects) {
        for (Player player : players) {
//            player.setNothingBlock(true);
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
                    player.collideProps(props);
                    propsArrayList.remove(i--);
                }
            }
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
            int mouseX = e.getX();
            int mouseY = e.getY();
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
        Global.mouse.mouseTrig(e, state, trigTime);
    }

    public void setLabels() {
        labels.add(new Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85-100, "F", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85-100, "R", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30-100, String.valueOf(mainPlayer.transformCDTime()), FontLoader.Future(20)));

    }

    public void setAnimation(){
        runnerDark = new Animation(AllImages.runnerDark);
        runnerLight = new Animation(AllImages.runnerLight);
        runnerNormal = new Animation(AllImages.runnerNormal);
        changeBody = new Animation(AllImages.changeBody);
        no = new Animation(AllImages.no);
        imgWarning = new Animation(AllImages.WARNING);
    }


    /**
     * 先有玩家
     * */
    private void producePlayer(){
        mainPlayer = new TeachPlayer(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.beige, Player.RoleState.HUNTER, Global.SCREEN_X, Global.SCREEN_Y-100);
        players.add(mainPlayer);
        players.add(new TeachComputerPlayer(500, 500, AllImages.blue, Player.RoleState.PREY, Global.SCREEN_X, Global.SCREEN_Y));
        teachStrings.add("");
        teachStrings.add("      使用鍵盤WASD上下移動       ");
        teachStrings.add("當身分為HUNTER時左下角格子出現叉叉");
        teachStrings.add("     請試著追捕其他角色獲取積分並交換身分        ");


        labelTime.play();
        labelTime.loop();
        teachLabel=new Label(Global.SCREEN_X/4-100,Global.SCREEN_Y-40,teachStrings.get(0),FontLoader.dotChinese(40));
    }
    private void produceBee(){
        teachStrings.add("");
        teachStrings.add("             身分為普通玩家        ");
        teachStrings.add("     即可使用魔法棒click物件        ");
        teachStrings.add("     並按R成為該物件hide在map中        ");

        transformObstacles.add(new MovingObstacle(800, 300, AllImages.bee));
        transformObstacles.forEach(transformObstacle -> gameObjectList.addAll(List.of(transformObstacle)));

    }
    private void produceProps(){
        teachStrings.add("");
        teachStrings.add("          map出現random道具        ");
        teachStrings.add("           請touch道具        ");
        teachStrings.add("        當道具為瞬間移動時        ");
        teachStrings.add("          左下方格則亮起        ");
        teachStrings.add("     按F並click地上任一點即可順移        ");

        propsArrayList.add(new Props(Global.SCREEN_X / 2, Global.SCREEN_Y / 2 - 100,Props.Type.teleportation));
        propsArrayList.add(new Props( Global.SCREEN_X / 3 - 100, Global.SCREEN_Y / 3 - 100,Props.Type.teleportation));
    }
    private void produceCongratulations(){
        teachStrings.add("");
        teachStrings.add("            恭喜~已具備基本技能        ");
        teachStrings.add("            可以到遊戲模式遊玩囉        ");
        teachStrings.add("");
    }

}


