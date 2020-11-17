//*******************************************************************
//*  Network Programming - Unit 6 Remote Method Invocation          *
//*  Program Name: ArithmeticServer                                 *
//*  The program is the RMI server. It binds the ArithmeticRMIImpl  *
//*    with name server.                                            *
//*  2014.02.26                                                     *
//*******************************************************************
import java.rmi.*;
import java.rmi.server.*;
/*
javac ForumInterface.java
javac Server.java
javac ForumRMIClient.java
javac ForumRMIImpl.java
rmic ForumRMIImpl
start rmiregistry
java ForumServer


java ForumRMIClient

*/
public class Server
{
	// Bind ArithmeticServer and Registry
	public static void main(String[] args)
	{
		//System.setSecurityManager(new RMISecurityManager());
		try
		{
			RMIImpl name = new RMIImpl();
			System.out.println("Registering ...");
			Naming.rebind("forum", name);	// arithmetic is the name of the service
			System.out.println("Register success");
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}