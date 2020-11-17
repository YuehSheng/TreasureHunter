//*******************************************************************
//*  Network Programming - Unit 6 Remote Method Invocation          *
//*  Program Name: ArithmeticRMIImpl                                *
//*  The program implements the services defended in the interface, *
//*    ForumInterface.java, for Java RMI.                      *
//*  2014.02.26                                                      *
//*******************************************************************
import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RMIImpl<Synchronized> extends UnicastRemoteObject implements RMIInterface
{
	// This implementation must have a public constructor.
	// The constructor throws a RemoteException.
	public RMIImpl() throws RemoteException
	{
		super(); 	// Use constructor of parent class
	}


	synchronized public int register(String str,String pass)throws RemoteException {
		File member = new File("member.txt");
		FileWriter myWriter = null;
		Scanner myReader = null;
		try {
			myReader = new Scanner(member);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(str.equals(s[0])) {
					if(pass.equals(s[1])){//log in
						return 1;
					}
					else{//wrong pass
						return 2;
					}
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		try {
			myWriter = new FileWriter("member.txt",true);
			myWriter.write(str+" "+pass+"\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 3;
	}

	synchronized public void create(String account,String subject, String context)throws RemoteException {
		int lastNo = 0;
		File data = new File("data.txt");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d/HH:mm:ss", Locale.ENGLISH);
		String date = dateFormat.format(new Date());
		FileWriter myWriter = null;
		Scanner myReader = null;
		try{
			myReader = new Scanner(data);
			String last = "";
			while (myReader.hasNext()){
				last = myReader.next();
			}
			if(!last.equals("")){
				lastNo = Integer.parseInt(last);
			}

			myReader.close();

			myWriter = new FileWriter("data.txt",true);
			myWriter.write(account+" "+subject+" [ "+context+" ] "+date+" "+(lastNo+1)+"\n");
			myWriter.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	synchronized public ArrayList<String> subject()throws RemoteException {
		ArrayList<String> subjects = new ArrayList<>();
		File data = new File("data.txt");
		Scanner myReader = null;
		try{
			myReader = new Scanner(data);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				subjects.add(s[s.length-1]+". "+s[1]);
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return subjects;
	}


	synchronized public void reply(String account,String msg,int no) throws RemoteException{
		File reply = new File("reply.txt");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d/HH:mm:ss", Locale.ENGLISH);
		String date = dateFormat.format(new Date());
		FileWriter myWriter = null;
		Scanner myReader = null;
		try{
			myWriter = new FileWriter("reply.txt",true);
			myWriter.write(no +" "+ account +" "+msg+" "+date+"\n");
			myWriter.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	synchronized public String discussion(int no) throws RemoteException {
		String replies = "";
		File reply = new File("reply.txt");
		File data = new File("data.txt");
		Scanner myReader = null;
		String subject = "";
		String context = "";
		String host = "";
		String D = "";
		try{
			myReader = new Scanner(data);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(s[s.length-1].equals(String.valueOf(no))){
					host = s[0];
					subject = s[1];
					for(int i = 3;!s[i].equals("]");i++){
						context += s[i]+" ";
					}
					D = s[s.length-2];
					break;
				}
			}
			replies += "Subject: "+subject+"\ncontext: "+ context + "\nby: "+host + "\nDate:"+ D+"\n";
			myReader = new Scanner(reply);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(s[0].equals(String.valueOf(no))){
					replies += "\nreplier:"+s[1]+"\nreply:";
					for(int i = 2;i < s.length-1;i++){
						replies += s[i]+" ";
					}
					replies += "\nDate:"+s[s.length-1]+"\n";
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return replies;
	}
	synchronized public ArrayList<String> del_list(String host) throws RemoteException {
		ArrayList<String> list = new ArrayList<String>();
		File reply = new File("reply.txt");
		File data = new File("data.txt");
		Scanner myReader = null;
		try{
			myReader = new Scanner(data);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(s[0].equals(host)){
					list.add(s[s.length-1]+". "+s[1]+"\n");
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}

		return list;
	}
	synchronized public boolean delete(int no) throws RemoteException {
		boolean flag = true;
		File reply = new File("reply.txt");
		File data = new File("data.txt");
		Scanner myReader = null;
		FileWriter myWriter = null;
		String delLine = "";

		try{
			myReader = new Scanner(reply);
			int rnum = 0;
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(s[0].equals(String.valueOf(no))){
					delLine = line;
					rnum++;
				}
			}
			if(rnum > 1){
				return false;
			}

			myReader = new Scanner(reply);	//del reply
			String newReply = "";
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				if(!delLine.equals(line)){
					newReply += line+"\n";
				}
			}
			myWriter = new FileWriter("reply.txt");
			myWriter.write(newReply);
			myWriter.close();

			myReader = new Scanner(data);	//del data
			String newData = "";
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				if(!s[s.length-1].equals(String.valueOf(no))){
					newData += line+"\n";
				}
			}
			myWriter = new FileWriter("data.txt");
			myWriter.write(newData);
			myWriter.close();

		}
		catch (IOException e){
			e.printStackTrace();
		}
		return true;
	}

	synchronized public ArrayList<Integer> sub_list() throws RemoteException {
		ArrayList<Integer> list  = new ArrayList<>();
		File data = new File("data.txt");
		Scanner myReader = null;
		try{
			myReader = new Scanner(data);
			while (myReader.hasNextLine()){
				String line = myReader.nextLine();
				String[] s = line.split(" ");
				list.add(Integer.parseInt(s[s.length-1]));
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return list;
	}
}