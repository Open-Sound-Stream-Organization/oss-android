package open_sound_stream.ossapp.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import open_sound_stream.ossapp.data.Result;
import open_sound_stream.ossapp.data.model.LoggedInUser;
import open_sound_stream.ossapp.ui.login.LoginViewModel;

public class NetworkHandler {
    private Context context;
    private static String baseUrl = "https://de0.win/api/v1/";

    public NetworkHandler(Context context) {
        this.context = context;
    }

    public void fetchAlbumData() {
        String url = "album/";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input methods
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        });

        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void fetchPlaylistData() {
        String url = "playlist/";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input method
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        });

        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void tryLogin (String username, String password) {

        String url = "apikey/";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseUrl + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String apiKey = "";
                    JSONArray arr = response.getJSONArray("objects");

                    for (int i = 0; i < arr.length(); i++) {
                        apiKey = arr.getJSONObject(i).getString("purpose");
                    }

                    Singleton.setAPIKey(apiKey);

                    LoggedInUser user = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);
                    LoginViewModel.lastLoginResult = new Result.Success<>(user);

                    LoginViewModel.loginSuccess();

                } catch (JSONException ex) {
                    LoginViewModel.loginFailed();
                    throw new RuntimeException(ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoginViewModel.loginFailed();
            }
        })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");

                String text = username + ":" + password;
                byte[] data = new byte[0];

                try {
                    data = text.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                headers.put("Authorization", "basic " + base64);

                return headers;
            }
        };

        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);

    }

}
