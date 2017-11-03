package koiapp.pr.com.koiapp.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

public class UnsafeOkHttpGlideModule implements GlideModule {
        @Override
        public void applyOptions(Context context, GlideBuilder builder) {

        }

        @Override
        public void registerComponents(Context context, Glide glide) {
//            glide.register(GlideUrl.class, InputStream.class, new UnsafeOkHttpClient());
        }
    }