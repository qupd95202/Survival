package game.core;

import game.controllers.SceneController;
import game.utils.Path;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Global {

    public enum KeyCommand {
        UP(2),
        DOWN(3),
        LEFT(4),
        RIGHT(5),
        TRANSFORM(6),
        TELEPORTATION(7);


        private int value;

        KeyCommand(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

//        public static KeyCommand fromMovementChangeDirection(Movement movement) {
//            int x = movement.getVector2D().getX();
//            int y = movement.getVector2D().getY();
//            if (x == 0 && y > 0) {
//                return DOWN;
//            }
//            if (x < 0 && y == 0) {
//                return LEFT;
//            }
//            if (x == 0 && y < 0) {
//                return UP;
//            }
//            if (x > 0 && y == 0) {
//                return RIGHT;
//            }
//            return DOWN;//預設
//        }
    }

    /**
     * 分類地圖可變物件所屬的類型
     * 為了讓可變物件移動到所不符合地圖類型時 能被檢查突兀
     */
    public enum MapAreaType {
        FOREST,//森林
        VOLCANO,//火山
        VILLAGE,//村莊
        ICEFIELD,//冰原
        NONE;
    }

    public static final boolean IS_DEBUG = true;

    public static final int UPDATE_TIMES_PER_SEC = 60;
    public static final int NANOSECOUND_PER_UPDATE = 1000000000 / UPDATE_TIMES_PER_SEC;

    public static final int FRAME_LIMIT = 60;
    public static final int LIMIT_DELTA_TIME = 1000000000 / FRAME_LIMIT;

    //視窗大小
    public static final int WINDOW_WIDTH = 1100;
    public static final int WINDOW_HEIGHT = 700;
    public static final int SCREEN_X = WINDOW_WIDTH - 8 - 8;
    public static final int SCREEN_Y = WINDOW_HEIGHT - 31 - 8;

    //角色大小
    public static final int PLAYER_WIDTH = 64;
    public static final int PLAYER_HEIGHT = 64;

    //路障大小
    public static final int OBSTACLE_WIDTH = 64;
    public static final int OBSTACLE_HEIGHT = 64;

    //素材單位大小
    public static final int UNIT_WIDTH = 48;
    public static final int UNIT_HEIGHT = 48;

    //遊戲地圖大小(格數)
    public static final int MAP_WIDTH = 80;
    public static final int MAP_HEIGHT = 80;
    //遊戲地圖大小(像素大小)
    public static final int MAP_PIXEL_WIDTH = MAP_WIDTH * UNIT_WIDTH;
    public static final int MAP_PIXEL_HEIGHT = MAP_HEIGHT * UNIT_HEIGHT;

    //角色移動速度
    public static final int NORMAL_SPEED = 2;
    public static final int COMPUTER_SPEED1 = 1;
    public static final int COMPUTER_SPEED2 = 3;
    public static final int COMPUTER_SPEED3 = 7;

    //電腦追蹤移動獵物範圍
    public static final int COMPUTER_CHASE_DISTANCE1 = 300;
    public static final int COMPUTER_CHASE_DISTANCE2 = 700;
    public static final int COMPUTER_CHASE_DISTANCE3 = 1100;

    //電腦追蹤道具範圍
    public static final int COMPUTER_PROPS_CHASE_DISTANCE1 = 200;
    public static final int COMPUTER_PROPS_CHASE_DISTANCE2 = 400;
    public static final int COMPUTER_PROPS_CHASE_DISTANCE3 = 600;


    //預設AI感知之最近距離
    public static final float NEAREST = 500f;

    //加速道具
    public static final int ADD_SPEED = 2;
    //速度上限
    public static final int SPEED_MAX = 10;
    //地圖上道具數量上限
    public static final int PROPS_AMOUNT_MAX = 6;

    //選擇遊戲時間
    public static final int CHOOSE_GAMETIME = 300;

    public static final int PROPS_AMOUNT_MAX_SURVIVAL_GAME = 10;


    //瞬間移動格的位子
    public static final int RUNNER_X = 0;
    public static final int RUNNER_Y = Global.SCREEN_Y - 100;
    public static final int GAME_SCENE_BOX_SIZE = 100;

    public static ArrayList<Image> bumpImg = new ArrayList<Image>(List.of(SceneController.getInstance().imageController().tryGetImage(
                    new Path().img().actors().bump().Bump1()),
            SceneController.getInstance().imageController().tryGetImage(
                    new Path().img().actors().bump().Bump2()),
            SceneController.getInstance().imageController().tryGetImage(
                    new Path().img().actors().bump().Bump3()),
            SceneController.getInstance().imageController().tryGetImage(
                    new Path().img().actors().bump().Bump4()),
            SceneController.getInstance().imageController().tryGetImage(
                    new Path().img().actors().bump().Bump5())));


    /**
     * 輸出範圍內的隨機數字
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static boolean getProbability(int probability) {
        return random(1, 100) < probability;
    }
}