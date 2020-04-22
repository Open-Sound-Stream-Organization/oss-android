package open_sound_stream.ossapp;

import io.reactivex.SingleEmitter;
import open_sound_stream.ossapp.network.Singleton;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


import open_sound_stream.ossapp.network.Singleton;
import open_sound_stream.ossapp.ui.login.OSSLoginActivity;

import android.os.IBinder;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import open_sound_stream.ossapp.MediaPlayerService.LocalBinder;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;

/**
 * Allows playback of a single MP3 file via the UI. It contains a {@link MediaPlayerHolder}
 * which implements the {@link PlayerAdapter} interface that the activity uses to control
 * audio playback.
 */
public final class MainActivity extends AppCompatActivity {

    private SeekBar mSeekbarAudio;

    private OSSRepository repo;
    private MediaPlayerService mPlayerService;
    private boolean mBound = false;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaylistFragment(), "Playlists");
        adapter.addFragment(new PlayerFragment(), "Player");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new TracksFragment(), "Tracks");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_queue_music_white_48);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_play_circle_outline_white_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_person_white_48);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_album_white_48);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_audiotrack_white_48);
    }




    ////After the binding process the MediaPlayerService can be used like a normal class.
    ////It should not be accessed before the onServiceConnected method below was called,
    ////because until the value of mPlayerService will be NULL. Check if mBound is true.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
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

            repo = new OSSRepository(getApplicationContext());
            Track track = new Track(1337, "Sandstorm");
            track.setLocalPath("android.resources://" + getPackageName() + "/raw/sandstorm");
            repo.insertTrack(track);

            Track track2 = new Track(42, "Never gonna give you up");
            track2.setLocalPath("android.resources://" + getPackageName() + "/raw/rick");
            repo.insertTrack(track2);

            mPlayerService.addToCurrentPlaylist(1337);
            mPlayerService.addToCurrentPlaylist(42);

            mPlayerService.initializePlayback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void initializeUI() {
        ImageButton mPlayPauseButton = findViewById(R.id.button_playPause);
        ImageButton mPrevButton = findViewById(R.id.button_prev);
        ImageButton mNextButton = findViewById(R.id.button_next);
        mSeekbarAudio = findViewById(R.id.seekbar_audio);

        mPlayerService.initializeUI(mPlayPauseButton, mPrevButton, mNextButton, mSeekbarAudio);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        OSSRepository db = new OSSRepository(getApplicationContext());

        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaylistFragment(), "Playlists");
        adapter.addFragment(new PlayerFragment(), "Player");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new TracksFragment(), "Tracks");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_queue_music_white_48);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_play_circle_outline_white_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_person_white_48);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_album_white_48);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_audiotrack_white_48);




        Singleton.fetchPreferences(this);

    }
        Track t = new Track(1, "Sandstorm");
        Track s = new Track(2, "test");
        t.setLocalPath("android.resources://open_sound_stream.ossapp/raw/sandstorm.mp3");
        db.insertTrack(t);
        db.insertTrack(s);

        //mPlayerService.addToCurrentPlaylist(2);
        //mPlayerService.initializePlayback();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.login:
                Intent loginActivity= new Intent(MainActivity.this, OSSLoginActivity.class);
                startActivity(loginActivity);
                return true;
            case R.id.logout:
                Singleton.logOut(this);
                Toast.makeText(getApplicationContext(), "You are now logged out!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.options:

                return true;
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        // get menu items
        MenuItem mLogin = menu.findItem(R.id.login);
        MenuItem mLogout = menu.findItem(R.id.logout);

        // switch between showing log in / log out buttons
        if (Singleton.getLoginState()) {
            mLogin.setVisible(false);
            mLogin.setEnabled(false);

            mLogout.setVisible(true);
            mLogout.setEnabled(true);
        } else {
            mLogin.setVisible(true);
            mLogin.setEnabled(true);

            mLogout.setVisible(false);
            mLogout.setEnabled(false);
        }

        super.onPrepareOptionsMenu(menu);

        return true;
    }

}
