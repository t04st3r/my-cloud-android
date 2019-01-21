package org.hopto.mycloud.mycloud.api;

import android.app.Activity;
import android.os.AsyncTask;

import org.hopto.mycloud.mycloud.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Request extends AsyncTask<Void, Void, Boolean>  {

    protected static String serverUrl;
    protected static String accessToken;
    protected static String refreshToken;
    protected Activity activity;


    /**
     * Abstract class for http requests toward API
     * @param activity
     */
    protected Request(Activity activity){
        this.activity = activity;
        if(serverUrl == null){
            serverUrl = activity.getString(R.string.server_url);
        }
    }

    /**
     * Read response data from InputStream
     * @param input
     * @return
     * @throws Exception
     */
    public static String inputToString(InputStream input) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        input.close();
        return sb.toString();
    }

}
