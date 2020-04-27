package open_sound_stream.ossapp.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import open_sound_stream.ossapp.network.Singleton;

public class TracksFragment extends Fragment {

    private View RootView = null;
    private Track selectedTrack = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        if(this.RootView == null){


            this.RootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        }


        return this.RootView;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview) {


            //copied from StOv
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Track obj = (Track) lv.getItemAtPosition(acmi.position);

            this.selectedTrack = obj;

            menu.setHeaderTitle(obj.getTitle());
            menu.add("Add to Queue");
            menu.add("Next Track");
            menu.add("Download");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getTitle().toString()) {
            case "Download":
                // TODO: Code for Download
                Toast.makeText(this.getContext(), this.selectedTrack.getTitle() + " is downloading", Toast.LENGTH_LONG).show();
                this.selectedTrack = null;
                return true;

            case "Next Track":
                // Add selected track one positions after the current track
                Singleton.mPlayerService.addToCurrentPlaylist(Singleton.mPlayerService.mPlayerAdapter.getCurrentPlaylistPosition()+1, (int)this.selectedTrack.getTrackId());
                Toast.makeText(this.getContext(), this.selectedTrack.getTitle() + " will be played next", Toast.LENGTH_LONG).show();
                this.selectedTrack = null;
                return true;

            case "Add to Queue":
                // add track to the back of a queue
                Singleton.mPlayerService.mPlayerAdapter.addToCurrentPlaylist((int)selectedTrack.getTrackId());
                Toast.makeText(this.getContext(), this.selectedTrack.getTitle() + " has been added to the queue", Toast.LENGTH_LONG).show();
                this.selectedTrack = null;
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

                registerForContextMenu(listview);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Play the selected song when the item is clicked
                        Track selectedItem = (Track) parent.getItemAtPosition(position);
                        Singleton.mPlayerService.mPlayerAdapter.resetCurrentPlaylist();
                        Singleton.mPlayerService.mPlayerAdapter.addToCurrentPlaylist((int)selectedItem.getTrackId());
                        Singleton.mPlayerService.mPlayerAdapter.initializePlayback();
                        Toast.makeText(context, "Now playing: " + selectedItem.getTitle(), Toast.LENGTH_LONG).show();
                    }
                });

                Log.d("updateDB", "Trackfragment updated");


            }
        });



    }


}