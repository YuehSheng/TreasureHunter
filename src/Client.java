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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.rmi.*;
import java.util.*;
import java.awt.Color;

public class Client
{
	public static ArrayList<JButton> b = new ArrayList<>();
	public static JLabel label = new JLabel();
	public static JScrollPane scrollableList;
	static boolean login = false;
	static boolean match = false;
	static boolean wait = false;
	static int sight = 3;
	static boolean turn = false;
	static String acc;
	static OutputStream out;
	static InputStream in;
	public static void map_enable(boolean bool){
		for (JButton button : b) {
			button.setEnabled(bool);
		}
	}

	public static byte[] send(int mode,String data) throws IOException {
		byte[] b = new byte[4];
		ByteBuffer.wrap(b).putInt(0,mode);
		out.write(b);
		b = new byte[100];
		b = data.getBytes();
		out.write(b);
		b = new byte[4];
		in.read(b);
		return b;
	}

	public static void sendMode(int mode) throws IOException {
		byte[] b = new byte[4];
		ByteBuffer.wrap(b).putInt(0,mode);
		out.write(b);
		out.write("1".getBytes());
	}

	public static ArrayList<String> refresh() throws IOException {
		ArrayList<String> strings = new ArrayList<>();
		sendMode(3);
		byte[] b = new byte[4];
		in.read(b);
		int num = ByteBuffer.wrap(b,0,4).getInt(0);
		for(int i=0;i < num;i++){
			b = new byte[100];
			in.read(b);
			String str = new String(b).trim();
			strings.add(str);
		}
		return strings;
	}

	private static void game() throws IOException {
//		int width = 1300,height = 1000;
//		JFrame game = new JFrame("Treasure Hunter");
//		game.setVisible(false);
//		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		JLabel land =  new JLabel("Choose a spot to land");
//		land.setBounds(width/2-110,30, 150,20);
//		JLabel turn =  new JLabel("Your turn");
//		turn.setBounds(1000,30, 150,20);
//		JButton Move = new JButton("Move");
//		JButton Search = new JButton("Search");
//		JButton Props = new JButton("Props");
//		JButton Dig = new JButton("Dig");
//		Move.setBounds(1000, height / 5, 200, 50);
//		JLabel searchMsg =  new JLabel("Get roughly direction of treasure");
//		searchMsg.setBounds(1000,height / 5*2 - 40, 200, 50);
//
//		Search.setBounds(1000, height / 5*2, 200, 50);
//		Props.setBounds(1000, height / 5*3, 200, 50);
//		Dig.setBounds(1000, height / 5*4, 200, 50);
//		JLabel digMsg =  new JLabel("Dig the place below player");
//		digMsg.setBounds(1000,height / 5*4 - 40, 200, 50);
//		Move.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				map_enable(true);
//			}
//		});
//
//		Search.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				/*search for treasure*/
//
//			}
//		});
//
//		Dig.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				/*dig the place below player*/
//
//			}
//		});
//
//		Props.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				map_enable(false);
//			}
//		});
//
//		for(int j = 60;j < height-100;j+=20) {
//			for (int i = 60; i < width - 400; i += 20) {
//				JButton button = new JButton();
//				button.setBackground(new Color(80, 150, 100));
//				button.setBounds(i, j, 20, 20);
//				b.add(button);
//				button.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						String[] target = e.getSource().toString().split(",");
//						land.setText(target[1] + " " + target[2]);
//						int x = Integer.parseInt(target[1]);
//						int y = Integer.parseInt(target[2]);
//						for (JButton button : b) {
//							int bx = button.getX();
//							int by = button.getY();
//							if (bx == x && by == y) {
//								button.setBackground(new Color(0, 150, 240));
//							}
//							else if(Math.abs(x-bx) < sight*20){
//								if(Math.abs(y-by) < sight*20)
//									button.setBackground(new Color(200, 200, 200));
//								else
//									button.setBackground(new Color(80, 150, 100));
//							}
//							else
//								button.setBackground(new Color(80, 150, 100));
//							button.setEnabled(false);
//						}
//					}
//				});
//			}
//		}
//
//		for(JButton button : b){
//			game.add(button);
//		}
//		game.add(digMsg);
//		game.add(searchMsg);
//		game.add(Move);
//		game.add(Search);
//		game.add(Dig);
//		game.add(Props);
//		game.add(land);
//		game.add(turn);
//		game.setSize(width,height);
//		game.setLayout(null);
//		game.setLocationRelativeTo(null);
//		game.setVisible(true);
	}

