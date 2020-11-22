import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.*;

public class Server_test {
    public static void main(String[] args) {
        Socket c;
        OutputStream o;
        Scanner scanner = new Scanner(System.in);
        int mode;
        String data,temp;
        byte[] buf = null;
        try {
            c = new Socket("127.0.0.1",6666);
            o = c.getOutputStream();
            while(true){
                mode = scanner.nextInt();
                temp = scanner.nextLine();
                data = scanner.nextLine();
                if(data.equals("back")){
                    break;
                }
                buf = new byte[ByteBuffer.allocate(4).putInt(mode).array().length + data.getBytes().length];
                System.arraycopy(ByteBuffer.allocate(4).putInt(mode).array(), 0, buf, 0, 
                        ByteBuffer.allocate(4).putInt(mode).array().length);
                System.arraycopy(data.getBytes(),0,buf,4,data.getBytes().length);
                
                o.write(buf);
                o.flush();
            }
            o.close();
            scanner.close();
            c.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}