package eli.per.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import java.io.File;
import java.io.IOException;
import eli.per.filegroup.LoadListView;
import eli.per.filegroup.R;

public class CustomVideoDialog extends BaseDialog implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private ImageButton buttonMore;
    private File file;
    private GestureDetector gestureDetector;

    private CustomSeekBar seekBar;
    private UpdateThread updateThread;

    public CustomVideoDialog(Context context, File file, LoadListView.RefreshHandler refreshHandler) {
        super(context, file, refreshHandler, R.style.dialog);
        this.file = file;
        initView();
        initVideo();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            /**
             * 单击屏幕 退出
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                dismiss();
                return super.onSingleTapConfirmed(e);
            }

            /**
             * 长按屏幕 显示更多操作
             * @param e
             */
            @Override
            public void onLongPress(MotionEvent e) {
                showActionDialog();
                super.onLongPress(e);
            }
        });
    }

    /**
     * 初始化窗口和视图
     */
    @Override
    public void initView() {
        setContentView(R.layout.videodialog);
        buttonMore = findViewById(R.id.video_more);
        buttonMore.setOnClickListener(this);
        timeText = findViewById(R.id.video_time);
        showTime();
        seekBar = findViewById(R.id.video_seekbar);
        updateThread = new UpdateThread();
        setWindowAnimation();
    }

    /**
     * 初始化视频播放器
     */
    private void initVideo() {
        surfaceView = findViewById(R.id.video_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setKeepScreenOn(true);
        surfaceHolder.addCallback(this);
    }

    /**
     * 设置窗体动画
     */
    @Override
    public void setWindowAnimation() {
        getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.start();
                    //设置进度条全部的时间
                    seekBar.setTime(mediaPlayer.getDuration());
                    //启动更新进度条的线程
                    updateThread = new UpdateThread();
                    updateThread.start();
                }
            });
        } catch (IOException e) {
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
        updateThread.interrupt();
    }

    /**
     * 按钮点击
     * @param view
     */
    @Override
    public void onClick(View view) {
        showActionDialog();
    }

    /**
     * 重写触摸方法
     * 将数据传给GestureDetector判断事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 更新进度条和播放进度的线程
     */
    class UpdateThread extends Thread {
        @Override
        public void run() {
            while(mediaPlayer != null && !this.isInterrupted()) {
                try {
                    //当进度条被重新定位时，更新播放的位置
                    if (seekBar.isSeek()) {
                        int seekTime = seekBar.getSeekTime();
                        seekBar.cancelSeek();
                        mediaPlayer.seekTo(seekTime);
                        continue;
                    }
                    //获取目前的播放位置，更新进度条
                    int position = mediaPlayer.getCurrentPosition();
                    seekBar.setCurrentTime(position);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                } catch (IllegalStateException e) {
                    break;
                }
            }
        }
    }
}
