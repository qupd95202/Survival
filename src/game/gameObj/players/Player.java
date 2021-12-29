package game.gameObj.players;

import game.Menu.FontLoader;
import game.Menu.Label;
import game.Menu.Mouse;
import game.core.Global;
import game.core.Movement;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.Transformation;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.TransformObstacle;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;
import game.network.Client.ClientClass;
import game.scene_process.Camera;
import game.utils.CommandSolver;
import game.utils.Delay;
import game.utils.Path;
import game.controllers.AudioResourceController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static game.gameObj.Pact.*;

public class Player extends GameObject implements CommandSolver.KeyListener {


    public enum RoleState {
        HUNTER,
        BUMPING,
        PREY;
    }

    public enum MovingState {
        WALK,
        RUN,
        STAND,
        BUMP;
    }


    public Animation bumpAnimation = new Animation(AllImages.bump, 30);
    private Animation hunterAnimation = new Animation(AllImages.HUNTER);
    private Animation outrageAnimation = new Animation(AllImages.outrage);
    private Animation teleAnimation = new Animation(AllImages.teleAnimation, 5);


    //移動相關
    protected Movement movement;
    protected boolean canMove;
    public Delay canMoveDelay;
    protected MovingState movingState;
    public Delay moveDelay;
    private Delay teleDelay;

    //交換身分相關
    protected RoleState roleStateBeforeBump;
    public RoleState roleState;
    protected int point; //積分
    protected Player bumpPlayer; //撞到的角色
    protected Delay pointDelay;
    protected Delay collisionDelay;

    //變身相關
    protected boolean canTransform;
    protected Animation storedTransformAnimation;
    protected Delay transformCD;//冷卻時間
    protected Delay transformTime;

    //獵人狂暴化
    public boolean canOutrage;
    public boolean isInOutrage;
    public boolean startOutrage;
    public Delay outRageCD;//冷卻時間
    public Delay outRageTime;//持續時間

    //動畫處理部分拉出
    protected Animation originalAnimation;
    public Animation currentAnimation;

    //道具相關
    private boolean isUseTeleportation;//判斷是否按F(瞬間移動鍵)
    private boolean canUseTeleportation; //有沒有撿到此道具
    protected Delay trapDelay;
    public boolean isThunder;
    public boolean isSuperStar;
    private int currentSpeed;
    private Delay superStarDelay;
    public boolean isHunterStop;
    public boolean isDecreaseGameTime;
    public boolean isHunterWatcher;
    private Delay hunterWatcherDelay;

    //扣分相關
    private boolean isInClosedArea;

    //連線相關
    private int id = 0;

    //名字
    private String name;
    private Label nameLabel;


    public Player(int x, int y, ImgArrAndType imageArrayList, RoleState roleState, String name) {
        super(x, y, Global.PLAYER_WIDTH, Global.PLAYER_HEIGHT);
        movement = new Movement(Global.NORMAL_SPEED);//一般角色移動
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
        point = 0;
        this.name = name;
        Font font = new Font("", Font.BOLD, 15);
        nameLabel = new Label(painter().getX() + 10, painter().getY(), name, font, Color.BLACK);

        canMove = true;
        canPass = true;
        canTransform = true;
        canOutrage = true;
        isInOutrage = false;
        startOutrage = false;
        isUseTeleportation = false;
        canUseTeleportation = false;
        isInClosedArea = false;

        this.roleState = roleState;
        roleStateBeforeBump = roleState;
        originalAnimation = new Animation(imageArrayList);
        if (roleState == RoleState.HUNTER) {
            currentAnimation = hunterAnimation;
        } else {
            currentAnimation = originalAnimation;
        }

        pointDelay = new Delay(60);
        collisionDelay = new Delay(180);
        canMoveDelay = new Delay(180);
        transformCD = new Delay(600); //十秒
        transformTime = new Delay(420); //七秒
        outRageCD = new Delay(1200);
        outRageTime = new Delay(300);
        trapDelay = new Delay(120);
        superStarDelay = new Delay(600);
        teleDelay = new Delay(40);
        hunterWatcherDelay = new Delay(600);
        moveDelay = new Delay(1);
        moveDelay.play();
        moveDelay.loop();
        movingState = MovingState.STAND;
    }


    @Override
    public void paintComponent(Graphics g) {
        currentAnimation.paint(painter().left(), painter().top(), painter().width(), painter().height(), g);
        namePaint(g);
    }

