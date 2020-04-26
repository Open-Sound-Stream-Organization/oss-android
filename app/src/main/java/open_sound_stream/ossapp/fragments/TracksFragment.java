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

import java.lang.reflect.Array;
import java.util.List;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;

public class TracksFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View RootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        OSSRepository db = new OSSRepository(getActivity().getApplicationContext());




        db.getAllTracks().observe(this, new Observer<List<Track>>() {
            @Override
            public void onChanged(List<Track> allTracks) {



                if(allTracks == null){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Alert message to be shown");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


                Track[] allTracksArray = new Track[allTracks.size()];
                allTracks.toArray(allTracksArray);

                ListView listview = (ListView) RootView.findViewById(R.id.listview);
                ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<Track>(getActivity().getApplicationContext(), R.layout.list_item, allTracksArray );
                listview.setAdapter(arrayAdapter);

            }
        });









        return RootView;

    }


}