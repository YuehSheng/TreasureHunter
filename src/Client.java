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
  static boolean near = false;
  static int sight = 3;
  static boolean running = false;
  static byte[] map;
  static int x = -3,y = -3;
  static int[] prop = {0,0,0,0};  // arrow wall increase decrease
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
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
    out.flush();
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

  public static void useProps(int item) throws IOException {
    // 0 arrow, 1 wall, 2 increase, 3 decrease
    send(12,String.valueOf(item));
    prop[item]--;
    if(item == 2){
      sight += 2;
    }
    running = false;
  }


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
      error.setLocationRelativeTo(f);
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
      boolean end = false;
      JFrame matchTable = new JFrame("Match table");
      matchTable.setSize(400,500);
      matchTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      matchTable.getContentPane().setLayout(null);
      JButton refresh = new JButton("Refresh");
      final JTextField rName = new JTextField();
      JFrame waiting = new JFrame("waiting");
      DefaultListModel<String> l1 = new DefaultListModel<>();
      JList<String> list = new JList<>(l1);
      JButton join = new JButton("Join");
      JButton create = new JButton("Create");
      while(!end){
        match = false;
        rName.setBounds(50,50, 100,30);
        /*
        get tables from server
        */

        for(String s : refresh()){
          l1.addElement(s);
        }
        list = new JList<>(l1);
        list.setBounds(100,100, 200,75);
        scrollableList = new JScrollPane(list);
        scrollableList.setBounds(50,150,100,180);
        scrollableList.setSize(100,210);
        scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


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
        land.setBounds(width/2-200,30, 300,20);
        JLabel turn =  new JLabel("");
        turn.setBounds(1000,30, 150,20);
        JButton Move = new JButton("Move");
        JButton Search = new JButton("Search");

        JButton Dig = new JButton("Dig");
        Move.setBounds(1000, height / 5, 200, 50);


        JLabel searchMsg =  new JLabel("Get roughly direction of treasure");
        searchMsg.setBounds(1000,height / 5*2 - 40, 200, 50);
        Search.setBounds(1000, height / 5*2, 200, 50);

        JLabel propsMsg =  new JLabel("Props");
        JButton arrow = new JButton("Arrow");
        JButton wall = new JButton("Wall");
        JButton increase = new JButton("Increase");
        JButton decrease = new JButton("Decrease");
        arrow.setBounds(905, height / 5*3, 70, 50);
        wall.setBounds(985, height / 5*3, 70, 50);
        increase.setBounds(1065, height / 5*3, 100, 50);
        decrease.setBounds(1175, height / 5*3, 100, 50);

        Dig.setBounds(1000, height / 5*4, 200, 50);
        JLabel digMsg =  new JLabel("Dig the place below player");
        digMsg.setBounds(1000,height / 5*4 - 40, 200, 50);
        digMsg.setBounds(1000,height / 5*3 - 40, 200, 50);

        /*button function*/

        Move.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            map_enable(true);
          }
        });

        Search.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            /*search for treasure*/
            String data = new String(x+" "+y);
            try {
              int result = ByteBuffer.wrap(send(11,data),0,4).getInt();
              String str = "";
              switch (result){
                case 0:
                  str = "ground";
                  break;
                case 1:
                  str = "north";
                  break;
                case 2:
                  str = "south";
                  break;
                case 3:
                  str = "west";
                  break;
                case 4:
                  str = "Northwest";
                  break;
                case 5:
                  str = "Southwest";
                  break;
                case 6:
                  str = "east";
                  break;
                case 7:
                  str = "Northeast";
                  break;
                case 8:
                  str = "Southeast";
                  break;
              }
              land.setVisible(true);
              land.setText(str);
              running = false;
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          }
        });

        Dig.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            /*dig the place below player*/
            try {
              byte[] buf = send(13,x+" "+y);
              int result = ByteBuffer.wrap(buf,0,4).getInt();
              if(result == 1){
                land.setText("You Win!!");
              }
              else{
                land.setText("Not this position");
              }
              running = false;
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        });

        /* props */
        arrow.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              useProps(0);
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        });
        wall.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              useProps(1);
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        });
        increase.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              useProps(2);
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        });
        decrease.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              useProps(3);
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        });

        /* near alert */
        JFrame alert = new JFrame("Alert");