	/*private static void matchTable() throws IOException, InterruptedException {
		JFrame matchTable = new JFrame("Match table");
		matchTable.setSize(400,500);
		matchTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		matchTable.getContentPane().setLayout(null);
		JTextField text = new JTextField();
		text.setBounds(50,50, 100,30);
		JTextArea textArea = new JTextArea(20, 20);



		//get tables from server

		DefaultListModel<String> l1 = new DefaultListModel<>();
		for(String s : refresh()){
			l1.addElement(s);
		}
		JList<String> list = new JList<>(l1);
		list.setBounds(100,100, 200,75);
		scrollableList = new JScrollPane(list);
		scrollableList.setBounds(50,150,100,180);
		scrollableList.setSize(100,210);
		scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JButton refresh = new JButton("Refresh");
		refresh.setBounds(200,150,100,100);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					matchTable.getContentPane().remove(scrollableList);
					DefaultListModel<String> l1 = new DefaultListModel<>();
					for(String s : refresh()){
						l1.addElement(s);
					}
					JList<String> list = new JList<>(l1);
					list.setBounds(100,100, 200,75);
					scrollableList = new JScrollPane(list);
					scrollableList.setBounds(50,150,100,180);
					scrollableList.setSize(100,210);
					scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					matchTable.getContentPane().add(scrollableList);
					matchTable.invalidate();
					matchTable.validate();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		JButton join = new JButton("Join");
		join.setBounds(200,260,100,100);
		join.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JList l = (JList) scrollableList.getViewport().getView();//get new list
				String sel = l.getSelectedValue().toString();
				System.out.println("select "+sel);
				try {
					byte[] buf = send(2,sel);

					int result = ByteBuffer.wrap(buf,0,4).getInt(0);
					if(result == 1){
						System.out.println("join");
						match = true;
						matchTable.setVisible(false);
					}
					else {
						System.out.println("fail");
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		JButton create = new JButton("Create");
		create.setBounds(200,50,100,30);
    create.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
				try {
					String name = text.getText();
					if(name.equals("")){
						return;
					}
					System.out.println(name);
					byte[] buf = send(1,name);
					int result = ByteBuffer.wrap(buf,0,4).getInt(0);
					if(result == 1){
						System.out.println("Create success");

						JFrame waiting = new JFrame("waiting");
						JPanel p1 = new JPanel(new BorderLayout());
						p1.add(new JLabel("Waiting for the hunter..."), BorderLayout.SOUTH);
						JButton button = new JButton("Exit");
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								matchTable.setEnabled(true);
								waiting.setEnabled(false);
								waiting.setVisible(false);
								matchTable.remove(waiting);
//								exit
								try {
									sendMode(4);
								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
						});
						waiting.setUndecorated(true);
						waiting.getContentPane().add(p1);
						waiting.getContentPane().add(button,BorderLayout.SOUTH);
						waiting.pack();
						waiting.setLocationRelativeTo(matchTable);
						waiting.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						waiting.setVisible(true);
						matchTable.setEnabled(false);
						wait = true;
//						System.out.println(in.available());
					}
					else{
						System.out.println("Create fail");
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
    });

		matchTable.add(join);
		matchTable.add(create);
		matchTable.add(text);
		matchTable.getContentPane().add(scrollableList);
		matchTable.add(refresh);
		matchTable.setVisible(true);
		while (true){
			if(wait){
				if(in.available() > 0){
					byte[] buf = new byte[4];
					in.read(buf);
				}
				System.out.println("waiting");
			}
			Thread.sleep(100);
		}
	}*/

