import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Game extends Thread {
    Socket P1 = null;
    Socket P2 = null;
    InputStream P1_in = null;
    InputStream P2_in = null;
    OutputStream P1_out = null;
    OutputStream P2_out = null;

    public Game(Socket player1, Socket player2) {
        P1 = player1;
        P2 = player2;
        try {
            P1_in   = P1.getInputStream();
            P1_out  = P1.getOutputStream();
            P2_in   = P2.getInputStream();
            P2_out  = P2.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }      
    }

    public void run(){

    }
}
