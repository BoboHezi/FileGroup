package eli.per.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

public class CustomImageView extends AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = this.getClass().getName();

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private Matrix scaleMatrix;
    private VelocityTracker velocityTracker;
    private FlingRunnable flingRunnable;
    private PressAction pressAction;

    private boolean isFirst = false;
    private boolean isAutoScale = false;
    private float initScale;
    private float maxScale;
    private float midScale;
    private float minScale;
    private float maxOverScale;

    private int lastPointCount;
    private boolean isCanDrag = false;
    private float lastX;
    private float lastY;

    private int touchSlop;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomImageView(final Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        scaleMatrix = new Matrix();
        this.setOnTouchListener(this);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();

                if (getScale() < midScale) {
                    post(new AutoScaleRunnable(midScale, x, y));
                } else {
                    post(new AutoScaleRunnable(initScale, x, y));
                }
                return true;
            }

            /**
             * 点击事件，点击消失
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (pressAction != null)
                    pressAction.singleTap();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (pressAction != null)
                    pressAction.longPress();
                super.onLongPress(e);
            }
        });
    }

    /**
     * 设置点击消失的接口
     * @param pressAction
     */
    public void setOnPressAction(PressAction pressAction) {
        this.pressAction = pressAction;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {

        if (!isFirst) {
            isFirst = true;
            Drawable drawable = getDrawable();
            if (drawable == null)
                return;

            int width = getWidth();
            int height = getHeight();
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            float scale = 1.0f;

            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }
            if ((dw < width && dh < height) || (dw > width && dh > height)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            scaleMatrix.postTranslate(dx, dy);
            scaleMatrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(scaleMatrix);

            initScale = scale;
            maxScale = scale * 4;
            midScale = scale * 2;
            minScale = initScale / 4;
            maxOverScale = maxScale * 2;
        }
    }

    /**
     * 获取当前缩放比例
     * @return
     */
    private final float getScale() {
        float[] values = new float[9];
        scaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = scaleMatrix;
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + rect.width() * 0.5f;
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + rect.height() * 0.5f;
        }
        scaleMatrix.postTranslate(deltaX, deltaY);
    }

    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0.0f;
        float deltaY = 0.0f;

        int width = getWidth();
        int height = getHeight();

        if (isCheckLeftAndRight) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }
        if (isCheckTopAndBottom) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }
        scaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();

        if (getDrawable() == null)
            return true;

        if ((scaleFactor > 1.0f && scaleFactor * scale < maxOverScale) || (scaleFactor < 1.0f && scaleFactor * scale > minScale)) {
            if (scale * scaleFactor > maxOverScale + 0.01f) {
                scaleFactor = maxOverScale / scale;
            }
            if (scale * scaleFactor < minScale + 0.01f) {
                scaleFactor = minScale / scale;
            }
            scaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(scaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (gestureDetector.onTouchEvent(motionEvent))
            return true;
        scaleGestureDetector.onTouchEvent(motionEvent);

        float x = 0.0f;
        float y = 0.0f;

        int pointCount = motionEvent.getPointerCount();
        for (int i = 0; i<pointCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }

        x /= pointCount;
        y /= pointCount;

        if (lastPointCount != pointCount) {
            isCanDrag = false;
            lastX = x;
            lastY = y;
        }
        lastPointCount = pointCount;
        RectF rectF = getMatrixRectF();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                velocityTracker = VelocityTracker.obtain();
                if (velocityTracker != null) {
                    velocityTracker.addMovement(motionEvent);
                }
                if (flingRunnable != null) {
                    flingRunnable.cancelFling();
                    flingRunnable = null;
                }

                isCanDrag = false;
                if (rectF.width() > getWidth() + 0.1f || rectF.height() > getHeight() + 0.1f) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.1f || rectF.height() > getHeight() + 0.1f) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                float dx = x - lastX;
                float dy = y - lastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    if (getDrawable() != null) {

                        if (velocityTracker != null) {
                            velocityTracker.addMovement(motionEvent);
                        }

                        isCheckLeftAndRight = true;
                        isCheckTopAndBottom = true;

                        if (rectF.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                    }
                    scaleMatrix.postTranslate(dx, dy);
                    checkBorderWhenTranslate();
                    setImageMatrix(scaleMatrix);
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                lastPointCount = 0;
                if (getScale() < initScale) {
                    post(new AutoScaleRunnable(initScale, getWidth() / 2, getHeight() / 2));
                }
                if (getScale() > maxScale) {
                    post(new AutoScaleRunnable(maxScale, getWidth() / 2, getHeight() / 2));
                }
                if (isCanDrag) {
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(motionEvent);
                        velocityTracker.computeCurrentVelocity(1000);
                        final float vX = velocityTracker.getXVelocity();
                        final float vY = velocityTracker.getYVelocity();

                        flingRunnable = new FlingRunnable(getContext());
                        flingRunnable.fling(getWidth(), getHeight(), (int)-vX, (int)-vY);
                        post(flingRunnable);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }
        return true;
    }

    /**
     * 判断是否是移动的操作
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > touchSlop;
    }

    private class AutoScaleRunnable implements Runnable {
        private float targetScale;
        private float tempScale;
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.93f;

        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.targetScale = targetScale;
            this.x = x;
            this.y = y;

            if (getScale() < targetScale) {
                tempScale = BIGGER;
            }
            if (getScale() > targetScale) {
                tempScale = SMALLER;
            }
        }
        @Override
        public void run() {
            scaleMatrix.postScale(tempScale, tempScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(scaleMatrix);

            float currentScale = getScale();

            if ( (tempScale > 1.0f) && currentScale < targetScale || (tempScale < 1.0f) && currentScale > targetScale ) {
                postDelayed(this, 16);
            } else {
                float scale = targetScale / currentScale;
                scaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(scaleMatrix);
                isAutoScale = false;
            }
        }
    }

    private class FlingRunnable implements Runnable {
        private Scroller scroller;
        private int currentX;
        private int currentY;

        public FlingRunnable(Context context) {
            scroller = new Scroller(context);
        }
        public void cancelFling() {
            scroller.forceFinished(true);
        }
        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            RectF rectF = getMatrixRectF();
            if (rectF == null)
                return;
            final int startX = Math.round(-rectF.left);
            final int minX;
            final int maxX;
            final int minY;
            final int maxY;

            if (rectF.width() > viewWidth) {
                minX = 0;
                maxX = Math.round(rectF.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }
            final int startY = Math.round(-rectF.top);
            if (rectF.height() > viewHeight) {
                minY = 0;
                maxY = Math.round(rectF.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }
            currentX = startX;
            currentY = startY;
            if (startX != maxX || startY != maxY) {
                scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
        }

        @Override
        public void run() {
            if (scroller.isFinished())
                return;
            if (scroller.computeScrollOffset()) {
                final int newX = scroller.getCurrX();
                final int newY = scroller.getCurrY();
                scaleMatrix.postTranslate(currentX - newX, currentY - newY);
                checkBorderWhenTranslate();
                setImageMatrix(scaleMatrix);

                currentX = newX;
                currentY = newY;

                postDelayed(this, 16);
            }
        }
    }

    public interface PressAction {
        public void singleTap();
        public void longPress();
    }
}