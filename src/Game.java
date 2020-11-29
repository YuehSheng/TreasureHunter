import java.io.*;
import java.lang.ProcessBuilder.Redirect.Type;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Random;

public class Game extends Thread {
    int P1 = 0;
    int P2 = 1;

    public enum Item { // mix with item type
        nothing, treasure, arrow, wall, increase,decrease // props*3 treasure*1
    }
    
    String Roomname;
    Socket[] Play = new Socket[2];
    InputStream[] in = new InputStream[2];
    OutputStream[] out = new OutputStream[2];
    boolean win = false;
    String order = null;
    int[] pos_x = new int[2]; // player1 and player2 position
    int[] pos_y = new int[2];

    // use to create map
    // use to send
    byte[] map = new byte[42*42];

    public void createMap(){ //create map
        for (int i = 0;i < 42;i++){
            for(int j = 0;j < 42;j++){
                map[42*i+j] = (byte) (Item.nothing.ordinal() & 0xff);
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
                    map[42 * y + x] = (byte) (Item.treasure.ordinal() & 0xff);
                    break;
                case 1:
                case 2:
                case 3:
                    map[42 * y + x] = (byte) (Item.arrow.ordinal() & 0xff);
                    break;
                case 4:
                case 5:
                case 6:
                    map[42 * y + x] = (byte) (Item.wall.ordinal() & 0xff);
                    break;
                case 7:
                case 8:
                case 9:
                    map[42 * y + x] = (byte) (Item.increase.ordinal() & 0xff);
                    break;
                case 10:
                case 11:
                case 12:
                    map[42 * y + x] = (byte) (Item.decrease.ordinal() & 0xff);
                    break;
            }
        }
    }


    
    public Game(String Roomname,Socket player1, Socket player2) {
        this.Roomname = Roomname;
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

    public String find_direction(int x, int y){
        int treasure_x = 0;
        int treasure_y = 0;
        int dirX = 0;
        int dirY = 0;
        String direction;
        for (int i = 0; i < 42 * 42; i++) {
            if (map[i] == (byte) (Item.treasure.ordinal() & 0xff)) {
                treasure_y = i / 42;
                treasure_x = i % 42;
                break;
            }
        }
        dirX = x - treasure_x;
        dirY = y - treasure_y;
        if (dirX == 0) {
            if (dirY == 0) {
                direction = "ground";
            } else if (dirY > 0) {
                direction = "north";
            } else {
                direction = "south";
            }
        } else if (dirX > 0) {
            if (dirY == 0) {
                direction = "west";
            } else if (dirY > 0) {
                direction = "Northwest";
            } else {
                direction = "Southwest";
            }
        } else {
            if (dirY == 0) {
                direction = "east";
            } else if (dirY > 0) {
                direction = "Northeast";
            } else {
                direction = "Southeast";
            }
        }
        return direction;
    }

    public void run() {
        int turn_counter = (int) (Math.random() * 2); // 0 is P1 turn , 1 is P2 turn
        int[] wait_counter = new int[2]; //arrow stop counter
        wait_counter[P1] = 0;
        wait_counter[P2] = 0;
        int mode = 0;
        byte[] client_mode = new byte[4];
        byte[] client_action = new byte[100];
        order = "run";// run, stop, win
        createMap();
        try {
            while (!win) {
                out[turn_counter].write(order.getBytes());
                out[turn_counter].write(map);
                in[turn_counter].read(client_mode);
                in[turn_counter].read(client_action);
                mode = ByteBuffer.wrap(client_mode, 0, 4).getInt();
                
                switch(mode){
                    case 0: // move 
                        //client should send int x, int y
                        //read move position
                        //have to delete this position props
                        int x = ByteBuffer.wrap(client_action,0,4).getInt();
                        int y = ByteBuffer.wrap(client_action,4,4).getInt();
                        map[42*y+x] = (byte) (Item.nothing.ordinal() & 0xff);
                        if(wait_counter[(turn_counter+1)%2] == 0){
                            order = "run";
                        }
                        break;
                    case 1: // search
                        //read position
                        //send direction
                        x = ByteBuffer.wrap(client_action, 0, 4).getInt();
                        y = ByteBuffer.wrap(client_action, 4, 4).getInt();
                        String direction = find_direction(x, y);
                        out[turn_counter].write(direction.getBytes());
                        if (wait_counter[(turn_counter + 1) % 2] == 0) {
                            order = "run";
                        }
                        break;
                    case 2: // props
                        //read item number

                        break;
                    case 3: // dig
                        //read dig position

                        break;
                    case 4: // wait : if order is stop client should send 'wait' back
                        break;
                }
                
                
                turn_counter = (turn_counter+1)%2;
            }
        } catch (IOException e) {
            //if someone out should send server to delete this room
            e.printStackTrace();
        }
    }
}
