import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashSet;

public class Game extends Thread {
    int P1 = 0;
    int P2 = 1;

    public enum Item { // mix with item type
        nothing, treasure, arrow, wall, increase, decrease, PLAYER1, PLAYER2 // props*3 treasure*1
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
    byte[] map = new byte[42 * 42];

    public void createMap() { // create map
        for (int i = 0; i < 42; i++) {
            for (int j = 0; j < 42; j++) {
                map[42 * i + j] = (byte) (Item.nothing.ordinal() & 0xff);
            }
        }
        HashSet<Integer> randPos = new HashSet<>();
        while (randPos.size() < 50) {
            randPos.add((int) (Math.random() * 42 * 42));
        }
        Object[] itemPos = randPos.toArray();

        map[42 * ((Integer) itemPos[0] % 42) + (Integer)itemPos[0] / 42] = (byte) (Item.treasure.ordinal() & 0xff);
        for (int i = 1; i < 50; i++) {
            int x = (Integer) itemPos[i] / 42;
            int y = (Integer) itemPos[i] % 42;
            System.out.println(x + " " + y);
            if(i % 4 == 0){
                map[42 * y + x] = (byte) (Item.arrow.ordinal() & 0xff);
            }
            else if(i % 4 == 1){
                map[42 * y + x] = (byte) (Item.wall.ordinal() & 0xff);
            }
            else if(i % 4 == 2){
                map[42 * y + x] = (byte) (Item.increase.ordinal() & 0xff);
            }
            else {
                map[42 * y + x] = (byte) (Item.decrease.ordinal() & 0xff);
            }
        }
    }

    public Game(String Roomname, Socket player1, Socket player2) {
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

    public int find_direction(int x, int y) {
        int treasure_x = 0;
        int treasure_y = 0;
        int dirX = 0;
        int dirY = 0;
        int direction;
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
                direction = 0; // "ground"
            } else if (dirY > 0) {
                direction = 1; // "north"
            } else {
                direction = 2; // "south"
            }
        } else if (dirX > 0) {
            if (dirY == 0) {
                direction = 3; // "west"
            } else if (dirY > 0) {
                direction = 4; // "Northwest"
            } else {
                direction = 5; // "Southwest"
            }
        } else {
            if (dirY == 0) {
                direction = 6; // "east"
            } else if (dirY > 0) {
                direction = 7; // "Northeast"
            } else {
                direction = 8; // "Southeast"
            }
        }
        return direction;
    }

    public void run() {
        int turn_counter = (int) (Math.random() * 2); // 0 is P1 turn , 1 is P2 turn
        int[] wait_counter = new int[2]; // arrow stop counter
        wait_counter[P1] = 0;
        wait_counter[P2] = 0;
        boolean[] wall_defense = new boolean[2];
        wall_defense[0] = false;
        wall_defense[1] = false;
        int mode = 0;
        byte[] client_mode = new byte[4];
        byte[] client_action = new byte[100];
        order = "run";// run, wait, decrease, win
        createMap();
        try {
            while (!win) {
                client_mode = new byte[4];
                client_action = new byte[100];
                out[turn_counter].write(order.getBytes());
                out[turn_counter].flush();
                Thread.sleep(20);
                out[turn_counter].write(map);
                in[turn_counter].read(client_mode);
                System.out.println("get mode" + ByteBuffer.wrap(client_mode).getInt());
                in[turn_counter].read(client_action);
                mode = ByteBuffer.wrap(client_mode, 0, 4).getInt();

                switch (mode) {
                    case 10: // move
                        // client should send int x, int y
                        // read move position
                        // have to delete this position props
                        String pos = new String(client_action).trim();
                        String[] s = pos.split(" ");
                        int x = Integer.parseInt(s[0]);
                        int y = Integer.parseInt(s[1]);
                        System.out.println(x + " " + y);
                        if(turn_counter == 0){
                            map[42 * y + x] = (byte) (Item.PLAYER1.ordinal() & 0xff);
                        }else{
                            map[42 * y + x] = (byte) (Item.PLAYER2.ordinal() & 0xff);
                        }

                        if (wait_counter[(turn_counter + 1) % 2] == 0) {
                            order = "run";
                        } else {
                            order = "wait";
                        }
                        byte[] buf = new byte[]{0,0,0,1};
                        out[turn_counter].write(buf);
                        break;
                    case 11: // search
                        // read position
                        // send direction
                        // message 0 = ground, 1 = north, 2 = south, 3 = west, 4 = Northwest
                        // 5 = Southwest, 6 = east, 7 = Northeast, 8 = Southeast
                        pos = new String(client_action).trim();
                        s = pos.split(" ");
                        x = Integer.parseInt(s[0]);
                        y = Integer.parseInt(s[1]);
                        System.out.println(x + " " + y);
                        int direction = find_direction(x, y);
                        byte[] Dirbuf = new byte[4];
                        out[turn_counter].write(ByteBuffer.wrap(Dirbuf).putInt(direction).array()); // send direction
                        if (wait_counter[(turn_counter + 1) % 2] == 0) {
                            order = "run";
                        } else {
                            order = "wait";
                        }
                        break;
                    case 12: // props
                        // read item number
                        // use which item and change order and wait_counter to next player
                        // arrow = 0, wall = 1, increase = 2, decrease = 3

                        int item = ByteBuffer.wrap(client_action,0,4).getInt();
                        String item_message;
                        if(item == 0){ //arrow
                            if(!wall_defense[(turn_counter+1)%2]){
                                order = "wait";
                                wait_counter[(turn_counter+1)%2] = 2;
                                wall_defense[(turn_counter+1)%2] = false;
                            }
                            else {
                                order = "defense";
                            }
                        }
                        else if(item == 1){
                            wall_defense[turn_counter] = true;
                        }
                        break;
                    case 13: // dig
                        // read dig position

                        break;
                    case 14: // wait : if order is stop client should send 'wait' back
                        wait_counter[turn_counter]--;
                        if (wait_counter[(turn_counter + 1) % 2] == 0) {
                            order = "run";
                        } else {
                            order = "wait";
                        }
                        break;
                }

                turn_counter = (turn_counter + 1) % 2;
            }
        } catch (IOException | InterruptedException e) {
            // if someone out should send server to delete this room
            e.printStackTrace();
        }
    }
}
