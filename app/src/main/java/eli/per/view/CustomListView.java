package eli.per.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class CustomListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "CustomListView";
    public static final boolean LOAD_STATE_LOADING = true;
    public static final boolean LOAD_STATE_NON_LOADING = false;

    private int lastVisiblePosition = 0;

    //是否加载标记
    private boolean isLoading = false;

    //加载更多监听器
    private OnLoadMoreListener loadMoreListener;

    public CustomListView(Context context) {
        this(context, null);
    }

    public CustomListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomListView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.setOnScrollListener(this);
    }

    /**
     * 滚动监听
     * @param absListView
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) { }

    /**
     * 滚动状态监听
     * @param absListView
     * @param scrollstate
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollstate) {

        //当正处于加载时，不会再次启动加载程序
        if (isLoading)
            return;

        View v = absListView.getChildAt(absListView.getChildCount() - 1);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int y = location[1];

        //当list滑动至底部、未处于加载之中
        //触发加载动作
        if ((this.getLastVisiblePosition() == this.getAdapter().getCount() - 1) && (scrollstate == SCROLL_STATE_IDLE)) {
            if (lastVisiblePosition == y) {
                this.loadMoreListener.loadMore();
                lastVisiblePosition = 0;
            } else {
                lastVisiblePosition = y;
            }
        }
    }

    //设置加载监听器
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    //设置当前是否加载
    public void setLoadState(boolean isLoading) {
        this.isLoading = isLoading;
    }

    //加载监听器
    public interface OnLoadMoreListener {
        void loadMore();
    }
}
