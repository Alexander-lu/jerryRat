import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    String pathname = "res/webroot" + s2;
                    File file = new File(pathname);
                    String fileHouZui = file.getName();
                    String fileHouZuiR = fileHouZui.substring(fileHouZui.lastIndexOf("." + 1));
                    if (file.isDirectory()) {
                        pathname = "res/webroot" + s2 + "/index.html";
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
        out.println("HTTP/1.0 200,OK");
        SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        String str = sdf.format(new Date());
        out.println("Date:"+str+"GMT");
        out.println("server:JerryRat/1.0");
        out.println("Content-Length="+readLine);
        switch (fileHouZuiR){
            case "jpg":
                out.println("Content-Type: "+"image/GIF;"+"charset=UTF-8");
                break;
            case "txt":
                out.println("Content-Type: "+"text/html;"+"charset=UTF-8");
                break;
            case "jpeg":
                out.println("Content-Type: "+"image/JPEG;"+"charset=UTF-8");
                break;
            default:
                out.println("Content-Type: "+"text/html;"+"charset=UTF-8");
                break;
        }
        File fileLastTime = new File(pathname);
        long l = fileLastTime.lastModified();
        String lastTime = sdf.format(new Date(l));
        out.println("Last-Modified="+lastTime + " GMT");
        out.println(outWords);
    }
}
