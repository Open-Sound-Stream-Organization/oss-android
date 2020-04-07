package open_sound_stream.ossapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Allows playback of a single MP3 file via the UI. It contains a {@link MediaPlayerHolder}
 * which implements the {@link PlayerAdapter} interface that the activity uses to control
 * audio playback.
 */
public final class MainActivity extends AppCompatActivity {

    public static final int MEDIA_RES_ID = R.raw.jazz_in_paris;

    private SeekBar mSeekbarAudio;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        initializeSeekbar();
        initializePlaybackController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayerAdapter.loadMedia(MEDIA_RES_ID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isChangingConfigurations() && !mPlayerAdapter.isPlaying()) {
            mPlayerAdapter.release();
        }
    }

    private void initializeUI() {
        Button mPlayPauseButton = findViewById(R.id.button_playPause);
        Button mPrevButton = findViewById(R.id.button_prev);
        Button mNextButton = findViewById(R.id.button_next);
        mSeekbarAudio = findViewById(R.id.seekbar_audio);

        mPlayPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayerAdapter.isPlaying()) {
                            mPlayerAdapter.pause();
                        }
                        else {
                            mPlayerAdapter.play();
                        }
                    }
                });

        mPrevButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayerAdapter.getCurrentPlaybackPosition() <= 5000)
                        {
                            //to previous track
                        } else {
                            mPlayerAdapter.seekTo(0);
                        }
                    }
                });
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekbarAudio.setProgress(position, true);
            }
        }

        @Override
        public void onStateChanged(@State int state) {
        }

        @Override
        public void onPlaybackCompleted() {
        }
    }
}