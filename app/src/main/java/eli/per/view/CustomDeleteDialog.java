package eli.per.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import eli.per.filegroup.LoadListView;
import eli.per.filegroup.R;

public class CustomDeleteDialog extends BaseDialog implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    private Context context;
    private Button buttonCancel;
    private Button buttonDelete;

    private LoadListView.RefreshHandler refreshHandler;

    public CustomDeleteDialog(Context context, LoadListView.RefreshHandler refreshHandler) {
        super(context, null, null, R.style.dialog_action);
        this.context = context;
        this.refreshHandler = refreshHandler;
        initView();
    }

    /**
     * 初始化视图组件
     */
    @Override
    public void initView() {
        setContentView(R.layout.deletedialog);
        setWindowAnimation();

        buttonCancel = findViewById(R.id.delete_cancel);
        buttonDelete = findViewById(R.id.delete_confirm);
        buttonDelete.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    /**
     * 重写show方法，定义窗口样式和动画
     */
    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    /**
     * 设置窗口动画
     */
    @Override
    public void setWindowAnimation() {
        getWindow().setWindowAnimations(R.style.dialog_delete_animation);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.delete_cancel:
                dismiss();
                break;

            case R.id.delete_confirm:
                dismiss();
                refreshHandler.sendEmptyMessage(LoadListView.HANDLER_STATE_DELETE);
                break;
        }
    }
}
