package koiapp.pr.com.koiapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;

import static koiapp.pr.com.koiapp.utils.FragmentUtils.showProgress;


public class ActivityLogin extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    EditText etUserName;
    EditText etPassw;
    Button btnLogin;
    View btnForgotPassw;
    CheckBox cbSavePass;
    ProgressBar progressBar;
    RelativeLayout rlContent;
    String currentUsername = "";
    String currentPassword = "";
    ScrollView scrollView;
    private FirebaseAuth mAuth;

    // ...
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    View vGooglelogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ActivityLogin.this, R.color.colorPrimary));
        }

        findView();

        Intent i = getIntent();
        if (i != null) {
            Bundle e = i.getExtras();
            if (e != null) {
                String emailCreated = e.getString("username_created");
                if (!TextUtils.isEmpty(emailCreated)) {
                    etUserName.setText(emailCreated);
                }
            }
        }
        vGooglelogin.setVisibility(View.VISIBLE);
        addListener();
        currentUsername = etUserName.getText().toString();
        currentPassword = etPassw.getText().toString();

    }

    private void findView() {
        rlContent = (RelativeLayout) findViewById(R.id.rl_content);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_login);
        etUserName = (EditText) findViewById(R.id.edtUserName);
        etPassw = (EditText) findViewById(R.id.edtPassw);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnForgotPassw = findViewById(R.id.btnForgotPassw);
        scrollView = (ScrollView) findViewById(R.id.scr_content);
        cbSavePass = (CheckBox) findViewById(R.id.cbSavePassw);
        vGooglelogin = findViewById(R.id.google_login);

    }

    ProgressDialog progressDialog;
    GoogleApiClient mGoogleApiClient;

    private void addListener() {
        vGooglelogin.setOnClickListener(v -> {
                    progressDialog = ProgressDialog.show(ActivityLogin.this, "", "Đăng nhập bằng Google", true);
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, 13);
                }
        );
        btnLogin.setOnClickListener(this);
        btnForgotPassw.setOnClickListener(this);
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                final boolean pHasFocus = etPassw.hasFocus();
                scrollView.post(() -> {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    if (pHasFocus) etPassw.requestFocus();
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 13) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    // Get account information
                    String mFullName = acct.getDisplayName();
                    String mEmail = acct.getEmail();

                    firebaseAuthWithGoogle(acct);
                }
                Log.e("result", resultCode + "");

            } else Log.e("result fail", result.getStatus() + "");

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Activity Start", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Activity Start", "signInWithCredential:success");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            progressDialog.dismiss();
                            updateUI(currentUser);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Activity Start", "signInWithCredential:failure", task.getException());
                        Toast.makeText(ActivityLogin.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        updateUI(null);
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Phương thức validate username và password. Có check kết nối internet
     * Chấp nhận username không trống, không chứa dấu cách
     * Chấp nhận password không trống, độ dài >= 6 kí tự
     * <p>
     * Khi một trong hai hoặc cả 2 thông tin bị sai, có đưa ra thông báo và focus vào edittext chứa
     * thông tin sai
     */
    private void validateLoginData() {
        showProgress(true, progressBar);
        if (!isNetworkConnected(this)) {//chưa bật mạng
            showProgress(false, progressBar);
            FragmentUtils.getInstance(this).showToast("Không có kết nối internet");
            rlContent.setVisibility(View.VISIBLE);
            return;
        }

        etUserName.setError(null);
        etPassw.setError(null);
        String email = etUserName.getText().toString();
        String password = etPassw.getText().toString();
        if (TextUtils.isEmpty(email)) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etUserName.requestFocus();
            etUserName.setError("Yêu cầu nhập trường này");
            return;
        }
        if (email.contains(" ")) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etUserName.requestFocus();
            etUserName.setError("Tên đăng nhập không được chứa dấu cách");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etPassw.requestFocus();
            etPassw.setError("Mật khẩu không hợp lệ");
            return;
        }
        doLogin(email, password);
    }

    private void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(ActivityLogin.this, "Đăng nhập thành công",
                                Toast.LENGTH_SHORT).show();
                        showProgress(false, progressBar);
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(ActivityLogin.this, "Đăng nhập thất bại" + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        showProgress(false, progressBar);
                        updateUI(null);
                    }

                    // ...
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(this, ActivityStart.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    /**
     * Phương thức hiển thị dialog yêu cầu người dùng nhập vào địa chỉ email đã đăng kí để tạo yêu
     * cầu khôi phục mật khẩu. Dialog sẽ đóng khi người dùng gửi email đi, hoặc chạm ra ngoài dialog
     * để hủy
     */
    private void showDialogForgotPassword() {
        View view = View.inflate(ActivityLogin.this, R.layout.dialog_forgot_passw, null);
        final Dialog dialog = AppUtils.createDialogFromActivity(ActivityLogin.this);
        final EditText editText = (EditText) view.findViewById(R.id.edit_forgot_password);
        Button btnSend = (Button) view.findViewById(R.id.btn_forgot_password_send);
        btnSend.setOnClickListener(v -> {
            String email = editText.getText().toString();
            if (TextUtils.isEmpty(email)) {
                FragmentUtils.getInstance(ActivityLogin.this).showToast(getString(R.string.toast_get_password_not_input_email));
                return;
            }
            if (!isEmailValid(email)) {
                FragmentUtils.getInstance(ActivityLogin.this).showToast(getString(R.string.toast_get_password_not_email_invalid));
                return;
            }
            dialog.dismiss();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Reset password", "Email sent.");
                            showDialogGuideRelogin();
                        }
                    });
        });
        dialog.setContentView(view);
        dialog.show();
    }


    /**
     * Hàm kiểm tra có kết nối internet (mạng di động hoặc wifi)
     *
     * @param context context hiện tại
     * @return trạng thái kết nối. true nếu có kết nối, ngược lại false
     */
    public static boolean isNetworkConnected(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection deprecation
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    private boolean isEmailValid(String email) {
        String exprefssion = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(exprefssion, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void showDialogGuideRelogin() {
        View view = View.inflate(ActivityLogin.this, R.layout.dialog_relogin, null);
        final Dialog dialog = AppUtils.createDialogFromActivity(ActivityLogin.this);
        dialog.setContentView(view);
        Button btnSend = (Button) view.findViewById(R.id.btn_forgot_password_send);
        btnSend.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                validateLoginData();
                break;
            case R.id.btnForgotPassw:
                showDialogForgotPassword();
                break;
        }
    }
}