	public static void main (String[] args)
	{
		try {
			Socket sc = new Socket("127.0.0.1",6666);
			out = sc.getOutputStream();
			in = sc.getInputStream();
			JFrame f=new JFrame("Treasure Hunter");
			f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			final JLabel label = new JLabel();
			label.setBounds(20,150, 200,50);
			final JPasswordField value = new JPasswordField();
			value.setBounds(100,75,100,30);
			JLabel name=new JLabel("Username:");
			name.setBounds(20,20, 80,30);
			JLabel l2=new JLabel("Password:");
			l2.setBounds(20,75, 80,30);
			JButton login_b = new JButton("Login / Register");
			login_b.setBounds(40,120, 200,30);
			final JTextField text = new JTextField();
			text.setBounds(100,20, 100,30);
			f.add(value); f.add(name); f.add(label); f.add(l2); f.add(login_b); f.add(text);
			f.setSize(300,300);
			f.setLayout(null);
			f.setLocationRelativeTo(null);
			f.setVisible(true);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JFrame error = new JFrame("");
			error.setSize(250,250);
			error.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			JLabel msg1 = new JLabel("Wrong password or this account");
			JLabel msg2 = new JLabel("has been registered.");
			JLabel msg3 = new JLabel("Please try again");
			msg1.setBounds(20,50,250,20);
			msg2.setBounds(53,70,250,20);
			msg3.setBounds(67,100,200,80);
			JButton ok = new JButton("OK");
			ok.setBounds(75,150,80,40);
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					error.setVisible(false);
				}
			});
			error.add(msg1);
			error.add(msg2);
			error.add(msg3);
			error.add(ok);
			error.setLayout(null);
			error.setLocationRelativeTo(null);
			error.setVisible(false);

			login_b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String account = text.getText();
					String pass = new String(value.getPassword());
					if(account.equals("") ||pass.equals("")){
						return;
					}
					String data = account+" "+pass;
					label.setText(data);
					try {
						int result = ByteBuffer.wrap(send(0,data)).getInt(0);
						System.out.println(result);
						if(result == 1 ||result == 2){
						  acc = account;
							login = true;
						}
						else{
							error.setVisible(true);
						}
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			});
			/*Login*/
			while(!login){ Thread.sleep(1000); }
			f.setVisible(false);
			match = false;
			/*match*/
			/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						matchTable();
						return;
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			});*/
			JFrame matchTable = new JFrame("Match table");
			matchTable.setSize(400,500);
			matchTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			matchTable.getContentPane().setLayout(null);
			final JTextField rName = new JTextField();
			rName.setBounds(50,50, 100,30);
			JFrame waiting = new JFrame("waiting");
//			JTextArea textArea = new JTextArea(20, 20);


		/*
		get tables from server
		*/
			DefaultListModel<String> l1 = new DefaultListModel<>();
			for(String s : refresh()){
				l1.addElement(s);
			}
			JList<String> list = new JList<>(l1);
			list.setBounds(100,100, 200,75);
			scrollableList = new JScrollPane(list);
			scrollableList.setBounds(50,150,100,180);
			scrollableList.setSize(100,210);
			scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			JButton refresh = new JButton("Refresh");
			refresh.setBounds(200,150,100,100);
			refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						matchTable.getContentPane().remove(scrollableList);
						DefaultListModel<String> l1 = new DefaultListModel<>();
						for(String s : refresh()){
							l1.addElement(s);
						}
						JList<String> list = new JList<>(l1);
						list.setBounds(100,100, 200,75);
						scrollableList = new JScrollPane(list);
						scrollableList.setBounds(50,150,100,180);
						scrollableList.setSize(100,210);
						scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
						matchTable.getContentPane().add(scrollableList);
						matchTable.invalidate();
						matchTable.validate();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});
			JButton join = new JButton("Join");
			join.setBounds(200,260,100,100);
			join.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JList l = (JList) scrollableList.getViewport().getView();//get new list
					String sel = l.getSelectedValue().toString();
					System.out.println("select "+sel);
					try {
						byte[] buf = send(2,sel);

						int result = ByteBuffer.wrap(buf,0,4).getInt(0);
						if(result == 1){
							System.out.println("join");
							match = true;
							matchTable.setVisible(false);
						}
						else {
							System.out.println("fail");
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			JButton create = new JButton("Create");
			create.setBounds(200,50,100,30);
			create.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String name = rName.getText();
						if(name.equals("")){
							return;
						}
						System.out.println(name);
						byte[] buf = send(1,name);
						int result = ByteBuffer.wrap(buf,0,4).getInt(0);
						if(result == 1){
							System.out.println("Create success");


							JPanel p1 = new JPanel(new BorderLayout());
							p1.add(new JLabel("Waiting for the hunter..."), BorderLayout.SOUTH);
							JButton button = new JButton("Exit");
							button.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									matchTable.setEnabled(true);
									waiting.setEnabled(false);
									waiting.setVisible(false);
									matchTable.remove(waiting);
									/*exit*/
									try {
										sendMode(4);
									} catch (IOException ex) {
										ex.printStackTrace();
									}
								}
							});
							waiting.setUndecorated(true);
							waiting.getContentPane().add(p1);
							waiting.getContentPane().add(button,BorderLayout.SOUTH);
							waiting.pack();
							waiting.setLocationRelativeTo(matchTable);
							waiting.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
							waiting.setVisible(true);
							matchTable.setEnabled(false);
							wait = true;
