import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class JerryRat implements Runnable {
    public static final String SERVER_PORT = "8080";
    ServerSocket serverSocket;
    public JerryRat() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
    }



    @Override
    public void run() {
        while(true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String s = in.readLine();
                while (s != null) {
                    String[] s1 = s.split(" ");
                    String s2 = s1[1];
                    File file = new File("res/webroot" + s2);
                    if (!file.exists()) {
                        Scanner scanner = new Scanner(new File("res/webroot" + s2 + "/index.html"));
                        while (scanner.hasNext()) {
                            out.println(scanner.nextLine());
                        }
                    } else {
                        Scanner scanner = new Scanner(file);
                        while (scanner.hasNext()) {
                            out.println(scanner.nextLine());
                        }
                    }

                    s = in.readLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).run();
    }
}
