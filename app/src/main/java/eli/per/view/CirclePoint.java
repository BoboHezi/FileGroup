package eli.per.view;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

public class CirclePoint extends View {

    private int color;
    //插值器
    private static final TimeInterpolator interpolator = new DecelerateInterpolator();

    public CirclePoint(Context context) {
        this(context, null);

    }

    public CirclePoint(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CirclePoint(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        color = getColor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 绘制小球
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = Math.min(getMeasuredHeight(), getMeasuredWidth());

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
    }

    /**
     * 设置被选中的状态
     */
    public void setFoucs() {
        color = Color.parseColor("#ff0000");
        this.animate().scaleX(2.0f).scaleY(2.0f).setDuration(200).setInterpolator(interpolator);
        postInvalidate();
    }

    /**
     * 设置未被选中的状态
     */
    public void setDismiss() {
        color = Color.parseColor("#777777");
        this.animate().scaleX(2.0f).scaleY(2.0f).setDuration(200).setInterpolator(interpolator);
        postInvalidate();
    }

    /**
     * 取消多选状态
     */
    public void reset() {
        color = getColor();
        this.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(interpolator);
        postInvalidate();
    }

    /**
     * 获取随机的颜色
     * @return
     */
    private int getColor() {
        int color;
        String colors[] = {
                "#2a53a1", "#791c51", "#195823",
                "#8c4826", "#8c8926", "#266f8c",
                "#00ff55", "#864849", "#203619",
                "#515151", "#138c54", "#5c1d69",
                "#59215b", "#0c3f86", "#24b6a7",
        };
        String str = colors[new Random().nextInt(15)];
        color = Color.parseColor(str);
        return color;
    }
}