//						System.out.println(in.available());
						}
						else{
							System.out.println("Create fail");
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			matchTable.add(join);
			matchTable.add(create);
			matchTable.add(rName);
			matchTable.getContentPane().add(scrollableList);
			matchTable.add(refresh);
			matchTable.setVisible(true);
			while (true){
				if(wait){
					if(in.available() > 0){
						byte[] buf = new byte[4];
						in.read(buf);
						match = true;
						matchTable.setVisible(false);
						waiting.setVisible(false);
						System.out.println("match!");
						break;
					}
					System.out.println("waiting");
				}
				else if(match){
					matchTable.getContentPane().removeAll();
					matchTable.setVisible(false);
					System.out.println("match!");
					break;
				}
				Thread.sleep(100);
			}

			/*game*/

			int width = 1300,height = 1000;
			JFrame game = new JFrame("Treasure Hunter");
			game.setVisible(false);
			game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JLabel land =  new JLabel("Choose a spot to land");
			land.setBounds(width/2-110,30, 150,20);
			JLabel turn =  new JLabel("Your turn");
			turn.setBounds(1000,30, 150,20);
			JButton Move = new JButton("Move");
			JButton Search = new JButton("Search");
			JButton Props = new JButton("Props");
			JButton Dig = new JButton("Dig");
			Move.setBounds(1000, height / 5, 200, 50);
			JLabel searchMsg =  new JLabel("Get roughly direction of treasure");
			searchMsg.setBounds(1000,height / 5*2 - 40, 200, 50);

			Search.setBounds(1000, height / 5*2, 200, 50);
			Props.setBounds(1000, height / 5*3, 200, 50);
			Dig.setBounds(1000, height / 5*4, 200, 50);
			JLabel digMsg =  new JLabel("Dig the place below player");
			digMsg.setBounds(1000,height / 5*4 - 40, 200, 50);
			Move.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					map_enable(true);
				}
			});

			Search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/*search for treasure*/

				}
			});

			Dig.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/*dig the place below player*/

				}
			});

			Props.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					map_enable(false);
				}
			});

			for(int j = 60;j < height-100;j+=20) {
				for (int i = 60; i < width - 400; i += 20) {
					JButton button = new JButton();
					button.setBackground(new Color(80, 150, 100));
					button.setBounds(i, j, 20, 20);
					b.add(button);
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String[] target = e.getSource().toString().split(",");
							land.setText(target[1] + " " + target[2]);
							int x = Integer.parseInt(target[1]);
							int y = Integer.parseInt(target[2]);
							for (JButton button : b) {
								int bx = button.getX();
								int by = button.getY();
								if (bx == x && by == y) {
									button.setBackground(new Color(0, 150, 240));
								}
								else if(Math.abs(x-bx) < sight*20){
									if(Math.abs(y-by) < sight*20)
										button.setBackground(new Color(200, 200, 200));
									else
										button.setBackground(new Color(80, 150, 100));
								}
								else
									button.setBackground(new Color(80, 150, 100));
								button.setEnabled(false);
							}
						}
					});
				}
			}

			for(JButton button : b){
				game.add(button);
			}
			game.add(digMsg);
			game.add(searchMsg);
			game.add(Move);
			game.add(Search);
			game.add(Dig);
			game.add(Props);
			game.add(land);
			game.add(turn);
			game.setSize(width,height);
			game.setLayout(null);
			game.setLocationRelativeTo(null);
			game.setVisible(true);
			if(false){
				sc.close();
			}
		}
		catch(Exception e)
		{
			System.out.println("exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}