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

import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.network.Singleton;

public class AlbumsMenuActivity extends AppCompatActivity {

    private AlbumWithTracks album;
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

        this.album = (AlbumWithTracks) getIntent().getSerializableExtra("album");

        setContentView(R.layout.playlist_menu);
        super.onCreate(savedInstanceState);

        Track[] tracksArray = new Track[album.albumTracks.size()];
        album.albumTracks.toArray(tracksArray);
        ListView listview = (ListView) findViewById(R.id.listview);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(this.album.toString());

        ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<Track>(this, R.layout.list_item, tracksArray);
        listview.setAdapter(arrayAdapter);


        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Play the selected song when the item is clicked
                Track selectedItem = (Track) parent.getItemAtPosition(position);
                Singleton.getInstance().mPlayerService.resetCurrentPlaylist();
                Singleton.getInstance().mPlayerService.addToCurrentPlaylist((int)selectedItem.getTrackId());
                Singleton.getInstance().mPlayerService.initializePlayback();
                Toast.makeText(getBaseContext(), "Now playing: " + selectedItem.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        Log.d("updateDB", "AlbumMenuActivity updated");
    }

}
