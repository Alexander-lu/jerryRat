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
        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String st = in.readLine();
                while (st != null) {
                    if (st.startsWith("GET") | st.startsWith("get") | st.startsWith("HEAD") | st.startsWith("head")) {
                        boolean ifHead = false;
                        if (st.startsWith("HEAD") | st.startsWith("head")) {
                            ifHead = true;
                        }
                        boolean ifOld = true;
                        String s2;
                        String s = URLDecoder.decode(st, "utf-8");
                        String s1;
                        if(ifHead){
                            s1 = s.substring(5);
                        }else {
                            s1 = s.substring(4);
                        }

                        if (st.endsWith("HTTP/1.0") | st.endsWith("HTTP/1.1")) {
                            ifOld = false;
                            s2 = s1.substring(0, s1.length() - 9);
                        } else {
                            s2 = s1;
                        }
                        if (ifOld) {
                            if (ifHead) {
                                st = in.readLine();
                                continue;
                            }
                        }
                        if (s2.equals("/endpoints/user-agent")) {
                            String userAgent = in.readLine();
                            while (!userAgent.startsWith("User-Agent")) {
                                userAgent = in.readLine();
                            }
                            String substring = userAgent.substring(12);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String str = sdf.format(new Date());
                            clientSocket.getOutputStream().write(("HTTP/1.0 200 OK" + "\r\n" +
                                    "Date: " + str + "\r\n" + "Server: Apache/11.0" + "\r\n" +
                                    "Content-Length: " + substring.length() + "\r\n" + "Content-Type: " +
                                    "" + "text/html" + ";charset=utf-8" + "\r\n" + "\r\n").getBytes());
                            clientSocket.getOutputStream().write(substring.getBytes());
                            clientSocket.getOutputStream().flush();
                        } else {
                            String pathname = "res/webroot" + s2;
                            File file = new File(pathname);
                            String fileName = "";
                            if (file.isDirectory()) {
                                pathname = "res/webroot" + s2 + "/index.html";
                                File file1 = new File(pathname);
                                fileName = file1.getName();
                            } else {
                                fileName = file.getName();
                            }
                            String contentType;
                            String[] split = fileName.split("\\.");
                            if (split.length == 1) {
                                contentType = "text/html";
                            } else {
                                String s3 = split[split.length - 1];
                                switch (s3) {
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
                                }
                            }
                            try {
                                FileInputStream fr = new FileInputStream(pathname);
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                String str = sdf.format(new Date());
                                File fileLastTime = new File(pathname);
                                long l = fileLastTime.lastModified();
                                long length = fileLastTime.length();
                                if (!ifOld) {
                                    clientSocket.getOutputStream().write(("HTTP/1.0 200 OK" + "\r\n" + "Date: " + str + "\r\n" + "Server: Apache/11.0" + "\r\n" + "Content-Length: " + length + "\r\n" + "Content-Type: " + contentType + ";charset=utf-8" + "\r\n" + "Last-Modified: " + sdf.format(new Date(l)) + "\r\n" + "\r\n").getBytes());
                                    clientSocket.getOutputStream().flush();
                                }
                                byte[] chs = new byte[1024];
                                int len;
                                while ((len = fr.read(chs)) != -1) {
                                    if (!ifHead) {
                                        clientSocket.getOutputStream().write(chs, 0, len);
                                        clientSocket.getOutputStream().flush();
                                    }
                                }
                                fr.close();
                            } catch (FileNotFoundException e) {
                                clientSocket.getOutputStream().write(("HTTP/1.0 404 Not Found" + "\r\n" + "\r\n").getBytes());
                                clientSocket.getOutputStream().flush();
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if (st.startsWith("POST")|st.startsWith("post")){
                        String afterDecode = URLDecoder.decode(st, "utf-8");
                        String deletePost = afterDecode.substring(5);
                        String url = deletePost.substring(0, deletePost.length() - 9);
                        if (url.equals("/endpoints/null")) {
                            File file = new File("res/webroot/null");
                            if(!file.exists()){
                                file.createNewFile();
                            }
                            String contentLength = in.readLine();
                            while (!contentLength.startsWith("Content-Length")) {
                                contentLength = in.readLine();
                            }
                            String[] split = contentLength.split(": ");
                            String length = split[1];
                            int lengthNumber = Integer.parseInt(length);
                            String black = in.readLine();
                            while (!black.equals("")) {
                                black = in.readLine();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream("res/webroot/null");
                            for (int i = 0; i <lengthNumber ; i++) {
                                in.read();
                            }
                            fileOutputStream.close();
                            clientSocket.getOutputStream().write(("HTTP/1.0 204 Not Content" + "\r\n" + "\r\n").getBytes());
                            clientSocket.getOutputStream().flush();
                        }else {
                            File file = new File("res/webroot/emails");
                            if(!file.exists()){
                                file.mkdirs();
                            }
                            String realUrl = "res/webroot"+url;
                            File emailFail = new File(realUrl);
                            try {
                                emailFail.createNewFile();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            String contentLength = in.readLine();
                            while (!contentLength.startsWith("Content-Length")) {
                                contentLength = in.readLine();
                            }
                            String[] split = contentLength.split(": ");
                            String length = split[1];
                            int lengthNumber = Integer.parseInt(length);
                            String black = in.readLine();
                            while (!black.equals("")) {
                                black = in.readLine();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(emailFail,true);
                            for (int i = 0; i <lengthNumber ; i++) {
                                fileOutputStream.write(in.read());
                                fileOutputStream.flush();
                            }
                            fileOutputStream.close();
                        }
                    }
                    else if (st.equals("")) {
                    }
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
