package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import eli.per.filegroup.R;

public class CustomSeekBar extends View {

    private static final String TAG = "CustomSeekBar";

    private int backColor;
    private int progressColor;
    private float backHeight;
    private float progressHeight;
    private int textColor;
    private float textHeight;
    private int totalTime;

    private boolean isTouching = false;
    private boolean isSeek = false;
    private int seekTime;
    private float touchX;

    private Paint paint;
    private int currentTime;


    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);

        paint = new Paint();
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.CustomVideoSeekBar);
        backColor = ta.getColor(R.styleable.CustomVideoSeekBar_backcolor, Color.GRAY);
        backHeight = ta.getDimension(R.styleable.CustomVideoSeekBar_backheight, 5);

        progressColor = ta.getColor(R.styleable.CustomVideoSeekBar_progresscolor, Color.RED);
        progressHeight = ta.getDimension(R.styleable.CustomVideoSeekBar_progressheight, 5);

        textColor = ta.getColor(R.styleable.CustomVideoSeekBar_textcolor, Color.WHITE);
        textHeight = ta.getDimension(R.styleable.CustomVideoSeekBar_textheight, 30);

        totalTime = ta.getInt(R.styleable.CustomVideoSeekBar_totaltime, 100);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算控件实际宽度
        int realWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        //计算分割点位置
        float end_X = (getCurrentTime() * 1.0f/ totalTime) * realWidth;
        //设置时间数字
        String text = formatTime(currentTime / 1000) + "/" + formatTime(totalTime / 1000);

        if (isTouching) {
            //处于触摸状态下，通过触摸点来设置分割点和数字
            end_X = touchX;
            seekTime = (int) (end_X / realWidth * totalTime);
            text = formatTime(seekTime / 1000) + "/" + formatTime(totalTime / 1000);
        }

        //文字
        paint.setColor(textColor);
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text);
        int y = (int) (-(paint.descent() + paint.ascent()) / 2) + 30;
        canvas.drawText(text, realWidth - textWidth, y, paint);

        //绘制未来的时间
        if (currentTime < totalTime) {
            paint.setColor(backColor);
            paint.setStrokeWidth(backHeight);
            paint.setAntiAlias(true);
            canvas.drawLine(end_X , 10, realWidth, 10, paint);
        }

        //绘制过去的时间
        if (currentTime > 0) {
            paint.setAntiAlias(true);
            paint.setColor(progressColor);
            paint.setStrokeWidth(progressHeight);
            canvas.drawLine(0, 10, end_X , 10, paint);
            canvas.drawCircle(end_X, 10, 8, paint);
        }
    }

    public synchronized void setTime(int time) {
        this.totalTime = time;
    }

    public synchronized int getCurrentTime() {
        return currentTime;
    }

    public synchronized int getSeekTime() {
        return this.seekTime;
    }

    public synchronized boolean isSeek() {
        return this.isSeek;
    }

    public synchronized void cancelSeek() {
        this.isSeek = false;
    }

    public synchronized void setCurrentTime(int time) {
        if (time < 0) {
            throw new IllegalStateException("time will not less than 0.");
        }

        if (time > totalTime) {
            time = totalTime;
        }

        if (time <= totalTime) {
            this.currentTime = time;
            postInvalidate();
        }
    }

    private String formatTime(int time) {
        String result;
        int min = time / 60;
        int sen = time % 60;
        result = String.format("%02d", min) + ":" + String.format("%02d", sen);
        return result;
    }

    /**
     * 处理触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouching = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouching = false;
            isSeek = true;
        }

        //获得屏幕的横向定位
        if (isTouching) {
            touchX = event.getX();
        }
        return super.onTouchEvent(event);
    }
}
