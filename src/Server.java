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
javac RMIInterface.java
javac Server.java
javac Client.java
javac RMIImpl.java
rmic 	RMIImpl
start rmiregistry
java 	Server

java Client

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
			Naming.rebind("TreasureHunter", name);	// arithmetic is the name of the service
			System.out.println("Register success");
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}