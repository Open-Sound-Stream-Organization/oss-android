package open_sound_stream.ossapp;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;


public class ArtistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View RootView = inflater.inflate(R.layout.fragment_artist, container, false);


        OSSRepository db = new OSSRepository(getActivity().getApplicationContext());

        db.getAllArtists().observe(this, new Observer<List<ArtistWithAlbums>>() {
            @Override
            public void onChanged(List<ArtistWithAlbums> allArtists) {

                ArtistWithAlbums[] allArtistArray = new ArtistWithAlbums[allArtists.size()];

                allArtists.toArray(allArtistArray);

                ListView listview = (ListView) RootView.findViewById(R.id.listview);
                ArrayAdapter<ArtistWithAlbums> arrayAdapter = new ArrayAdapter<ArtistWithAlbums>(getActivity(), R.layout.list_item, allArtistArray);
                listview.setAdapter(arrayAdapter);

            }
        });


        return RootView;

    }


}