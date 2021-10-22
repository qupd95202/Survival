package game.graphic;

import game.utils.Delay;

import java.awt.*;

//道具觸發時動畫 只會放一次動畫
public class PropsAnimation extends Animation {
    private Delay animationSpeedDelay;//動畫播放速度
    private Delay AnimationTime;//動畫播放多久
    private boolean playPropsAnimation;
    private int count;

    public PropsAnimation(ImgArrAndType imgArrAndType, int animationSpeed, int animationTime) {
        super(imgArrAndType);
        animationSpeedDelay = new Delay(animationSpeed);
        count = 0;
        AnimationTime = new Delay(animationTime);
        playPropsAnimation = false;
    }

    public void paint(int x, int y, int width, int height,Graphics g) {
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
        if (animationSpeedDelay.count()) {
            count = ++count % img.getImageArrayList().size();
        }
    }

}
