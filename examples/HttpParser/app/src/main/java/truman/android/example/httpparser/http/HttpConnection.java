package truman.android.example.httpparser.http;

import static truman.android.example.httpparser.http.HttpConstants.EMPTY;
import static truman.android.example.httpparser.http.HttpConstants.METHOD;
import static truman.android.example.httpparser.http.HttpConstants.REQ_GET;
import static truman.android.example.httpparser.http.HttpConstants.REQ_POST;
import static truman.android.example.httpparser.http.HttpConstants.RPATH;
import static truman.android.example.httpparser.http.HttpConstants.VERSION;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import truman.android.example.httpparser.ui.Ui;

public class HttpConnection {

   private static final String TAG = HttpConnection.class.getSimpleName() + ".2ruman";
   private static final boolean DEBUG = true;

   private final Socket clientSocket;
   private HttpServerCallback serverCallback;

   private HttpConnection(Socket clientSocket) {
      this.clientSocket = clientSocket;
   }

   public static HttpConnection create(Socket clientSocket) {
      return new HttpConnection(clientSocket);
   }

   public HttpConnection setServerCallback(HttpServerCallback serverCallback) {
      this.serverCallback = serverCallback;
      return this;
   }

   public void listen() {
      try (BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
           BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream())) {
         while (true) {
            Bundle request = getRequest(bis);
            String method = request.getString(METHOD, EMPTY);
            switch (method) {
               case REQ_GET:
                  handleGet(request, bis, bos);
                  break;
               case REQ_POST:
                  handlePost(request, bis, bos);
                  break;
               default:
                  throw new UnsupportedOperationException("Not supported method: " + method);
            }
         }
      } catch (Exception e) {
         Ui.println("Client disconnected: " + e.getMessage());
      }
   }

   private Bundle getRequest(BufferedInputStream bis) throws IOException {
      String line = readLine(bis);
      if (DEBUG) {
         Log.d(TAG, "getRequest: " + line.strip());
      }

      Bundle bundle = new Bundle();
      StringTokenizer st = new StringTokenizer(line);

      if (!st.hasMoreTokens()) {
         throw new IOException("Empty method");
      }
      String method = st.nextToken();
      bundle.putString(METHOD, method);

      if (!st.hasMoreTokens()) {
         throw new IOException("Empty resource path");
      }
      String rpath = st.nextToken();
      bundle.putString(RPATH, rpath);

      if (!st.hasMoreTokens()) {
         throw new IOException("Empty version");
      }
      String version = st.nextToken();
      bundle.putString(VERSION, version);

      return bundle;
   }

   private void handleGet(Bundle request, BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
      String line;
      while (!(line = readLine(bis)).isEmpty()) {
         if (DEBUG) {
            Log.d(TAG, "line : " + line);
         }
         if (isEmptyLine(line)) {
            break;
         }
         splitLineAsRequest(line, request);
      }
      if (DEBUG) {
         Log.d(TAG, "End <GET> request");
      }
      if (serverCallback != null) {
         serverCallback.onGet(request);
      }
      bis.close();
      bos.close();
   }

   private void handlePost(Bundle request, BufferedInputStream bis, BufferedOutputStream bos) throws IOException {

      int contentLen = 0;
      String contentType = "";
      Map<String, String> contentTypeParams = new HashMap<>();

      String line;
      while (!(line = readLine(bis)).isEmpty()) {
         if (DEBUG) {
            Log.d(TAG, "line : " + line);
         }

         if (line.toLowerCase().startsWith("content-type:")) {
            var parsed = parseValueAndParams(line);
            contentType = parsed.first;
            contentTypeParams = parsed.second;
         } else if (line.toLowerCase().startsWith("content-length:")) {
            contentLen = parseInteger(line);
         }

         if (isEmptyLine(line)) {
            if (DEBUG) {
               Log.d(TAG, "[ Empty line ]");
               Log.d(TAG, "Content type: " + contentType + ", params: " + contentTypeParams);
               Log.d(TAG, "Content length: " + contentLen);
            }
            if (contentLen > 0) {
               byte[] buff = bis.readNBytes(contentLen);
               if (serverCallback != null) {
                  serverCallback.onPost(request, buff);
               }
            }
            break;
         } else {
            splitLineAsRequest(line, request);
         }
      }
      if (DEBUG) {
         Log.d(TAG, "End <POST> request");
      }
      bis.close();
      bos.close();
   }

   private String readLine(InputStream bis) throws IOException {
      StringBuffer sb = new StringBuffer();
      int ch = 0;

      while (true) {
         int val = bis.read();
         if (val == -1) break;

         if (ch == '\r' & ((ch = val) == '\n')) {
            sb.append((char) ch);
            break;
         }
         sb.append((char) ch);
      }
      return sb.toString();
   }

   private boolean isEmptyLine(String line) {
      return "\r\n".equals(line);
   }

   private void splitLineAsRequest(String line, Bundle request) {
      String[] parts = line.split(":", 2);
      if (parts.length == 2) {
         String key = parts[0].trim();
         String val = parts[1].trim();
         request.putString(key, val);
      }
   }

   private int parseInteger(String line) {
      int length = 0;
      try {
         length = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
      } catch (NumberFormatException ignored) {
      }
      return length;
   }

   private Pair<String, Map<String, String>> parseValueAndParams(String line) {
      String values = line.substring(line.indexOf(":") + 1).trim();
      String[] parts = values.split(";");

      String value = parts[0];

      Map<String, String> params = new HashMap<>();
      for (int i = 1 ; i < parts.length ; i++) {
         String[] param = parts[i].split("=");
         if (param.length == 2) {
            params.put(param[0].trim(), param[1].trim());
         }
      }
      return Pair.create(value, params);
   }
}
