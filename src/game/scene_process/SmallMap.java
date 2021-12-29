package game.scene_process;

import game.gameObj.GameObject;
import game.gameObj.Rect;
import game.graphic.AllImages;
import game.graphic.Animation;

import java.awt.*;

public class SmallMap extends Rect {

    private final double zoomX; // 小地圖的X縮放率
    private final double zoomY; // 小地圖的Y縮放率
    private Animation smallMapBackGround;

    public SmallMap(int x, int y, int width, int height, Double inputZoomX, Double inputZoomY) {
        super(x,y,width,height);
        this.zoomX = inputZoomX;
        this.zoomY = inputZoomY;
        //小地圖背景
        smallMapBackGround=new Animation(AllImages.inputButton);
    }


    /** 將目標物件換成方格，可以自訂顏色跟大小 */
    public void paint(Graphics g, GameObject target, Color c, int width, int height) {

        Rect targetPainter = target.painter();
        g.setColor(c);
        g.fillRect(targetPainter.left(),
                targetPainter.top(),
                width,
                height);
    }


    /** 將目標物件用圖片代替，可以自訂大小 */
    public void paint(Graphics g, GameObject target, Image img, int width, int height){
        Rect targetPainter = target.painter();
        g.drawImage(img, targetPainter.left(), targetPainter.top(), width, height, null);
    }

    /** 開啟小地圖 */
    public void start(Graphics g) {
        smallMapBackGround.paint( 0,0, 190, 190,g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(zoomX, zoomY);  // 縮放畫布
    }
}

