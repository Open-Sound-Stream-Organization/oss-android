package open_sound_stream.ossapp.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.lang.reflect.Array;
import java.util.List;

import open_sound_stream.ossapp.MainActivity;
import open_sound_stream.ossapp.R;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;

public class TracksFragment extends Fragment {

    private View RootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        if(this.RootView == null){


            this.RootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        }


        return this.RootView;

    }

    public void UpdateTracksFragment(Context context){

        OSSRepository db = new OSSRepository(context);
        db.getAllTracks().observe(this, new Observer<List<Track>>() {
            @Override
            public void onChanged(List<Track> allTracks) {
                Track[] allTracksArray = new Track[allTracks.size()];
                allTracks.toArray(allTracksArray);

                ListView listview = (ListView) RootView.findViewById(R.id.listview);
                ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<Track>(getActivity().getApplicationContext(), R.layout.list_item, allTracksArray );
                listview.setAdapter(arrayAdapter);



                Log.d("updateDB", "trackfragment geupdatet");


            }
        });



    }


}