package open_sound_stream.ossapp;

import android.os.Bundle;
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

public class PlaylistMenuActivity extends AppCompatActivity {

    private PlaylistWithTracks playlist;



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


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Track track = arrayAdapter.getItem(position);

                Toast.makeText(getApplicationContext(), track.toString() + " angeklickt", Toast.LENGTH_LONG).show();

            }
        });
    }

}
