package net.yazeed44.imagepicker.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by yazeed44 on 6/14/15.
 */
public class ImageEntry implements Serializable {
    public final int imageId;
    public final String path;
    public final long dateAdded;
    public boolean isPicked = false;
    public boolean isVideo = false;

    private int orientation = 0;
    private int maxDimen = 1000;

    private Bitmap bitmap;

    public ImageEntry(String path, Bitmap bitmap, Long dateAdded) {
        this.path = path;
        this.imageId = dateAdded.intValue();
        this.bitmap = bitmap;
        this.dateAdded = dateAdded;
    }

    public String getBase64() {
        String rt = "";
        Bitmap bmp = getScaledBitmap();
        ExifInterface exif;
        int angle = 0;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);


            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Matrix matrix1 = new Matrix();

        //set image rotation value to 45 degrees in matrix.
        matrix1.postRotate(angle);

        //Create bitmap with new values.
        bmp = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), matrix1, true);
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return rt;
    }

    public Bitmap getBitmapRotated() {
        Bitmap bmp = getScaledBitmap();
        ExifInterface exif;
        int angle = 0;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);


            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Matrix matrix1 = new Matrix();

        //set image rotation value to 45 degrees in matrix.
        matrix1.postRotate(angle);

        //Create bitmap with new values.
        bmp = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), matrix1, true);
        return bmp;
    }

    public String getBase64Rotated(Context context) {
        String rt = "";
        Bitmap bmp = null;
        try {
            bmp = getBitmapResignedAndRotated(getUri(), context);
            if (bmp != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                return Base64.encodeToString(b, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rt;
    }

    public Bitmap getBitmapResignedAndRotated(Uri photoUri, Context context) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri, context);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > maxDimen || rotatedHeight > maxDimen) {
            float widthRatio = ((float) rotatedWidth) / ((float) maxDimen);
            float heightRatio = ((float) rotatedHeight) / ((float) maxDimen);
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

    public int getOrientation(Uri photoUri, Context mContext) {
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

    public Bitmap getBitmap() {
        if (TextUtils.isEmpty(path)) {
            return bitmap;
        } else {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Uri uri = Uri.fromFile(imgFile);

                return rotateImage(BitmapFactory.decodeFile(imgFile.getAbsolutePath()), orientation);
            }
        }
        return null;
    }

    public Uri getUri() {
        if (TextUtils.isEmpty(path)) {
            return null;
        } else {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                return Uri.fromFile(imgFile);
            }
        }
        return null;
    }


    public Bitmap getScaledBitmap() {
        Bitmap origin_bm = getBitmap();
        if (origin_bm == null) return null;
        int original_width = origin_bm.getWidth();
        int original_height = origin_bm.getHeight();
        int new_width = original_width;
        int new_height = original_height;
        if (original_width > maxDimen) {
            new_width = maxDimen;
            new_height = (new_width * original_height) / original_width;
        }
        if (new_height > maxDimen) {
            new_height = maxDimen;
            new_width = (new_height * original_width) / original_height;
        }
        return Bitmap.createScaledBitmap(origin_bm, new_width, new_height, false);
    }

    private Bitmap rotateImage(Bitmap before, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(before, 0, 0, before.getWidth(), before.getHeight(), matrix, true);
    }

    public ImageEntry(final Builder builder) {
        this.path = builder.mPath;
        this.imageId = builder.mImageId;
        this.dateAdded = builder.mDateAdded;
    }

    public static ImageEntry from(final Cursor cursor) {
        return Builder.from(cursor).build();
    }

    public static ImageEntry from(final Uri uri) {
        return Builder.from(uri).build();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ImageEntry && ((ImageEntry) o).path.equals(path);
    }

    @Override
    public String toString() {
        return "ImageEntry{" +
                "path='" + path + '\'' +
                '}';
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public static class Builder {

        public static int count = -1;
        private final String mPath;
        private int mImageId;
        private long mDateAdded;

        public Builder(final String path) {
            this.mPath = path;
        }

        public static Builder from(final Uri uri) {

            return new Builder(uri.getPath())
                    .imageId(count--)
                    ;

        }

        public static Builder from(final Cursor cursor) {
            final int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            final int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            final int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

            final int imageId = cursor.getInt(imageIdColumn);
            final String path = cursor.getString(dataColumn);
            final long dateAdded = cursor.getLong(dateAddedColumn);

            return new ImageEntry.Builder(path)
                    .imageId(imageId)
                    .dateAdded(dateAdded)
                    ;

        }


        public Builder imageId(int imageId) {
            this.mImageId = imageId;
            return this;
        }

        public Builder dateAdded(long timestamp) {
            this.mDateAdded = timestamp;
            return this;
        }


        public ImageEntry build() {
            return new ImageEntry(this);
        }


    }

    public int getMaxDimen() {
        return maxDimen;
    }

    public void setMaxDimen(int maxDimen) {
        this.maxDimen = maxDimen;
    }
}
