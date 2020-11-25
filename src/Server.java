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
	public static class RoomType{
		String RoomName;
		String P1_name;
		Socket P1;
		String P2_name;
		Socket P2;
		RoomType(String name, String P1_name, Socket P1){
			this.RoomName = name;
			this.P1_name = P1_name;
			this.P1 = P1;
		}
	}

	public ScType 			scType; //Socket and name
	public Queue<ScType> 	usersocket = new LinkedList<ScType>();
	public Queue<RoomType> 	room = new LinkedList<RoomType>();
	
		
	
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
					System.out.println("P" + namecounter + " in");
					Thread desktThread = new Thread(new desk(sc, namecounter++));
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
		String PlayName;
		Socket sc = null;
		InputStream in = null;
		OutputStream out = null;
		int port = 6666;
		int mode;
		byte[] buf = new byte[100];
		boolean flag = true;
		String data;
		public desk(Socket socket, int name) {
			sc = socket;
			ThreadName = name;
		}

		public void run() {
			try {
				in = sc.getInputStream();
				while(flag){
					/*
					* 0~4 mode
					* 5~54 account
					* 55~104 password
					*
					* */
					buf = new byte[100];
					in.read(buf);
					mode = ByteBuffer.wrap(buf, 0, 4).getInt(); //login = 0,create = 1,join = 2,refresh = 3,back = 4
					data = new String(ByteBuffer.wrap(buf, 4, buf.length-4).array());//0:playname,1:roomname,2:join roomname
					System.out.println(mode);
					System.out.println(data);
					switch(mode){
						case 0: //name
							PlayName = data;
							System.out.println(data);
							break;
						case 1: //create
							new Server().room.add(new RoomType(data, PlayName, sc));
							break;
						case 2: //join
							break;
						case 3: //reflesh
							break;
						case 4: //exit
							break;
					}
					
				}
				in.close();
				sc.close();
			} 
			catch (IOException e) {
				System.err.println(e);
			}

		}

	}
}