package game.Menu;

import game.core.Global;
import game.utils.CommandSolver;
import game.utils.Delay;

import java.awt.*;
import java.awt.event.KeyEvent;

public class EditText extends Label implements CommandSolver.KeyListener {

    private String editText;
    private int editLimit;

    private String hintString;
    private Boolean isEditable;
    private Delay delay;
    private int size;


    public EditText(int x, int y,String hintString) {
        super(x, y,"");
        this.hintString = hintString;
        isEditable =false;
        delay =new Delay(Global.UPDATE_TIMES_PER_SEC / 2);;
        delay.loop();
        editLimit=15;
        editText="";
        this.size=size;
    }
    public void setEditLimit(int n) {
        editLimit = n;
    }
    private boolean isOverEditLimit() {
        return !(editLimit == -1 || editText.length() < editLimit);
    }
    private boolean isexcepion(char c) {
        if (c >= 33 && c <= 47
                || c >= 48 && c <= 64
                || c >= 91 && c <= 96
                || c >= 123 && c < 126) {
            return true;
        }
        return false;
    }

    private boolean isLegalRange(char c) {
        if (c >= 30 && c <= 39) {
            return true;
        }
        if (c >= 65 && c <= 90) {
            return true;
        }
        return c >= 97 && c <= 122;
    }


    @Override
    public void paintComponent(Graphics g) {
        if(isEditable && editText==""){
            g.setColor(Color.black);
            g.drawString(hintString,painter().left(),painter().top());
            if(!delay.count()) {
                g.drawString("|", painter().left() - 5, painter().top() - 2);
            }
        }else {
            g.setColor(Color.black);
            Font font=new Font("", Font.BOLD,20);
            g.setFont(font);
            g.drawString(editText,painter().left(),painter().top());
        }
    }


    @Override
    public void update() {

    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {

    }

    @Override
    public void keyReleased(int commandCode, long trigTime) {

    }

    @Override
    public void keyTyped(char c, long trigTime) {
        if (isEditable) {
            if (isexcepion(c)) {
                editText = editText + c;
            }else if (c == KeyEvent.VK_BACK_SPACE) {
                if (editText.length() > 0) {
                    editText = editText.substring(0, editText.length() - 1);
                }
            } else if (!isOverEditLimit() && isLegalRange(c) && Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                editText = editText + c;
            } else if (!isOverEditLimit() && c >= 65 && c <= 90) {
                editText = editText + (char) (c + 32);
            }
            System.out.println(editText);
        }
        ;
    }

    public void setIsEditable(Boolean inputIsEdited){
        isEditable =inputIsEdited;
    }
    public Boolean getIsEditable(){
        return isEditable;
    }
    public String getEditText(){
        return editText;
    }

}
