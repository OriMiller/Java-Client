/**
d * @author orimiller
 * This is a Java Client/Server Socket-based file download program 
 * developed by Ori Miller for his 11th grade (Junior) year 
 * independent study of Computer Science
 */
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Server {
	public static String homeFile = "/";
	public static ArrayList<Handler> connecties = new ArrayList<Handler>();
	private static ServerSocket server;
	public static void main(final String[] args) throws Exception 
	{
		//System.out.println(InetAddress.getLocalHost());
		System.out.println("Server Startup");
		server = new ServerSocket(2525); //Creation of ServerSocket
		
		//DatagramSocket s = new DatagramSocket(8888);
		  
		
		System.out.println("Connect to this server with: " + InetAddress.getLocalHost());
		//System.out.println("Please type the home directory");
		System.out.println("Server is Running at Directory " + homeFile);
		System.out.println(new File(homeFile).getAbsolutePath());
		
		//ThreadGroup group = new ThreadGroup("Connections"); //Creation of ThreadGroup that houses all of the individual Threads for each individual connection
		while(true) {
			new Handler(server.accept()); //Creates unnamed Handler with the accepted socket connection to client and adds it to the ThreadGroup
			//group.list();
		}
	} 
}

class Handler extends Thread
{	
	private static File file = new File(Server.homeFile);
	private static Socket socket; //Socket connected to client
	private static DataInputStream in;
	private static DataOutputStream out;
	Handler(Socket socket) throws IOException
	{
		//super((String)socket.getInetAddress().toString());
		Handler.socket = socket;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		this.start();
	}
	public static void getList(File file) throws IOException
	{
		if(file.exists() && file.canRead())
		{
			ArrayList<String> strs = new ArrayList<String>();
			System.out.println(Arrays.toString(file.list()));
			System.out.println(file.list().length);
			for(int i=0;i<file.listFiles().length;i++)
			{
				File tem = file.listFiles()[i];
				if(tem.isDirectory())
				{
					strs.add(file.listFiles()[i].getName() + "(2)");
				} 
				else if(tem.isFile())
				{
					strs.add(file.listFiles()[i].getName() + "(1)");
				}
			}
			String[] outList = Arrays.copyOf(strs.toArray(), strs.size(), String[].class);
			out.writeInt(Serializer.serialize(outList).length);
			out.write(Serializer.serialize(outList));
		}
	}
	public static void downloadFile(int fileIndex) throws IOException
	{
		File myFile = new File(file.listFiles()[fileIndex].getPath());
		if(myFile.canRead()){
		byte [] mybytearray  = new byte [(int)myFile.length()];
		out.writeInt(mybytearray.length);
		FileInputStream fis = new FileInputStream(myFile);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    OutputStream os = socket.getOutputStream();
        bis.read(mybytearray,0,mybytearray.length);
        System.out.println("Sending " + file.list()[fileIndex] + "(" + mybytearray.length + " bytes) to " + socket.toString());
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println("Done.");
        myFile = new File(Server.homeFile);
        bis.close();
        } else {
        	System.out.println("ACCESS DENIED");
        }
	}
	public void run()
	{
		try 
		{
			System.out.println("CONNECTION: " + socket.toString());
			while(true)
			{
					String firstLine = in.readUTF();
					if(firstLine.startsWith("REFRESH"))
					{
						System.out.println("Refresh: " + socket.toString());
						getList(file);
					} 
					else if(firstLine.startsWith("SEND"))
					{
						int get = in.readInt();
						if(file.listFiles()[get].isFile())
						{
							downloadFile(get);
							System.out.println(file.getAbsolutePath());
						} 
						else if(file.listFiles()[get].isDirectory())
						{
							file = new File(file.listFiles()[get].getPath());
							System.out.println(file.getAbsolutePath());
						}
					} 
					else if(firstLine.startsWith("HOME"))
					{
						file = new File(Server.homeFile);
						System.out.println(file.getAbsolutePath());
					} 
					else if(firstLine.startsWith("CLOSING"))
					{
						System.out.println("Closing: " + socket.toString());
						socket.close();
						break;
					} 
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public String toString()
	{
		return "Thread " + this.getName() + " / " + socket.toString() ;
	}
}