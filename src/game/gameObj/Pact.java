package game.gameObj;

import java.util.ArrayList;

public class Pact {
    public static final int DISCONNECT = -1;
    public static final int CONNECT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    public static final int LEFT = 4;
    public static final int RIGHT = 5;
    public static final int RELEASE_UP = 12;
    public static final int RELEASE_DOWN = 13;
    public static final int RELEASE_LEFT = 14;
    public static final int RELEASE_RIGHT = 15;
    public static final int TRANSFORM = 6;
    public static final int TELEPORTATION = 7;
    public static final int UPDATE_POSITION = 8;
    public static final int START_GAME = 9;
    public static final int COMPUTER_MOVE = 10;
    public static final int COMPUTER_UPDATE_POSITION = 11;
    public static final int CHOOSE_STORED_ANIMATION = 16;
    public static final int COMPUTER_TRANSFORM = 17;
    public static final int PROPS_GEN = 18;
    public static final int COMPUTER_WHOISNEAR = 20;
    public static final int COMPUTER_MAINPLAYER_WHOISNEAR = 21;
    public static final int COMPUTER_WHICHISNEAR = 22;
    public static final int PLAYER_COLLISION_PLAYER = 23;
    public static final int PLAYER_COLLISION_COMPUTER = 24;
    public static final int COMPUTER_COLLISION_COMPUTER = 25;
    public static final int PLAYER_COLLISION_PROPS = 26;
    public static final int COMPUTER_COLLISION_PROPS = 27;
    public static final int PLAYER_CHOOSE_TRANSFORM = 28;
    public static final int PLAYER_USE_TELEPORTATION = 29;
    public static final int POINT_UPDATE = 30;

//    public static final int PLAYER_POSITION = 19;
    //public static final int COMPUTER_CHASE 不確定
//    public static final int GET_PROPS=11;
//    public static final int ROUND_END=11;

    public static ArrayList<String> bale(String... str) {
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i = 0; i < str.length; i++) {
            tmp.add(str[i]);
        }
        return tmp;
    }
}
