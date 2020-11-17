import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*public class SimpleListener extends JFrame
        implements ActionListener {
    int act = 0;     // act用來記錄按鈕被次數的變數

    public static void main(String[] args) {
        SimpleListener test = new SimpleListener();
    }

    // 用建構方法來建立元件、將元件加入視窗、顯示視窗
    public SimpleListener() {
        setTitle("Listener 示範");    // 設定視窗標題
        JButton mybutton = new JButton("換個標題");

        // 通知按鈕物件：本物件要當傾聽者
        mybutton.addActionListener(this);

        getContentPane().add(mybutton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420,140);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        act++;    // 將按鈕次數加 1

        // 將視窗標題欄改為顯示按鈕次數
        setTitle("發生 " + act + " 次按鈕事件");
    }
}*/

public class test implements ActionListener{
    public static TextField tf=new TextField();
    public static void main(String[] args) {
        test test = new test();
        int width = 600,height = 600;
        tf.setBounds(200,50, 150,20);
        JFrame f=new JFrame("Button Example");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ArrayList<JButton> b = new ArrayList<>();
        for(int j = 80;j < height-100;j+=20) {
            for (int i = 80; i < width - 100; i += 20) {
                JButton button = new JButton();
                button.setBounds(i, j, 20, 20);
                b.add(button);
                button.addActionListener(test);
            }
        }
        for(JButton button : b){
            f.add(button);
        }
        f.add(tf);
        f.setSize(width,height);
        f.setLayout(null);
        f.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        String[] target = e.getSource().toString().split(",");
        tf.setText(target[1]+" "+target[2]);
    }
}
