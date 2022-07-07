import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                String st = in.readLine();
                while (st != null) {
                   if (st.startsWith("GET")|st.startsWith("get")) {
                       if(st.endsWith("HTTP/1.0")|st.endsWith("HTTP/1.1")){
                       String s = URLDecoder.decode(st, "utf-8");
                       String s1 = s.substring(4);
                        String s2 = s1.substring(0,s1.length()-9);
                        if (s2.equals("/endpoints/user-agent")) {
                            String userAgent = in.readLine();
                            while(!userAgent.startsWith("User-Agent")){
                                userAgent = in.readLine();
                            }
                            String substring = userAgent.substring(12);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String str = sdf.format(new Date());
                            clientSocket.getOutputStream().write(("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/11.0"+"\r\n"+"Content-Type: "+ "text/html"+";charset=utf-8"+"\r\n"+"\r\n").getBytes());
                            clientSocket.getOutputStream().write(substring.getBytes());
                        }else{

                            String pathname = "res/webroot" + s2;
                            File file = new File(pathname);
                            String fileName="";
                            if (file.isDirectory()) {
                                pathname = "res/webroot" + s2 + "/index.html";
                                File file1 = new File(pathname);
                                fileName = file1.getName();
                            }else {
                                fileName = file.getName();
                            }
                            String contentType;
                            String[] split = fileName.split("\\.");

                            if (split.length == 1) {
                                contentType = "text/html";
                            }else {
                                String s3 = split[split.length - 1];
                                switch (s3){
                                    case "pdf":
                                        contentType = "application/pdf";
                                        break;
                                    case "ai":
                                        contentType = "application/postscript";
                                        break;
                                    case "xml":
                                        contentType = "application/atom+xml";
                                        break;
                                    case "json":
                                        contentType = "application/json";
                                        break;
                                    case "doc":
                                        contentType = "application/msword";
                                        break;
                                    case "js":
                                        contentType = "application/javascript";
                                        break;
                                    case "css":
                                        contentType = "text/css";
                                        break;
                                    case "html":
                                        contentType = "text/html";
                                        break;
                                    case "jpg":
                                        contentType = "image/jpeg";
                                        break;
                                    case "tiff":
                                        contentType = "image/tiff";
                                        break;
                                    case "gif":
                                        contentType = "image/gif";
                                        break;
                                    case "png":
                                        contentType = "image/png";
                                        break;
                                    case "jpeg":
                                        contentType = "image/jpeg";
                                        break;
                                    case "wbmp":
                                        contentType = "image/vnd.wap.wbmp";
                                        break;
                                    case "jpe":
                                        contentType = "image/jpeg";
                                        break;
                                    default:
                                        contentType = "text/html";
                                        break;
                                } }
                            try {
                                FileInputStream fr = new FileInputStream(pathname);
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                String str = sdf.format(new Date());
                                File fileLastTime = new File(pathname);
                                long l = fileLastTime.lastModified();
                                long length = fileLastTime.length();
                                clientSocket.getOutputStream().write(("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/11.0"+"\r\n"+"Content-Length: "+length+"\r\n"+"Content-Type: "+ contentType+";charset=utf-8"+"\r\n"+"Last-Modified: "+sdf.format(new Date(l))+"\r\n"+"\r\n").getBytes());
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
                        }}
                       else {
                           String s = URLDecoder.decode(st, "utf-8");
                           String s2 = s.substring(4);
                           if (s2.equals("/endpoints/user-agent")) {
                               String userAgent = in.readLine();
                               while(!userAgent.startsWith("User-Agent")){
                                   userAgent = in.readLine();
                               }
                               String substring = userAgent.substring(12);
                               SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                               sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                               String str = sdf.format(new Date());
                               clientSocket.getOutputStream().write(("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/11.0"+"\r\n"+"Content-Type: "+ "text/html"+";charset=utf-8"+"\r\n"+"\r\n").getBytes());
                               clientSocket.getOutputStream().write(substring.getBytes());
                           }else{

                               String pathname = "res/webroot" + s2;
                               File file = new File(pathname);
                               String fileName="";
                               if (file.isDirectory()) {
                                   pathname = "res/webroot" + s2 + "/index.html";
                                   File file1 = new File(pathname);
                                   fileName = file1.getName();
                               }else {
                                   fileName = file.getName();
                               }
                               String contentType;
                               String[] split = fileName.split("\\.");

                               if (split.length == 1) {
                                   contentType = "text/html";
                               }else {
                                   String s3 = split[split.length - 1];
                                   switch (s3){
                                       case "pdf":
                                           contentType = "application/pdf";
                                           break;
                                       case "ai":
                                           contentType = "application/postscript";
                                           break;
                                       case "xml":
                                           contentType = "application/atom+xml";
                                           break;
                                       case "json":
                                           contentType = "application/json";
                                           break;
                                       case "doc":
                                           contentType = "application/msword";
                                           break;
                                       case "js":
                                           contentType = "application/javascript";
                                           break;
                                       case "css":
                                           contentType = "text/css";
                                           break;
                                       case "html":
                                           contentType = "text/html";
                                           break;
                                       case "jpg":
                                           contentType = "image/jpeg";
                                           break;
                                       case "tiff":
                                           contentType = "image/tiff";
                                           break;
                                       case "gif":
                                           contentType = "image/gif";
                                           break;
                                       case "png":
                                           contentType = "image/png";
                                           break;
                                       case "jpeg":
                                           contentType = "image/jpeg";
                                           break;
                                       case "wbmp":
                                           contentType = "image/vnd.wap.wbmp";
                                           break;
                                       case "jpe":
                                           contentType = "image/jpeg";
                                           break;
                                       default:
                                           contentType = "text/html";
                                           break;
                                   } }
                               try {
                                   FileInputStream fr = new FileInputStream(pathname);
                                   SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                                   sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                   String str = sdf.format(new Date());
                                   File fileLastTime = new File(pathname);
                                   long l = fileLastTime.lastModified();
                                   long length = fileLastTime.length();
                                   clientSocket.getOutputStream().write(("HTTP/1.0 200 OK"+"\r\n"+"Date: "+str+"\r\n"+"Server: Apache/11.0"+"\r\n"+"Content-Length: "+length+"\r\n"+"Content-Type: "+ contentType+";charset=utf-8"+"\r\n"+"Last-Modified: "+sdf.format(new Date(l))+"\r\n"+"\r\n").getBytes());
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
                               }}
                        }
                   }else if (st.equals("")) {}
                    st = in.readLine();
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
