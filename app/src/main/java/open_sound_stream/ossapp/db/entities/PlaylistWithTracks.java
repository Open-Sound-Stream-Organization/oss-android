package open_sound_stream.ossapp.db.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class PlaylistWithTracks {
    @Embedded
    public Playlist playlist;
    @Relation(parentColumn = "playlistId",
            entityColumn = "trackId",
            associateBy = @Junction(PlaylistTrackCrossRef.class))
    public List<Track> trackList;

    @Override
    public String toString(){
        return this.playlist.getPlaylistName();
    }
}
