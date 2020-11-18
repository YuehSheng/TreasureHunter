//*******************************************************************
//*  Network Programming - Unit 6 Remote Method Invocation          *
//*  Program Name: CalculatorRMIClient                              *
//*  The program is a RMI client.                                   *
//*  Usage: java CalculatorRMIClient op num1 num2,                  *
//*         op = add, sub, mul, div                                 *
//*  2014.02.26                                                     *
//*******************************************************************
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.*;
import java.util.*;
import java.awt.Color;

public class Client
{
	public static ArrayList<JButton> b = new ArrayList<>();
	public static JLabel label = new JLabel();
	static boolean login = false;
	public static void main (String[] args)
	{
		RMIInterface o = null;
		Scanner scanner = new Scanner(System.in);

		try
		{
			o = (RMIInterface) Naming.lookup("rmi://127.0.0.1/TreasureHunter");
			System.out.println("RMI server connected");
		}
		catch(Exception e)
		{
			System.out.println("Server lookup exception: " + e.getMessage());
		}

		try {
			int width = 600,height = 600;
			JFrame f=new JFrame("Treasure Hunter");
			f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			final JLabel label = new JLabel();
			label.setBounds(20,150, 200,50);
			final JPasswordField value = new JPasswordField();
			value.setBounds(100,75,100,30);
			JLabel l1=new JLabel("Username:");
			l1.setBounds(20,20, 80,30);
			JLabel l2=new JLabel("Password:");
			l2.setBounds(20,75, 80,30);
			JButton login_b = new JButton("Login");
			login_b.setBounds(100,120, 80,30);
			final JTextField text = new JTextField();
			text.setBounds(100,20, 100,30);
			f.add(value); f.add(l1); f.add(label); f.add(l2); f.add(login_b); f.add(text);
			f.setSize(300,300);
			f.setLayout(null);
			f.setVisible(true);
			login_b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String data = "Username " + text.getText();
					data += ", Password: "
									+ new String(value.getPassword());
					label.setText(data);
					/*till get server confirm */
					login = true;
				}
			});

			while(!login){ Thread.sleep(1000); }
			f.setVisible(false);
			f=new JFrame("Treasure Hunter");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JFrame round =new JFrame("Round");
			JLabel land =  new JLabel("Choose a spot to land");
			land.setBounds(225,50, 150,20);
			for(int j = 80;j < height-100;j+=20) {
				for (int i = 80; i < width - 100; i += 20) {
					JButton button = new JButton();
					button.setBackground(new Color(200, 238, 200));
					button.setBounds(i, j, 20, 20);
					b.add(button);
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String[] target = e.getSource().toString().split(",");
							land.setText(target[1] + " " + target[2]);
							int x = Integer.parseInt(target[1]);
							int y = Integer.parseInt(target[2]);
							for (JButton button : b) {
								if (button.getX() == x && button.getY() == y) {
									button.setBackground(new Color(0, 20, 240));
								} else {
									button.setBackground(new Color(200, 238, 200));
								}
								button.setEnabled(false);
							}
						}
					});
				}
			}

			for(JButton button : b){
				f.add(button);
			}
			f.add(land);
			f.setSize(width,height);
			f.setLayout(null);
			f.setVisible(true);
		}
		catch(Exception e)
		{
			System.out.println("exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}