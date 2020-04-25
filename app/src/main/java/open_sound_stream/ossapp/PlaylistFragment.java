package open_sound_stream.ossapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;

public class PlaylistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View RootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        OSSRepository db = new OSSRepository(getActivity().getApplicationContext());

        db.getAllPlaylists().observe(this, new Observer<List<PlaylistWithTracks>>() {
            @Override
            public void onChanged(List<PlaylistWithTracks> allPLaylists) {

                PlaylistWithTracks[] allPlaylistArray = new PlaylistWithTracks[allPLaylists.size()];

                allPLaylists.toArray(allPlaylistArray);

                ListView listview = (ListView) RootView.findViewById(R.id.listview);
                ArrayAdapter<PlaylistWithTracks> arrayAdapter = new ArrayAdapter<PlaylistWithTracks>(getActivity(), R.layout.list_item, allPlaylistArray);
                listview.setAdapter(arrayAdapter);

            }
        });

        return RootView;
    }


}