    @Override
    public void update() {
        move();
        currentAnimation.update();
        propsEffectUpdate();
        transformResetUpdate();
        outrageUpdate();
        if (trapDelay.count()) {
            canMove = true;
        }
        addPoint();
        if (id != 0) {
            exchangeUpdateInConnect();
        } else {
            exchangeUpdate();
        }
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue() ||
                commandCode == Global.KeyCommand.DOWN.getValue() ||
                commandCode == Global.KeyCommand.LEFT.getValue() ||
                commandCode == Global.KeyCommand.RIGHT.getValue()) {
            movingState = MovingState.WALK;
        }
        //觸發Movement監聽到的移動變化
        movement.keyPressed(commandCode, trigTime);
    }


    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue() ||
                commandCode == Global.KeyCommand.DOWN.getValue() ||
                commandCode == Global.KeyCommand.LEFT.getValue() ||
                commandCode == Global.KeyCommand.RIGHT.getValue()) {
            movingState = MovingState.STAND;
        }
        if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
            clickedTeleportation();
        }
        if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
            transform();
            outrage();
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }


    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime, ArrayList<MapObject> unPassMapObjects, ArrayList<TransformObstacle> transformObstacles, Camera camera, Mouse mouse) {
        if (state == CommandSolver.MouseState.PRESSED) {
            int mouseX = e.getX() + camera.painter().left();
            int mouseY = e.getY() + camera.painter().top();
            for (int i = 0; i < transformObstacles.size(); i++) {
                TransformObstacle transformObstacle = transformObstacles.get(i);
                if (transformObstacle.isXYin(mouseX, mouseY)) {
                    if (id != 0) {
                        ClientClass.getInstance().sent(PLAYER_CHOOSE_TRANSFORM, bale(Integer.toString(i)));
                    } else {
                        chooseTransformObject(transformObstacle);
                    }
                }
            }
            for (MapObject mapObject : unPassMapObjects) {
                if (mapObject.isXYNotIn(mouseX, mouseY) || mapObject.isXYNotInMap(mouseX, mouseY)) {
                    return;
                }
            }
            if (id != 0) {
                ClientClass.getInstance().sent(PLAYER_USE_TELEPORTATION, bale(Integer.toString(mouseX), Integer.toString(mouseY)));
            } else {
                useTeleportation(mouseX, mouseY);
            }
        }
        mouse.mouseTrig(e, state, trigTime);
    }

    public void move() {
        if (!canMove) {
            return;
        }
        //一般移動位置的部分
        translate(movement.getVector2D().getX(), movement.getVector2D().getY());
        keepInMap();
        movement.getVector2D().setX(0);
        movement.getVector2D().setY(0);
        movement.setDeltaX(0);
        movement.setDeltaY(0);
    }

    /**
     * 讓角色不會超出邊界
     */
    public void keepInMap() {
        if (touchBottom() || touchLeft() || touchRight() || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }

    public void isCollisionForMovement(GameObject gameObject) {
        int x = movement.getVector2D().getX();
        int y = movement.getVector2D().getY();
        rightAndLeftIsCollision(gameObject, x);
        upAndDownIsCollision(gameObject, y);
    }

    public void upAndDownIsCollision(GameObject gameObject, int y) {
        if (collider().right() < gameObject.collider().left()) {
            return;
        }
        if (collider().left() > gameObject.collider().right()) {
            return;
        }
        if (collider().bottom() + y < gameObject.collider().top()) {
            return;
        }
        if (collider().top() + y > gameObject.collider().bottom()) {
            return;
        }
        translateY(-y);
    }

    public void rightAndLeftIsCollision(GameObject gameObject, int x) {
        if (collider().right() + x < gameObject.collider().left()) {
            return;
        }
        if (collider().left() + x > gameObject.collider().right()) {
            return;
        }
        if (collider().bottom() < gameObject.collider().top()) {
            return;
        }
        if (collider().top() > gameObject.collider().bottom()) {
            return;
        }
        translateX(-x);
    }

    public void exchangeRole(Player player) {
        if (roleState != player.roleState && roleState != RoleState.BUMPING && player.roleState != RoleState.BUMPING) {
            if (isCollision(player)) {
                if (currentAnimation != bumpAnimation && player.currentAnimation != player.bumpAnimation) {
                    bumpPlayer = player;
                    bump(bumpPlayer);
                    pointExchange(bumpPlayer);
                    AudioResourceController.getInstance().play(new Path().sound().background().exchange());
                }
            }
        }
    }

    public void exchangeUpdate() {
        if (canMoveDelay.count()) {
            roleStateExchange(bumpPlayer);
            canMove = true;
            bumpPlayer.canMove = true;
        }
        if (collisionDelay.count()) {
            animationUpdate();
            bumpPlayer.animationUpdate();
        }
    }

    public void exchangeRoleInConnect(Player player) {
        bumpPlayer = player;
        bump(bumpPlayer);
        pointExchange(bumpPlayer);
        AudioResourceController.getInstance().play(new Path().sound().background().exchange());
    }

    public void exchangeUpdateInConnect() {
        if (canMoveDelay.count()) {
            roleStateExchange(bumpPlayer);
            canMove = true;
            bumpPlayer.canMove = true;
        }
        if (collisionDelay.count()) {
            animationUpdate();
            bumpPlayer.animationUpdate();
        }
    }

    public void pointExchange(Player player) {
        int temp = point;
        setPoint(player.getPoint());
        player.setPoint(temp);
    }

    public void bump(Player player) {
        setCurrentAnimation(bumpAnimation);
        player.setCurrentAnimation(bumpAnimation);
        if (roleState == RoleState.PREY) {
            canMove = false;
        }
        if (player.roleState == RoleState.PREY) {
            player.canMove = false;
        }
        //碰撞狀態
        roleState = RoleState.BUMPING;
        player.roleState = RoleState.BUMPING;

        collisionDelay.play();
        canMoveDelay.play();
    }

    public void animationUpdate() {
        if (roleState == RoleState.HUNTER) {
            currentAnimation = hunterAnimation;
        } else if (roleState == RoleState.PREY) {
            currentAnimation = originalAnimation;
        }
    }

    public void roleStateExchange(Player player) {
        roleState = player.roleStateBeforeBump;
        player.roleState = roleStateBeforeBump;
        roleStateBeforeBump = roleState;
        player.roleStateBeforeBump = player.roleState;
    }

    public void chooseTransformObject(Transformation transformation) {
        storedTransformAnimation = new Animation(transformation.getImgArrAndType());
    }

    public void transform() {
        if (storedTransformAnimation == null ||
                !canTransform ||
                roleState == RoleState.HUNTER ||
                roleState == RoleState.BUMPING) {
            return;
        }
        AudioResourceController.getInstance().play(new Path().sound().background().transform());
        currentAnimation = storedTransformAnimation;
        transformCD.play();
        if (!transformTime.count()) {
            transformTime.stop();
        }
        transformTime.play();
        canTransform = false;
    }

    public void outrage() {
        if (!canOutrage || roleState != RoleState.HUNTER) {
            return;
        }
        AudioResourceController.getInstance().play(new Path().sound().background().outrage());
        currentSpeed = movement.getSpeed();
        movement.setSpeed(9);
        isInOutrage = true;
        startOutrage = true;
        outRageCD.play();
        outRageTime.play();
        canOutrage = false;
        currentAnimation = outrageAnimation;
    }

    public void outrageUpdate() {
        if (outRageTime.count()) {
            isInOutrage = false;
            getMovement().setSpeed(getCurrentSpeed());
            if (roleState == RoleState.HUNTER) {
                currentAnimation = hunterAnimation;
            } else if (roleState == RoleState.PREY) {
                currentAnimation = originalAnimation;
            }
        }
        if (outRageCD.count()) {
            canOutrage = true;
        }
    }

    protected void transformResetUpdate() {
        if (transformCD.count()) {
            canTransform = true;
        }
        if (transformTime.count()) {
            animationUpdate();
        }
        if (teleDelay.count()) {
            if (roleState == RoleState.HUNTER) {
                currentAnimation = hunterAnimation;
            } else {
                currentAnimation = originalAnimation;
            }
        }
    }


    /**
     * 每秒增加1積分，若在扣分區，每秒扣2積分
     */
    protected void addPoint() {
        if (roleState == RoleState.HUNTER) {
            return;
        }
        pointDelay.play();
        if (pointDelay.count()) {
            if (isInClosedArea) {
                if (point > 0) {
                    point -= 1;
                }
            } else if (movingState == MovingState.WALK) {
                point++;
            }
        }
    }

    public void clickedTeleportation() {
        if (canUseTeleportation) {
            isUseTeleportation = true;
            if (ClientClass.getInstance().getID() == id) {
                Global.mouse.setImg(Mouse.teleportationMouse);
            }
        }
    }

    public void useTeleportation(int x, int y) {
        if (!isUseTeleportation || !canUseTeleportation) {
            return;
        }
        AudioResourceController.getInstance().play(new Path().sound().background().teleportation());
        currentAnimation = teleAnimation;
        setXY(x, y);
        isUseTeleportation = false;
        canUseTeleportation = false;
        Global.mouse.setImg(Mouse.magicWand);
        teleDelay.play();
    }


    public void collideProps(Props props) {
        switch (props.getPropsType()) {
            case addSpeed:
                if (Global.IS_DEBUG) {
                    System.out.println("加速");
                }
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                if (movement.getSpeed() < Global.SPEED_MAX) {
                    movement.addSpeed(1);
                }
                break;

            case teleportation:
                if (Global.IS_DEBUG) {
                    System.out.println("瞬移");
                }
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                canUseTeleportation = true;
                break;
            case trap:
                if (Global.IS_DEBUG) {
                    System.out.println("不能動");
                }
                AudioResourceController.getInstance().play(new Path().sound().background().trap());
                canMove = false;
                trapDelay.play();
                break;
            default:
                if (Global.IS_DEBUG) {
                    System.out.println("加分");
                }
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                point += 10;
        }
    }

    public void namePaint(Graphics g) {
        if (roleState == RoleState.HUNTER) {
            nameLabel.setColor(Color.RED);
        } else {
            nameLabel.setColor(Color.BLACK);
        }
        if (currentAnimation == hunterAnimation || currentAnimation == originalAnimation) {
            nameLabel.setXY(painter().getX() + 10, painter().getY());
            nameLabel.paint(g);
        }
    }

    public void collidePropsInSurvivalMode(Props props) {
        switch (props.getPropsType()) {
            case addSpeed:
                if (Global.IS_DEBUG)
                    System.out.println("加速");
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                if (isSuperStar) {
                    currentSpeed++;
                }
                if (movement.getSpeed() < Global.SPEED_MAX) {
                    movement.addSpeed(1);
                }
                break;

            case teleportation:
                if (Global.IS_DEBUG)
                    System.out.println("瞬移");
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                canUseTeleportation = true;
                break;

            case trap:
                if (Global.IS_DEBUG)
                    System.out.println("不能動");
                AudioResourceController.getInstance().play(new Path().sound().background().trap());
                canMove = false;
                trapDelay.play();
                break;
            case thunder:
                if (Global.IS_DEBUG)
                    System.out.println("打雷");
                AudioResourceController.getInstance().play(new Path().sound().background().thunder());
                isThunder = true;
                break;
            case superStar:
                if (Global.IS_DEBUG)
                    System.out.println("超級星星");
                AudioResourceController.getInstance().stop(new Path().sound().background().superstar());
                AudioResourceController.getInstance().play(new Path().sound().background().superstar());
                superStarDelay.stop();
                superStarDelay.play();
                isSuperStar = true;
                currentSpeed = movement.getSpeed();
                movement.setSpeed(Global.SPEED_MAX);
                break;
            case timeStop:
                if (Global.IS_DEBUG)
                    System.out.println("獵人時間暫停");
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                isHunterStop = true;
                break;
            case hunterWatcher:
                if (Global.IS_DEBUG) {
                    System.out.println("透視");
                }
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                hunterWatcherDelay.play();
                isHunterWatcher = true;
                break;
            default:
                if (Global.IS_DEBUG)
                    System.out.println("遊戲時間減少");
                AudioResourceController.getInstance().play(new Path().sound().background().addSpeed());
                isDecreaseGameTime = true;

        }
    }

    public void propsEffectUpdate() {
        if (superStarDelay.count()) {
            isSuperStar = false;
            AudioResourceController.getInstance().stop(new Path().sound().background().superstar());
            movement.setSpeed(currentSpeed);
        }
        if (hunterWatcherDelay.count()) {
            isHunterWatcher = false;
        }
    }

    public MovingState getMovingState() {
        return movingState;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    private void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public Animation getStoredTransformAnimation() {
        return storedTransformAnimation;
    }

    public boolean isUseTeleportation() {
        return isUseTeleportation;
    }

    public boolean isCanUseTeleportation() {
        return canUseTeleportation;
    }

    public int transformCDTime() {
        return 10 - transformCD.getCount() / 60;
    }

    public Global.MapAreaType getPositionType() {
        if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH / 2 && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT / 2) {
            return Global.MapAreaType.FOREST;
        } else if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH / 2 && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT) {
            return Global.MapAreaType.ICEFIELD;
        } else if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT / 2) {
            return Global.MapAreaType.VOLCANO;
        } else {
            return Global.MapAreaType.VILLAGE;
        }
    }

    public Global.MapAreaType getTransformationAnimationType() {
        return currentAnimation.getMapAreaType();
    }

    public void setInClosedArea(boolean inClosedArea) {
        isInClosedArea = inClosedArea;
    }

    public boolean isInClosedArea() {
        return isInClosedArea;

    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getX() {
        return painter().getX();
    }

    public int getY() {
        return painter().getY();
    }

//    public void sentPositionUpdate() {
//        ClientClass.getInstance().sent(Pact.PLAYER_POSITION, bale(String.valueOf(painter().getX()), String.valueOf(painter().getY())));
//    }

    public int ID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public Movement getMovement() {
        return movement;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Animation getOriginalAnimation() {
        return originalAnimation;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public int getOutrageCd() {
        return 20 - outRageCD.getCount() / 60;
    }

    public void setRoleState(RoleState inputRoleState) {
        this.roleState = inputRoleState;
    }

    public void setRoleStateBeforeBump(RoleState roleStateBeforeBump) {
        this.roleStateBeforeBump = roleStateBeforeBump;
    }

}
