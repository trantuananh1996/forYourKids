package koiapp.pr.com.koiapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;

import static koiapp.pr.com.koiapp.utils.FragmentUtils.showProgress;


public class ActivityRegister extends Activity implements View.OnClickListener {
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

    EditText edtConfirmPassw;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ActivityRegister.this, R.color.colorPrimary));
        }
        findView();
        btnLogin.setText("Đăng kí tài khoản");
        btnForgotPassw.setVisibility(View.GONE);
        cbSavePass.setVisibility(View.GONE);
        findViewById(R.id.ll_confirm_passw).setVisibility(View.VISIBLE);

        addListener();
        currentUsername = etUserName.getText().toString();
        currentPassword = etPassw.getText().toString();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            showDialogUpdateUserInfo(currentUser);

        } else {

        }
    }

    ImageEntry avatarSelected;

    class MyPickListener implements Picker.PickListener {
        ImageView iv;

        public MyPickListener(ImageView ivAvatar) {
            this.iv = ivAvatar;
        }


        @Override
        public void onPickedSuccessfully(final ArrayList<ImageEntry> images) {
            avatarSelected = null;
            if (images.size() > 0) {
                avatarSelected = images.get(0);
                if (iv!=null)
                Glide.with(ActivityRegister.this)
                        .load(avatarSelected.path)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(iv);
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(ActivityRegister.this, "Chưa chọn ảnh nào",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageToFirebase(ImageEntry img, final FirebaseUser user, final String name) {
        if (avatarSelected == null) return;
        StorageReference roomRef = FirebaseStorage.getInstance().getReference("usersAvatar").child(user.getUid());
        final long timeStamp = System.currentTimeMillis() / 1000;
        String fileName = user.getUid() + "_" + timeStamp;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        img.setMaxDimen(500);
        img.getBitmapRotated().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] aData = baos.toByteArray();
        UploadTask uploadTask = roomRef.child(fileName).putBytes(aData);
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(ActivityRegister.this, "Upload ảnh thất bại",
                    Toast.LENGTH_SHORT).show();
            updateUserInfo(user, name, null);
        }).addOnSuccessListener(taskSnapshot -> {
            String downloadUrl = taskSnapshot.getDownloadUrl() == null ? "" : taskSnapshot.getDownloadUrl().toString();
            if (TextUtils.isEmpty(downloadUrl)) return;
            updateUserInfo(user, name, downloadUrl);
        });
    }

    private void updateUserInfo(final FirebaseUser currentUser, String name, String photo) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo == null ? null : Uri.parse(photo))
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityRegister.this, "Cập nhật thông tin thành công",
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ActivityRegister.this, ActivityLogin.class);
                        i.putExtra("username_created", currentUser.getEmail());
                        FirebaseAuth.getInstance().signOut();
                        startActivity(i);
                        finish();
                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(ActivityRegister.this, "Xảy ra lỗi khi cập nhật",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDialogUpdateUserInfo(final FirebaseUser currentUser) {
        View view = View.inflate(ActivityRegister.this, R.layout.dialog_update_user_info, null);
        final Dialog dialog = AppUtils.createDialogFromActivity(ActivityRegister.this);
        final ImageView ivAvatar;
        final EditText tvUserName;
        TextView tvUserBirthday;
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvUserName = (EditText) view.findViewById(R.id.et_user_name);
        tvUserBirthday = (TextView) view.findViewById(R.id.tv_user_birthday);
        tvUserBirthday.setVisibility(View.GONE);


        view.findViewById(R.id.ll_account_management).setOnClickListener(view12 -> {
            UIUtil.hideKeyboard(ActivityRegister.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (AppUtils.getInstance(getApplicationContext()).verifyStoragePermissions(ActivityRegister.this)) {
                    Picker picker = new Picker.Builder(ActivityRegister.this, new MyPickListener(ivAvatar), R.style.MIP_theme).setLimit(1).build();
                    picker.startActivity();
                }
            } else {
                Picker picker = new Picker.Builder(ActivityRegister.this, new MyPickListener(ivAvatar), R.style.MIP_theme).setLimit(1).build();
                picker.startActivity();
            }
        });
        view.findViewById(R.id.btn_update).setOnClickListener(view1 -> {
            String name = tvUserName.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(ActivityRegister.this, "Tên không được để trống",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (avatarSelected == null) updateUserInfo(currentUser, name, null);
            else uploadImageToFirebase(avatarSelected, currentUser, name);

        });
        dialog.setContentView(view);
        dialog.show();
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
        edtConfirmPassw = (EditText) findViewById(R.id.edtConfirmPassw);
    }

    private void addListener() {
        btnLogin.setOnClickListener(this);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void validateRegisterData() {
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
        String confirmPassw = edtConfirmPassw.getText().toString();
        if (TextUtils.isEmpty(email)) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etUserName.requestFocus();
            etUserName.setError("Yêu cầu nhập trường này");
            return;
        }
        if (!isEmailValid(email)) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etUserName.requestFocus();
            etUserName.setError("Địa chỉ email không đúng định dạng");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            etPassw.requestFocus();
            etPassw.setError("Mật khẩu không hợp lệ");
            return;
        }
        if (!password.equals(confirmPassw)) {
            rlContent.setVisibility(View.VISIBLE);
            showProgress(false, progressBar);
            edtConfirmPassw.requestFocus();
            edtConfirmPassw.setError("Xác nhận mật khẩu không khớp");
            return;
        }
        createAccount(email, password);
        //TODO: Login
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgress(false, progressBar);
                    Log.d("Register", "createUserWithEmail:success");
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Register", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(ActivityRegister.this, "Đăng kí tài khoản thành công",
                                Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(ActivityRegister.this, "Email này đã được đăng kí",
                                    Toast.LENGTH_SHORT).show();
                        }
                        Log.w("Register", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(ActivityRegister.this, "Xảy ra lỗi khi đăng kí",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                validateRegisterData();
                break;
        }
    }
}
