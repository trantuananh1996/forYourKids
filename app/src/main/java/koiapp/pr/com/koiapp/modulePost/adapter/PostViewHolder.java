package koiapp.pr.com.koiapp.modulePost.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import koiapp.pr.com.koiapp.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_avatar)
    public ImageView avatar;
    @BindView(R.id.tv_title)
    public TextView title;
    @BindView(R.id.tv_description)
    public TextView description;
    @BindView(R.id.tv_time)
    public TextView time;
    @BindView(R.id.tv_count_view)
    public TextView view;

    public PostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}