package koiapp.pr.com.koiapp.modulePost.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.modulePost.activity.ActivityViewPostWebView;
import koiapp.pr.com.koiapp.modulePost.model.KidsCornerPost;
import koiapp.pr.com.koiapp.utils.ViewUtils;

import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE_KOMT;


/**
 * Created by Tran Anh
 * on 4/6/2017.
 */

public class ListPostHorzAdapter extends RecyclerView.Adapter<ListPostHorzAdapter.PostViewHolder> {
    private Activity activity;
    List<KidsCornerPost> posts;
    @LayoutRes
    private int layoutId = R.layout.item_news_kc;

    private boolean showView = false;

    public ListPostHorzAdapter(Activity activity, List<KidsCornerPost> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(layoutId, parent, false);
        return new PostViewHolder(view);
    }

    private int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    private int getNavBarHeight() {
        Resources resources = activity.getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        KidsCornerPost post = posts.get(position);
        loadImage(post.getAvatar(), holder.avatar);
        holder.title.setText(post.getTitle());
        holder.view.setText(String.valueOf(post.getViews()));
        if (showView)
            holder.vCountView.setVisibility(View.VISIBLE);
        else holder.vCountView.setVisibility(View.GONE);
        int totalHeight = ViewUtils.getScreenHeightInPx(activity);
        float avaiHeightForContent = totalHeight - getStatusBarHeight()
                - getNavBarHeight()
                - ViewUtils.convertDpToPixel(114, activity);
        int padding10 = (int) ViewUtils.convertDpToPixel(10f, activity);
        int halfW = (ViewUtils.getScreenWidthInPx(activity) - padding10) * 3 / 5;
        holder.allContent.setPadding(padding10, 0, padding10, 0);
        holder.allContent.setLayoutParams(new ViewGroup.LayoutParams(halfW, (int) (avaiHeightForContent / 3)));
        holder.allContent.requestLayout();

    }
    public void loadImage(String url, ImageView iv) {
        if (url == null || iv == null) return;
//        if (mContext == null) return;
        if (url.toLowerCase().contains("noimage")) {
            Glide.with(activity)
                    .load(R.drawable.no_image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(iv);
        } else
            Glide.with(activity).load(URL_BASE_KOMT + url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(url))
                    .fallback(ContextCompat.getDrawable(activity, R.drawable.no_image))
                    .error(ContextCompat.getDrawable(activity, R.drawable.no_image))
                    .into(iv);
    }


    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public boolean isShowView() {
        return showView;
    }

    public void setShowView(boolean showView) {
        this.showView = showView;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView avatar;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_count_view)
        TextView view;
        @BindView(R.id.ll_count_view)
        View vCountView;
        @BindView(R.id.content)
        View allContent;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != -1) {
                    Intent intent = new Intent(activity, ActivityViewPostWebView.class);
                    intent.putExtra("url", posts.get(getAdapterPosition()).getUrl());
                    activity.startActivity(intent);
                }
            });
        }
    }
}
