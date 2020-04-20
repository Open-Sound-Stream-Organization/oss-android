package open_sound_stream.ossapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.widget.Button;
import android.widget.SeekBar;

import open_sound_stream.ossapp.MediaPlayerService.LocalBinder;

/**
 * Allows playback of a single MP3 file via the UI. It contains a {@link MediaPlayerHolder}
 * which implements the {@link PlayerAdapter} interface that the activity uses to control
 * audio playback.
 */
public final class MainActivity extends AppCompatActivity {

    private SeekBar mSeekbarAudio;

    private MediaPlayerService mPlayerService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isChangingConfigurations() && !mPlayerService.isPlaying()) {
            mPlayerService.release();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mPlayerService = binder.getService();
            mBound = true;
            initializeUI();
            mPlayerService.addToCurrentPlaylist(R.raw.jazz_in_paris);
            mPlayerService.addToCurrentPlaylist(R.raw.sandstorm);
            mPlayerService.initializePlayback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void initializeUI() {
        Button mPlayPauseButton = findViewById(R.id.button_playPause);
        Button mPrevButton = findViewById(R.id.button_prev);
        Button mNextButton = findViewById(R.id.button_next);
        mSeekbarAudio = findViewById(R.id.seekbar_audio);

        mPlayerService.initializeUI(mPlayPauseButton, mPrevButton, mNextButton, mSeekbarAudio);
    }
}