package koiapp.pr.com.koiapp.moduleManager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleManager.AdapterRegisterLearn;
import koiapp.pr.com.koiapp.moduleManager.model.PendingRegisterLearn;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

/**
 * Created by nguyentt
 * on 6/13/2017.
 */

public class FragmentDashboardManager extends PrFragment {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String schoolId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_manager, container, false);
        findView();
        User user = PrRealm.getInstance(getActivity().getApplicationContext()).getCurrentUser();
        if (user != null) {
            schoolId = user.getManager_at();
        }
        getData();
        rootView.findViewById(R.id.btn_approve_all).setOnClickListener(view -> {
            if (AppUtils.alertGetYesNo("", "Bạn có muốn duyệt tất cả yêu cầu", getActivity())) {
                for (PendingRegisterLearn item : pendingList) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("schoolManager/pending/register_learn").child(schoolId);
                    dbRef.child(item.getuId_request()).child("approved_at").setValue(System.currentTimeMillis() / 1000);
                    dbRef.child(item.getuId_request()).child("approved").setValue(true);
                    item.setApproved(true);
                }
                if (mAdapter != null) mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }


    AdapterRegisterLearn mAdapter;
    List<PendingRegisterLearn> pendingList = new ArrayList<>();

    DatabaseReference dbRef;

    private void getData() {
        User user = PrRealm.getInstance(getActivity().getApplicationContext()).getCurrentUser();
        if (user == null) {
            FragmentUtils.getInstance(getActivity()).showToast("Bạn chưa đăng nhập");
            return;
        }
        if (TextUtils.isEmpty(user.getManager_at())) {
            FragmentUtils.getInstance(getActivity()).showToast("Bạn chưa đăng kí quản trị trường nào");
            return;
        }
        progressDialog = ProgressDialog.show(getActivity(), "", "Đang lấy dữ liệu", true);
        dbRef = FirebaseDatabase.getInstance().getReference("schoolManager/pending/register_learn").child(user.getManager_at());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    FragmentUtils.getInstance(getActivity()).showToast("Hiện chưa có đơn đăng kí nào được gửi đến");
                    return;
                }
                if (pendingList == null) pendingList = new ArrayList<>();
                Debug.prLog("Data manager", dataSnapshot.getValue().toString());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Debug.prLog("Data each", d.getValue().toString());
                    PendingRegisterLearn p = d.getValue(PendingRegisterLearn.class);
                    pendingList.add(p);
                }
                Debug.prLog("Data manager", new Gson().toJson(pendingList));


                mAdapter = new AdapterRegisterLearn(getActivity(), pendingList);
                mAdapter.setSchoolId(schoolId);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                progressDialog.dismiss();

//                dbRef.addValueEventListener(dbRefListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Debug.prLog("Data error", databaseError.getMessage());

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void findView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_register_manager);
    }
}
