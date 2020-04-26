package open_sound_stream.ossapp;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.network.Singleton;

public class TracksFragment extends Fragment {

    private Context context;
    private ArrayAdapter<Track> arrayAdapter;

    TracksFragment (Context context) {
        this.context = context;
    }

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

                arrayAdapter = new ArrayAdapter<Track>(getActivity().getApplicationContext(), R.layout.list_item, allTracksArray );
                listview.setAdapter(arrayAdapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Track selectedItem = (Track) parent.getItemAtPosition(position);
                        Singleton.mPlayerService.mPlayerAdapter.resetCurrentPlaylist();
                        Singleton.mPlayerService.mPlayerAdapter.initializePlayback();
                        Singleton.mPlayerService.mPlayerAdapter.addToCurrentPlaylist((int)selectedItem.getTrackId());
                        Toast.makeText(context, "Now playing: " + selectedItem.getTitle(), Toast.LENGTH_LONG).show();
                    }
                });

                registerForContextMenu(listview);

            }
        });

        return RootView;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.AddToPlayingQueue:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


                /*db.getAllTracks().observe(this, new Observer<List<Track>>() {
                    @Override
                    public void onChanged(List<Track> allTracks) {
                        Track selectedItem = allTracks.get((int)info.id);

                        Singleton.mPlayerService.mPlayerAdapter.addToCurrentPlaylist((int)selectedItem.getTrackId());
                        Toast.makeText(context, selectedItem.getTitle() + " has been added to queue", Toast.LENGTH_LONG).show();
                    }
                });*/

                return true;

            case R.id.NextTrack:

                return true;

            case R.id.Download:

                return true;
        };
        return false;
    }

}