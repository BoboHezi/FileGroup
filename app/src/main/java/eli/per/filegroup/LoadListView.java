package eli.per.filegroup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eli.per.view.CustomListAdapter;
import eli.per.view.CustomPhotoDialog;
import eli.per.view.CustomListView;
import eli.per.view.CustomVideoDialog;

public class LoadListView implements CustomListView.OnLoadMoreListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "LoadListView";
    public static final int HANDLER_STATE_REFRESH = 1;
    public static final int HANDLER_STATE_LOAD_ALL = 2;
    public static final int HANDLER_STATE_DELETE = 3;

    enum FileType {
        PHOTO,
        VIDEO,
        OTHER
    }

    private Context context;
    private RefreshHandler refreshHandler;
    private CustomListView fileListView;
    private LinearLayout bottomBar;
    private CustomListAdapter fileListAdapter;
    private List<Map<String, Object>> list;
    private List<File> files = new ArrayList<>();
    private File selectedFile;

    private CustomPhotoDialog photoDialog;
    private CustomVideoDialog videoDialog;
    private List<Integer> selectedItems;

    private boolean isLongClick = false;
    private static final int pageCount = 5;

    public LoadListView(Context context, Activity activity) {
        this.context = context;

        this.fileListView = activity.findViewById(R.id.filelist);
        this.bottomBar = activity.findViewById(R.id.bottomBar);

        this.fileListView.setOnItemClickListener(this);
        this.fileListView.setOnItemLongClickListener(this);
        this.fileListView.setLoadMoreListener(this);

        initList();
    }

    /**
     * 初始化ListView
     */
    private void initList() {
        list = new ArrayList<>();
        fileListAdapter = new CustomListAdapter(context, list);
        //为ListView设置适配器
        fileListView.setAdapter(fileListAdapter);
        files = new ReadFiles().readFiles();
        refreshHandler = new RefreshHandler();
    }

    /**
     * 开启线程进行数据的读取
     */
    public void loadFiles() {
        new AsyncReadFile(refreshHandler).start();
    }

    /**
     * 载入更多
     */
    @Override
    public void loadMore() {
        loadFiles();
    }

    /**
     * 点击某一条记录触发事件
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //当目前处于长按状态，或者有被选中的状态，则不响应单击事件
        if ((selectedItems != null && selectedItems.size() > 0) || isLongClick)
            return;

        selectedFile = files.get(position);

        if (checkFileType(selectedFile) == FileType.PHOTO) {
            //点击图片文件
            photoDialog = new CustomPhotoDialog(context, selectedFile, refreshHandler);
            photoDialog.show();
        }else if (checkFileType(selectedFile) == FileType.VIDEO) {
            //点击视频文件
            videoDialog = new CustomVideoDialog(context, selectedFile, refreshHandler);
            videoDialog.show();
        }
    }

    /**
     * 长按事件
     * @param adapterView
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (selectedItems == null) {
            selectedItems = new ArrayList<>();
        }

        if (selectedItems.contains(position)) {
            //当列表中存在该Item，移除
            selectedItems.remove(selectedItems.indexOf(position));
        } else {
            //当列表中不存在该Item，添加
            selectedItems.add(position);
        }

        //更新选中的Item
        notifySelected();

        //当目前存在被选中的Item，则显示底部的按钮框，否则隐藏
        float dip40 = dip2px(context, 40) - 2;
        if (selectedItems.size() > 0) {
            bottomBar.animate().translationY(-dip40).setDuration(300).start();
        } else {
            bottomBar.animate().translationY(2).setDuration(300).start();
        }
        return true;
    }

    /**
     * 删除文件对应记录
     * @param file
     */
    private void deleteFileFromView(File file) {
        if (file != null) {
            int index = files.indexOf(file);
            if (checkFileType(file) == FileType.PHOTO && photoDialog != null && photoDialog.isShowing()) {
                photoDialog.dismiss();
            } else if (checkFileType(file) == FileType.VIDEO && videoDialog != null && videoDialog.isShowing()) {
                videoDialog.dismiss();
            }
            //从ListView中删除记录
            fileListAdapter.removeItem(index);
            //从文件列表中删除记录
            files.remove(index);
            //如果该文件处于选中的状态，则需要更新
            if (selectedItems.contains(index)) {
                selectedItems.remove(selectedItems.indexOf(index));
                notifySelected();
            }
            selectedFile = null;
        }
    }

    /**
     * 删除对应文件
     * @param file 需要删除的文件
     */
    private void deleteFileFromDisk(File file) {
        if (file != null) {
            if (file.exists() && file.isFile())
                file.delete();
        }
    }

    /**
     * 检查文件的类型
     * @param file
     * @return
     */
    public static FileType checkFileType(File file) {
        if (file.getName().contains("IMG")) {
            return FileType.PHOTO;
        } else if (file.getName().contains("VID")) {
            return FileType.VIDEO;
        } else {
            return FileType.OTHER;
        }
    }

    /**
     * 更新被选中的Item
     */
    private void notifySelected() {
        fileListAdapter.setSelectedItem(selectedItems);
        fileListAdapter.notifyDataSetChanged();
    }

    /**
     * 更新视图的Handler
     */
    public class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            //刷新ListView
            if (msg.what == HANDLER_STATE_REFRESH) {
                //提醒ListView刷新页面
                fileListAdapter.notifyDataSetChanged();
            } else if (msg.what == HANDLER_STATE_LOAD_ALL) {
                Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT).show();
            } else if (msg.what == HANDLER_STATE_DELETE) {
                deleteFileFromView(selectedFile);
            }
        }
    }

    /**
     * 读取数据的异步线程
     */
    class AsyncReadFile extends Thread {

        private RefreshHandler refreshHandler;

        public AsyncReadFile(RefreshHandler refreshHandler) {
            this.refreshHandler = refreshHandler;
        }

        @Override
        public void run() {
            int start = fileListView.getCount();
            int end = (start + pageCount) > files.size() ? files.size() : (start + pageCount);

            //当文件全部载入结束后，则不再进行更新
            if (start >= end) {
                refreshHandler.sendEmptyMessage(HANDLER_STATE_LOAD_ALL);
                return;
            }

            fileListView.setLoadState(CustomListView.LOAD_STATE_LOADING);
            for (int i = start; i < end; i ++) {
                File file = files.get(i);
                Map<String, Object> map = new HashMap<>();

                //设置时间
                Date date = new Date(file.lastModified());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time = format.format(date);

                //获取对应缩略图
                Bitmap thumbnail = null;
                if (checkFileType(file) == FileType.PHOTO) {
                    time = "Photo    " + time;
                    thumbnail = getImageThumbnail(file.getPath(), 500, 350);
                } else if (checkFileType(file) == FileType.VIDEO) {
                    time = "Video    " + time;
                    thumbnail = getVideoThumbnail(file.getPath(), 500, 350, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                }

                map.put("time", time);
                map.put("image", thumbnail);

                //添加数据至数据集合
                list.add(map);
                refreshHandler.sendEmptyMessage(HANDLER_STATE_REFRESH);
            }
            fileListView.setLoadState(CustomListView.LOAD_STATE_NON_LOADING);
        }
    }

    /**
     * 获取图片的缩略图
     * @param imagePath 图片路径
     * @param width     缩略图宽度
     * @param height    缩略图高度
     * @return          缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;

        int w = options.outWidth;
        int h = options.outHeight;
        int beWidth = w / width;
        int beHeight = h / height;
        int be;

        if (beWidth < beHeight)
            be = beWidth;
        else
            be = beHeight;
        if (be <= 0)
            be = 1;

        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * @param videoPath 视频路径
     * @param width     缩略图宽度
     * @param height    缩略图高度
     * @param kind
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * Dip转像素
     * @param context
     * @param dpValue
     * @return
     */
    public float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }
}