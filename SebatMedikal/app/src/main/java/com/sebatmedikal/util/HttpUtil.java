package com.sebatmedikal.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by orhan on 19.05.2017.
 */
public class HttpUtil {
    private static int timeout = 5000;

    public static String timeoutErrorString = "ERROR_TO";
    public static String otherErrorString = "UNKNOWNERROR";

    private static String JSESSIONID;


    private static final String COOKIES_HEADER = "Set-Cookie";
    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public static String sendGet(String URL) {
        LogUtil.logMessage(HttpUtil.class, Level.INFO, "GET URL: " + URL);
        try {
            URL obj = new URL(URL);
            HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setConnectTimeout(timeout);

            httpConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (SocketTimeoutException socketTimeoutException) {
            return timeoutErrorString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return otherErrorString;
        }
    }

    public static String sendPost(String URL, String BODY) {
        LogUtil.logMessage(HttpUtil.class, Level.INFO, "POSTED URL: " + URL);
        LogUtil.logMessage(HttpUtil.class, Level.INFO, "POSTED BODY: " + BODY);

        try {
            URL url = new URL(URL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(timeout);

            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                httpConnection.setRequestProperty("Cookie",
                        TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
            }

            httpConnection.setRequestMethod("POST");

            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Connection", "keep-alive");

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(BODY.getBytes("UTF8"));
            outputStream.flush();
            outputStream.close();

            InputStream is = httpConnection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Map<String, List<String>> headerFields = httpConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (NullUtil.isNotNull(cookiesHeader)) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            for (int i = 0; i < httpConnection.getHeaderFields().size(); ++i) {
                LogUtil.logMessage(HttpUtil.class, httpConnection.getHeaderFieldKey(i) + " ---> " + httpConnection.getHeaderField(i));
            }

            httpConnection.disconnect();

            LogUtil.logMessage(HttpUtil.class, Level.INFO, "POSTED RESPONSE: " + response.toString());

            return response.toString();
        } catch (SocketTimeoutException socketTimeoutException) {
            return timeoutErrorString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return otherErrorString;
        }
    }
}
