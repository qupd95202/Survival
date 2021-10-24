package game.graphic;

import game.utils.Delay;

import java.awt.*;

//道具觸發時動畫 只會放一次動畫
public class PropsAnimation extends Animation {
    private Delay animationSpeedDelay;//動畫播放速度
    private Delay AnimationTime;//動畫播放多久
    private boolean playPropsAnimation; //是否播放動畫
    private int count;
    //動畫顯示位置
    private int x;
    private int y;
    private int width;
    private int height;

    public PropsAnimation(int x, int y, int width, int height, ImgArrAndType imgArrAndType, int animationSpeed, int animationTime) {
        super(imgArrAndType);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        animationSpeedDelay = new Delay(animationSpeed);
        count = 0;
        AnimationTime = new Delay(animationTime);
        playPropsAnimation = false;
    }

    public void paint(Graphics g) {
        if (playPropsAnimation) {
            if (AnimationTime.getCount() == 0) {
                AnimationTime.play();
            }
            g.drawImage(img.getImageArrayList().get(count), x, y, width, height, null);

            if (AnimationTime.count()) {
                playPropsAnimation = false;
            }
        }
    }

    public void update() {
        if (playPropsAnimation) {
            if (animationSpeedDelay.count()) {
                count = ++count % img.getImageArrayList().size();
            }
        }
    }

    public boolean isPlayPropsAnimation() {
        return playPropsAnimation;
    }

    public void setPlayPropsAnimation(boolean playPropsAnimation) {
        this.playPropsAnimation = playPropsAnimation;
    }
}
