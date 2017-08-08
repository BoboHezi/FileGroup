package eli.per.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import eli.per.filegroup.LoadListView;
import eli.per.filegroup.R;

public class CustomActionDialog extends BaseDialog implements View.OnClickListener {

    private Context context;
    private Button buttonShare;
    private Button buttonDelete;
    private Button buttonCancel;
    private File file;

    private LoadListView.RefreshHandler refreshHandler;

    public CustomActionDialog(Context context, File file, LoadListView.RefreshHandler refreshHandler) {
        super(context, null, null, R.style.dialog_action);
        this.context = context;
        this.file = file;
        this.refreshHandler = refreshHandler;
        initView();
    }

    /**
     * 初始化视图组件
     */
    @Override
    public void initView() {
        setContentView(R.layout.actiondialog);
        setWindowAnimation();

        buttonShare = findViewById(R.id.action_share);
        buttonShare.setOnClickListener(this);
        buttonDelete = findViewById(R.id.action_delete);
        buttonDelete.setOnClickListener(this);
        buttonCancel = findViewById(R.id.action_cancel);
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
        getWindow().setWindowAnimations(R.style.dialog_action_animation);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_share:
                Toast.makeText(context, "Sharing", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_delete:
                dismiss();
                CustomDeleteDialog deleteDialog = new CustomDeleteDialog(context, refreshHandler);
                deleteDialog.show();
                break;

            case R.id.action_cancel:
                dismiss();
                break;
        }
    }

    /**
     * 使用系统自带的分享
     */
    private void shareTo() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/mp4");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getPath()));
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }
}
