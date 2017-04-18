/**
 * @author orimiller
 * This is a Java Client/Server Socket-based file download program 
 * developed by Ori Miller for his 11th grade (Junior) year 
 * independent study of Computer Science
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.sun.xml.internal.ws.encoding.MtomCodec.ByteArrayBuffer;

public class Client {
		private static String ip = "";
		private static Socket socket;
		//private static File file;
		private static int bytesRead;
	    private static int current;
		private static DataInputStream in;
		private static DataOutputStream out;
		private static String[] filelist;
		private static JButton refresh = new JButton("Refresh");
		private static JButton download = new JButton("Download");
		private static JButton home = new JButton("Home");
		private static JList<String> list = new JList<String>();
		private static JScrollPane sPane = new JScrollPane();
		private static JPanel panel = new JPanel();
		private static JPanel buttonPanel = new JPanel();
		private static JFrame frame = new JFrame("Java File Transfer Client");
		private static JFileChooser chooser = new JFileChooser();
		private static JProgressBar bar = new JProgressBar();
		public static String FILE_TO_RECEIVED = "/Users/omiller/Desktop";
		
	public Client() throws UnknownHostException, IOException{
		try{
		socket = new Socket("0.0.0.0",2525);
	    in = new DataInputStream(socket.getInputStream());
	    out = new DataOutputStream(socket.getOutputStream());
	    chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    GridLayout layout = new GridLayout(2,1,10,10);
	    GridLayout layout2 = new GridLayout(1,2,10,10);
	    GridLayout layout3 = new GridLayout(1,3,10,10);
	    buttonPanel.setLayout(layout3);
	    panel.setLayout(layout2);
		frame.setLayout(layout);
		frame.add(panel);
		panel.add(sPane);
		sPane.getViewport().add(list);
		frame.add(buttonPanel);
		buttonPanel.add(refresh);
		buttonPanel.add(home);
		buttonPanel.add(download);
		//panel.add(bar);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int)screenSize.getHeight()/2;
		int width = (int)screenSize.getWidth()/2;
		frame.setSize(width, height);
		updateList();
		//new JOptionPane().createDialog(frame, "Error");
		
		refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateList();
				System.out.println(FILE_TO_RECEIVED);
			}
		});
		download.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					int get = list.getSelectedIndex();
					if(get != -1 && (list.getSelectedValue().endsWith("(1)"))){
						//int result = chooser.showOpenDialog(frame);
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int chooseResponse = chooser.showOpenDialog(frame);
						if(chooseResponse == JFileChooser.APPROVE_OPTION){
							FILE_TO_RECEIVED = chooser.getSelectedFile().getAbsolutePath();
							download(get);
						} else {
							new JOptionPane().createDialog(frame, "Error");
						}
					} else if(get != -1){
						out.writeUTF("SEND");
						out.writeInt(get);
						System.out.println();
						//FILE_TO_RECEIVED += list.getSelectedValue().substring(0,(list.getSelectedValue().length()-3)) + "/";
						//file = new File(FILE_TO_RECEIVED);
						//if(!file.exists()){
							//file.mkdir();
							//updateList();
						//}
						updateList();
					}
				} catch(Exception e1){
					e1.printStackTrace();
				}
				System.out.println(FILE_TO_RECEIVED);
			}
		});
		home.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					try {
						out.writeUTF("HOME");
						updateList();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println(FILE_TO_RECEIVED);
			}
		});
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        try{
		        	out.writeUTF("CLOSING");
		        } catch(Exception e1){
		        	
		        }
		       
		    } 
		};
		frame.addWindowListener(exitListener);
		} catch(ConnectException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Couldn't Connect to " + ip + ", Please try again later");
			System.exit(0);
		}
	}
	private void run() throws Exception{
		while(true){
			Thread.sleep(100);
		}
    }
	private static void download(int selectedValue) throws IOException{
		String temp = FILE_TO_RECEIVED + "/" + list.getSelectedValue().substring(0,(list.getSelectedValue().length()-3));
		if(!(new File(temp).exists())){
		current = 0;
		out.writeUTF("SEND");
		out.writeInt(selectedValue);
		int gets = in.readInt();
		System.out.println(temp);
		new File(temp).createNewFile(); //creates an empty file with the name of the selected file
	    byte [] mybytearray  = new byte [gets]; //creates empty byte array with the size sent from the server
	    //bar.setMaximum(gets);
	    //bar.setMinimum(0);
	    InputStream is = socket.getInputStream();
	    FileOutputStream fos = new FileOutputStream(temp);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    //float time1 = (float)System.nanoTime();
	    bytesRead = is.read(mybytearray,0,mybytearray.length);
	    current = bytesRead;
	    if(current < mybytearray.length){
		    do{
		    	
			    bar.setStringPainted(true);
			    bytesRead = is.read(mybytearray, current, mybytearray.length - current);
			    if(bytesRead > 0){
			    current += bytesRead;
			    //bar.setValue(current);
			    }
			    
			    System.out.println(current);
			    
			    } while(bytesRead > 0); 
	    }
	    //float time2 = (float)System.nanoTime();
	    //float Speed = (float)((float)(current/1000.0) / (float)((time2/time1)/1000.0));
	    //System.out.println("Time = " + ((time2/time1)/1000.0));
	    System.out.println("Total Megabytes = " + (current/1000000.0));
	    //System.out.println("Speed = " + Speed  + " MB/sec");
	    bos.write(mybytearray, 0 , current);
	    bos.flush();
	    fos.flush();
	    bos.close();
	    fos.close();
	    System.out.println("File " + temp + " downloaded (" + current + " bytes read)");
		}
		System.gc();
	}
	private static void updateList(){
		try {
			out.writeUTF("REFRESH");
	        int e = in.readInt();
	        byte[] byteA = new byte[e]; 
	        in.read(byteA);
	        filelist = (String[]) Serializer.deserialize(byteA);
	        list.setListData(filelist);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		try{
			Client client = new Client();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			client.run();
			out.writeUTF("CLOSING");
		} catch (Exception e){
			e.printStackTrace();
		}
	}	
}