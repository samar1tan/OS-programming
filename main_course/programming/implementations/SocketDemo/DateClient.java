import java.net.*;
import java.io.*;

public class DateClient extends Thread {

    private String ip;
    private int port;

    DateClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket sock = new Socket(this.ip, this.port);

            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String date;
            while ((date = in.readLine()) != null) {
                System.out.println(date);
            }

            sock.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

}
