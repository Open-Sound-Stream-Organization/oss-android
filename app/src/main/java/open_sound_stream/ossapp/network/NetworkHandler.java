package open_sound_stream.ossapp.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
    private static String baseUrl = "https://de0.win/api/v1/";
    private OSSRepository repo;

    public NetworkHandler(Context context) {
        this.context = context;
        repo = new OSSRepository(context);
    }

    // calls all fetch methods in correct order
    public void fetchAll() {
        fetchAlbumData();
        fetchArtistData();
        fetchTrackData();
        fetchPlaylistData();
    }

    public void fetchAlbumData() {
        String url = "album/";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input methods
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject album = jsonArray.getJSONObject(i);
                        String albumName = album.getString("name");
                        long albumId = album.getLong("id");
                        Album newAlbum = new Album(albumId, albumName);
                        repo.insertAlbum(newAlbum);
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
                Log.d("debug", Singleton.getAPIKey());
                headers.put("Authorization", Singleton.getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void fetchPlaylistData() {
        String url = "playlist/";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
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
                headers.put("Authorization", Singleton.getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void fetchTrackData() {
        String url = "song/";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parse json and call db input method
                try {
                    JSONArray jsonArray = response.getJSONArray("objects");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject track = jsonArray.getJSONObject(i);
                        String trackName = track.getString("title");
                        long trackId = track.getLong("id");

                        // get album id
                        String albumStr = track.getString("album");
                        albumStr = albumStr.replaceAll("/api.*album/", "");
                        long albumID = Long.parseLong(albumStr);
                        //TODO: download file and save locally
                        String localPath = "";
                        Track newTrack = new Track(trackId, trackName);
                        newTrack.setInAlbumId(albumID);
                        newTrack.setLocalPath(localPath);
                        repo.insertTrack(newTrack);
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
                headers.put("Authorization", Singleton.getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void fetchArtistData() {
        String url = "artist";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(baseUrl + url, null, new Response.Listener<JSONObject>() {
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
                            Log.d("debug", albumIdStr);
                            long albumId = Long.parseLong(albumIdStr);

                            repo.addAlbumToArtist(albumId, artistId);
                        }
                        Artist newArtist = new Artist(artistId, artistName);
                        repo.insertArtist(newArtist);
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
                headers.put("Authorization", Singleton.getAPIKey());
                return headers;
            }
        };
        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);
    }

    public void tryLogin (Context context, String username, String password, String serverURI)  {

        String url = "apikey/";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("purpose", "Android Session");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://" + serverURI + apiURL + url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String apiKey = "";
                    apiKey = response.getString("key");

                    Singleton.logIn(apiKey, serverURI, context);

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

        Singleton.getInstance(context).getRequestQueue().add(jsonRequest);

    }

}
