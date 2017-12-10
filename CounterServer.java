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
import java.util.Collections;
import java.net.SocketException;

public class CounterServer implements Runnable {
    private int[] portList;
	private ArrayList<Integer> valueList = new ArrayList<Integer>();
	private ArrayList<Thread> socketPortList = new ArrayList<Thread>();
	private boolean runing;
    private int counter = 0;
    private ConnectTCP ct = new ConnectTCP();
    private int sum = 0;
    
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
            runing=ct.runingStatus();
			setAndStart(socket, oOut);
			runing = true;
			while (runing) {
				asker(socket, oIn, oOut);
            }
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
    /*
    public int getSum() {
        int sum = 0;
        synchronized (valueList) {
            for (Integer i : valueList) {
                sum += i;
            }
            return sum;
        }
    }//getSum*/
    public int getBiggest() {
        synchronized (valueList) {
            return valueList.indexOf(Collections.max(valueList)) + 1;
        }
    }//getBiggest
    /*public int getCount() {
        return counter;
    }//getCount*/
    public class SocketSum extends Thread{
        private final int port;
	    private final int addres;
        private int omaSum = 0;
        private ServerSocket serverSocket;
        private boolean runing = true;
        
      public SocketSum(int addres, int port) throws IOException {
            this.port = port;
            this.addres = addres;
        }//SocketSum
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(5000);
                Socket socket = serverSocket.accept();
                System.out.println("Socket number:"+addres+" Connect port:"+port);
                OutputStream oS = socket.getOutputStream();
                InputStream iS = socket.getInputStream();
                ObjectOutputStream oOn = new ObjectOutputStream(oS);
                ObjectInputStream oIn = new ObjectInputStream(iS);
                int value = 0;
                int tmp = 0; 

                while (runing){

                    value = oIn.readInt();
                    //System.out.println("V:"+value+" S:"+addres);
                    if(value==0){
                        System.out.println(addres + " get value:"+value);
                        break;
                    }
                    tmp = valueList.get(addres);
                    //value += tmp;
                    sum += value;
                    valueList.set(addres,value+tmp);
                    counter++;
                }
                    System.out.println("Close soket:"+addres);
                    socket.close();
                } catch (IOException e) {
                    //System.out.println(addres + " IOException");
                    try {
                        join(1000);
                    } catch (InterruptedException ex) {
                        System.out.println(addres + " Broken ");
                    }
                }
        }//run
    }//SocketSum
    private void asker(Socket socket, ObjectInputStream oIn, ObjectOutputStream oOut) throws IOException {
		while (runing) {
			int cases = oIn.readInt();
			try {
				switch (cases) {

				case 0:

                    runing = false;
                    System.out.println(valueList.toString());
					for (Thread saie : socketPortList) {
						saie.join();
					}
					System.out.println("Case:" + cases + " close...");
					break;

				case 1:
					//System.out.println(valueList.toString());
					//System.out.println("Case:" + cases + "	answer:" + sum);
					oOut.writeInt(sum);
					oOut.flush();
					break;

				case 2:
					//System.out.println(valueList.toString());
					//System.out.println("Case:" + cases + " answer:" + getBiggest());
					oOut.writeInt(getBiggest());
					oOut.flush();
					break;

				case 3:

					//System.out.println(valueList.toString());
					//System.out.println("Case:" + cases + " answer:" + counter);
					oOut.writeInt(counter);
					oOut.flush();
					break;

				default:

                    runing = false;
                    System.out.println(valueList.toString());
					System.out.println("Case:" + cases + " close...");
					oOut.writeInt(-1);
					oOut.flush();
					break;

				}

			} catch (Exception e) {
				e.toString();
			}

        }
    }//asker
}//CounterServer