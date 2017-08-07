package eli.per.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class CirclePoint extends View {

    public CirclePoint(Context context) {
        this(context, null);

    }

    public CirclePoint(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CirclePoint(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
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

        Paint paint = new Paint();
        paint.setColor(getColor());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredHeight() / 2, paint);
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
