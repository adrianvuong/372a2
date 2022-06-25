import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class Sender extends javax.swing.JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Sender() {
        initComponents();
    }
	private int seq = 0;
	
	public static void main(String args[]) throws IOException {
		/* example of ack and eot
		byte[] c = {6, 4};
		String str = new String(c);
		
		System.out.println();
		
	 	testing substring because forgot
		String test = "hello";
		System.out.println(test.substring(0, test.length()-1));
		
		testing file but forgot how while loops work, so waste of time
		File file = new File("send.txt");
		FileReader fr= new FileReader(file);
		
		while(fr.read() != -1) {
			System.out.println((char)fr.read());
		}
		*/
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Sender().setVisible(true);
            }
        });
        

		/* done in send method
		String data = "";
		
		for(int i = 0; i < 3; i++) {
			data = data + (char) fr.read();
		}
		if (seq == 0) {
			data = data + "0";
			seq = 1;
		}
		else if(seq == 1) {
			data = data + "1";
			seq = 0;
		}
		*/
	
		/*
		byte[] b = data.getBytes();
		for(int i = 0; i<b.length;i++) {
			System.out.println(b[i]);
		}
		
		DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(), ip, 4455);
		s.send(dp);
		System.out.println("Send");
		*/
		
		
		
	}
	public void isAlive(DatagramSocket s, InetAddress ip, int dataPort, int ackPort) throws IOException {
		String alive = "alie";
		try {
			DatagramPacket dp = new DatagramPacket(alive.getBytes(), alive.length(), ip, dataPort);
			s.send(dp);
			byte [] b = new byte[4];
			dp = new DatagramPacket(b, b.length, ip, ackPort);
			s.receive(dp);
			String msg = new String(dp.getData(),0, dp.getLength()); 
			System.out.println("ISALIVE?: "+msg);
		}
		catch (IOException ie) {
			System.out.println("not alive");
		}
		s.close();
		
	}
	public void send(FileReader fr, DatagramSocket s, InetAddress ip, int dataPort, int ackPort, int timeout, boolean unreliable) throws IOException, InterruptedException {
		//used to be int
		DatagramPacket dp = null;
		
		String data;
		// while until end of file then send eot
		int p = 0; //count to 10th packet to drop if unreliable = true
		int c = fr.read();
		int i;
		while(c != -1) {
			data = "";
			i = 0;
			while(i < 3 && c != -1) {
				data = data + (char) c;
				c = fr.read();
				i++;
			}
			//int lastSeq = getSeq();
			if(getSeq() == 0) {
				data = data + "0";
				setSeq(1);
			}
			else if(getSeq() == 1) {
				data = data + "1";
				setSeq(0);
			}
			p++;
			//System.out.println(data);
			dp =new DatagramPacket(data.getBytes(),data.length(),ip, dataPort); //sending packet
			byte[] b = new byte[4];
			
			try {
				if(p % 10 == 0 && unreliable == true) {
					//System.out.println("Dropping 10th packet");
					//Thread.sleep((long)timeout); no
					s.setSoTimeout(timeout);
					DatagramPacket ack = new DatagramPacket(b, b.length, ip, ackPort);
					s.receive(ack);
				}
				else {
					s.send(dp);
					s.setSoTimeout(timeout);
					//s.setSoTimeout(timeout);


					//System.out.println("Sent packet");
					//receiving using ackport

					//String ack = new String(dp.getData(), 0, dp.getLength());
					/*
					if((Character.getNumericValue(ack.charAt(ack.length()-1))) == lastSeq){
						//not sure if needed acks are always fine
					}
					else {
						s.send(dp);
					}
					*/
					//System.out.println(ack);
				}

			}
			catch (SocketTimeoutException ste) { //timeout
				//System.out.println("resending line 157");
				//resend(s, dp, b, ip, ackPort, timeout); idea didnt work
				s.send(dp);
				p++; //p incremented again because packet "sent" again
				//s.setSoTimeout(timeout);
				//DatagramPacket ack = new DatagramPacket(b, b.length, ip, ackPort);
				//s.receive(ack);
				
			}
			DatagramPacket ack = new DatagramPacket(b, b.length, ip, ackPort);
			s.receive(ack);
			numPacketsField.setText(Integer.toString(p));
			numPacketsField.update(numPacketsField.getGraphics()); //neat
    		//numPacketsLbl.setText(Integer.toString(p)); //breaks because java sucks
			//System.out.println("Packet count: "+p);
		}
		data = "end";
		dp = new DatagramPacket(data.getBytes(), data.length(),ip, dataPort);
		s.send(dp);
		s.close();
		//return p;
	}
	public void resend(DatagramSocket s, DatagramPacket dp, byte [] b, InetAddress ip, int ackPort, int timeout) { //dont look at this
		try {
			s.send(dp);
			DatagramPacket ack = new DatagramPacket(b, b.length, ip, ackPort);
			s.receive(ack);
		}
		catch (SocketTimeoutException ste){
			System.out.println("resending in method");
			resend(s, dp, b, ip, ackPort, timeout);
		}
		catch (IOException ioe) {
			System.out.println("broke in method");
		}
	}
	
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getSeq() {
		return seq;
	}
	//todo isalive function, done now
	
	//gui here
	private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ipField = new javax.swing.JTextField();
        dataField = new javax.swing.JTextField();
        ackField = new javax.swing.JTextField();
        fileField = new javax.swing.JTextField();
        timeoutField = new javax.swing.JTextField();
        ipLbl = new javax.swing.JLabel();
        dPortLbl = new javax.swing.JLabel();
        ackPortLbl = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        packetLbl = new javax.swing.JLabel();
        timeoutLabel = new javax.swing.JLabel();
        aliveBtn = new javax.swing.JButton();
        //aliveBtn = new javax.swing.JToggleButton(); toggle button toggles on and off, not valueble for this so i just disabled the send button
        sendBtn = new javax.swing.JButton();
        reliableBox = new javax.swing.JCheckBox();
        numPacketsField = new javax.swing.JTextField();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("File Transfer");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(310, 240));
        setResizable(false);

        ipField.setText("127.0.0.1");
 
        dataField.setText("4455");

        ackField.setText("3321");

        fileField.setText("send.txt");

        timeoutField.setText("5000");
 
        ipLbl.setText("IP Address:");

        dPortLbl.setText("Data Port:");

        ackPortLbl.setText("ACKs Port:");

        fileNameLabel.setText("Filename:");

        packetLbl.setText("Number of Sent in-order packets:");

        timeoutLabel.setText("Timeout (us):");

        aliveBtn.setText("ISALIVE?");
        aliveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aliveBtnActionPerformed(evt);
            }
        });

        sendBtn.setText("SEND");
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBtnActionPerformed(evt);
            }
        });
        reliableBox.setText("Unreliable?");

        numPacketsField.setEditable(false);
        numPacketsField.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(aliveBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(fileNameLabel)
                            .addComponent(ackPortLbl)
                            .addComponent(ipLbl)
                            .addComponent(dPortLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dataField)
                            .addComponent(ipField)
                            .addComponent(ackField)
                            .addComponent(fileField, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(reliableBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(timeoutLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeoutField, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packetLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numPacketsField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ipLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dPortLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ackPortLbl)
                    .addComponent(ackField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameLabel)
                    .addComponent(fileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sendBtn)
                    .addComponent(aliveBtn, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reliableBox)
                    .addComponent(timeoutLabel)
                    .addComponent(timeoutField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packetLbl)
                    .addComponent(numPacketsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }   

    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {                                        

    	int dataPort, ackPort, timeout;
    	File file;
    	FileReader fr = null;
    	DatagramSocket s = null;
        try {
        	InetAddress ip = InetAddress.getByName(ipField.getText());
    		dataPort = Integer.parseInt(dataField.getText());
    		ackPort = Integer.parseInt(ackField.getText());
        	file = new File(fileField.getText());
        	fr = new FileReader(file);
    		try {
    			timeout = Integer.parseInt(timeoutField.getText())/1000;
    		}
    		catch(Exception e) {
    			timeout = 2; //timeout = 1 breaks everything
    		}
    		
        	s = new DatagramSocket(ackPort);
    	
    		//int num;
			//System.out.println(file.getName()+"\n"+fr.read()+"\n"+ip+"\n"+dataPort+"\n"+ackPort+"\n"+timeout); //testing
    		if(reliableBox.isSelected()) {
    			//System.out.println("unreliable");
    			send(fr,s,ip, dataPort, ackPort, timeout, true);
    		}
    		else {
    			//System.out.println("reliable");
    			send(fr,s,ip, dataPort, ackPort, timeout, false);
    		}
    		sendBtn.setEnabled(false);
    		aliveBtn.setEnabled(false);
    		reliableBox.setEnabled(false);
    		//numPacketsLbl.setText(Integer.toString(num));

    		//timeout = timeout from gui * 1000 
    		//try catch throws socket timeout exception
    		
    		//should be found in gui
    		/* done in send method now
    		try {
    			send(fr, s, ip, port);
    			s.setSoTimeout(Integer.parseInt(timeoutField.getText()));
    		}
    		catch(SocketTimeoutException ste) {
    			System.out.println("ACK timeout so we shall resend previous datagram >:(");
    			send(fr, s, ip, port);
    		}
    		*/
        }
        catch (Exception e) {
            	System.out.println("Invalid input"); //make dialog box maybe
        }
    }

                                          

    private void aliveBtnActionPerformed(java.awt.event.ActionEvent evt) {     
    	DatagramSocket s = null;
        try {
        	InetAddress ip = InetAddress.getByName(ipField.getText());
    		int dataPort = Integer.parseInt(dataField.getText());
    		int ackPort = Integer.parseInt(ackField.getText());
        	s = new DatagramSocket(ackPort);
        	//s.setSoTimeout(Integer.parseInt(timeoutField.getText())/1000);
        	isAlive(s, ip, dataPort, ackPort); // is_connected() works i guess but this does too
        	/* no point probably
    		try {
    			isAlive(s, ip, dataPort, ackPort);
    		}
    		catch(SocketTimeoutException ste) { //sending one packet so this is fine
    			System.out.println("ACK timeout so we shall resend previous datagram >:("); //paige wrote this
    			isAlive(s, ip, dataPort, ackPort);
    		}
    		*/
        }
        catch (Exception e) {
            	System.out.println("Invalid input"); //make dialog box maybe
        }
        sendBtn.setEnabled(true);
        aliveBtn.setEnabled(false);
    }                                        
	
    private javax.swing.JTextField ackField;
    private javax.swing.JLabel ackPortLbl;
    //private javax.swing.JToggleButton aliveBtn;
    private javax.swing.JButton aliveBtn;
    private javax.swing.JLabel dPortLbl;
    private javax.swing.JTextField dataField;
    private javax.swing.JTextField fileField;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField ipField;
    private javax.swing.JLabel ipLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField numPacketsField;
    private javax.swing.JLabel packetLbl;
    private javax.swing.JCheckBox reliableBox;
    private javax.swing.JButton sendBtn;
    private javax.swing.JTextField timeoutField;
    private javax.swing.JLabel timeoutLabel;
}
//try catch timeout exception in microseconds