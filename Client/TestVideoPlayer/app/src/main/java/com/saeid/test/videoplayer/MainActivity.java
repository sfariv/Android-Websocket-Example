package com.saeid.test.videoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.tavendo.autobahn.AutobahnConnection;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Saeid";
    private static final String localhost = "10.0.0.57";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private VideoView vv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startWebSocketConnection();

        vv = (VideoView) findViewById(R.id.video_view);
        final MediaPlayer.OnBufferingUpdateListener onBufferingListener = new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                Log.d(LOG_TAG, "Status: Video is prepared");
                reportClientStatus(ClientStatus.Status.Buffering, String.valueOf(percent));
                if(percent == 100) {
                    mediaPlayer.setOnBufferingUpdateListener(null);
                }
            }
        };
        vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //mConnection.sendTextMessage("Video rendering started");
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        reportClientStatus(ClientStatus.Status.Loading, String.valueOf(vv.getBufferPercentage()));
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        reportClientStatus(ClientStatus.Status.Loaded, String.valueOf(vv.getBufferPercentage()));
                        break;
                    case 703:
                        //mConnection.sendTextMessage("NETWORK BANDWIDTH: " + extra + " kbps");
                        break;
                }
                return false;
            }
        });
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnBufferingUpdateListener(onBufferingListener);
                reportClientStatus(ClientStatus.Status.Prepared, String.valueOf(vv.getBufferPercentage()));
                mediaPlayer.start();
            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                reportClientStatus(ClientStatus.Status.Finished, "0");
            }
        });
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                reportClientStatus(ClientStatus.Status.Error, what + " " + extra);
                return false;
            }
        });
        Uri videoUri = Uri.parse("http://" + localhost + ":8888/movie.mp4");
        vv.setVideoURI(videoUri);
        reportClientStatus(ClientStatus.Status.Preparing, videoUri.toString());
    }

    private void startWebSocketConnection() {

        final String wsuri = "ws://" + localhost + ":3000";

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    mConnection.sendTextMessage("Hello, world!");
                }

                @Override
                public void onTextMessage(String payload) {
                    consumeMessage(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(LOG_TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Log.d(LOG_TAG, "" + e);
        }
    }

    private void reportClientStatus(ClientStatus.Status statusCode, String statusValue) {
        ClientStatus status = new ClientStatus();
        status.setStatus(statusCode);
        status.setValue(statusValue);
        try {
            ObjectMapper mapper = new ObjectMapper();
            if(mConnection.isConnected()) {
                mConnection.sendTextMessage(mapper.writeValueAsString(status));
            } else {
                Log.d(LOG_TAG, "Not connected yet!");
            }
        } catch (JsonProcessingException e) {
            Log.d(LOG_TAG, "" + e);
        }
    }

    private void consumeMessage(String payload) {
        Log.d(LOG_TAG, "Message received: " + payload);
    }
}
