import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {
	static InetAddress ip;
	static int dataPort;
	static int ackPort;
	static String fileName;
	static int seq = 0;
	
	public static void main (String args[]) throws IOException {
		long startTime = 0;
		/* testing for a single number
		int i = 0;
		String str = Integer.toString(i);
		byte[] z = str.getBytes();
		str = new String(z);
		System.out.println((byte) Integer.toString(i).charAt(0));
		*/
		try {
			ip = InetAddress.getByName(args[0]);
			dataPort = Integer.parseInt(args[1]);
			ackPort = Integer.parseInt(args[2]);
			fileName = args[3];
			
		} catch(Exception e){
			System.out.println("Invalid Command Line Arguement\nCommand Line Arguements must follow format:<IP address> <UDP port used by receiver> <UDP port used by sender> <filename>");
		}
		BufferedWriter bw = null;
		try {
			//System.out.println(ip.toString());
			File file = new File(fileName);
			file.createNewFile(); 
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		DatagramSocket s = new DatagramSocket(dataPort);
		byte [] b = new byte[4];

		boolean timer = false;
		while(true) {
			//mds
			DatagramPacket dp = new DatagramPacket(b, b.length, ip, dataPort); //stolen
			s.receive(dp);
			String message = new String(dp.getData(), 0, dp.getLength());
			//System.out.println("Received message: "+message); shown in gui
			byte[] ack = {6, (byte) Integer.toString(seq).charAt(0)}; //jank af
			try {
				if(message.equals("end")){
					break;
				}
				else if(message.equals("alie")) {
					dp = new DatagramPacket(ack, ack.length, ip, ackPort);
					s.send(dp);
					//System.out.println("alive");
				}
				else if(Integer.parseInt(message.substring(message.length() -1)) == seq) {
					if(timer == false) {
						startTime = System.nanoTime();
						timer = true;
					}
					//System.out.println(message.substring(0,message.length() -1));
					bw.write(message.substring(0,message.length()-1));
					dp = new DatagramPacket(ack, ack.length, ip, ackPort);
					s.send(dp);
					bw.flush(); // to check if sent
					if(seq == 0) {
						seq = 1;
					}
					else if(seq == 1) {
						seq = 0;
					}
				}

			}
			catch(Exception e) {
				System.out.println("packet corrupt");
			}
		}
		s.close();
		bw.close();
		long endTime = System.nanoTime();
		long totalTime = (endTime-startTime)/1000000000;
		System.out.println("Total-Transmission-Time: "+totalTime+ " seconds");
	}
}
//4 bytes mds (maximum datagram size)