package koiapp.pr.com.koiapp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Tran Anh
 * on 8/5/2016.
 */
public class SquareImageViewByHeight extends ImageView {
    public SquareImageViewByHeight(Context context) {
        super(context);
    }

    public SquareImageViewByHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewByHeight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareImageViewByHeight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

}
