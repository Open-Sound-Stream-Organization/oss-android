package open_sound_stream.ossapp.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;

public class Singleton {
    private static Singleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private static String APIKey;
    private static String ServerURI;
    private static boolean loggedIn = false;

    private Singleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Singleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static String getAPIKey () {
        return APIKey;
    }

    public static void logIn(String apiKey, String serverURL, Context context) {
        APIKey = apiKey;
        ServerURI = serverURL;
        loggedIn = true;

        updatePreferences(context);

    }

    public static void logOut(Context context) {
        APIKey = "";
        ServerURI = "";
        loggedIn = false;

        updatePreferences(context);

    }

    public static boolean getLoginState() {
        return loggedIn;
    }

    public static void fetchPreferences (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("oss-app", Context.MODE_PRIVATE);

        // second parameter is the default value that returns if the preference should not exist
        APIKey = preferences.getString("api-key", "");
        ServerURI = preferences.getString("server-uri", "");

        // somehow saving and retrieving the logged-in status did not work as a boolean value
        // hence this not very nice solution with a string
        String loggedInString = preferences.getString("logged-in", "");

        switch (loggedInString) {
            case "1":
                loggedIn = true;
                break;

            case "0":
                loggedIn = false;
                break;

            default:
                loggedIn = false;
                break;
        }

    }

    private static void updatePreferences (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("oss-app", Context.MODE_PRIVATE);

        // save the key and the log-in state in the preferences
        preferences.edit().putString("api-key", APIKey).commit();
        preferences.edit().putString("server-uri", ServerURI).commit();

        // somehow saving and retrieving the logged-in status did not work as a boolean value
        // hence this not very nice solution with a string
        if (loggedIn)
            preferences.edit().putString("logged-in", "1").commit();
        else
            preferences.edit().putString("logged-in", "0").commit();
    }

    public static String getServerURI () {
        return ServerURI;
    }

}
