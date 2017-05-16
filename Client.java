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

import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Client {
		private String ip;
		private Socket socket;
		private int bytesRead;
		private int current;
		private DataInputStream in;
		private DataOutputStream out;
		private String[] filelist;
		private JButton refresh = new JButton("Refresh");
		private JButton download = new JButton("Download");
		private JButton home = new JButton("Home");
		private JList<String> list = new JList<String>();
		private JScrollPane sPane = new JScrollPane();
		private JPanel panel = new JPanel();
		private JPanel buttonPanel = new JPanel();
		private static JFrame frame = new JFrame("Java File Transfer Client");
		private JFileChooser chooser = new JFileChooser();
		private String FILE_TO_RECEIVED;
		
	    public Client() throws UnknownHostException, IOException{
		try{
		new JOptionPane();
		ip = JOptionPane.showInputDialog("Please Enter IP Address of Server");
		if(ip.equals(null)){
			System.exit(0);
		}
		socket = new Socket(ip,2525);
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
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int)screenSize.getHeight()/2;
		int width = (int)screenSize.getWidth()/2;
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		updateList();
		new JOptionPane().createDialog(frame, "Error");
		
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
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int chooseResponse = chooser.showOpenDialog(frame);
						if(chooseResponse == JFileChooser.APPROVE_OPTION){
							FILE_TO_RECEIVED = chooser.getSelectedFile().getAbsolutePath();
							download(get);
						} else {
							new JOptionPane().createDialog(frame, "Error");
						}
					} else if(get != -1){
						out.writeInt(2);
						out.write(get);
						System.out.println(get);
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
						out.writeInt(3);
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
		        	out.writeInt(4);
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
	private void download(int selectedValue) throws IOException{
		String temp = FILE_TO_RECEIVED + "/" + list.getSelectedValue().substring(0,(list.getSelectedValue().length()-3));
		if(!(new File(temp).exists())){
		current = 0;
		out.writeInt(2);
		out.writeInt(selectedValue);
		int gets = in.readInt();
		System.out.println(gets);
		System.out.println(temp);
		new File(temp).createNewFile(); //creates an empty file with the name of the selected file
	    byte[] mybytearray  = new byte[gets]; //creates empty byte array with the size sent from the server
	    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
	    bytesRead = in.read(mybytearray,0,mybytearray.length);
	    current = bytesRead;
	    if(current < mybytearray.length){
		    do{
			    bytesRead = in.read(mybytearray, current, mybytearray.length - current);
			    if(bytesRead > 0){
			    	current += bytesRead;
			    }
			    	System.out.println(current);
			    } while(bytesRead > 0); 
	    }
	    System.out.println("total bytes = " + current);
	    bos.write(mybytearray, 0 , current);
	    bos.flush();
	    bos.close();
	    System.out.println("File " + temp + " downloaded (" + current + " bytes read)");
		}
		
		
		
		
	}
	private void updateList(){
		try {
			System.out.println("REFRESH");
			out.writeInt(1);
			System.out.println("REFRESH-1");
	        int e = in.readInt();
	        System.out.println(e);
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
			client.run();
		} catch (Exception e){
			e.printStackTrace();
		}
	}	
}