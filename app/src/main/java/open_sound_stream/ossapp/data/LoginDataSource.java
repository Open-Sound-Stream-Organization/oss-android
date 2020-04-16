package open_sound_stream.ossapp.data;

import android.content.Context;

import open_sound_stream.ossapp.data.model.LoggedInUser;
import open_sound_stream.ossapp.network.NetworkHandler;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "Login";
    NetworkHandler networkHandler;

    public void login(Context context, String username, String password) {

        try {

            this.networkHandler = new NetworkHandler(context);

            try {
                networkHandler.tryLogin(username, password);
            } catch (RuntimeException ex) {
                throw new RuntimeException(ex);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
