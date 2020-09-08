package com.neteru.mobileussdcodex.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.CallOperator;
import com.neteru.mobileussdcodex.classes.adapters.UssdAdapter;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private UssdAdapter adapter;
    private DatabaseManager manager;
    private RecyclerView recyclerView;
    private Ussd callRequestUssd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritesActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        manager = new DatabaseManager(FavoritesActivity.this);

        load();

        if (getSupportActionBar() != null) {
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
            upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.favorites_str));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);


        }
    }

    public void load(){
        List<Ussd> ussdList = manager.db_readByIsFavorite(true);
        if (ussdList != null){

            adapter = new UssdAdapter(ussdList, R.layout.ussd_model, FavoritesActivity.this, new UssdAdapter.UssdAdapterListener() {
                @Override
                public void refresh() {load();}

                @Override
                public void refreshAndScrollToBottom() {load();scrollToBottom();}

                @Override
                public void askPermission(Ussd ussd) {
                    callRequestUssd = ussd;
                    ActivityCompat.requestPermissions(FavoritesActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                }
            }, 1);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }else {
            Toast.makeText(FavoritesActivity.this, R.string.no_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                CallOperator.getInstance(FavoritesActivity.this, callRequestUssd).showDialog();

            } else {

                Toast.makeText(FavoritesActivity.this, R.string.auth_denied, Toast.LENGTH_SHORT).show();
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
