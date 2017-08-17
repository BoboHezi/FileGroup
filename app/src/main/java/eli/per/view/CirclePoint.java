package eli.per.view;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.util.Random;

public class CirclePoint extends View {

    //默认状态下的颜色
    private final int RANDOM_COLOR = getRandomColor();
    //选中状态在的颜色
    private final int SELECTED_COLOR = Color.parseColor("#047AD4");
    //画笔颜色
    private int paintColor;
    private Paint paint;
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
        paintColor = RANDOM_COLOR;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
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

        paint.setColor(paintColor);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        //绘制折线
        if (paintColor == SELECTED_COLOR) {
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            paint.setStrokeCap(Paint.Cap.ROUND);
            int startX  = (int) (width * 0.3);
            int startY  = (int) (width * 0.5);
            int centerX = (int) (width * 0.5);
            int centerY = (int) (width * 0.71);
            int endX    = (int) (width * 0.8);
            int endY    = (int) (width * 0.35);
            canvas.drawLine(startX, startY, centerX, centerY, paint);
            canvas.drawLine(centerX, centerY, endX, endY, paint);
        }
    }

    /**
     * 设置被选中的状态
     */
    public void setFocus() {
        paintColor = SELECTED_COLOR;
        this.animate().scaleX(2.0f).scaleY(2.0f).setDuration(200).setInterpolator(interpolator);
        postInvalidate();
    }

    /**
     * 取消被选中状态
     */
    public void setDismiss() {
        paintColor = RANDOM_COLOR;
        this.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(interpolator);
        postInvalidate();
    }

    /**
     * 获取是否被选中
     * @return
     */
    public boolean isSelected() {
        return paintColor == SELECTED_COLOR;
    }

    /**
     * 获取随机的颜色
     * @return
     */
    private int getRandomColor() {
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
