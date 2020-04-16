package open_sound_stream.ossapp.network;

import android.content.Context;
import android.util.Log;
import android.view.textclassifier.TextLinks;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class NetworkHandler {
    private Context context;
    private static String baseUrl = "de0.win/api/v1/";

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
}