//			alert.setVisible(false);
        alert.setLayout(null);
        alert.setSize(275,200);
        alert.setLocationRelativeTo(game);
        alert.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JLabel alertMsg = new JLabel("Treasure is in your sight!");
        alertMsg.setBounds(50,50,150,50);
        alert.add(alertMsg);

        /*set map*/
        for(int j = 60;j < height-100;j+=20) {
          for (int i = 60; i < width - 400; i += 20) {
            JButton button = new JButton();
            button.setBackground(new Color(80, 150, 100));
            button.setBounds(i, j, 20, 20);
            button.setEnabled(false);
            b.add(button);
            button.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                String[] target = e.getSource().toString().split(",");//get position from e.getSource()
//							land.setText(target[1] + " " + target[2]);
                x = Integer.parseInt(target[1]);//get land x,y
                y = Integer.parseInt(target[2]);
                x = (x - 60)/20;
                y = (y - 60)/20;
                for (JButton button :b){
                  int bx = button.getX();  /*get buttons' position and set their background color*/
                  int by = button.getY();
                  bx = (bx - 60)/20;
                  by = (by - 60)/20;
                  if (bx == x && by == y) { //land spot
                    button.setBackground(new Color(0, 150, 240));  //for feedback the player
                    break;
                  }
                }
                try {
                  System.out.println(x+" "+y);
                  byte[] b = send(10,x+" "+y);
                  int item = ByteBuffer.wrap(b,0,4).getInt();
                  switch (item){
                    case 0:    	//nothing
                      break;
                    case 1:			//arrow
                      prop[0]++;
                      break;
                    case 2:			//wall
                      prop[1]++;
                      break;
                    case 3:			//increase
                      prop[2]++;
                      break;
                    case 4:			//decrease
                      prop[3]++;
                      break;
                  }
                } catch (IOException  ex) {
                  ex.printStackTrace();
                }
                running = false;
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
        game.add(propsMsg);
        game.add(arrow);
        game.add(wall);
        game.add(increase);
        game.add(decrease);
        game.add(land);
        game.add(turn);
        game.setSize(width,height);
        game.setLayout(null);
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        Move.setEnabled(false);
        arrow.setEnabled(false);
        wall.setEnabled(false);
        increase.setEnabled(false);
        decrease.setEnabled(false);
        Dig.setEnabled(false);
        Search.setEnabled(false);
        turn.setVisible(false);

        /*start to get server's order*/
        while (true){
          if(running){
            Thread.sleep(200);
          }
          else{
            for(JButton button:b){
              button.setEnabled(false);
            }
            Move.setEnabled(false);
            increase.setEnabled(false);
            decrease.setEnabled(false);
            wall.setEnabled(false);
            arrow.setEnabled(false);
            Dig.setEnabled(false);
            Search.setEnabled(false);//disable
            turn.setText("Not your turn");

            byte[] buf = new byte[10];
            in.read(buf);  		//read order
            String str = new String(buf).trim();
            System.out.println(str);

            if(str.equals("run") || str.equals("decrease") ||str.equals("defense")){
              turn.setText("Your turn");

              if(str.equals("decrease") && sight > 1){
                land.setText("Your sight is decreased!");
                sight--;
              }
              else if(str.equals("defense")){
                land.setText("You defensed an arrow!");
              }

              map = new byte[42*42];
              in.read(map); //read map
              running = true;
              turn.setVisible(true);
              Move.setEnabled(true);
              if(x >=0 &&y >= 0) {
                Dig.setEnabled(true);
                Search.setEnabled(true);  //set enable to click
              }
              if(prop[0] > 0)
                arrow.setEnabled(true);
              if(prop[1] > 0)
                wall.setEnabled(true);
              if(prop[2] > 0)
                increase.setEnabled(true);
              if(prop[3] > 0)
                decrease.setEnabled(true);

            }
            else if(str.contains("wait")){
              if(str.indexOf(4) == '1'){
                land.setText("You still remain one round to unfreeze");
              }
              else{
                land.setText("You was shot by Ashe's arrow, remain two rounds to unfreeze");
              }
              send(14,"wait");
            }
            else if(str.equals("win")){
              break;
            }
            else if(str.equals("exit")){
              land.setText("another hunter escaped, You win!");
              break;
            }

            // refresh map when getting new one
            for (JButton button : b) {
              int bx = button.getX();  /*get buttons' position and set their background color*/
              int by = button.getY();
              bx = (bx - 60)/20;
              by = (by - 60)/20;
              if (bx == x && by == y) { //land spot
                button.setBackground(new Color(0, 150, 240));
                if(map[42*by + bx] == (byte)1){
                  near = true;
                  System.out.println("near");
                }
              }
              else if(Math.abs(x-bx) < sight){			//default sight == 2, can increase or decrease
                if(Math.abs(y-by) < sight) { //inside the area
                  if(map[by*42 + bx] == (byte)1){
                    near = true;
                    System.out.println("near");
                  }

                  if(map[by*42 + bx] == (byte)2||map[by*42 + bx] == (byte)3||map[by*42 + bx] ==(byte) 4||map[by*42 + bx] == (byte)5){
                    button.setBackground(new Color(200, 170, 20));
                  }
                  else if(map[by*42 + bx] == (byte)6||map[by*42 + bx] == (byte)7){
                    button.setBackground(new Color(200, 70, 20));
                  }
                  else{
                    button.setBackground(new Color(200, 200, 200));
                  }
                }
                else
                  button.setBackground(new Color(80, 150, 100));
              }
              else
                button.setBackground(new Color(80, 150, 100));
            }
            if(near){
              alert.setEnabled(true);
              alert.setVisible(true);
            }
            else{
              alert.setVisible(false);
            }
            near = false;
          }
        }
        Thread.sleep(5000);
        game.setVisible(false);
        matchTable.setVisible(true);
        matchTable.setEnabled(true);
//        matchTable.removeAll();
        match = false;
        wait = false;
      }
    }
    catch(Exception e)
    {
      System.out.println("exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}