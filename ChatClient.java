import java.awt.GridLayout;
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
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 * @author orimiller
 * This is a Java Client/Server Socket-based file download program 
 * developed by Ori Miller for his 11th grade (Junior) year 
 * independent study of Computer Science
 *
 */
public class Client {
		private static String ip = "127.0.0.1";
		private static File file;
		private static int bytesRead;
	    private static int current;
	    private static FileOutputStream fos = null;
	    private static BufferedOutputStream bos = null;
		private static Socket socket;
		private static DataInputStream in;
		private static DataOutputStream out;
		private static String[] filelist;
		private static JButton refresh = new JButton("Refresh");
		private static JButton download = new JButton("Download");
		private static JList<String> list = new JList<String>();
		private static JScrollPane sPane = new JScrollPane();
		private static JPanel panel = new JPanel();
		private static JPanel buttonPanel = new JPanel();
		private static JFrame frame = new JFrame("File Transfer Client" + ip);
		private final static String FILE_TO_RECEIVED_ORG = "/Users/orimiller/TEMP1/";
		public static String FILE_TO_RECEIVED = "/Users/orimiller/TEMP1/";
		
	public Client() throws UnknownHostException, IOException{
		
		socket = new Socket(ip,2525);
	    in = new DataInputStream(socket.getInputStream());
	    out = new DataOutputStream(socket.getOutputStream());
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
		buttonPanel.add(download);		
		//frame.setSize(400, 400);
		frame.pack();
		updateList();
		refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateList();
			}
		});
		download.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					int get = list.getSelectedIndex();
					if(get != -1 && (list.getSelectedValue().endsWith("(1)"))){
						current = 0;
						out.writeUTF("SEND");
						out.writeInt(get);
						int gets = in.readInt();
						FILE_TO_RECEIVED += list.getSelectedValue().substring(0,(list.getSelectedValue().length()-3));
						System.out.println(FILE_TO_RECEIVED);
						file = new File(FILE_TO_RECEIVED);
						file.createNewFile();
					    byte [] mybytearray  = new byte [gets];
					    InputStream is = socket.getInputStream();
					    fos = new FileOutputStream(FILE_TO_RECEIVED);
					    bos = new BufferedOutputStream(fos);
					    bytesRead = is.read(mybytearray,0,mybytearray.length);
					    current = bytesRead;
					    if(current < mybytearray.length){
						    do{
							    bytesRead = is.read(mybytearray, current, mybytearray.length - current);
							    if(bytesRead > 0){
							    current += bytesRead;
							    }
							    System.out.println(current);
						    } while(bytesRead > 0); 
					    } 
					    bos.write(mybytearray, 0 , current);
					    bos.flush();
					    System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + current + " bytes read)"); 
					} else if(get != -1){
						out.writeUTF("SEND");
						out.writeInt(get);
						FILE_TO_RECEIVED += list.getSelectedValue().substring(0,(list.getSelectedValue().length()-3)) + "/";
						File file = new File(FILE_TO_RECEIVED);
						if(!file.exists()){
							file.mkdir();
							updateList();
						}
					}
				} catch(Exception e1){
					e1.printStackTrace();
				}
			}
		});
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        int confirm = JOptionPane.showOptionDialog(
		             null, "Are You Sure to Close Application?", 
		             "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
		             JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == 0) {
		        	try {
						out.writeUTF("CLOSING");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		           System.exit(0);
		        }
		    }
		};
		frame.addWindowListener(exitListener);
	}
	private void run() throws Exception{
		while(true){
			
		}
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
