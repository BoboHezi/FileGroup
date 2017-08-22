package eli.per.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import eli.per.filegroup.LoadListView;

public abstract class BaseDialog extends Dialog {

    public final String TAG = this.getClass().getName();

    private Context context;
    protected TextView timeText;
    protected File file;
    private LoadListView.RefreshHandler refreshHandler;

    public BaseDialog(Context context, File file, LoadListView.RefreshHandler refreshHandler, int style) {
        super(context, style);
        this.context = context;
        this.file = file;
        this.refreshHandler = refreshHandler;
    }

    /**
     * 初始化视图，必须在子类中重写
     */
    protected abstract void initView();

    protected abstract void setWindowAnimation();

    /**
     * 弹出对话框
     */
    protected void showActionDialog() {
        CustomActionDialog actionDialog = new CustomActionDialog(context,file, refreshHandler);
        actionDialog.show();
    }

    /**
     * 设置窗口和风格
     */
    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    /**
     * 设置时间
     */
    protected void showTime() {
        if (timeText != null) {
            Date date = new Date(file.lastModified());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String time = format.format(date);
            timeText.setText(time);
        }
    }
}
