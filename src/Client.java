//*******************************************************************
//*  Network Programming - Unit 6 Remote Method Invocation          *
//*  Program Name: CalculatorRMIClient                              *
//*  The program is a RMI client.                                   *
//*  Usage: java CalculatorRMIClient op num1 num2,                  *
//*         op = add, sub, mul, div                                 *
//*  2014.02.26                                                     *
//*******************************************************************
import java.rmi.*;
import java.util.*;


class Client
{
	public static void main(String[] args)
	{
		RMIInterface o = null;
		Scanner scanner = new Scanner(System.in);
		boolean r = false;
		String account = "";

		try
		{
			o = (RMIInterface) Naming.lookup("rmi://127.0.0.1/forum");
			System.out.println("RMI server connected");
		}
		catch(Exception e)
		{
			System.out.println("Server lookup exception: " + e.getMessage());
		}
		try {
			assert o != null;
			while (true){

				System.out.println("\n(1) Register(Login) (2) Create (3) Subjects (4) Reply (5) Show context (6) Delete subject (7)Exit");
				int chose = scanner.nextInt();
				switch (chose){
					case 1:
						if(r){
							System.out.println(" You have been registered !"+account);
						}
						else{
							System.out.print("Input the account name: ");
							account = scanner.next();
							System.out.print("Input the password: ");
							String pass = scanner.next();
							int result = o.register(account,pass);
							if(result == 1){
								System.out.println("Log in success !");
								r = true;
							}
							else if(result == 2){
								System.out.println("Wrong password !");
								r = false;
							}
							else {
								System.out.println("Sign up success !");
								r = true;
							}
						}
						break;
					case 2:
						if(r){
							String temp = scanner.nextLine();
							System.out.print("Input subject: ");
							String subject = scanner.next();
							System.out.print("Input context: ");
							temp = scanner.nextLine();
							String context = scanner.nextLine();

							o.create(account,subject,context);
							System.out.print("Create a subject!");
						}
						else{
							System.out.print("Not yet registered !");
						}
						break;
					case 3:
						ArrayList<String> subjects = o.subject();
						if(subjects.isEmpty()){
							System.out.print("No subject!");
							break;
						}
						for(String subject:subjects){
							System.out.println(subject);
						}
						break;
					case 4:
						if(r){
							subjects = o.subject();
							if(subjects.isEmpty()){
								System.out.print("No subject!");
								break;
							}
							for(String subject:subjects){
								System.out.println(subject);
							}
							System.out.print("Which subject do you want to reply to (Input the No.) :");
							int no = scanner.nextInt();
							ArrayList<Integer> sub_list = o.sub_list();
							boolean get = false;
							for(Integer i:sub_list){
								if(i == no){
									System.out.print("Input the reply for No."+no+": ");
									String msg = scanner.nextLine();
									msg = scanner.nextLine();
									if(!o.sub_list().contains(i)){//secure
										break;
									}
									o.reply(account,msg,no);
									System.out.print("Reply!");
									get = true;
									break;
								}
							}
							if(!get){
								System.out.println("Wrong request,please try again!");
							}
						}
						else{
							System.out.print("Not yet registered !");
						}
						break;
					case 5:
						subjects = o.subject();
						if(subjects.isEmpty()){
							System.out.print("No subject!");
							break;
						}
						for(String subject:subjects){
							System.out.println(subject);
						}
						System.out.println("Show which subject (Input the No.) : ");
						int no = scanner.nextInt();

						ArrayList<Integer> sub_list = o.sub_list();

						boolean get = false;
						for(Integer i:sub_list){
							if(i == no){
								String Discussion = o.discussion(no);
								System.out.println(Discussion);
								get = true;
								break;
							}
						}
						if(!get) {
							System.out.println("Wrong number!");
						}

						break;
					case 6:
						if(r){
							ArrayList<String> list = o.del_list(account);
							if(list.isEmpty()){
								System.out.println("You don't have any subject !");
								break;
							}
							System.out.println("Your subjects: "+list.size());
							int[] nos = new int[list.size()];
							int cur = 0;
							for(String str : list){
								System.out.print(str);
								String[] s = str.split(" ");
								s[0] = s[0].substring(0,s[0].length()-1);
								nos[cur] = Integer.parseInt(s[0]);
								cur++;
							}
							System.out.print("Delete which ? ");
							int del = scanner.nextInt();
							boolean flag = false;
							for (int value : nos) {
								if (del == value) {
									flag = true;
									break;
								}
							}
							if(flag){
								if(o.delete(del)){
									System.out.println("Delete subject No."+del);
								}
								else {
									System.out.println("Can't delete subject No."+del);
								}

							}
							else{
								System.out.println("Wrong number!");
							}
						}
						else{
							System.out.print("Not yet registered !");
						}
						break;
				}
				if(chose == 7){
					break;
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("forum exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}