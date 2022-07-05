import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
                    String[] s1 = s.split(" ");
                    String s2 = s1[1];
                    String pathname = "res/webroot" + s2;
                    File file = new File(pathname);
                    String fileHouZuiR="";
                    if (file.isDirectory()) {
                        pathname = "res/webroot" + s2 + "/index.html";
                        fileHouZuiR = "html";
                    }else {
                        String fileHouZui = file.getName();
                        fileHouZuiR = fileHouZui.substring(fileHouZui.lastIndexOf(".")+1);
                    }
                    String outWords = "";
                    FileReader fr = new FileReader(pathname);
                    char[] chs = new char[1024];
                    int readLine = 0;
                    int len;
                    while ((len = fr.read(chs))!=-1) {
                        readLine +=len;
                        outWords+=new String(chs,0,len);
                    }
                    response(out,readLine,pathname,outWords,fileHouZuiR);
                    fr.close();
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
    public void response (PrintWriter out,int readLine,String pathname,String outWords,String fileHouZuiR){
        out.println("HTTP/1.0 200 OK");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String str = sdf.format(new Date());
        switch (fileHouZuiR){
            case "gif":
                out.println("Content-Type: "+"image/gif");
                break;
            case "json":
                out.println("Content-Type: "+"application/json");
                break;
            case "word":
                out.println("Content-Type: "+"application/msword");
                break;
            case "pdf":
                out.println("Content-Type: "+"application/pdf");
                break;
            case "png":
                out.println("Content-Type: "+"image/png");
                break;
            case "txt":
                out.println("Content-Type: "+"text/plain");
                break;
            case "jpg":
                out.println("Content-Type: "+"image/jpeg");
                break;
            case "html":
                out.println("Content-Type: "+"text/html");
                break;
            default:
                out.println("Content-Type: "+"text/plain");
                break;
        }
        out.println("Content-Length: "+readLine);
        out.println("Date: "+str);
        File fileLastTime = new File(pathname);
        long l = fileLastTime.lastModified();
        out.println("Last-Modified: "+sdf.format(new Date(l)));
        out.println("Server: Apache/0.8.4");
        out.println(outWords);
    }
}
