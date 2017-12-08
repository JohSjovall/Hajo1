import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ConnectTCP {
	private boolean runing = true;
	protected int[] ConnectList(Socket socket, ObjectInputStream oIn, ObjectOutputStream oOut) throws Exception {
		int[] portList;
		int t;
		try {
			t = oIn.readInt();
			if (t >= 2 || t <= 10) {
				portList = new int[t];
				for (int i = 0; i < t; i++) {
					portList[i] = (int) (1025 + (Math.random() * 64510));
				}
				System.out.println("ConnectList made " + t + " port numper");
				return portList;
			} // if

		} catch (Exception e) {
			oOut.writeInt(-1);
			oOut.flush();
			runing = false;
		}
		return portList = new int[1];
	}
	public boolean runingStatus(){
        return runing;
        }
	protected Socket makeTCP() throws IOException {
		int portNo = 13370;
		int counter = 0;
		ServerSocket serverSocket = new ServerSocket(portNo);
		Socket socket = new Socket();
		while (counter < 5) {
			try {
				SendUDP();
				serverSocket.setSoTimeout(5000);
				socket = serverSocket.accept();
				serverSocket.close();
				System.out.println("TCP online");
				break;

			} catch (Exception e) {
				counter++;
				System.out.println("Connect fail try:"+counter);
			}
		}
		return socket;
	}//makeTCP
	private void SendUDP() throws IOException {
		int portNo = 13370;
		String port = Integer.toString(portNo);
		DatagramSocket socketUDP = new DatagramSocket();
		byte[] data = port.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 3126);
		socketUDP.send(packet);
		socketUDP.close();
        System.out.println("UDP sended");
	}//SendUDP
	
}