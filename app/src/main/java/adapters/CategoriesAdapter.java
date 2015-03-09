package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.turndigital.carrefourdemo.R;

import datamodels.Category;

public class CategoriesAdapter extends ArrayAdapter<Category> {
    private Context context;
    private int layoutResourceId;
    private Category[] data;

    public CategoriesAdapter(Context context, int layoutResourceId, Category[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textTitle = (TextView) row.findViewById(R.id.text_title);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Category category = data[position];
        holder.textTitle.setText(category.getName());

        return row;
    }

    static class ViewHolder {
        TextView textTitle;
    }
}
