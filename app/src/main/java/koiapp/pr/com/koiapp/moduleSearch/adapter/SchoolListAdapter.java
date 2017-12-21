package koiapp.pr.com.koiapp.moduleSearch.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.maps.model.PlacesSearchResult;

import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSchoolInfo.activity.ActivitySchoolInfo;
import koiapp.pr.com.koiapp.moduleSearch.model.Result;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;

/**
 * Created by nguyetdtm
 * on 4/17/2017.
 */

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.SchoolListViewHolder> {
    private List<PlacesSearchResult> schools;
    private FragmentActivity activity;

    private boolean isViewMaps = true;

    public SchoolListAdapter(List<PlacesSearchResult> schools, FragmentActivity activity) {
        this.schools = schools;
        this.activity = activity;
    }

    @Override
    public SchoolListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_school_infomation, parent, false);
        return new SchoolListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SchoolListViewHolder holder, int position) {
        PlacesSearchResult school = schools.get(position);
        if (school.photos!= null && school.photos.length > 0)
            new GoogleMapApiHelper(activity).loadImage(holder.logo, school.photos[0].photoReference);
        holder.name.setText(school.name);
        holder.description.setText("Giới thiệu: " + school.name);
        holder.address.setText("Địa chỉ: " + school.vicinity);
    }

    @Override
    public int getItemCount() {
        return schools == null ? 0 : schools.size();
    }

    public boolean isViewMaps() {
        return isViewMaps;
    }

    public void setViewMaps(boolean viewMaps) {
        isViewMaps = viewMaps;
    }

    class SchoolListViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;
        TextView address;
        TextView description;

        public SchoolListViewHolder(View itemView) {
            super(itemView);
            logo = (ImageView) itemView.findViewById(R.id.iv_school_logo);
            name = (TextView) itemView.findViewById(R.id.tv_school_name);
            address = (TextView) itemView.findViewById(R.id.tv_school_address);
            description = (TextView) itemView.findViewById(R.id.tv_school_description);


            if (isViewMaps) {
                itemView.setOnClickListener(v -> {
                    if (getAdapterPosition() < 0) return;
                    Intent intent = new Intent(activity, ActivitySchoolInfo.class);
                    intent.putExtra("SchoolId", schools.get(getAdapterPosition()).placeId);
                    activity.startActivity(intent);
//                        FragmentViewOnMaps fragmentViewOnMaps = new FragmentViewOnMaps();
//                        fragmentViewOnMaps.setItemSchool(schools.get(getAdapterPosition()));
//                        FragmentUtils.addNewFragment(fragmentViewOnMaps, "Xem bản đồ", activity);
                });
            }
        }
    }
}
