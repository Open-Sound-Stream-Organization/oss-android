package open_sound_stream.ossapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

import open_sound_stream.ossapp.MainActivity;
import open_sound_stream.ossapp.R;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;

public class PlaylistFragment extends Fragment {

    private List<PlaylistWithTracks> allPlaylists;


    private View RootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {



        if( this.RootView == null){

            this.RootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        }


        return this.RootView;
    }

    private void setPlaylistList(List<PlaylistWithTracks> pl){
        this.allPlaylists = pl;
    }

    public void updatePlaylistFragment(Context context){
        OSSRepository db = new OSSRepository(context);

        db.getAllPlaylists().observe(this, new Observer<List<PlaylistWithTracks>>() {

            @Override
            public void onChanged(List<PlaylistWithTracks> allPlaylists) {



                PlaylistWithTracks[] allPlaylistArray = new PlaylistWithTracks[allPlaylists.size()];
                allPlaylists.toArray(allPlaylistArray);
                ListView listview = (ListView) getView().findViewById(R.id.listview);
                ArrayAdapter<PlaylistWithTracks> arrayAdapter = new ArrayAdapter<PlaylistWithTracks>(getActivity(), R.layout.list_item, allPlaylistArray);
                listview.setAdapter(arrayAdapter);

                Log.d("updateDB", "Playlistfragment geupdatet");

            }
        });






    }




}