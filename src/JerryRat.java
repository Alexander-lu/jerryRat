import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

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
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String s = in.readLine();
                while (s != null) {
                    if (!s.equals("")) {
                        String[] s1 = s.split(" ");
                        String s2 = s1[1];
                        String pathname = "res/webroot" + s2;
                        File file = new File(pathname);
                        String mimeType="";
                        if (file.isDirectory()) {
                            pathname = "res/webroot" + s2 + "/index.html";
                            File file1 = new File(pathname);
                            URLConnection connection = file1.toURL().openConnection();
                            mimeType = connection.getContentType();
                        }else {
                            URLConnection connection = file.toURL().openConnection();
                            mimeType = connection.getContentType();
                        }
                        try {
                            FileInputStream fr = new FileInputStream(pathname);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String str = sdf.format(new Date());
                            File fileLastTime = new File(pathname);
                            long l = fileLastTime.lastModified();
                            long length = fileLastTime.length();
                            clientSocket.getOutputStream().write(("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/0.8.4"+"\r\n"+"Content-Length: "+length+"\r\n"+"Content-Type: "+ mimeType+"\r\n"+"Last-Modified: "+sdf.format(new Date(l))+"\r\n"+"\r\n").getBytes());
                            clientSocket.getOutputStream().flush();
                            byte[] chs = new byte[1024];
                            int len;
                            while ((len = fr.read(chs))!=-1) {
                                clientSocket.getOutputStream().write(chs,0,len);
                                clientSocket.getOutputStream().flush();
                            }
                            fr.close();
                        } catch (FileNotFoundException e) {
                            clientSocket.getOutputStream().write(("HTTP/1.0 404 Not Found"+"\r\n"+"\r\n").getBytes());
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    s = in.readLine();
                }
            } catch (IOException e) {
              e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).run();
    }
}
