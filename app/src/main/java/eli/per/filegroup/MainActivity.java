package eli.per.filegroup;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import java.io.IOException;
import eli.per.view.CustomListView;

/**
 * author Eli Chang And Fei Wei
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "MainActivity";

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    private LoadListView loadListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //设置List
        loadListView = new LoadListView(this, this);
        loadListView.loadFiles();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.video);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(this);

        resetVideoPlayer();
    }

    private void resetVideoPlayer() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = (width * 9) / 16;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        surfaceView.setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setLooping(true);

        try {
            mediaPlayer.setDataSource("/sdcard/1/video.mov");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}