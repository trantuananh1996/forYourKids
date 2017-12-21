package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Photo;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;
import koiapp.pr.com.koiapp.utils.TouchImageView;
import koiapp.pr.com.koiapp.utils.view.PrFragment;


public class FragmentAlbumSlideshow extends PrFragment {
    public static final String TAG = FragmentAlbumSlideshow.class.getName();
    public List<Photo> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount;
    public int selectedPosition = 0;
    private FragmentPhoto parentFragment;
    View next, prev;
    private boolean overlayShow = true;
    View imageController;
    public boolean showComment = true;

    @NonNull
    public static FragmentAlbumSlideshow newInstance() {
        return new FragmentAlbumSlideshow();
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
              View v = inflater.inflate(R.layout.fragment_home_album_image_slider, container, false);
        viewPager = v.findViewById(R.id.viewpager);
        lblCount = v.findViewById(R.id.lbl_count);
        next = v.findViewById(R.id.next);
        prev = v.findViewById(R.id.prev);
        imageController = v.findViewById(R.id.image_controller);
        if (images == null) {
            try {// fix null crash reporting
                images = parentFragment.detail.getPhotos();
                selectedPosition = parentFragment.clicked_pos;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (images == null) return v;
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        v.findViewById(R.id.iv_close).setOnClickListener(view -> getActivity().onBackPressed());
        v.findViewById(R.id.iv_download).setVisibility(View.GONE);
        next.setOnClickListener(view -> {
            try {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        });
        prev.setOnClickListener(view -> {
            try {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        });


        imageController.setOnTouchListener((view, motionEvent) -> true);
        return v;
    }




    boolean likedPerPhoto = false;



    @Override
    public void update() {
        super.update();
        if (images == null) {
            images = parentFragment.detail.getPhotos();
            selectedPosition = parentFragment.clicked_pos;
        }
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, true);
        displayMetaInfo(selectedPosition);
    }

    private void resetZoomPage(int position) {
        View view = viewPager.getChildAt(position);
        if (view != null) {
            TouchImageView imageViewPreview = view.findViewById(R.id.image_preview);
            if (imageViewPreview != null) imageViewPreview.setZoom(1f);
        }
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            displayMetaInfo(position);
//            overlayShow = false;
//            disableOverlay();
            if (selectedPosition == images.size() - 1) {
                resetZoomPage(selectedPosition - 1);
                next.setVisibility(View.INVISIBLE);
                prev.setVisibility(View.VISIBLE);
            } else if (selectedPosition == 0) {
                resetZoomPage(selectedPosition + 1);
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            } else {
                resetZoomPage(selectedPosition - 1);
                resetZoomPage(selectedPosition + 1);
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        likedPerPhoto = false;
        lblCount.setText(String.format(getString(R.string.str_pos_total), position + 1, images.size()));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setParentFragment(FragmentPhoto parentFragment) {
        this.parentFragment = parentFragment;
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            TouchImageView imageViewPreview = view.findViewById(R.id.image_preview);
            imageViewPreview.setMaxZoom(3f);


            Photo image = images.get(position);
//            GlideHelper.load(FragmentAlbumSlideshow.this,image.getPath()).into(imageViewPreview);

            new GoogleMapApiHelper(parentFragment.getActivity()).loadImage(imageViewPreview
                    , image.getPhotoReference());

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
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

}