package koiapp.pr.com.koiapp.modulePost.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.modulePost.activity.FragmentCategory;
import koiapp.pr.com.koiapp.modulePost.activity.FragmentKidsonlineCorner;
import koiapp.pr.com.koiapp.modulePost.model.Category;
import koiapp.pr.com.koiapp.utils.ViewUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;

import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE_KOMT;

/**
 * Created by Tran Anh
 * on 4/21/2017.
 */

public class KidsCategoryCoverAdapter extends PagerAdapter {
    private FragmentKidsonlineCorner parentFragment;
    private List<Category> categories;

    public KidsCategoryCoverAdapter(FragmentKidsonlineCorner parentFragment) {
        this.parentFragment = parentFragment;
        categories = parentFragment.categories;
    }

    public static final int KBDTLG = 500; // ko biết đặt tên là gì

    @Override
    public int getItemPosition(Object object) {
        return categories.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(parentFragment.getActivity()).inflate(R.layout.category_viewpager, container, false);
        View viewToFitSize = view.findViewById(R.id.vp_cover_match_child);
        if (categories != null && categories.size() > 0) {
            position = position % categories.size();
            final Category category = categories.get(position);
            int viewWidth = ViewUtils.getScreenWidthInPx(parentFragment.getActivity()) / 5 * 3;
            int viewHeight = (9 * viewWidth) / 16;
            viewToFitSize.getLayoutParams().width = viewWidth;
            viewToFitSize.getLayoutParams().height = viewHeight;
            viewToFitSize.requestLayout();
            ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);
            Debug.prLog("Url to load", category.getAvatar());
            Glide.with(parentFragment.getActivity()).load(URL_BASE_KOMT + category.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCover);
//            loadImage(category.getAvatar(), ivCover);
            ivCover.setOnClickListener(v -> {
                FragmentCategory fragmentCategory = new FragmentCategory();
                fragmentCategory.setCategoryId(category.getId());
                fragmentCategory.setParentActivity(parentFragment.getActivity());
                FragmentManager manager = parentFragment.getChildFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up_enter,
                        R.anim.slide_left_exit,
                        R.anim.slide_down_enter,
                        R.anim.slide_right_exit);
                transaction.add(R.id.container1, fragmentCategory
                        , fragmentCategory.getClass().getName());
                transaction.addToBackStack(null);
                transaction.commit();
            });
            container.addView(view);
        }
        return view;
    }

    public void loadImage(String url, ImageView iv) {
        if (url == null || iv == null) return;
        Debug.prLog("Cover", URL_BASE_KOMT + url);
        Glide.with(parentFragment.getActivity()).load(URL_BASE_KOMT + url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(url))
                .fallback(ContextCompat.getDrawable(parentFragment.getActivity(), R.drawable.no_image))
                .error(ContextCompat.getDrawable(parentFragment.getActivity(), R.drawable.no_image))
                .into(iv);
    }


    @Override
    public int getCount() {
        if (categories != null && categories.size() > 0)
            return categories.size() * KBDTLG; // simulate infinite by big number of products
        else
            return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
