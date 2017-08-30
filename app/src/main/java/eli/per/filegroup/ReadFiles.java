package eli.per.filegroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadFiles {

    /**
     * 读取所有的文件
     * @return
     */
    public List readFiles() {
        File photoFolder = new File("/sdcard/blueeye/photos/");
        File videoFolder = new File("/sdcard/blueeye/videos/");

        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
        List<File> allFiles = new ArrayList<>();

        //读取所有的图片
        File files[] = photoFolder.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("IMG"))
                    allFiles.add(file);
            }
        }
        //读取所有的视频
        files = videoFolder.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("VID"))
                    allFiles.add(file);
            }
        }
        //按时间进行逆序排序
        Collections.sort(allFiles, new ComparatorByLastModified());
        return allFiles;
    }

    class ComparatorByLastModified implements Comparator<File> {

        @Override
        public int compare(File f1, File f2) {

            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return -1;
            else if (diff == 0)
                return 0;
            else
                return 1;
        }
    }
}