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
				out = sc.getOutputStream();
				while(flag){
					/*
					* 0~3 mode
					* 4~53 account
					* 54~103 password
					*
					* */
					buf = new byte[4];
					in.read(buf);
					mode = ByteBuffer.wrap(buf).getInt(); //login = 0,create = 1,join = 2,refresh = 3,back = 4
					buf = new byte[100];
					int len = in.read(buf);
					data = new String(buf);//0:playname,1:roomname,2:join roomname
					switch(mode){
						case 0: //name
							mode = -1;
							String[] s = data.split(" ");
							String acc = s[0];
							String pass = s[1].trim();
							System.out.println(acc+" "+pass);
							File account = new File("src/account.txt");
							FileWriter myWriter;
							Scanner myReader;
							buf = new byte[]{0, 0, 0, 0};
							try {
								myReader = new Scanner(account);
								while (myReader.hasNextLine()){
									String line = myReader.nextLine();
									s = line.split(" ");
									if(acc.equals(s[0])) {
										if(pass.equals(s[1])){//log in
											ByteBuffer.wrap(buf,0,4).putInt(1);
										}
										else{//wrong pass
											ByteBuffer.wrap(buf,0,4).putInt(-1);
										}
									}
								}
								myReader.close();
								if(Arrays.equals(buf,new byte[] {0,0,0,0})){
									myWriter = new FileWriter("src/account.txt",true);
									myWriter.write(acc+" "+pass+"\n");
									myWriter.close();
									ByteBuffer.wrap(buf,0,4).putInt(2);
								}
								out.write(buf);

							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}

							PlayName = data;
							break;
						case 1: //create
							new Server().room.add(new RoomType(data, PlayName, sc));
							break;
						case 2: //join
							break;
						case 3: //refresh

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