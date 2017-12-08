import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Hellovastaaja {
public static final int PORTTI = 3128;
private static ArrayList<Integer> luvut = new ArrayList<Integer>();
private static int[] porttiTaulukko;
//private static SokettiKuuntelija[] soketit;
public static void main(String[] args) throws Exception {
    Socket cs = new Socket();
    int yritys = 0;
    while (yritys<5) {
        try{
            int porttiNo = 3128;
            String portti = Integer.toString(porttiNo);
            try (DatagramSocket UDPSoketti = new DatagramSocket()) {
                byte[] data = portti.getBytes();
                DatagramPacket packett = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 3126);
                UDPSoketti.send(packett);
            }
            System.out.println("UDP lähetetty!");
            ServerSocket ss = new ServerSocket(PORTTI);
            ss.setSoTimeout(5000);
            cs = ss.accept();
            ss.close();
            System.out.println("Yhteyssaatu:"+cs.getInetAddress()+" portti:"+cs.getPort());
            break;
        }//try
        catch (Exception e){
            yritys++;
            System.out.println("Yritys "+yritys+" epäonnistui");
            //lopeta();
        } //catch
        System.out.println("Connection from " + cs.getInetAddress() + "port " + cs.getPort());
        } // while
        new Handler(cs).start();
    } // main

    static class Handler extends Thread {
        private Socket client;
    public Handler(Socket s) { client = s; }
    public void run() {
        try {
            System.out.println("Spawning thread ...");
            InputStream iS = client.getInputStream();
            OutputStream oS = client.getOutputStream();
            ObjectOutputStream oOut = new ObjectOutputStream(oS);
            ObjectInputStream oIn = new ObjectInputStream(iS);
            try {
                int t;
                //oIn.setSoTimeout(5000);
                t = (int)oIn.readObject();
                if(t>=2||t<=10){
                    porttiTaulukko = new int[t]; 
                    System.out.println("tarvittavien portien määrä on "+t );
                    for(int i = 0; i < t; i++){
                        porttiTaulukko[i] = (int) (1025 + (Math.random() * 64510));
                        System.out.println("luotu portti: "+porttiTaulukko[i]);
                    }//for
                }//if
                portit(porttiTaulukko);
            }//try
            catch (IOException e) {
                oOut.writeInt(-1);
                oOut.flush();
                client.close();
            }//cath
        }//try
        catch (Exception e) {
            throw new Error(e.toString());
            }
        System.out.println("... thread done.");
        } // run
    public void portit (int[] portit){
        //soketit = new Socket[portit.length];
        for (int i = 0; i < portit.length; i++) {
			//soketit[i] = new SokettiKuuntelija (i, portit[i]);
            //soketit[i].start();
			// käynnistetään säie
		}
    }
    static class SokettiKuuntelija extends Thread {
        private final int portti;
		private final int saieId;
		private int omaSum = 0;
		private ServerSocket ss;
        SokettiKuuntelija (int saieId, int portti) {
			this.portti = portti;
            this.saieId = saieId;
		}
        public void run() {
			try {
				ss = new ServerSocket(portti);
				ss.setSoTimeout(5000);
				Socket soketti = ss.accept();
				System.out.println("Soketin TCP muodostettu portissa " + portti);
				InputStream iS = soketti.getInputStream();
				ObjectInputStream oIn = new ObjectInputStream(iS);

				int lisattava = 0;
				int tmp;

				while (true) {
					lisattava = oIn.readInt();
					if (lisattava == 0)
						break;
					tmp = luvut.get(saieId);
					lisattava += tmp;
					luvut.set(saieId, lisattava);

				} // while
				soketti.close();
			} catch (IOException e) {
				try {
					join(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} // catch

		}//SokettiKuuntelija

    }
    } // class Handler
} // class Server