package open_sound_stream.ossapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import open_sound_stream.ossapp.R;

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


}