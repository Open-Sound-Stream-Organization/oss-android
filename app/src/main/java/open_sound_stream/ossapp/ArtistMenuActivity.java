package open_sound_stream.ossapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;
import open_sound_stream.ossapp.db.entities.Track;

public class ArtistMenuActivity extends AppCompatActivity {

    private ArtistWithAlbums artist;



    @Override
    public void onCreate(Bundle savedInstanceState){

        this.artist = (ArtistWithAlbums) getIntent().getSerializableExtra("artist");

        setContentView(R.layout.playlist_menu);
        super.onCreate(savedInstanceState);

        Album[] tracksArray = new Album[artist.artistAlbums.size()];
        artist.artistAlbums.toArray(tracksArray);
        ListView listview = (ListView) findViewById(R.id.listview);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(this.artist.toString());

        ArrayAdapter<Album> arrayAdapter = new ArrayAdapter<Album>(this, R.layout.list_item, tracksArray);
        listview.setAdapter(arrayAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Album album = arrayAdapter.getItem(position);

                Toast.makeText(getApplicationContext(), album.toString() + " angeklickt", Toast.LENGTH_LONG).show();

            }
        });
    }

}
