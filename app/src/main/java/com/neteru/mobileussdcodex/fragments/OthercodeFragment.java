package com.neteru.mobileussdcodex.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.CallOperator;
import com.neteru.mobileussdcodex.classes.adapters.UssdAdapter;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.List;

public class OthercodeFragment extends Fragment {
    private UssdAdapter adapter;
    private DatabaseManager manager;
    private RecyclerView recyclerView;
    private Ussd callRequestUssd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_othercode, container, false);

        recyclerView = root.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        manager = new DatabaseManager(getContext());

        load();

        return root;
    }

    private void load(){
        String fragmentID = "utilities";
        List<Ussd> ussdList = manager.db_readByFragment(fragmentID);

        if (ussdList != null){

            adapter = new UssdAdapter(ussdList, R.layout.ussd_model, getContext(), new UssdAdapter.UssdAdapterListener() {
                @Override
                public void refresh() {load();}

                @Override
                public void refreshAndScrollToBottom() {load();scrollToBottom();}

                @Override
                public void askPermission(Ussd ussd) {
                    callRequestUssd = ussd;
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},0);
                }

            }, 2);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                CallOperator.getInstance(getContext(), callRequestUssd).showDialog();

            } else {

                Toast.makeText(getContext(), R.string.auth_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.scrollToPosition(adapter.getItemCount() - 1));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0){load();}
    }

}
