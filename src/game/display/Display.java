package game.display;

import game.utils.GameCenter;
import game.core.Global;
import game.utils.GameKernel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Display extends JFrame {

    public Display(int width, int height) {
        setTitle("Survival");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false); //是否可調整大小
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        getContentPane().setCursor(blankCursor);

        int[][] commands = {
                {KeyEvent.VK_A, Global.KeyCommand.LEFT.getValue()},
                {KeyEvent.VK_W, Global.KeyCommand.UP.getValue()},
                {KeyEvent.VK_S, Global.KeyCommand.DOWN.getValue()},
                {KeyEvent.VK_D, Global.KeyCommand.RIGHT.getValue()},
                {KeyEvent.VK_R, Global.KeyCommand.TRANSFORM.getValue()},
                {KeyEvent.VK_F, Global.KeyCommand.TELEPORTATION.getValue()}
        };


        GameCenter gi = new GameCenter();
        GameKernel kernel = new GameKernel.Builder(gi, Global.LIMIT_DELTA_TIME, Global.NANOSECOUND_PER_UPDATE)
                .initListener(commands)
                .enableKeyboardTrack(gi)
                .keyCleanMode()
                .enableMouseTrack(gi)
                .trackChar()
                .gen();

        add(kernel);
        setVisible(true);
        kernel.run(Global.IS_DEBUG);

    }
}
