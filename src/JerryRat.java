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
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String s = in.readLine();
                while (s != null) {
                    if (!s.equals("")) {
                        String[] s1 = s.split(" ");
                        String s2 = s1[1];
                        String pathname = "res/webroot" + s2;
                        File file = new File(pathname);
//                    long length = file.length();
                        String mimeType="";
                        if (file.isDirectory()) {
                            pathname = "res/webroot" + s2 + "/index.html";
                            file = new File(pathname);
                            URLConnection connection = file.toURL().openConnection();
                            mimeType = connection.getContentType();
                        }else {
                            URLConnection connection = file.toURL().openConnection();
                            mimeType = connection.getContentType();
                        }
                        try {
                            String outWords = "";
                            FileReader fr = new FileReader(pathname);
                            char[] chs = new char[1024];
                            int readLine = 0;
                            int len;
                            while ((len = fr.read(chs))!=-1) {
                                readLine +=len;
                                outWords+=new String(chs,0,len);
                            }
                            response(out,readLine,pathname,outWords,mimeType);
                            fr.close();
                        } catch (FileNotFoundException e) {
                            out.println("HTTP/1.0 404 Not Found");
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
    public void response (PrintWriter out,int readLine,String pathname,String outWords,String mimeType){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String str = sdf.format(new Date());
        File fileLastTime = new File(pathname);
        long l = fileLastTime.lastModified();
        out.println("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/0.8.4"+"\r\n"+"Content-Length: "+readLine+"\r\n"+"Content-Type: "+ mimeType+"\r\n"+"Last-Modified: "+sdf.format(new Date(l))+"\r\n"+"\r\n"+outWords);
    }
}
