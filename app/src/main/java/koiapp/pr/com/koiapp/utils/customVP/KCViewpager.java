package koiapp.pr.com.koiapp.utils.customVP;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import com.pixplicity.multiviewpager.MultiViewPager;

import java.lang.reflect.Field;

/**
 * Created by Tran Anh
 * on 4/28/2017.
 */

public class KCViewpager extends MultiViewPager {

    // The speed of the scroll used by setCurrentItem()
    private static final int VELOCITY = 200;

   /* @Override
    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, VELOCITY);
    }
*/
    public KCViewpager(Context context) {
        super(context);
        postInitViewPager();
    }

    public KCViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    private ScrollerCustomDuration mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScroller.setScrollDurationFactor(scrollFactor);
    }
}
