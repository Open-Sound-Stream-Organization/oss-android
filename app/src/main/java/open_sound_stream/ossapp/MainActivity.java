package open_sound_stream.ossapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUpDown;
    private ListView listView;
    private TextView txtCantante, txtCancion;
    private ContentLoadingProgressBar progbar;

    private void init() {
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        //this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.toggleButton);
        this.listView = findViewById(R.id.listView);
        this.txtCantante = findViewById(R.id.txtCantante);
        this.txtCancion = findViewById(R.id.txtCancion);
        this.progbar = findViewById(R.id.progbar);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

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
        tabLayout.getTabAt(0).setIcon(R.drawable.icons8_playlist_48);
        tabLayout.getTabAt(1).setIcon(R.drawable.icons8_circled_play_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.icons8_person_48);
        tabLayout.getTabAt(3).setIcon(R.drawable.icons8_music_record_48);
        tabLayout.getTabAt(4).setIcon(R.drawable.icons8_musical_48);





    }



}
