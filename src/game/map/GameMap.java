package game.map;

import game.core.Global;
import game.gameObj.GameObject;
import game.gameObj.mapObj.MapObject;
import game.utils.Path;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GameMap {

    private MapObject[][] mapMapObjects;

    private MapLoader mapLoader;
    private static ArrayList<MapInfo> mapInfos;
    private static ArrayList<MapObject> mapObjects;

    public GameMap(int width, int height, String MapPath, String txtPath) {
        mapMapObjects = new MapObject[width][height];
        initialzeMapTiles(MapPath, txtPath);
    }

    private void initialzeMapTiles(String MapPath, String txtPath) {
        try {
            mapLoader = new MapLoader(
                    MapPath,
                    txtPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapInfos = mapLoader.combineInfo();
        mapObjects = mapLoader.createObjectArray(mapInfos);
    }

    public void paint(Graphics g) {
        for (GameObject mapObject : mapObjects) {
            mapObject.paintComponent(g);
        }
    }

    public void update() {

    }

    //整張地圖像素寬
    public int getWidth() {
        return mapMapObjects.length * Global.UNIT_WIDTH; //陣列數量 ＊ 圖的大小(像素)
    }

    //整張地圖像素高
    public int getHeight() {
        return mapMapObjects[0].length * Global.UNIT_HEIGHT; //陣列數量 ＊ 圖的大小(像素)

    }

    public  ArrayList<MapObject> getMapObjects() {
        return mapObjects;
    }
}
