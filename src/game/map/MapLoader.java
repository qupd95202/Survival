package game.map;

import game.core.Global;
import game.gameObj.GameObject;
import game.gameObj.mapObj.MapObject;

import java.io.IOException;
import java.util.ArrayList;


public class MapLoader {

    private final ArrayList<int[][]> mapArr;// bmp圖檔陣列
    private final ArrayList<String[]> txtArr;

    public MapLoader(String MapPath, String txtPath) throws IOException { //txt檔名  bmp檔名
        mapArr = new ReadBmp().readBmp(MapPath);
        txtArr = new ReadFile().readFile(txtPath);
    }

    /**
     * 地圖資料(整合bmp & txt)
     * @return 地圖資料（MapInfo類）的物件陣列
     */
    public ArrayList<MapInfo> combineInfo() {
        ArrayList<MapInfo> result = new ArrayList();

        for (int i = 0; i < mapArr.size(); i++) {
            for (int j = 0; j < txtArr.size(); j++) {

                //因為創建地圖時 每一格是用所選色號編碼存入
                //所以當相等時 將txt檔內 除色號外 其他資料存成一個MapInfo物件
                if (mapArr.get(i)[1][0] == Integer.parseInt(txtArr.get(j)[1])) {

                    MapInfo tmpMap = new MapInfo(
                            txtArr.get(j)[0], //txt內的 檔名
                            mapArr.get(i)[0][0],
                            mapArr.get(i)[0][1],
                            Integer.parseInt(txtArr.get(j)[2]),//txt內的 路徑
                            Integer.parseInt(txtArr.get(j)[3]));
                    result.add(tmpMap);
                }
            }
        }
        return result;
    }

    /**
     * 透過地圖資訊 轉換為 自定義的地圖物件
     * @param mapInfos 地圖資訊
     * @return 地圖資料（自定義類Tile）的物件陣列
     */
    public ArrayList<MapObject> createObjectArray(ArrayList<MapInfo> mapInfos) {
        // 創建陣列儲存地圖物件
        ArrayList<MapObject> mapMapObjects = new ArrayList<>();

        // 根據地圖資訊創建地圖物件
        for (MapInfo mapInfo : mapInfos) {
            if (mapInfo.getName().equals("tree1")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        100,
                        150,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("tree2")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        100,
                        150,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("winterTree1")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        100,
                        130,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("winterTree2")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        100,
                        200,
                        mapInfo.getName()));
            }

            if (mapInfo.getName().equals("rock")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        60,
                        60,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("volcanoTree")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        70,
                        100,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("castleWall")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        50,
                        50,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("tower")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH - 7,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        50,
                        100,
                        mapInfo.getName()));
            }
            if (mapInfo.getName().equals("house1")) {
                mapMapObjects.add(new MapObject(
                        mapInfo.getX() * Global.UNIT_WIDTH,
                        mapInfo.getY() * Global.UNIT_HEIGHT,
                        100,
                        100,
                        mapInfo.getName()));
            }

        }
        return mapMapObjects;
    }

}
