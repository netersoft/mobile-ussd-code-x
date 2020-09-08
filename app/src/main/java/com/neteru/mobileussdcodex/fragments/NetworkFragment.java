package com.neteru.mobileussdcodex.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.activities.FavoritesActivity;
import com.neteru.mobileussdcodex.activities.SearchActivity;
import com.neteru.mobileussdcodex.classes.CallOperator;
import com.neteru.mobileussdcodex.classes.adapters.UssdAdapter;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.List;

public class NetworkFragment extends Fragment {
    private UssdAdapter adapter;
    private DatabaseManager manager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String fragmentID, fragmentName;
    private Ussd callRequestUssd;
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_network, container, false);

        if (getActivity() != null){ activity = getActivity(); }

        recyclerView = root.findViewById(R.id.recycler);
        swipeRefreshLayout = root.findViewById(R.id.swipeIt);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        swipeRefreshLayout.setOnRefreshListener(this::load);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView.setLayoutManager(layoutManager);

        manager = new DatabaseManager(getContext());

        load();

        return root;
    }

    void setFragmentID(String s){
        fragmentID = s;
    }

    void setFragmentName(String s){
        fragmentName = s;
    }

    private void load(){
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
            }, 0);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }

        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_fav:
                Intent i_0 = new Intent(getContext(), FavoritesActivity.class);
                startActivityForResult(i_0, 4320);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.action_add:
                AlertDialog.Builder addUssd = new AlertDialog.Builder(activity);
                addUssd.setTitle(R.string.add_ussd);
                addUssd.setIcon(R.mipmap.mucx_launcher);

                LayoutInflater factory = LayoutInflater.from(getContext());

                @SuppressLint("InflateParams")
                final View addUssdView = factory.inflate(R.layout.add_ussd_layout, null);

                TextView currentInfo = addUssdView.findViewById(R.id.currentInfo);
                final EditText editText_1 = addUssdView.findViewById(R.id.editor_1);
                final EditText editText_2 = addUssdView.findViewById(R.id.editor_2);

                currentInfo.setText(fragmentName);

                editText_1.setInputType(InputType.TYPE_CLASS_TEXT);
                editText_1.setHint(R.string.title);

                editText_2.setInputType(InputType.TYPE_CLASS_PHONE);
                editText_2.setHint(R.string.ussd_code);

                addUssd.setView(addUssdView)
                        .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                            if (!editText_1.getText().toString().isEmpty() && !editText_2.getText().toString().isEmpty()){

                                long result = manager.db_insertValue(new Ussd(editText_1.getText().toString(),
                                        editText_2.getText().toString(),
                                        fragmentID,
                                        null,
                                        false,
                                        false));
                                if (result != -1){
                                    Toast.makeText(getContext(), R.string.code_added, Toast.LENGTH_SHORT).show();
                                    load();
                                    scrollToBottom();
                                }else {
                                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(getContext(), R.string.incomplete_informations, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .setCancelable(false)
                        .show();

                break;

            case R.id.action_search:
                Intent i_ = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(i_, 4320);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

        return super.onOptionsItemSelected(item);
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
