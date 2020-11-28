import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class Game extends Thread {
    int P1 = 0;
    int P2 = 1;
    public enum Item { // mix with item type
        nothing, treasure, arrow, wall
    }

    Socket[] Play = new Socket[2];
    InputStream[] in = new InputStream[2];
    OutputStream[] out = new OutputStream[2];
    boolean win = false;
    String order = null;
    int[] pos_x = new int[2]; // player1 and player2 position
    int[] pos_y = new int[2];

    // use to create map
    Queue<map_type> map = new LinkedList<map_type>();
    public class map_type { 
        int x;
        int y;
        int item_type; // 0 is nothing, 1 is win point,....

        map_type(int x, int y, int type) {
            this.x = x;
            this.y = y;
            item_type = type;
        }
    }
    
    public void createmap(){ //create map

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
        int turn_counter = (int) Math.random() * 2; // 0 is P1 turn , 1 is P2 turn
        order = "run";// run, stop, win
        try {
            while (!win) {
                out[turn_counter].write(order.getBytes());
                //out[turn_counter].write(map);
                
                
                turn_counter++;
                turn_counter%=2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
