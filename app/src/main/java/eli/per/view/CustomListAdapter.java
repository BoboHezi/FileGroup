package eli.per.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eli.per.filegroup.R;

public class CustomListAdapter extends BaseAdapter {

    private static final String TAG = "ListAdapter";

    private List<Map<String, Object>> data;
    private LayoutInflater inflater;
    private Animation animation;
    private Map<Integer, Boolean> isFirst;
    private List<Integer> selectedItem;
    private int size;

    public CustomListAdapter(Context context, List<Map<String, Object>> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.animation = AnimationUtils.loadAnimation(context, R.anim.anim_item_enter);
        this.isFirst = new HashMap<>();
        this.selectedItem = new ArrayList<>();
    }

    public final class ItemView {
        public CirclePoint typeColor;
        public TextView itemTime;
        public ImageView itemThumbnail;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        size = data.size();
        super.notifyDataSetChanged();
    }

    /**
     * 删除某条
     * @param index
     */
    public void removeItem(int index) {
        this.data.remove(index);
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView ;

        if (convertView == null) {
            itemView = new ItemView();
            convertView = inflater.inflate(R.layout.fileitem, null);
            itemView.typeColor = convertView.findViewById(R.id.typecolor);
            itemView.itemTime = convertView.findViewById(R.id.creattime);
            itemView.itemThumbnail = convertView.findViewById(R.id.thumbnail);
            convertView.setTag(itemView);
        } else {
            itemView = (ItemView) convertView.getTag();
        }
        if (isFirst.get(position) == null || isFirst.get(position)) {
            convertView.startAnimation(animation);
            isFirst.put(position, false);
        }
        itemView.itemTime.setText((String) data.get(position).get("time"));
        itemView.itemThumbnail.setImageBitmap((Bitmap)data.get(position).get("image"));

        if (selectedItem.contains(position)) {
            itemView.typeColor.setFocus();
        } else {
            itemView.typeColor.setDismiss();
        }

        return convertView;
    }

    /**
     * 设置被选中的记录
     * @param selectedItem
     */
    public void setSelectedItem(List<Integer> selectedItem) {
        this.selectedItem = selectedItem;
    }
}