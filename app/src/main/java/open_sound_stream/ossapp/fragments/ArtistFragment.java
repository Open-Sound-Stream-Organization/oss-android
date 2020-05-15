package open_sound_stream.ossapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.List;

import open_sound_stream.ossapp.AlbumsMenuActivity;
import open_sound_stream.ossapp.ArtistMenuActivity;
import open_sound_stream.ossapp.MainActivity;
import open_sound_stream.ossapp.R;
import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;


public class ArtistFragment extends Fragment {

    private View RootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        if(this.RootView == null){

            this.RootView = inflater.inflate(R.layout.fragment_artist, container, false);

        }
        return this.RootView;

    }

    public void UpdateArtistFragment(Context context){



        OSSRepository db = new OSSRepository(context);

        db.getAllArtists().observe(this, new Observer<List<ArtistWithAlbums>>() {
            @Override
            public void onChanged(List<ArtistWithAlbums> allArtists) {

                ArtistWithAlbums[] allArtistArray = new ArtistWithAlbums[allArtists.size()];

                allArtists.toArray(allArtistArray);

                ListView listview = (ListView) getView().findViewById(R.id.listview);
                ArrayAdapter<ArtistWithAlbums> arrayAdapter = new ArrayAdapter<ArtistWithAlbums>(getActivity(), R.layout.list_item, allArtistArray);
                listview.setAdapter(arrayAdapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                        ArtistWithAlbums ar = arrayAdapter.getItem(position);

                        Intent intent = new Intent(context, ArtistMenuActivity.class);

                        intent.putExtra("artist", ar);

                        startActivity(intent);




                    }
                });
                Log.d("updateDB", "artistfragment geupdatet");


            }
        });

    }





}