package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import net.tundigital.carrefourdemo.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import datamodels.Offer;

public class InstantOffersAdapter extends ArrayAdapter<Offer> {
    private Context context;
    private int layoutResourceId;
    private List<Offer> data;

    public InstantOffersAdapter(Context context, int layoutResourceId, List<Offer> data) {
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
            holder.textDesc = (TextView) row.findViewById(R.id.text_description);
            holder.textRemainHours = (TextView) row.findViewById(R.id.text_remainHours);
            holder.textRemainMinutes = (TextView) row.findViewById(R.id.text_remainMinutes);
            holder.textRemainSeconds = (TextView) row.findViewById(R.id.text_remainSeconds);
            holder.progressBar = (HoloCircularProgressBar) row.findViewById(R.id.holoCircularProgressBar);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        // set values
        final Offer offer = data.get(position);
        holder.textTitle.setText(offer.getName());
        holder.textDesc.setText(offer.getDescription());

        // set remain time
        long[] remainTime = offer.getRemainTime();
        holder.textRemainHours.setText((remainTime[2] < 10) ? "0" + remainTime[2] : "" + remainTime[2]);
        holder.textRemainMinutes.setText((remainTime[1] < 10) ? "0" + remainTime[1] : "" + remainTime[1]);
        holder.textRemainSeconds.setText((remainTime[0] < 10) ? "0" + remainTime[0] : "" + remainTime[0]);

        // calculate progress
        long createTime = offer.getCreateDate().getTime();
        long expireTime = offer.getExpireDate().getTime();
        long currentTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();

        float progress = ((float) (currentTime - createTime) / (expireTime - createTime));
        holder.progressBar.setProgress(progress);

//        // check values
//        if (currentTime >= expireTime) {
//            // delete this offer
//            offerDAO.open();
//            offerDAO.delete(offer.getId());
//            offerDAO.close();
//        }

        return row;
    }

    public static class ViewHolder {
        TextView textTitle;
        TextView textDesc;
        TextView textRemainHours;
        TextView textRemainMinutes;
        TextView textRemainSeconds;
        HoloCircularProgressBar progressBar;
    }
}
