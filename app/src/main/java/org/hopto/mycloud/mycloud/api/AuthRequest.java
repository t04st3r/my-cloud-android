package org.hopto.mycloud.mycloud.api;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.hopto.mycloud.mycloud.LoginActivity;
import org.hopto.mycloud.mycloud.R;
import org.json.JSONObject;

public class AuthRequest extends Request {

    private Map<String, String> authParams;
    private String username;
    private String password;
    private final String TAG = "AUTH_REQUEST";
    private final String contentTypeAuth = "application/x-www-form-urlencoded";
    private final String tokenPath = "/o/token/";

    /**
     * Handles authentication toward API
     *
     * @param activity
     */
    public AuthRequest(LoginActivity activity, String username, String password) {
        super(activity);
        this.username = username;
        this.password = password;
        loadAuthParams();
    }

    /**
     * Load params for authentication from auth.xml resource file
     */
    private void loadAuthParams(){
        if(authParams == null){
            authParams = new HashMap<>();
            authParams.put("grant_type", activity.getString(R.string.grant_type));
            authParams.put("username", username);
            authParams.put("password", password);
            authParams.put("scope", activity.getString(R.string.scope));
            authParams.put("client_secret", activity.getString(R.string.client_secret));
            authParams.put("client_id", activity.getString(R.string.client_id));
        }
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
        return sb.toString();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            //prepare the request
            URL url = new URL(serverUrl + tokenPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentTypeAuth);
            conn.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(setParams(authParams));
            os.flush();
            os.close();

            // save access token and refresh token
            loadTokens(inputToString(conn.getInputStream()));
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        ((LoginActivity)activity).showProgress(false);
        ((LoginActivity) activity).destroyAuthTask();
        if (success) {
            // TODO intent to main activity
            activity.finish();
        } else {
            EditText mPasswordView = ((LoginActivity)activity).getmPasswordView();
            mPasswordView.setError(activity.getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        ((LoginActivity) activity).destroyAuthTask();
        ((LoginActivity)activity).showProgress(false);
    }

    private void loadTokens(String response){
        try{
            JSONObject respObj = new JSONObject(response);
            accessToken = respObj.getString("access_token");
            refreshToken = respObj.getString("refresh_token");
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
