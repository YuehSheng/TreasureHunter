import java.io.*;
import java.lang.ProcessBuilder.Redirect.Type;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class Game extends Thread {
    int P1 = 0;
    int P2 = 1;
    public enum Item { // mix with item type
        nothing, treasure, arrow, wall, increase,decrease // props*3 treasure*1
    }

    Socket[] Play = new Socket[2];
    InputStream[] in = new InputStream[2];
    OutputStream[] out = new OutputStream[2];
    boolean win = false;
    String order = null;
    int[] pos_x = new int[2]; // player1 and player2 position
    int[] pos_y = new int[2];


    public class map_type {
        byte type; // 0 is nothing, 1 is win point,....
        map_type(Item type) {
            this.type = (byte) (type.ordinal() & 0xff);
        }
    }

    // use to create map
    map_type[][] map = new map_type[42][42];
    // use to send
    byte[] send_map = new byte[42*42];

    public void createMap(){ //create map
        for (int i = 0;i < 42;i++){
            for(int j = 0;j < 42;j++){
                map[i][j] = new map_type(Item.nothing);
            }
        }
        HashSet<Integer> randPos = new HashSet<>();
        while(randPos.size() < 13){
            randPos.add((int)(Math.random()*42*42));
        }
        Object[] itemPos = randPos.toArray();
        for(int i = 0;i < 13;i++){
            int x = (Integer)itemPos[i] / 42;
            int y = (Integer)itemPos[i] % 42;
            System.out.println(x+" "+y);
            switch (i){
                case 0:
                    map[x][y] = new map_type(Item.treasure);
                    send_map[42 * x + y] = map[x][y].type;
                    break;
                case 1:
                case 2:
                case 3:
                    map[x][y] = new map_type(Item.arrow);
                    send_map[42 * x + y] = map[x][y].type;
                    break;
                case 4:
                case 5:
                case 6:
                    map[x][y] = new map_type(Item.wall);
                    send_map[42 * x + y] = map[x][y].type;
                    break;
                case 7:
                case 8:
                case 9:
                    map[x][y] = new map_type(Item.increase);
                    send_map[42 * x + y] = map[x][y].type;
                    break;
                case 10:
                case 11:
                case 12:
                    map[x][y] = new map_type(Item.decrease);
                    send_map[42 * x + y] = map[x][y].type;
                    break;
            }
        }
    }


    
    public Game(Socket player1, Socket player2) {
        Play[P1] = player1;
        Play[P2] = player2;
        try {
            in[P1] = player1.getInputStream();
            in[P2] = player2.getInputStream();
            out[P1] = player1.getOutputStream();
            out[P2] = player2.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int turn_counter = (int) (Math.random() * 2); // 0 is P1 turn , 1 is P2 turn
        order = "run";// run, stop, win
        createMap();
        try {
            while (!win) {
                out[turn_counter].write(order.getBytes());
                out[turn_counter].write(send_map);
                
                
                turn_counter = (turn_counter+1)%2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
