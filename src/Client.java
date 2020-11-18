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

public class Client implements ActionListener
{
	public static ArrayList<JButton> b = new ArrayList<>();
	public static TextField tf=new TextField();
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
			Client client = new Client();
			int width = 600,height = 600;
			tf.setBounds(200,50, 150,20);
			JFrame f=new JFrame("Treasure Hunter");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			for(int j = 80;j < height-100;j+=20) {
				for (int i = 80; i < width - 100; i += 20) {
					JButton button = new JButton();
					button.setBackground(new Color(200, 238, 200));
					button.setBounds(i, j, 20, 20);
					b.add(button);
					button.addActionListener(client);
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
		catch(Exception e)
		{
			System.out.println("exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public void actionPerformed(ActionEvent e){
		String[] target = e.getSource().toString().split(",");
		tf.setText(target[1]+" "+target[2]);
		int x = Integer.parseInt(target[1]);
		int y = Integer.parseInt(target[2]);
		
		for(JButton button: b){
			if(button.getX() == x&&button.getY() == y){
				button.setBackground(new Color(0, 20, 240));
			}
			else{
				button.setBackground(new Color(200, 238, 200));
			}
		}
	}
}