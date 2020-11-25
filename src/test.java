import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.*;

public class test {
    private static final long serialVersionUID = 1L;

    private static void matchTable() {
        // Create and set up the window.
        final JFrame frame = new JFrame("Match table");
        // Display the window.
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set flow layout for the frame
        frame.getContentPane().setLayout(new FlowLayout());

        JTextArea textArea = new JTextArea(20, 20);
        DefaultListModel<String> l1 = new DefaultListModel<>();
        l1.addElement("Item1 ");
        l1.addElement("Item2 ");
        l1.addElement("Item3 ");
        l1.addElement("Item4 ");
        l1.addElement("Item4 ");
        l1.addElement("Item4        ");
        l1.addElement("Item4 ");
        l1.addElement("Item4 ");
        l1.addElement("Item4 ");
        l1.addElement("Item4 ");
        l1.addElement("Item4 ");

        JList<String> list = new JList<>(l1);
        list.setBounds(100,100, 200,75);
        JScrollPane scrollableTextArea = new JScrollPane(list);

        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(scrollableTextArea);
    }
    public static void main(String[] args) {


        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                matchTable();
            }
        });
    }
}