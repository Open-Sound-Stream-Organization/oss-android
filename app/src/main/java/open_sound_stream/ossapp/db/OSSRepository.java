package open_sound_stream.ossapp.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import open_sound_stream.ossapp.db.daos.AlbumDao;
import open_sound_stream.ossapp.db.daos.ArtistDao;
import open_sound_stream.ossapp.db.daos.PlaylistDao;
import open_sound_stream.ossapp.db.daos.PlaylistTrackCrossRefDao;
import open_sound_stream.ossapp.db.daos.TrackDao;
import open_sound_stream.ossapp.db.daos.UserDao;
import open_sound_stream.ossapp.db.daos.UserTrackCrossRefDao;
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;
import open_sound_stream.ossapp.db.entities.Playlist;
import open_sound_stream.ossapp.db.entities.PlaylistTrackCrossRef;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.db.entities.User;
import open_sound_stream.ossapp.db.entities.UserTrackCrossRef;
import open_sound_stream.ossapp.db.entities.UserWithTracks;

public class OSSRepository {
    // Member
    private PlaylistDao playlistDao;
    private TrackDao trackDao;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private PlaylistTrackCrossRefDao playlistTrackCrossRefDao;
    private UserDao userDao;
    private UserTrackCrossRefDao userTrackCrossRefDao;

    // Constructor. Initializes the Repository with DAOs
    public OSSRepository(Context context) {
        OSSDatabase db = OSSDatabase.getInstance(context);
        playlistDao = db.playlistDao();
        trackDao = db.trackDao();
        albumDao = db.albumDao();
        artistDao = db.artistDao();
        playlistTrackCrossRefDao = db.playlistTrackCrossRefDao();
        userDao = db.userDao();
        userTrackCrossRefDao = db.userTrackCrossRefDao();
    }

    // Track wrapper functions
    public void insertTrack(Track track) {
        Completable.fromAction( () -> trackDao.insertTrack(track))
                        .subscribeOn(Schedulers.io())
                        .subscribe();
    }

    public void insertTrack(String title, String localPath) {
        Track track = new Track(title, localPath);
        insertTrack(track);
    }

    public LiveData<List<Track>>getAllTracks() {
        return trackDao.getAllTracks();
    }

    public LiveData<Track> getTrackById(int id) {
        return  trackDao.getTrackById(id);
    }

    public LiveData<Track> getTrackByTitle(String title) {
        return trackDao.getTrackByTitle(title);
    }

    // Playlist wrapper functions

    // Insert playlist object
    public void insertPlaylist(Playlist playlist) {
        Completable.fromAction(() -> playlistDao.insertPlaylist(playlist))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    // insert playlist with given name
    public void insertPlaylist(String playlistName) {
        Playlist playlist  = new Playlist(playlistName);
        insertPlaylist(playlist);
    }

    // Add one Track to a Playlist
    public void addTrackToPlaylist(Playlist playlist, Track track) {
        PlaylistTrackCrossRef playlistTrackCrossRef = new PlaylistTrackCrossRef(playlist.getPlaylistId(), track.getTrackId());
        Completable.fromAction(() -> playlistTrackCrossRefDao.insertPlaylistTrackCrossRef(playlistTrackCrossRef))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    // Add a List of Tracks to a Playlist
    public void addTracksToPlaylist(Playlist playlist, List<Track> tracks) {
        for (Track track : tracks) {
            addTrackToPlaylist(playlist, track);
        }
    }

    // Remove a Track from a Playlist
    public void removeTrackFromPlaylist(Playlist playlist, Track track) {
        PlaylistTrackCrossRef playlistTrackCrossRef = playlistTrackCrossRefDao.getPlaylistTrackCrossRefById(playlist.getPlaylistId(), track.getTrackId()).getValue();
        playlistTrackCrossRefDao.deletePlaylistTrackCrossRef(playlistTrackCrossRef);
    }

    // Remove a List of Tracks from a Playlist
    public void removeTracksFromPlaylist(Playlist playlist, List<Track> tracks) {
        for (Track track: tracks) {
            removeTrackFromPlaylist(playlist, track);
        }
    }

    // Get all playlists with their tracks
    public LiveData<List<PlaylistWithTracks>> getAllPlaylists() {
        return playlistDao.getPlaylists();
    }

    // Get a playlist with tracks by its id
    public LiveData<PlaylistWithTracks> getPlaylistById(long id) {
        return playlistDao.getPlaylistById(id);
    }

    public LiveData<PlaylistWithTracks> getPlaylistByName(String name) {
        return playlistDao.getPlaylistByName(name);
    }

    public LiveData<List<PlaylistWithTracks>> getPlaylistsWithTracks() {
        return playlistDao.getPlaylistsWithTracks();
    }

    // Artist wrapper function
    public void insertArtist(Artist artist) {
        Completable.fromAction( () -> artistDao.insertArtist(artist))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insertArtist(String artistName) {
        Artist artist = new Artist(artistName);
        insertArtist(artist);
    }

    public LiveData<List<ArtistWithAlbums>> getAllArtists() {
        return artistDao.getArtistsWithAlbums();
    }

    public LiveData<ArtistWithAlbums> getArtistById(long id) {
        return artistDao.getArtistById(id);
    }

    public LiveData<ArtistWithAlbums> getArtistByName(String artistName) {
        return artistDao.getArtistByName(artistName);
    }

    // Album wrapper functions
    public void insertAlbum(Album album) {
        Completable.fromAction( () -> albumDao.insertAlbum(album))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insertAlbum(String albumName) {
        Album album = new Album(albumName);
        insertAlbum(album);
    }

    public LiveData<List<AlbumWithTracks>> getAllAlbums() {
        return albumDao.getAlbumsWithTracks();
    }

    public LiveData<AlbumWithTracks> getAlbumById(long id) {
        return albumDao.getAlbumById(id);
    }

    public LiveData<AlbumWithTracks> getAlbumByName(String albumName) {
        return albumDao.getAlbumByName(albumName);
    }

    // User Wrapper Functions

    public void insertUser(User user) {
        Completable.fromAction(() -> userDao.insertUser(user))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insertUser(String userName) {
        User user = new User(userName);
        insertUser(user);
    }

    public void deleteUser(User user) {
        Completable.fromAction(() -> userDao.deleteUser(user))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public LiveData<List<UserWithTracks>> getUsers() {
        return userDao.getAllUsers();
    }

    public LiveData<UserWithTracks> getUserById(long id) {
        return userDao.getUserById(id);
    }

    public LiveData<UserWithTracks> getUserByName(String userName) {
        return userDao.getUserByName(userName);
    }

    public void addTrackToUser(Track track, User user) {
        UserTrackCrossRef crossRef = new UserTrackCrossRef(track.getTrackId(), user.getUserId());
        Completable.fromAction(() -> userTrackCrossRefDao.insertUserTrackCrossRefDao(crossRef))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
