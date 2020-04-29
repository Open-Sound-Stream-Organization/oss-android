package open_sound_stream.ossapp.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

import open_sound_stream.ossapp.MainActivity;
import open_sound_stream.ossapp.R;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.Artist;

public class AlbumsFragment extends Fragment {

    private View RootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(this.RootView == null){

            this.RootView = inflater.inflate(R.layout.fragment_albums, container, false);
        }

        return this.RootView;

    }

    public void updateAlbumsFragment(Context context){

        OSSRepository db = new OSSRepository(context);

        db.getAllAlbums().observe(this, new Observer<List<AlbumWithTracks>>() {
            @Override
            public void onChanged(List<AlbumWithTracks> allAlbums) {



                AlbumWithTracks[] allAlbumArray = new AlbumWithTracks[allAlbums.size()];

                allAlbums.toArray(allAlbumArray);

                ListView listview = (ListView) getView().findViewById(R.id.listview);
                ArrayAdapter<AlbumWithTracks> arrayAdapter = new ArrayAdapter<AlbumWithTracks>(getActivity(), R.layout.list_item, allAlbumArray);
                listview.setAdapter(arrayAdapter);


                Log.d("updateDB", "albumfragment geupdatet");

            }
        });


    }


}