package com.db.node;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Observable;

public class ReadNode implements java.util.Observer {
    private final String url;
    public ReadNode(String url){
        this.url = url;
    }

    @Override
    public void update(Observable o, Object arg) {

        String url = "http://localhost:1026/update";
        String charset = "UTF-8";
        String param = "value";
        File dbFile = new File("./db.zip");
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
        ) {
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + dbFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(dbFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(dbFile.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();

            writer.append("--" + boundary + "--").append(CRLF).flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int responseCode = 0;
        try {
            responseCode = ((HttpURLConnection) connection).getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(responseCode != 200)
            System.out.println("Couldn't update this node");

    }
}
