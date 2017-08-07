package eli.per.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageButton;
import java.io.File;
import eli.per.filegroup.LoadListView;
import eli.per.filegroup.R;

public class CustomPhotoDialog extends BaseDialog implements CustomImageView.PressAction, View.OnClickListener {

    private CustomImageView imageView;
    private ImageButton buttonMore;
    private Bitmap bitmap;

    public CustomPhotoDialog(Context context, File file, LoadListView.RefreshHandler refreshHandler) {
        super(context, file, refreshHandler, R.style.dialog);
        initView();
    }

    /**
     * 初始化窗口和视图
     */
    @Override
    public void initView() {
        setContentView(R.layout.picturedialog);
        setWindowAnimation();

        imageView = findViewById(R.id.image_show);
        imageView.setOnPressAction(this);
        buttonMore = findViewById(R.id.photo_more);
        buttonMore.setOnClickListener(this);
        timeText = findViewById(R.id.photo_time);
        showTime();
        showPicture();
    }

    /**
     * 设置窗体动画
     */
    @Override
    public void setWindowAnimation() {
        getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    /**
     * 定义窗口和动画，通过文件显示图片
     */
    public void showPicture() {
        try {
            bitmap = BitmapFactory.decodeFile(file.getPath());
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
        }
        this.show();
    }

    /**
     * 单击事件
     */
    @Override
    public void singleTap() {
        dismiss();
    }

    /**
     * 长按事件
     */
    @Override
    public void longPress() {
        showActionDialog();
    }

    /**
     * 按钮点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        showActionDialog();
    }
}
