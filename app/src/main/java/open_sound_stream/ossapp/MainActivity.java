package open_sound_stream.ossapp;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;
import open_sound_stream.ossapp.fragments.AlbumsFragment;
import open_sound_stream.ossapp.fragments.ArtistFragment;
import open_sound_stream.ossapp.fragments.PlayerFragment;
import open_sound_stream.ossapp.fragments.PlaylistFragment;
import open_sound_stream.ossapp.fragments.TracksFragment;
import open_sound_stream.ossapp.network.NetworkHandler;
import open_sound_stream.ossapp.network.Singleton;

import android.app.Fragment;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import open_sound_stream.ossapp.ui.login.OSSLoginActivity;

import open_sound_stream.ossapp.MediaPlayerService.LocalBinder;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;

public final class MainActivity extends AppCompatActivity implements MediaPlayerService.uiCallback {

    private SeekBar mSeekbarAudio;


    private static final int PERMISSION_REQUEST_CODE = 1;

    private OSSRepository repo;
    private boolean mBound = false;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;



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
        if (!isChangingConfigurations() && !Singleton.mPlayerService.isPlaying()) {
            Singleton.mPlayerService.release();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            Singleton.mPlayerService = binder.getService();
            mBound = true;

            initializeUI();

            Singleton.mPlayerService.setCallback(MainActivity.this);

            //Code for playback testing, requires an mp3 file named "run.mp3" in the download directory
            /*repo = new OSSRepository(getApplicationContext());
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            Track track = new Track(1337, "Run");
            track.setLocalPath(downloadPath + "/run.mp3");
            Artist artist = new Artist(42, "Awolnation");
            repo.insertArtist(artist);
            Album album = new Album(66, "Beautiful Things");
            repo.insertAlbum(album);
            track.setArtistId(42);
            track.setInAlbumId(66);
            repo.insertTrack(track);

            Singleton.mPlayerService.addToCurrentPlaylist(1337);

            Singleton.mPlayerService.initializePlayback();*/
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

        Singleton.mPlayerService.initializeUI(mPlayPauseButton, mPrevButton, mNextButton, mSeekbarAudio);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        OSSRepository db = new OSSRepository(getApplicationContext());

        Intent intent = new Intent(this, MediaPlayerService.class);
        startForegroundService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // request permission to access local storage for audio and cover download
        if(Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
            else {
                // do alternate code here
            }
        }




        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());


        adapter.addFragment(new PlayerFragment(), "Player");
        adapter.addFragment(new PlaylistFragment(), "Playlists");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new TracksFragment(), "Tracks");
        this.viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(this.viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_play_circle_outline_white_48);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_queue_music_white_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_person_white_48);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_album_white_48);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_audiotrack_white_48);

        Singleton.fetchPreferences(this);

       this.syncWithServer();

    }

    public void syncWithServer(){

        if(Singleton.getLoginState()){

            NetworkHandler nh2 = new NetworkHandler(this );
            nh2.fetchAll();

            Context context = getApplicationContext();

            PlaylistFragment pf= (PlaylistFragment) this.adapter.getItem(1);
            ArtistFragment arf= (ArtistFragment) this.adapter.getItem(2);
            AlbumsFragment alf= (AlbumsFragment) this.adapter.getItem(3);
            TracksFragment tf= (TracksFragment) this.adapter.getItem(4);

            pf.updatePlaylistFragment(context);
            arf.UpdateArtistFragment(context);
            alf.updateAlbumsFragment(context);
            tf.UpdateTracksFragment(context);
        }

        else{

            Toast.makeText(this, "Synchronisation zum Server erst nach Log-In möglich!",
                    Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.login:
                Intent loginActivity= new Intent(MainActivity.this, OSSLoginActivity.class);
                startActivity(loginActivity);
                return true;
            case R.id.logout:
                NetworkHandler nh = new NetworkHandler(this);
                nh.tryLogOut(this);

                return true;

            case R.id.sync:

                Log.d("updateDB", "clicked on sync button");

                this.syncWithServer();
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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store music and album cover files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permission", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("permission", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void updateUI() {
        PlayerFragment pl = (PlayerFragment) adapter.getItem(0);
        pl.updatePlayer();
    }

}
