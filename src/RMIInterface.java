//*******************************************************************
//*  Network Programming - Unit 6 Remote Method Invocation          *
//*  Program Name: ArithmeticInterface                              *
//*  The program defines the interface for Java RMI.                *
//*  2014.02.26                                                     *
//*******************************************************************
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIInterface extends Remote
{
	public int register(String str, String pass) throws RemoteException;
	public ArrayList<Integer> sub_list() throws RemoteException;
	public ArrayList<String> subject() throws RemoteException;
	public void create(String account, String subject, String context) throws RemoteException;
	public void reply(String account, String msg, int no) throws RemoteException;
	public String discussion(int no) throws RemoteException;
	public boolean delete(int no) throws RemoteException;
	public ArrayList<String> del_list(String host) throws RemoteException;
}

