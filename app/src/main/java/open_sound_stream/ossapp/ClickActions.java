package open_sound_stream.ossapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import java.util.List;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.network.NetworkHandler;
import open_sound_stream.ossapp.network.Singleton;

public class ClickActions {

    public static Track onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview) {


            //copied from StOv
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Track obj = (Track) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle(obj.getTitle());
            menu.add("Add to Queue");
            menu.add("Next Track");
            menu.add("Download");

            return obj;
        }

        return null;
    }

    public static boolean onContextItemSelected(MenuItem item, Track selectedTrack, Context context) {

        switch (item.getTitle().toString()) {
            case "Download":
                // TODO: Code for Download
                NetworkHandler nh = new NetworkHandler(context);
                nh.downloadSong(selectedTrack.getTrackId());
                nh.downloadCover(selectedTrack.getInAlbumId());

                Toast.makeText(context, selectedTrack.getTitle() + " is downloading", Toast.LENGTH_LONG).show();
                selectedTrack = null;
                return true;

            case "Next Track":
                // Add selected track one positions after the current track
                Singleton.mPlayerService.addToCurrentPlaylist(Singleton.mPlayerService.mPlayerAdapter.getCurrentPlaylistPosition()+1, (int)selectedTrack.getTrackId());
                Toast.makeText(context, selectedTrack.getTitle() + " will be played next", Toast.LENGTH_LONG).show();
                selectedTrack = null;
                return true;

            case "Add to Queue":
                // add track to the back of a queue
                Singleton.mPlayerService.mPlayerAdapter.addToCurrentPlaylist((int)selectedTrack.getTrackId());
                Toast.makeText(context, selectedTrack.getTitle() + " has been added to the queue", Toast.LENGTH_LONG).show();
                selectedTrack = null;
                return true;
        }

        return false;
    }

}
