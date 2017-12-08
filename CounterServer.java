import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectException;
import java.util.ArrayList;

public class CounterServer implements Runnable {
    private int[] portList;
	private ArrayList<Integer> valueList = new ArrayList<Integer>();
	private ArrayList<Thread> socketPortList = new ArrayList<Thread>();
	private boolean running;
    private int counter = 0;
    private ConnectTCP ct = new ConnectTCP();
    
    public static void main(String[] args) {
		CounterServer counterServer = new CounterServer();
		counterServer.run();
	}

    public void run() {
		Socket socket = new Socket();
		ObjectOutputStream oOut = null;
		ObjectInputStream oIn = null;

		try {
			socket = ct.makeTCP();
			System.out.println("My port: " + socket.getPort());

			OutputStream oS = socket.getOutputStream();
			InputStream iS = socket.getInputStream();
			oOut = new ObjectOutputStream(oS);
			oIn = new ObjectInputStream(iS);

			portList = ct.ConnectList(socket, oIn, oOut);
			setAndStart(socket, oOut);
			running = true;
			//while (running) {
			//	asker(socket, oIn, oOut);
			//}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
        }
    }//run
    public void setAndStart(Socket socket, ObjectOutputStream oOut) throws IOException {
        for (int i = 0; i < portList.length; i++) {
            socketPortList.add(new SocketSum(i, portList[i]));
            valueList.add(i, 0);
            socketPortList.get(i).start();
        }
        for (int k = 0; k < portList.length; k++) {
            oOut.writeInt(portList[k]);
            oOut.flush();
        }
            
    }//setAndSend
    public class SocketSum extends Thread{
        private final int port;
	    private final int addres;
        private int omaSum = 0;
        private ServerSocket serverSocket;
        private boolean runing = true;
        
      public SocketSum(int addres, int port){
            this.port = port;
            this.addres = addres;
        }//SocketSum
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(5000);
                Socket socket = serverSocket.accept();
                System.out.println("Socket number:"+addres+" Connect port:"+port);

                InputStream iS = socket.getInputStream();
                ObjectInputStream oIn = new ObjectInputStream(iS);
                
                int value = 0;
                int tmp = 0;

                while (true){
                    value = oIn.readInt();
                    if(value==0||runing){
                        break;
                    }
                    tmp = valueList.get(addres);
                    value += tmp;
                    valueList.set(addres,value);
                    counter++;
                }
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
            }
        }//run
    }//SocketSum
}//CounterServer