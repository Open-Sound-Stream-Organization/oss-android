package open_sound_stream.ossapp.data;

import android.content.Context;
import android.util.Log;

import java.net.URL;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import open_sound_stream.ossapp.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "Login";

    public Result<LoggedInUser> login(URL serverURL, Context context, String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            /*LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");*/
            LoggedInUser user = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);

            RequestQueue queue = Volley.newRequestQueue(context);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, serverURL.getPath(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d(TAG, "Response is: "+ response.substring(0,500));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Log in did not work");
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
