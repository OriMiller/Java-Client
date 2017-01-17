import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * @author orimiller
 * This is a Java Client/Server Socket-based file download program 
 * developed by Ori Miller for his 11th grade (Junior) year 
 * independent study of Computer Science
 *
 */
public class main {
		
		public static ArrayList<Handler> connecties = new ArrayList<Handler>();
		
		public static void main(final String[] args) throws Exception {
			System.out.println("Server is running");
		 	ServerSocket server = new ServerSocket(2525);
		 	ThreadGroup group = new ThreadGroup("Connections");
			try{
				while(true){
				new Handler(server.accept(),group);
				group.list();
				}
			} finally {
				server.close();
			}
	}
}
class Handler extends Thread{
	private static File file = new File("/Users/orimiller/TEMP/");
	private Socket socket; 
	//private Date start;
	private static FileInputStream fis = null;
    private static BufferedInputStream bis = null;
    private static OutputStream os = null;
	private static DataInputStream in;
	private static DataOutputStream out;
	Handler(Socket socket, ThreadGroup group) throws IOException{
		super(group, (String)socket.getInetAddress().toString());
		this.socket = socket;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		this.start();
	}
	public static void getList(File file) throws IOException{
		if(file.exists()){
			String[] strs = new String[file.listFiles().length];
			for(int i=0;i<file.listFiles().length;i++){
				if(file.listFiles()[i].isFile()){
					strs[i] = (file.listFiles()[i].getName() + "(1)");
				}
				if(file.listFiles()[i].isDirectory()){
					strs[i] = (file.listFiles()[i].getName() + "(2)");
				}	
			}
			out.writeInt(Serializer.serialize(strs).length);
			out.write(Serializer.serialize(strs));
		}
	}
	public void run(){
		try {
			while(true){
					String firstLine = in.readUTF();
					if(firstLine.startsWith("REFRESH")){
						System.out.println("Refresh: " + socket.toString());
						getList(file);
					} else if(firstLine.startsWith("SEND")){
						int get = in.readInt();
						System.out.println(get);
						if(file.listFiles()[get].isFile())
						{
							File myFile = new File(file.listFiles()[get].getPath());
							byte [] mybytearray  = new byte [(int)myFile.length()];
							out.writeInt(mybytearray.length);
					        fis = new FileInputStream(myFile);
					        bis = new BufferedInputStream(fis);
					        bis.read(mybytearray,0,mybytearray.length);
					        os = socket.getOutputStream();
					        System.out.println("Sending " + file.list()[get] + "(" + mybytearray.length + " bytes)");
					        os.write(mybytearray,0,mybytearray.length);
					        os.flush();
					        System.out.println("Done.");
						} else if(file.listFiles()[get].isDirectory()){
							file = new File(file.listFiles()[get].getPath());
							System.out.println(file.getAbsolutePath());
						}
					} else if(firstLine.startsWith("CLOSING")){
						System.out.println("Closing: " + socket.toString());
						socket.close();
						break;
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String toString(){
		return "Thread " + this.getName() + " / " + this.socket.toString() ;
	}
}