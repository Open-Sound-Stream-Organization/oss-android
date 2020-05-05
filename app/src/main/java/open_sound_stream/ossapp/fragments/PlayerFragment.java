package open_sound_stream.ossapp.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import open_sound_stream.ossapp.R;
import open_sound_stream.ossapp.network.Singleton;

public class PlayerFragment extends Fragment {

    private View RootView = null;
    private boolean playing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (this.RootView == null){
            this.RootView = inflater.inflate(R.layout.fragment_player, container, false);
        }

        return this.RootView;
    }


    public void updatePlayer(){

        TextView artist = RootView.findViewById(R.id.artistName);
        TextView title = RootView.findViewById(R.id.trackName);
        ImageButton playPauseButton = RootView.findViewById(R.id.button_playPause);
        ImageView albumArt = RootView.findViewById(R.id.albumArt);
        artist.setText(Singleton.mPlayerService.getCurrentArtist());
        title.setText(Singleton.mPlayerService.getCurrentTitle());
        if (Singleton.mPlayerService != null && Singleton.mPlayerService.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.baseline_pause_white_48);
        } else {
            playPauseButton.setImageResource(R.drawable.baseline_play_arrow_white_48);
        }

        String coverPath = Singleton.mPlayerService.getCurrentAlbumPath();
        if (coverPath != "") {
            albumArt.setImageURI(Uri.parse(coverPath));
        } else {
            albumArt.setImageResource(R.drawable.baseline_audiotrack_white_48);
        }
    }

}