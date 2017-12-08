import java.net.*;
import java.io.*;

public class Hellolahettaja {
    public static void main(String[] args) throws Exception {
        int port = 3333;
        Socket s = new Socket("localhost", port);
        System.out.println("Connection made ...");
        InputStream iS = s.getInputStream();
        OutputStream oS = s.getOutputStream();
        // Seuraavan kahden järjestys merkitsevä!
        ObjectOutputStream oOut = new ObjectOutputStream(oS);
        ObjectInputStream oIn = new ObjectInputStream(iS);
        for (int i=0; i<10; i++) {
            String p = "Hello Word";
            oOut.writeObject(p);
            oOut.flush();
            System.out.println("Writing done ... sleeping..");
            Thread.sleep(2000);
            String p1 = (String)oIn.readObject();
            System.out.println("Put " + p);
            System.out.println("Got " + p1);
            } // for
            oIn.close(); oOut.close(); s.close();
            } // main
            } // class MirrorClient