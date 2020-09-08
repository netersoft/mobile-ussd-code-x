package com.neteru.mobileussdcodex.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.CallOperator;
import com.neteru.mobileussdcodex.classes.adapters.UssdAdapter;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.mobileussdcodex.classes.Resources.EMPTY;

public class SearchActivity extends AppCompatActivity {
    private DatabaseManager manager;
    private RecyclerView recyclerView;
    private List<Ussd> ussdList = new ArrayList<>();
    private UssdAdapter adapter;
    private String q;
    private Ussd callRequestUssd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        manager = new DatabaseManager(SearchActivity.this);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(EMPTY);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
            upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_ussd, menu);

        MenuItem mSearch = menu.findItem(R.id.search);

        SearchView search = (SearchView) mSearch.getActionView();
        search.setQueryHint(getResources().getString(R.string.search_str));
        search.setIconified(false);
        search.setIconifiedByDefault(true);
        search.setBackgroundColor(getResources().getColor(R.color.white));
        search.setQueryHint(getString(R.string.search_str));

        // Customisation de la couleur du hint
        SearchView.SearchAutoComplete searchAutoComplete = search.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.dimgray));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.black));

        // Customisation du bouton de fermeture
        final ImageView searchCloseButton = search.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_green_24dp));

        // Customisation de l'icône de recherche
        ImageView searchButton = search.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_green_24dp));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                q = query;
                load();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                q = newText;
                load();
                return false;
            }
        });

        return true;
    }

    public void load(){
        if (!q.isEmpty()) {
            ussdList = manager.db_searchByDescriptionAndCode(q);
        }else {
            ussdList.clear();
        }

        if (ussdList != null){
            adapter = new UssdAdapter(ussdList, R.layout.ussd_model, SearchActivity.this, new UssdAdapter.UssdAdapterListener() {
                @Override
                public void refresh() {load();}

                @Override
                public void refreshAndScrollToBottom() {load();scrollToBottom();}

                @Override
                public void askPermission(Ussd ussd) {
                    callRequestUssd = ussd;
                    ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                }
            }, 1);

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

                CallOperator.getInstance(SearchActivity.this, callRequestUssd).showDialog();

            } else {

                Toast.makeText(SearchActivity.this, R.string.auth_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.scrollToPosition(adapter.getItemCount() - 1));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        return true;
    }
}
