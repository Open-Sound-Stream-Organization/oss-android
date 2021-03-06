package open_sound_stream.ossapp.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import open_sound_stream.ossapp.data.Result;
import open_sound_stream.ossapp.data.model.LoggedInUser;
import open_sound_stream.ossapp.ui.login.LoginViewModel;


import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.Playlist;
import open_sound_stream.ossapp.db.entities.Track;

/***********
 * METHOD ORDER:
 * 1. fetchAlbumData()
 * 2. fetchArtistData()
 * 3. fetchTrackData()
 * 4. fetchPlaylistData()
************/


public class NetworkHandler {
    private Context context;
    private static String apiURL = "/api/v1/";
    private static String repertoireURL = "/repertoire/";
    private OSSRepository repo;

    public NetworkHandler(Context context) {
        this.context = context;
        repo = new OSSRepository(context);
    }

    // calls all fetch methods in correct order
    public void fetchAll() {
        repo.clearAllTables();
        fetchAlbumData(0, 0);
        fetchArtistData(0, 0);
        fetchTrackData(0, 0);
        fetchPlaylistData(0, 0);

        Log.d("network", "finished updating local database");
    }

    public void fetchAlbumData(int offset, int cycle) {
        String url = "/album?offset=" + Integer.toString(offset);
        Log.d("debug", Singleton.getInstance().getServerURI());
        JsonObjectRequest jsonRequest = new JsonObjectRequest("https://" + Singleton.getInstance().getServerURI() + apiURL + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input methods
                Log.d("network", response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject album = jsonArray.getJSONObject(i);
                        String albumName = album.getString("name");
                        long albumId = album.getLong("id");
                        Album newAlbum = new Album(albumId, albumName);
                        repo.insertAlbum(newAlbum);
                    }
                    JSONObject meta = response.getJSONObject("meta");
                    int objectCount = meta.getInt("total_count");
                    if(objectCount - offset * cycle > 200) {
                        fetchAlbumData(200, cycle + 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception", "unexpected json exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Singleton.getInstance().getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);
    }

    public void fetchPlaylistData(int offset, int cycle) {
        String url = "/playlist?offset=" + Integer.toString(offset);
        JsonObjectRequest jsonRequest = new JsonObjectRequest("https://" + Singleton.getInstance().getServerURI() + apiURL + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input method
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject playlist = jsonArray.getJSONObject(i);
                        String playlistName = playlist.getString("name");
                        long playlistId = playlist.getLong("id");
                        Log.d("debug", playlist.toString());
                        JSONArray songsInPlaylist = playlist.getJSONArray("songsinplaylist");

                        for(int j = 0; j < songsInPlaylist.length(); j++) {
                            JSONObject songsInPlaylistJSONObject = songsInPlaylist.getJSONObject(j);
                            JSONObject songObject = songsInPlaylistJSONObject.getJSONObject("song");
                            long trackId = songObject.getLong("id");

                            repo.addTrackToPlaylist(playlistId, trackId);
                        }

                        Playlist newPlaylist = new Playlist(playlistId, playlistName);
                        repo.insertPlaylist(newPlaylist);
                    }
                    JSONObject meta = response.getJSONObject("meta");
                    int objectCount = meta.getInt("total_count");
                    if(objectCount - offset * cycle > 200) {
                        fetchPlaylistData(200, cycle + 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception", "unexpected json exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Singleton.getInstance().getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);
    }

    public void fetchTrackData(int offset, int cycle) {
        String url = "/song?offset=" + Integer.toString(offset);
        JsonObjectRequest jsonRequest = new JsonObjectRequest("https://" + Singleton.getInstance().getServerURI() + apiURL + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input method
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject track = jsonArray.getJSONObject(i);
                        Log.d("debug", track.toString());
                        String trackName = track.getString("title");
                        long trackId = track.getLong("id");

                        // get album id
                        String albumStr = track.getString("album");
                        albumStr = albumStr.replaceAll("/api.*album/", "");
                        long albumID = Long.parseLong(albumStr);
                        String localPath = "";
                        Track newTrack = new Track(trackId, trackName);
                        newTrack.setInAlbumId(albumID);
                        newTrack.setLocalPath(localPath);
                        repo.insertTrack(newTrack);
                    }

                    JSONObject meta = response.getJSONObject("meta");
                    int objectCount = meta.getInt("total_count");
                    if(objectCount - offset * cycle > 200) {
                        fetchTrackData(200, cycle + 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception", "unexpected json exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Singleton.getInstance().getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);
    }

    public void fetchArtistData(int offset, int cycle) {
        String url = "/artist?offset=" + Integer.toString(offset);
        JsonObjectRequest jsonRequest = new JsonObjectRequest("https://" + Singleton.getInstance().getServerURI() + apiURL + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input method
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject artist = jsonArray.getJSONObject(i);
                        String artistName = artist.getString("name");
                        long artistId = artist.getLong("id");

                        // make artist album crossRef
                        JSONArray artistAlbums = artist.getJSONArray("albums");
                        for(int j = 0; j < artistAlbums.length(); j++) {
                            String albumIdStr = artistAlbums.getString(j);
                            albumIdStr = albumIdStr.replaceAll("/api.*album/", "");
                            long albumId = Long.parseLong(albumIdStr);

                            repo.addAlbumToArtist(albumId, artistId);
                        }
                        Artist newArtist = new Artist(artistId, artistName);
                        repo.insertArtist(newArtist);
                    }

                    JSONObject meta = response.getJSONObject("meta");
                    int objectCount = meta.getInt("total_count");
                    if(objectCount - offset * cycle > 200) {
                        fetchArtistData(200, cycle + 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception", "unexpected json exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Singleton.getInstance().getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tryLogin (Context context, String username, String password, String serverURI)  {

        String url = "apikey/";

        // generate the purpose string for the new API Key
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String purpose = "Android Session, " + dtf.format(now);

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("purpose", purpose);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://" + serverURI + apiURL + url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String apiKey = "", ID = "";
                    apiKey = response.getString("key");
                    ID = response.getString("id");

                    Singleton.getInstance().logIn(apiKey, serverURI, username, ID, context);

                    LoggedInUser user = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);
                    LoginViewModel.lastLoginResult = new Result.Success<>(user);

                    LoginViewModel.loginSuccess();

                } catch (JSONException ex) {
                    LoginViewModel.loginFailed();
                    throw new RuntimeException(ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoginViewModel.loginFailed();
            }
        })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");

                String text = username + ":" + password;
                byte[] data = new byte[0];

                try {
                    data = text.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                headers.put("Authorization", "basic " + base64);

                return headers;
            }
        };

        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);

    }

    public void downloadSong(long trackId) {
        String fileUri = "https://" + Singleton.getInstance().getServerURI() + repertoireURL + "song_file/" + Long.toString(trackId) + "/";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + "OSSApp" + "/" + Singleton.getInstance().getUsername() + "/" + Long.toString(trackId));

        // check if file is already downloaded
        if(file.exists()) {
            return;
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUri));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("OSS_audio_file_" + Long.toString(trackId));
            request.addRequestHeader("Authorization", Singleton.getInstance().getAPIKey());

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC + File.separator + "OSSApp" + File.separator + Singleton.getInstance().getUsername(), Long.toString(trackId));

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    public String getTrackFilePath(long trackId) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + "OSSApp" + "/" + Singleton.getInstance().getUsername() + "/" + Long.toString(trackId);
        return path;
    }

    public String getCoverFilePath (long albumId) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "OSSApp" + "/" + Singleton.getInstance().getUsername() + "/" + Long.toString(albumId);
        return path;
    }

    public void downloadCover(long albumId) {
        String fileUri = "https://" + Singleton.getInstance().getServerURI() + repertoireURL + "cover_file/" + Long.toString(albumId) + "/";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "OSSApp" + "/" + Singleton.getInstance().getUsername() + "/" + Long.toString(albumId));

        // check if file is already downloaded
        if (file.exists()) {
            return;
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUri));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + "OSSApp" + File.separator + Singleton.getInstance().getUsername(), Long.toString(albumId));
            request.setTitle("OSS_album_cover_" + Long.toString(albumId));
            request.addRequestHeader("Authorization", Singleton.getInstance().getAPIKey());

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);


        }
    }
      
    public void tryLogOut (Context context) {

        String url = "apikey/";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, "https://" + Singleton.getInstance().getServerURI() + apiURL + url + Singleton.getInstance().getID(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Singleton.getInstance().logOut(context);
                repo.clearAllTables();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO:
                // has to be changed later !!!
                Singleton.getInstance().logOut(context);
                repo.clearAllTables();

                //Toast.makeText(context, "Log out failed!", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Singleton.getInstance().getAPIKey());
                return headers;
            }
        };

        Singleton.getInstance().getRequestQueue(context).add(jsonRequest);

    }

}
