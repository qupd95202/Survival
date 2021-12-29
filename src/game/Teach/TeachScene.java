package game.Teach;


import game.Menu.*;
import game.Menu.Button;
import game.Menu.Label;
import game.controllers.AudioResourceController;
import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.MovingObstacle;
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

    private ArrayList<MapObject> unPassMapObjects;
    private Camera camera;
    private GameMap gameMap;
    private MovingObstacle bee;

    private Image imgForest;

    //道具生成與消失
    private Delay propsReProduce;
    private Delay propsRemove;
    private Delay labelTime;


    //左下角的方格
    Animation runnerLight;
    Animation runnerDark;
    Animation runnerNormal;
    Animation changeBody;
    Animation imgWarning;
    Animation no;//當玩家為獵人時變身格會放
    Animation teach;//點擊物件變身


    //提示訊息(畫面上所有的文字處理)
    private ArrayList<Label> labels;
    private Label teachLabel;
    private ArrayList<String> teachStrings;
    private int teachCount;
    //避免一直進入迴圈

    private Boolean firstProps;
    private Boolean firstCongratulations;
    private Boolean firstHunter;
    private Boolean useProps;
    private Boolean becomeBee;
    private Boolean pickProps;

    private Animation button;
    private game.Menu.Button backButton;
    private ArrayList<Button> buttons;

    @Override
    public void sceneBegin() {
        AudioResourceController.getInstance().play(new Path().sound().background().gameFirst());
        gameObjectList = new ArrayList<>();//初始ArrayList
        players = new ArrayList<>();
        labels = new ArrayList<Label>();
        teachStrings = new ArrayList<>();
        propsArrayList = new ArrayList<Props>();
        buttons=new ArrayList<>();
        labelTime = new Delay(120);


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
        teachCount = 0;

        firstHunter = true;
        firstProps = true;
        firstCongratulations = true;
        useProps = false;
        becomeBee=false;
        pickProps=false;
        button = new Animation(AllImages.inputButton);

        labels.add(new Label(100,80,"NEXT",FontLoader.cuteChinese(50)));
        buttons.add(new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.cross)));
        buttons.add(new Button(Global.SCREEN_X - 100, 20, Global.UNIT_WIDTH, Global.UNIT_HEIGHT, new Animation(AllImages.inputButton)));
        buttons.add(new Button(labels.get(3).collider().left()-10,labels.get(3).collider().bottom()-50,120,60,new Animation(AllImages.inputButton)));
        teach = new Animation(AllImages.teach1);



    }


    @Override
    public void sceneEnd() {
        this.gameObjectList = null; //將Game要畫的所有GameObject存起來
        this.propsArrayList = null;
        this.mainPlayer = null;
        this.players = null;
        this.unPassMapObjects = null;
        this.camera = null;
        this.gameMap = null;
        this.imgForest = null;
        this.propsReProduce = null;
        this.propsRemove = null;
        this.labelTime = null;
        this.runnerLight = null;
        this.runnerDark = null;
        this.runnerNormal = null;
        this.changeBody = null;
        this.imgWarning = null;
        this.no = null;
        this.labels = null;
        this.teachLabel = null;
        this.teachStrings = null;
        this.firstHunter = null;
        this.firstProps = null;
        this.firstCongratulations = null;
        AudioResourceController.getInstance().stop(new Path().sound().background().gameFirst());
    }

    @Override
    public void paint(Graphics g) {//留意畫的順序
        g.drawImage(imgForest, 0, 0, 1920, 1920, null);

        gameMap.paint(g);
        //用forEach將ArrayList中每個gameObject去paint()
        gameObjectList.forEach(gameObject -> gameObject.paint(g));
        propsPaint(g);
        bee.paint(g);

        button.paint(0, Global.SCREEN_Y - 100, Global.SCREEN_X, 100, g);

        for (int i=0;i<buttons.size();i++){
            if (i==1 ){
                if(buttons.get(1).isCollision(Global.mouse)){
                    buttons.get(1).paint(g);
                }
            }else if (i==2){
                if(buttons.get(2).isCollision(Global.mouse)) {
                    buttons.get(2).paint(g);
                }
            }else {
                buttons.get(i).paint(g);
            }
        }


        //判斷有沒有道具
        if (!mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runnerDark.paint(Global.RUNNER_X, Global.RUNNER_Y - 100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        } else if (mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()) {
            runnerNormal.paint(Global.RUNNER_X, Global.RUNNER_Y - 100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        } else {
            runnerLight.paint(Global.RUNNER_X, Global.RUNNER_Y - 100, Global.GAME_SCENE_BOX_SIZE, Global.GAME_SCENE_BOX_SIZE, g);
        }

        //變身格
        changeBody.paint(105, Global.SCREEN_Y - 200, 100, 100, g);
        for (int i = 0; i < labels.size()-1; i++) {
            labels.get(i).paint(g);
        }

        //獵人要印
        if (mainPlayer.roleState == Player.RoleState.HUNTER) {
            no.paint(130, Global.SCREEN_Y - 170, 50, 50, g);
        }else {
            if (mainPlayer.getStoredTransformAnimation() != null) {
                mainPlayer.getStoredTransformAnimation().paint(125, Global.SCREEN_Y - 180, 60, 60, g);
            }
            //prey要印的
            labels.get(3).paint(g);
        }

        //教學繪圖
        if (teachCount > 10 && teachCount <= 16) {
            teach.setImg(AllImages.teach2);
            teach.paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 20, 400, 150, g);
        }
        if (teachCount > 25 && teachCount < 33) {
            teach.setImg(AllImages.teach3);
            teach.paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 20, 400, 150, g);
        }
        if (teachCount > 25 && teachCount <= 33) {
            teach.paint(Global.SCREEN_X / 3, Global.SCREEN_Y / 20, 400, 150, g);
        }

        teachLabel.paint(g);
        Global.mouse.paint(g);

    }

    @Override
    public void update() {


        //為了解決player與npc重疊時 畫面物件顯示先後順序問題

        sortObjectByPosition();
        //用forEach將ArrayList中每個gameObject去update()
        keepNotPass(unPassMapObjects);
        allPropsUpdate();
        bee.moveTeachScene();

        gameObjectList.forEach(gameObject -> gameObject.update());
        cPlayerCheckOthersUpdate();
        playerCollisionCheckUpdate();
        propsCollisionCheckUpdate();
        camera.update();
        if(mainPlayer.roleState == Player.RoleState.HUNTER){
            labels.get(2).setWords(String.valueOf(mainPlayer.getOutrageCd()));
        }else {
            labels.get(2).setWords(String.valueOf(mainPlayer.transformCDTime()));
        }


        if (teachLabel != null) {
            if (labelTime.count() && teachCount < teachStrings.size() - 1) {
                teachCount++;
                teachLabel.setWords(teachStrings.get(teachCount));
            }
        }

        //確認變身為蜜蜂過
        if (mainPlayer.getCurrentAnimation().getImg() == AllImages.bee) {
            becomeBee=true;
        }
        //當變身過蜜蜂且未產生過道具且句子跑到16行時才會產生道具
        if(becomeBee && firstProps && teachCount>=16) {
            firstProps = false;
            produceProps();
        }
        //如果撿取過瞬間移動道具變true
        if(mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation()){
            pickProps=true;
        }
        //如果撿取過道具且圖像為甚麼都沒有的話代表已經使用過
        if(pickProps &&!mainPlayer.isCanUseTeleportation() && !mainPlayer.isUseTeleportation() && teachCount >= 32){
            if (labelTime.count()) {
                useProps=true;
            }
        }
        if(useProps &&firstHunter){
            firstHunter = false;
            produceHunter();

        }
        if (mainPlayer.getOutrageCd()<15  && firstCongratulations) {
            firstCongratulations = false;
            produceCongratulations();
        }

        if (firstCongratulations == false && teachCount == teachStrings.size() - 1) {
            SceneController.getInstance().change(new SurvivalPropsRuleScene());
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
        if (state == CommandSolver.MouseState.PRESSED) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            if (bee.isXYin(mouseX, mouseY)) {
                mainPlayer.chooseTransformObject(bee);
            }


            for (MapObject mapObject : unPassMapObjects) {
                if (mapObject.isXYNotIn(mouseX, mouseY)) {
                    return;
                }
            }
            mainPlayer.useTeleportation(mouseX, mouseY);
            if (Global.mouse.isCollision(buttons.get(0))) {
                SceneController.getInstance().change(new MenuScene());
            }
            if (Global.mouse.isCollision(buttons.get(2))) {
                SceneController.getInstance().change(new SurvivalPropsRuleScene());
            }
        }
        Global.mouse.mouseTrig(e, state, trigTime);
    }

    public void setLabels() {
        labels.add(new Label(Global.RUNNER_X + 75, Global.RUNNER_Y + 85 - 100, "F", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 75, Global.RUNNER_Y + 85 - 100, "R", FontLoader.Future(20)));
        labels.add(new Label(Global.RUNNER_X + Global.GAME_SCENE_BOX_SIZE + 5 + 15, Global.RUNNER_Y + 30 - 100, String.valueOf(mainPlayer.transformCDTime()), FontLoader.Future(20)));

    }

    public void setAnimation() {
        runnerDark = new Animation(AllImages.runnerDark);
        runnerLight = new Animation(AllImages.runnerLight);
        runnerNormal = new Animation(AllImages.runnerNormal);
        changeBody = new Animation(AllImages.changeBody);
        no = new Animation(AllImages.no);
        imgWarning = new Animation(AllImages.WARNING);
    }


    /**
     * 先有玩家
     */
    private void producePlayer() {
        mainPlayer = new TeachPlayer(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.beige, Player.RoleState.PREY, Global.SCREEN_X, Global.SCREEN_Y - 100);
        players.add(mainPlayer);
//        players.add(new TeachComputerPlayer(500, 500, AllImages.blue, Player.RoleState.PREY, Global.SCREEN_X, Global.SCREEN_Y));
        teachStrings.add("擊擊擊擊擊歡迎來到SURVIVAL擊擊擊");
        teachStrings.add("擊擊擊擊這裡為基本操作的教學擊擊擊");
        teachStrings.add("擊擊如果是第一次遊玩 請務必操作一次");
        teachStrings.add("擊擊擊擊擊擊就讓我們開始囉～擊擊擊");
        teachStrings.add("");
        teachStrings.add("鍵盤的 W-A-S-D-對應 上-下-左-右- 來移動");
        teachStrings.add("擊畫面中間為玩家圖像（像是小小兵）時擊擊");
        teachStrings.add("擊擊擊擊擊擊會有變身的功能擊擊擊擊擊擊");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊移動滑鼠游標（魔法棒）");//10
        teachStrings.add("擊擊擊點選地圖上的角色(例如：蜜蜂)");
        teachStrings.add("左下角 右邊格子就會出現可以變身角色的圖");
        teachStrings.add("");
        teachStrings.add("擊擊這時按下鍵盤R 就可以變身成功！ ");
        teachStrings.add("擊擊擊擊變身只能維持7秒!要注意！ ");
        teachStrings.add("擊擊擊擊擊擊擊操作看看~");
        teachStrings.add("");//17

        bee= new MovingObstacle(1000, 300, AllImages.bee);

        labelTime.play();
        labelTime.loop();
        teachLabel = new Label(Global.SCREEN_X / 4 - 100, Global.SCREEN_Y - 40, teachStrings.get(0), FontLoader.cuteChinese(40));
    }



    private void produceProps() {
        teachStrings.add("擊擊擊擊擊擊擊擊擊GOOD擊擊擊擊擊擊");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊擊現在來介紹瞬間移動功能~擊擊");
        teachStrings.add("擊擊擊擊遊戲中地圖會隨機出現道具擊擊擊");
        teachStrings.add("擊擊擊擊擊道具的功能是 \"隨機\" 的擊擊");
        teachStrings.add("擊擊擊擊擊其中一種就是 瞬間移動 擊擊");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊擊當道具為瞬間移動時");//25
        teachStrings.add("擊擊擊擊擊左下角 左邊格子會亮起");
        teachStrings.add("擊擊擊擊擊擊鍵盤按F 準備發動");
        teachStrings.add("擊擊擊擊滑鼠游標會變成光環圖示");
        teachStrings.add("擊擊擊擊點選任一地點即可瞬間移動");
        teachStrings.add("擊擊擊擊擊在逃命時非常好用！");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊擊擊擊操作看看~");
        teachStrings.add("");//33

        propsArrayList.add(new Props(Global.SCREEN_X / 2, Global.SCREEN_Y / 2 - 100, Props.Type.teleportation));
        propsArrayList.add(new Props(Global.SCREEN_X / 3 - 100, Global.SCREEN_Y / 3 - 100, Props.Type.teleportation));
    }

    private void produceHunter() {
        mainPlayer.setRoleState(Player.RoleState.HUNTER);
        mainPlayer.animationUpdate();
        //擊因為字體沒有因此不會出現，故用來抓區間使用
        teachStrings.add("擊擊擊擊擊擊擊擊擊GOOD擊擊擊擊擊擊");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊現在要來介紹當成為獵人時");
        teachStrings.add("擊擊擊玩家圖像會變成現在這個獵人角色");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊擊這個時候沒有變身功能");
        teachStrings.add("擊擊擊擊擊會換成獨特技能\"暴怒\"");
        teachStrings.add("擊擊擊擊\"暴怒\"施放後速度會大增");
        teachStrings.add("擊擊擊擊並且暫時看到其他玩家位置");
        teachStrings.add("擊擊擊還會讓其他玩家移動速度-1 (永久)");
        teachStrings.add("");
        teachStrings.add("擊擊擊多多利用 去追捕其他玩家(去撞他)");
        teachStrings.add("擊擊擊可以\"奪取他的積分\"並\"換他當獵人\"");
        teachStrings.add("");
        teachStrings.add("擊擊擊擊擊擊擊操作看看~");
        teachStrings.add("");

    }
    private void produceCongratulations() {
        teachStrings.add("    擊擊恭喜~已具備基本技能擊擊擊");
        teachStrings.add("    擊擊可以到遊戲模式遊玩囉擊擊擊");
        teachStrings.add("");
    }
}


