package org.hopto.mycloud.mycloud;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

class Request {

    private final String TAG = "MYCLOUD_REQUEST";
    private final String ctKey = "Content-Type";
    private final String ctValue = "application/x-www-form-urlencoded";
    private String url;
    private String method;
    private String params;

    public Request(String url, String method, Map<String, String> params) {
        this.url = url;
        this.method = method;
        this.params = this.setParams(params);
    }

    /**
     * URL encode params from Map to String
     *
     * @param params
     * @return String
     */
    private String setParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        String encoded = sb.toString();
        Log.w(TAG, encoded);
        return encoded;
    }

    /**
     * Return access_token
     *
     * @return
     */
    public String getAccessToken() {
        String response = null;
        try {

            //prepare the request
            URL url = new URL("http://" + this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(this.method);
            conn.setRequestProperty(this.ctKey, this.ctValue);
            conn.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(this.params);
            os.flush();
            os.close();

            //prepare the response
            int statusCode = conn.getResponseCode();
            InputStreamReader is = null;
            is = statusCode >= 200 && statusCode < 400 ?
                    new InputStreamReader(conn.getInputStream()) :
                    new InputStreamReader(conn.getErrorStream());
            BufferedReader br = new BufferedReader(is);
            String inputLine;
            StringBuffer res = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                res.append(inputLine);
            }
            br.close();
            response = res.toString();

        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
        return response;
    }
}
