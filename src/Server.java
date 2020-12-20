import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Scanner;

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
		int P1_num;
		Socket P1;
		int owner;
		String P2_name;
		int P2_num;
		Socket P2;
		boolean play;
		RoomType(String name, String P1_name, Socket P1,int owner){
			System.out.println("create "+name+" by "+P1_num);
			this.RoomName = name;
			this.P1_name = P1_name;
			this.P1 = P1;
			this.owner = owner;
			P1_num = owner;
			P2_name = "";
		}
	}

	public ScType 			scType; //Socket and name
	public static Queue<ScType> 	userSocket = new LinkedList<ScType>();
	public static Queue<RoomType> 	room = new LinkedList<RoomType>();
	
		
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket 	serverSocket 	= null;
		Socket 			sc 				= null;
		int				port			= 6666;
		int				namecounter		= 1;
		try{
			serverSocket = new ServerSocket(port);
			System.out.println("Waiting for request ...");
			Thread Wait_Exit = new Thread(new Wait_Game_Exit());
			Wait_Exit.start();
			Thread reopen = new Thread(new Reopen());
			reopen.start();
			try{
				while(true){
					sc = serverSocket.accept();
					new Server().userSocket.add(new Server.ScType(sc, namecounter)); // add in queue
					System.out.println("P" + namecounter + " in");
					Thread matchTableThread = new Thread(new matchTable(sc, namecounter++));
					matchTableThread.start();
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
			//System.err.println(e);
		}
	}
	
	public static class matchTable implements Runnable {
		int ThreadName;
		String PlayName;
		Socket sc = null;
		InputStream in = null;
		OutputStream out = null;
		boolean play = false;
		boolean end = false;
		int mode;
		byte[] buf = new byte[100];
		String data;

		public matchTable(Socket socket, int name) {
			sc = socket;
			ThreadName = name;
		}

		public void run() {
			try {
				in = sc.getInputStream();
				out = sc.getOutputStream();
				while (!play) {
					buf = new byte[4];
					in.read(buf); // read mode
					mode = ByteBuffer.wrap(buf, 0, 4).getInt(); // login = 0,create = 1,join = 2,refresh = 3,back = 4
					System.out.println(mode);
					buf = new byte[100];
					in.read(buf); // read data
					// delete useless byte to sure string is true
					int length = 0;
					for (int i = 0; i < buf.length; i++) {
						if (buf[i] == 0) {
							length = i;
							break;
						}
					}
					data = new String(buf, 0, length, "UTF-8");// 0:playname,1:roomname,2:join roomname
					switch (mode) {
						case 0: // name
							mode = -1;
							String[] s = data.split(" ");
							String acc = s[0];
							String pass = s[1].trim();
							System.out.println(acc + " " + pass);
							File account = new File("src/account.txt");
							FileWriter myWriter;
							Scanner myReader;
							buf = new byte[] { 0, 0, 0, 0 };
							try {
								myReader = new Scanner(account);
								while (myReader.hasNextLine()) {
									String line = myReader.nextLine();
									s = line.split(" ");
									if (acc.equals(s[0])) {
										if (pass.equals(s[1])) {// log in
											ByteBuffer.wrap(buf, 0, 4).putInt(1);
										} else {// wrong pass
											ByteBuffer.wrap(buf, 0, 4).putInt(-1);
										}
									}
								}
								myReader.close();
								if (Arrays.equals(buf, new byte[] { 0, 0, 0, 0 })) {
									myWriter = new FileWriter("src/account.txt", true);
									myWriter.write(acc + " " + pass + "\n");
									myWriter.close();
									ByteBuffer.wrap(buf, 0, 4).putInt(2);
								}
								out.write(buf);

							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}

							PlayName = acc;
							break;
						case 1: // create
							mode = -1;
							System.out.println(ThreadName + " create");
							String roomName = data.trim();
							buf = new byte[4];
							boolean exist = false;
							for (RoomType r : room) {
								if (r.RoomName.equals(roomName)) {
									exist = true;
									break;
								}
							}
							if (!exist) {
								room.add(new RoomType(roomName, PlayName, sc, ThreadName));
								ByteBuffer.wrap(buf, 0, 4).putInt(1);

							} else {
								ByteBuffer.wrap(buf, 0, 4).putInt(-1);
							}
							out.write(buf);
							break;
						case 2: // join
							mode = -1;
							buf = new byte[4];
							roomName = data.trim();
							for (RoomType r : room) {
								if (r.RoomName.equals(roomName)) {
									if (!r.play) {
										r.P2_name = this.PlayName;
										r.P2 = this.sc;
										r.play = true;
										r.P2_num = ThreadName;
										ByteBuffer.wrap(buf, 0, 4).putInt(1);
										out.write(buf);

										// send to P1
										r.P1.getOutputStream().write(buf);
										r.P1.getOutputStream().flush();
										play = true;
										
									} else {
										ByteBuffer.wrap(buf, 0, 4).putInt(0, -1);
										out.write(buf);
									}
								} else {
									ByteBuffer.wrap(buf, 0, 4).putInt(0, 0);
									out.write(buf);
								}
							}
							break;
						case 3: // refresh
							mode = -1;
							buf = new byte[4];
							ByteBuffer.wrap(buf, 0, 4).putInt(0, room.size());
							out.write(buf);
							for (RoomType r : room) {
								Thread.sleep(20); // avoid client receiving many rooms in one read()
								buf = r.RoomName.getBytes();
								out.write(buf);
							}
							break;
						case 4: // exit
							mode = -1;
							room.removeIf(r -> r.owner == ThreadName);
							play = false;
							break;
						case 5: // close match table
							mode = -1;
							play = true;
							for (RoomType r : room){
								if(r.P1_num == ThreadName){
									Game game = new Game(r.RoomName, r.P1, r.P1_num, r.P2, r.P2_num);
									game.start();
								}
							}
							break;
					}
				}
			} 
			catch (IOException | InterruptedException e) {
				//System.err.println(e);
				System.out.println("P"+this.ThreadName + " out");
				for (RoomType r : room){
					if(r.owner == ThreadName){
						if(r.P2_name.equals("")){ //only one
							room.remove(r);
							try {
								sc.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
//				System.out.println(e);
			}
		}
	}

	public static class Wait_Game_Exit implements Runnable{
		ServerSocket ssc;
		Socket sc;
		InputStream in;
		byte[] buf = new byte[100];
		public void run() {
			try {
				ssc = new ServerSocket(8888); //diferent port
				while(true){
					sc = ssc.accept();
					in = sc.getInputStream();
					in.read(buf); //read roomname
					String Roomname = new String(buf).trim();
					for(RoomType r : room){
						if(r.RoomName.equals(Roomname)){
							System.out.println(r.RoomName + " is end!");
							room.remove(r);
						}
					} // close room
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static class Reopen implements Runnable{ // if game end or enemy exit will reopen match table
		ServerSocket ssc;
		Socket sc;
		InputStream in;
		byte[] buf = new byte[100];
		
		public void run() {
			try {
				ssc = new ServerSocket(8889); // diferent port
				while (true) {
					int name1 = 0;
					int name2 = 0;
					sc = ssc.accept();
					in = sc.getInputStream();
					in.read(buf);
					name1 = ByteBuffer.wrap(buf, 0, 4).getInt();
					if (name1 != 0) {
						System.out.println("P" + name1 + " is reopen");
						for (ScType s : userSocket) {
							if (s.name == name1) {
								Thread matchTableThread = new Thread(new matchTable(s.s, s.name));
								matchTableThread.start();
							}
						}
					}
					buf = new byte[100];
					in.read(buf);
					name2 = ByteBuffer.wrap(buf, 0, 4).getInt();
					if (name2 != 0) {
						System.out.println("P" + name2 + " is reopen");
						for (ScType s : userSocket) {
							if (s.name == name2) {
								Thread matchTableThread = new Thread(new matchTable(s.s, s.name));
								matchTableThread.start();
							}
						}
					}
					
					in.close();
					sc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}