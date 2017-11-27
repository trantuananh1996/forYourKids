package koiapp.pr.com.koiapp.moduleAdmin.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import koiapp.pr.com.koiapp.moduleAdmin.adapter.AdapterChangeRole;
import koiapp.pr.com.koiapp.moduleAdmin.model.PendingChangeRole;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.view.PrFragment;


public class FragmentRequestChangeRole extends PrFragment {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_request_change_role, container, false);
        findView();

        getData();

        rootView.findViewById(R.id.btn_approve_all).setOnClickListener(view -> {
            if (AppUtils.alertGetYesNo("", "Bạn có muốn duyệt tất cả yêu cầu", getActivity())) {
                for (PendingChangeRole item : pendingList) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(item.getUser_request());
                    dbRef.child("role").setValue(item.getNew_role());
                    FirebaseDatabase.getInstance().getReference("admin/pending/role_switch").child(item.getUser_request()).child("approved").setValue(true);
                    item.setApproved(true);
                    FirebaseDatabase.getInstance().getReference("admin/pending/role_switch").child(item.getUser_request()).child("approved_at").setValue(System.currentTimeMillis() / 1000);
                    item.setApproved(true);
                }
                if (mAdapter != null) mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    AdapterChangeRole mAdapter;
    List<PendingChangeRole> pendingList = new ArrayList<>();

    DatabaseReference dbRef;

    private void getData() {
        progressDialog = ProgressDialog.show(getActivity(), "", "Đang lấy dữ liệu", true);
        dbRef = FirebaseDatabase.getInstance().getReference("admin/pending/role_switch");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (pendingList == null) pendingList = new ArrayList<>();
                Debug.prLog("Data manager", dataSnapshot.getValue().toString());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Debug.prLog("Data each", d.getValue().toString());
                    PendingChangeRole p = d.getValue(PendingChangeRole.class);
                    pendingList.add(p);
                }
                Debug.prLog("Data manager", new Gson().toJson(pendingList));


                mAdapter = new AdapterChangeRole(getActivity(), pendingList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                progressDialog.dismiss();

//                dbRef.addValueEventListener(dbRefListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void findView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_change_role);
    }
}
