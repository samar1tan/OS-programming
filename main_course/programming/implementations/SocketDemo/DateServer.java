import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class DateServer {

    public static void main (String[] args) {
        String port = "null";
        Scanner scan = new Scanner(System.in);
        System.out.println("Input the port number to bind: ");
        if (scan.hasNext()) {
            port = scan.next();
        }

        try {
            ServerSocket sock = new ServerSocket(Integer.parseInt(port));

            DateClient client = new DateClient("127.0.0.1", Integer.parseInt(port));
            client.start();

            Socket query = sock.accept();

            PrintWriter pout = new PrintWriter(query.getOutputStream(), true);
            pout.println(new Date().toString());

            query.close();

            client.join();
        } catch (NumberFormatException nfe) {
            System.err.println(nfe);
        } catch(IOException ioe) {
            System.err.println(ioe);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }

}
