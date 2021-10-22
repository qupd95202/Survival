package game.graphic;

import game.core.Global;
import game.utils.Delay;
import java.awt.*;


public class Animation {

    protected ImgArrAndType img;
    private int count;
    private Delay delay;

    public Animation(ImgArrAndType imgArrAndType) {
        img = imgArrAndType;
        delay = new Delay(30);
        delay.loop();
        count = 0;
    }


    public void paint(int x, int y, int width, int height, Graphics g) {
        g.drawImage(img.getImageArrayList().get(count), x, y, width, height, null);
    }

    public void update() {
        if (delay.count()) {
            count = ++count % img.getImageArrayList().size();
        }
    }

    public Global.MapAreaType getMapAreaType() {
        return img.getMapAreaType();
    }

    public void setImg(ImgArrAndType img) {
        this.img = img;
    }

    public ImgArrAndType getImg() {
        return img;
    }

    //自訂動畫速度（數字越小越快）
    public void setDelay(int animationSpeed) {
        this.delay = new Delay(animationSpeed);
        delay.loop();
    }
}