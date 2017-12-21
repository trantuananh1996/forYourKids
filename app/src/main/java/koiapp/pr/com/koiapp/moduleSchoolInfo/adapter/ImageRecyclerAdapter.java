package koiapp.pr.com.koiapp.moduleSchoolInfo.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSchoolInfo.activity.FragmentAlbumSlideshow;
import koiapp.pr.com.koiapp.moduleSchoolInfo.activity.FragmentPhoto;
import koiapp.pr.com.koiapp.moduleSearch.model.Photo;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.ViewUtils;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageSampleViewHolder> {
    private List<koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Photo> photos;
    private FragmentActivity activity;
    private FragmentPhoto parentFragment;


    public ImageRecyclerAdapter(FragmentPhoto parentFragment) {
        this.photos = parentFragment.detail.getPhotos();
        this.parentFragment = parentFragment;
        this.activity = parentFragment.getActivity();
    }

    @Override
    public ImageRecyclerAdapter.ImageSampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_image_selected, parent, false);
        return new ImageRecyclerAdapter.ImageSampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageRecyclerAdapter.ImageSampleViewHolder holder, final int position) {
        holder.remove.setVisibility(View.GONE);
        ViewUtils.setMarginFor(holder.thumbnail, 4, 4, 4, 4);
        koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Photo photo = photos.get(position);
        new GoogleMapApiHelper(parentFragment.getActivity()).loadImage(holder.thumbnail
                , photo.getPhotoReference());
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    class ImageSampleViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        View remove;

        ImageSampleViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_selected_width);
            itemView.findViewById(R.id.iv_selected).setVisibility(View.GONE);
            thumbnail.setVisibility(View.VISIBLE);
            remove = itemView.findViewById(R.id.iv_edit);
            remove.setVisibility(View.GONE);

            itemView.setOnClickListener(view -> {
                if (getAdapterPosition() == -1) return;
//                    Intent intent = new Intent(activity, ActivityAlbumSlideShow.class);
//                    intent.putExtra("albumId", parentFragment.getAlbum().getId());
//                    activity.startActivity(intent);

                FragmentAlbumSlideshow newFragment = FragmentAlbumSlideshow.newInstance();
                newFragment.setParentFragment(parentFragment);
                newFragment.images = photos;
                newFragment.selectedPosition = getAdapterPosition();

                parentFragment.clicked_pos = getAdapterPosition();
                FragmentUtils.addNewFragment(newFragment,"Photo", activity);
            });

        }
    }


}
