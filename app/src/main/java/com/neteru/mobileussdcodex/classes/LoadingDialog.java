package com.neteru.mobileussdcodex.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neteru.mobileussdcodex.R;

@SuppressWarnings("unused")
public class LoadingDialog {
    private AlertDialog LoadingBox;
    private TextView msgText;

    public LoadingDialog(Context context){

        LoadingBox = new AlertDialog.Builder(context).create();

        LayoutInflater factory = LayoutInflater.from(context);

        @SuppressLint("InflateParams")
        View LoadingView = factory.inflate(R.layout.loading_layout, null);

        ProgressBar progressBar = LoadingView.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

        msgText = LoadingView.findViewById(R.id.msg);
        String msg = context.getString(R.string.loading_in_progress);
        msgText.setText(msg);

        LoadingBox.setView(LoadingView);
        LoadingBox.setCancelable(false);
    }

    public void setCancelable(){
        LoadingBox.setCancelable(true);
    }

    public boolean isShowing(){
        return LoadingBox.isShowing();
    }

    public void show(){
        this.LoadingBox.show();
    }

    public void dismiss(){
        if (this.LoadingBox.isShowing()) this.LoadingBox.dismiss();
    }

    public void setMsg(String m){
        this.msgText.setText(m);
    }

}
