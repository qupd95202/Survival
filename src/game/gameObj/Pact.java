package game.gameObj;

import java.util.ArrayList;

public class Pact {
    public static final int DISCONNECT=-1;
    public static final int CONNECT=1;
    public static final int UP=2;
    public static final int DOWN=3;
    public static final int LEFT=4;
    public static final int RIGHT=5;
    public static final int RELEASE_UP=12;
    public static final int RELEASE_DOWN=13;
    public static final int RELEASE_LEFT=14;
    public static final int RELEASE_RIGHT=15;
    public static final int TRANSFORM=6;
    public static final int TELEPORTATION =7;
    public static final int MOUSE_CLICK =8;
    public static final int START_GAME=9;
    public static final int UPDATE=10;
//    public static final int GET_PROPS=11;
//    public static final int ROUND_END=11;

    public static ArrayList<String> bale(String ...str){
        ArrayList<String> tmp=new ArrayList<String>();
        for(int i =0;i<str.length;i++){
            tmp.add(str[i]);
        }
        return tmp;
    }
}
