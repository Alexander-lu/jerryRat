import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
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
            ) {
                ArrayList<Byte> a = new ArrayList<>();
                InputStream inputStream = clientSocket.getInputStream();
                int OOOO;
                while ((OOOO=inputStream.read()) != -1){
                    if (OOOO==13) {
                        OOOO=inputStream.read();
                        if (OOOO==10) {
                            a.add((byte)13);
                            a.add((byte)10);
                            break;
                        }
                    }else{
                        a.add((byte)OOOO);
                    }
                }
                byte[] bytes = new byte[a.size()];
                for (int i = 0; i < a.size(); i++) {
                    bytes[i] = a.get(i);
                }
                String bytes2String = new String(bytes, "UTF-8");
                String[] split = bytes2String.split("\r\n",2);
                String st = split[0];
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
                    if (s2.equals("/endpoints/user-agent")) {
                        split = split[1].split("\r\n",2);
                        String userAgent = split[0];
                        while (!userAgent.startsWith("User-Agent")) {
                            split = split[1].split("\r\n",2);
                            userAgent = split[0];
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
                        String[] splitName = fileName.split("\\.");
                        if (splitName.length == 1) {
                            contentType = "text/html";
                        } else {
                            String s3 = splitName[splitName.length - 1];
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
                            }
                            byte[] chs = new byte[1024];
                            int len;
                            while ((len = fr.read(chs)) != -1) {
                                if (!ifHead) {
                                    clientSocket.getOutputStream().write(chs, 0, len);
                                }
                            }
                            fr.close();
                        } catch (FileNotFoundException e) {
                            clientSocket.getOutputStream().write(("HTTP/1.0 404 Not Found" + "\r\n" + "\r\n").getBytes());
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (st.startsWith("POST")|st.startsWith("post")){
                while ((OOOO=inputStream.read()) != -1){
                    if (OOOO==13) {
                        OOOO=inputStream.read();
                        if (OOOO==10) {
                            OOOO=inputStream.read();
                            a.add((byte)13);
                            a.add((byte)10);
                            if (OOOO==13) {
                                OOOO=inputStream.read();
                                    break;
                            }else{
                                a.add((byte)OOOO);
                            }
                        }
                    }else{
                        a.add((byte)OOOO);
                    }
                }
                bytes = new byte[a.size()];
                for (int i = 0; i < a.size(); i++) {
                    bytes[i] = a.get(i);
                }
                bytes2String = new String(bytes, "UTF-8");
                split = bytes2String.split("\r\n",2);
                st = split[0];
                String afterDecode = URLDecoder.decode(st, "utf-8");
                String deletePost = afterDecode.substring(5);
                String url = deletePost.substring(0, deletePost.length() - 9);
                if (url.equals("/endpoints/null")) {
                            File file = new File("res/webroot/null");
                            if(!file.exists()){
                                file.createNewFile();
                            }
                            split = split[1].split("\r\n",2);
                            String contentLength = split[0];
                            while (!contentLength.startsWith("Content-Length")) {
                                split = split[1].split("\r\n",2);
                                contentLength = split[0];
                            }
                            String[] splitLength = contentLength.split(": ");
                            String length = splitLength[1];
                            int lengthNumber = Integer.parseInt(length);
                            split = split[1].split("\r\n",2);
                            String black = split[0];
                            while (!black.equals("")) {
                                split = split[1].split("\r\n",2);
                                black = split[0];
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream("res/webroot/null");
                            for (int i = 0; i <lengthNumber ; i++) {
                                inputStream.read();
                            }
                            fileOutputStream.close();
                            clientSocket.getOutputStream().write(("HTTP/1.0 204 Not Content" + "\r\n" + "\r\n").getBytes());
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
                            split = split[1].split("\r\n",2);
                            String contentLength = split[0];

                            while (!contentLength.startsWith("Content-Length")&!contentLength.startsWith("Content-Type")) {
                                split = split[1].split("\r\n",2);
                                contentLength = split[0];

                            }
                            int lengthNumber = 0;
                            String type = "";
                            if (contentLength.startsWith("Content-Length")) {
                                String[] splitLength = contentLength.split(": ");
                                String length = splitLength[1];
                                lengthNumber = Integer.parseInt(length);
                                while (!contentLength.startsWith("Content-Type")) {
                                    split = split[1].split("\r\n",2);
                                    contentLength = split[0];
                                }
                                String[] split1 = contentLength.split(": ");
                                type = split1[1];
                            }else if (contentLength.startsWith("Content-Type")) {
                                String[] split1 = contentLength.split(": ");
                                type = split1[1];
                                while (!contentLength.startsWith("Content-Length")) {
                                    split = split[1].split("\r\n",2);
                                    contentLength = split[0];
                                }
                                String[] splitL = contentLength.split(": ");
                                String length = splitL[1];
                                lengthNumber = Integer.parseInt(length);
                            }

                            FileOutputStream fileOutputStream = new FileOutputStream(emailFail,false);
                            for (int i = 0; i < lengthNumber ; i++) {
                                fileOutputStream.write(inputStream.read());
                            }
                            fileOutputStream.close();
                            clientSocket.getOutputStream().write(("HTTP/1.0 200 OK" + "\r\n" + "\r\n").getBytes());
                        }
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
