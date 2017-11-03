package koiapp.pr.com.koiapp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import koiapp.pr.com.koiapp.R;

import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE;
import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE_KOMT;


/**
 * Created by Tran Anh on 11/5/2016.
 */
public class ImageUtils {
    private static Context mContext;
    private static volatile ImageUtils instance = null;

    public static ImageUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (ImageUtils.class) {
                if (instance == null) {
                    instance = new ImageUtils(context);

                }
            }
        }
        return instance;
    }

    private ImageUtils(Context context) {
        mContext = context;
    }

    public String getBase64(Bitmap bmp) {
        String rt = "";
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return rt;
    }

    public void loadImage(String url, ImageView iv) {
        if (mContext instanceof AppCompatActivity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (((AppCompatActivity) mContext).isDestroyed()) return;
            }
        }
        if (url == null || iv == null) return;
//        if (mContext == null) return;
        if (url.toLowerCase().contains("noimage")) {
            Glide.with(mContext)
                    .load(R.drawable.no_image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(iv);
        } else
            Glide.with(mContext).load(URL_BASE_KOMT + url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(url))
                    .fallback(ContextCompat.getDrawable(mContext, R.drawable.no_image))
                    .error(ContextCompat.getDrawable(mContext, R.drawable.no_image))
                    .into(iv);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadImage(String url, ImageView iv, String signature) {
        if (url == null || iv == null || signature == null || mContext == null) return;
        if (url.toLowerCase().contains("noimage")) {
            Glide.with(mContext)
                    .load(R.drawable.no_image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(iv);
        } else Glide.with(mContext).load(URL_BASE + url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(signature))
                .error(ContextCompat.getDrawable(mContext, R.drawable.no_image))
                .fallback(ContextCompat.getDrawable(mContext, R.drawable.no_image))
                .into(iv);
    }


    public Bitmap scaleImageBitmap(Bitmap origin_bm, int max_size) {
        int original_width = origin_bm.getWidth();
        int original_height = origin_bm.getHeight();

        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > max_size) {
            //scale width to fit
            new_width = max_size;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > max_size) {
            //scale height to fit instead
            new_height = max_size;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return Bitmap.createScaledBitmap(origin_bm, new_width, new_height, true);
    }

    public int getOrientation(Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = mContext.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null) return -1;
        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Bitmap getBitmapResignedAndRotated(Uri photoUri, int max_dimen) throws IOException {
        InputStream is = mContext.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = mContext.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > max_dimen || rotatedHeight > max_dimen) {
            float widthRatio = ((float) rotatedWidth) / ((float) max_dimen);
            float heightRatio = ((float) rotatedHeight) / ((float) max_dimen);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }


    public Bitmap rotateImage(Bitmap before, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(before, 0, 0, before.getWidth(), before.getHeight(), matrix, true);
    }


    protected static void requestPermission(final Activity activity, final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showAlertDialog(mContext.getString(R.string.permission_title_rationale), rationale,
                    (dialog, which) -> ActivityCompat.requestPermissions(activity,
                            new String[]{permission}, requestCode), mContext.getString(R.string.label_ok), null, mContext.getString(R.string.label_cancel), activity)
            ;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    protected static void showAlertDialog(@Nullable String title, @Nullable String message,
                                          @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                          @NonNull String positiveText,
                                          @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                          @NonNull String negativeText,
                                          Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        builder.create().show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap getBitmapResignedAndRotated(Context context, Uri photoUri, int max_dimen) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > max_dimen || rotatedHeight > max_dimen) {
            float widthRatio = ((float) rotatedWidth) / ((float) max_dimen);
            float heightRatio = ((float) rotatedHeight) / ((float) max_dimen);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    public static String encodeBitmapToBase64(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
