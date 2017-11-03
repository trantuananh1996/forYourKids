package koiapp.pr.com.koiapp.modulePost.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import io.realm.Realm;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.modulePost.model.KidsCornerPost;
import koiapp.pr.com.koiapp.utils.NameConfigs;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;

/**
 * Created by Tran Anh
 * on 5/5/2017.
 */

public class ActivityViewPostWebView extends Activity {
    public static final String TAG = ActivityViewPostWebView.class.getName();
    WebView webview;
    View ivShare;
    String url;
    KidsCornerPost post;
    ProgressBar progressBar;
    TextView tvTitle;
    TextView tvSubtitle;
    ImageView ivCancel;
    View ivComment;

    TextView tvCountLike;
    TextView tvCountComment;
    ImageView ivFlower;
//    CommentModule commentModule;
    View like;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = PrRealm.getInstance(getApplicationContext()).getRealm(NameConfigs.DB_KIDSONLINE_CORNER_NAME);
        setContentView(R.layout.activity_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ActivityViewPostWebView.this, R.color.colorPrimary));
        }
        findView();
        tvTitle.setText("");
        tvSubtitle.setText("");
        tvCountLike.setText("Thích");
        tvCountComment.setText("Bình luận");
        initWebview(webview);
        int postToShareStr = getIntent().getExtras().getInt("POST_ID");
        post = realm.where(KidsCornerPost.class).equalTo("id", postToShareStr).findFirst();
        if (post != null) {
            url = post.getUrl();
            tvTitle.setText(post.getTitle());
            tvSubtitle.setText(post.getUrl());
         /*   commentModule = new CommentModule.Builder(ActivityViewPostWebView.this)
                    .setModeField(CommentModule.MODE_POST, post.getId())
                    .setAddCommentUrl("api/v3/corner-kidsonline/comments/writeComment")
                    .setEditCommentUrl("api/v3/corner-kidsonline/comments/editComment")
                    .setDeleteCommentUrl("api/v3/corner-kidsonline/comments/deleteComment")
                    .setGetCommentUrl("api/v3/corner-kidsonline/comments/loadComments")
                    .setLikeUrl("api/v3/corner-kidsonline/comments/like")
                    .setUnlikeUrl("api/v3/corner-kidsonline/comments/unlike")
                    .setViewInfo(false)
                    .setOnLoadReadyListener(new CommentModule.OnLoadCommentReadyListener() {
                        @Override
                        public void onLoadReady(DataLoadComment dataLoadComment) {
                            tvCountLike.setText(dataLoadComment.getLikes().getCountLikes() + " thích");
                            tvCountComment.setText(dataLoadComment.getComments().size() + " bình luận");
                            if (dataLoadComment.getLikes().getWasLiked() == 1) {
                                //noinspection deprecation
                                ivFlower.setAlpha(255);
                            } else {
                                //noinspection deprecation
                                ivFlower.setAlpha(77);
                            }
                        }
                    })
                    .build();*/
        }
        if (!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
            Debug.prLog("WV", "Loaded url: " + url);
        }
        addListener();
    }

    private void addListener() {
        ivShare.setOnClickListener(v -> {
            ShareDialog shareDialog = new ShareDialog(ActivityViewPostWebView.this);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(webview.getUrl()))
                        .setContentTitle(webview.getTitle())
                        .build();
                shareDialog.show(content);
            }
        });
        ivCancel.setOnClickListener(v -> ActivityViewPostWebView.this.finish());
        if (post != null) ivComment.setOnClickListener(v -> {
//                if (commentModule != null) commentModule.showDialogComment();
        });
        like.setOnClickListener(v -> {
//                if (commentModule != null) commentModule.switchLike();
        });
    }

    private int getPostIdFromUrl(String url) {
        int postId = 0;
        int start = url.indexOf("?coid=") + 6;
        if (start == -1) return postId;
        int end = url.length();
        String id = url.substring(start, end);
        try {
            postId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return postId;
    }

    private void initWebview(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new Browser());
        webView.setWebChromeClient(new MyWebClient());
       /* webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {

                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                Debug.prLog("Web loaded", newProgress + " percent");
            }
        });*/
      /*  webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Debug.prLog(TAG, url);
                if (!ActivityViewPostWebView.this.url.equals(url)) {
                    if (getPostIdFromUrl(url) == 0) {
                        ivComment.setVisibility(View.INVISIBLE);
                        like.setVisibility(View.INVISIBLE);
                    } else {
                        commentModule.setField_id(getPostIdFromUrl(url));
                    }
                } else {
                    commentModule.setField_id(post.getId());
                    ivComment.setVisibility(View.VISIBLE);
                    like.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Debug.prLog("WV", "page finished");
                tvTitle.setText(view.getTitle());
                tvSubtitle.setText(view.getUrl());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });*/
    }

    private void findView() {
        webview = (WebView) findViewById(R.id.webview_main);
        ivShare = findViewById(R.id.iv_share_fb);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivComment = findViewById(R.id.iv_comment);
        tvCountComment = (TextView) findViewById(R.id.tv_count_comment);
        tvCountLike = (TextView) findViewById(R.id.tv_count_like);
        ivFlower = (ImageView) findViewById(R.id.iv_flower);
        like = findViewById(R.id.like);
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) webview.goBack();
        else super.onBackPressed();
    }

    class Browser
            extends WebViewClient {
        Browser() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Debug.prLog(TAG, url);
            if (!ActivityViewPostWebView.this.url.equals(url)) {
                if (getPostIdFromUrl(url) == 0) {
                    ivComment.setVisibility(View.INVISIBLE);
                    like.setVisibility(View.INVISIBLE);
                } else {
//                    commentModule.setField_id(getPostIdFromUrl(url));
                }
            } else {
//                commentModule.setField_id(post.getId());
//                ivComment.setVisibility(View.VISIBLE);
//                like.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Debug.prLog("WV", "page finished");
            tvTitle.setText(view.getTitle());
            tvSubtitle.setText(view.getUrl());
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString) {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient
            extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            return BitmapFactory.decodeResource(ActivityViewPostWebView.this.getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {

                progressBar.setProgress(newProgress);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            Debug.prLog("Web loaded", newProgress + " percent");
        }

        public void onHideCustomView() {
            ((FrameLayout) ActivityViewPostWebView.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            ActivityViewPostWebView.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            ActivityViewPostWebView.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = ActivityViewPostWebView.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = ActivityViewPostWebView.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) ActivityViewPostWebView.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            ActivityViewPostWebView.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }
}
