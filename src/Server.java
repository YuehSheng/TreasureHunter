import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Server
{
	public static class ScType {
		Socket s;
		int name;

		ScType(Socket socket, int name) {
			this.s = socket;
			this.name = name;
		}
	}

	public ScType 			scType; //Socket and name
	public Queue<ScType> 	usersocket = new LinkedList<ScType>();
	public Queue<String> 	room = new LinkedList<String>();
	
		
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket 	serverSocket 	= null;
		Socket 			sc 				= null;
		int				port			= 6666;
		int				namecounter		= 0;
		try{
			serverSocket = new ServerSocket(port);
			System.out.println("Waiting for request ...");
			
			try{
				while(true){
					sc = serverSocket.accept();
					new Server().usersocket.add(new Server.ScType(sc, namecounter)); // add in queue
					Thread desktThread = new Thread(new desk(sc, namecounter));
					desktThread.start();
				}
			}
			catch(IOException e){
				System.err.println(e);
			}
			finally{
				serverSocket.close();
			}

		}
		catch(IOException e){
			System.err.println(e);
		}
		
	}
	
	public static class desk implements Runnable {
		int ThreadName;
		Socket sc = null;
		InputStream in = null;
		OutputStream out = null;
		int port = 6666;
		byte[] buf = new byte[100];

		public desk(Socket socket, int name) {
			sc = socket;
			ThreadName = name;
			
		}

		public void run() {

		}

	}
}