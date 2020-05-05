package open_sound_stream.ossapp;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.network.Singleton;

public class PlaylistMenuActivity extends AppCompatActivity {

    private PlaylistWithTracks playlist;
    private Track selectedTrack = null;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        selectedTrack = ClickActions.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return ClickActions.onContextItemSelected(item, selectedTrack, getBaseContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        this.playlist = (PlaylistWithTracks) getIntent().getSerializableExtra("playlist");

        setContentView(R.layout.playlist_menu);
        super.onCreate(savedInstanceState);

        Track[] tracksArray = new Track[playlist.trackList.size()];
        playlist.trackList.toArray(tracksArray);
        ListView listview = (ListView) findViewById(R.id.listview);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(this.playlist.toString());

        ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<Track>(this, R.layout.list_item, tracksArray);
        listview.setAdapter(arrayAdapter);


        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Play the selected song when the item is clicked
                Track selectedItem = (Track) parent.getItemAtPosition(position);
                Singleton.mPlayerService.resetCurrentPlaylist();
                Singleton.mPlayerService.addToCurrentPlaylist((int)selectedItem.getTrackId());

                if (position + 1 < tracksArray.length) {
                    for (int i = position + 1; i < tracksArray.length; i++) {
                        Singleton.mPlayerService.addToCurrentPlaylist((int) tracksArray[i].getTrackId());
                    }
                }

                Singleton.mPlayerService.initializePlayback();
                Toast.makeText(getBaseContext(), "Now playing: " + selectedItem.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        Log.d("updateDB", "PlaylistMenuActivity updated");
    }

}
