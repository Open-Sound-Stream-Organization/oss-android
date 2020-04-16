package open_sound_stream.ossapp.ui.login;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import open_sound_stream.ossapp.data.LoginRepository;
import open_sound_stream.ossapp.data.Result;
import open_sound_stream.ossapp.data.model.LoggedInUser;
import open_sound_stream.ossapp.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    static private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    static public  Result<LoggedInUser> lastLoginResult;
    static private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(Context context, String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(context, username, password);

        if (lastLoginResult instanceof Result.Error) {
            loginFailed();
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public static void loginSuccess () {
        LoggedInUser data = ((Result.Success<LoggedInUser>) lastLoginResult).getData();
        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));

        loginRepository.setLoggedInUser(((Result.Success<LoggedInUser>) lastLoginResult).getData());

    }

    public static void loginFailed () {
        loginResult.setValue(new LoginResult(R.string.login_failed));
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        /*if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }*/
        return true;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
    }
}
