package com.neteru.mobileussdcodex.classes.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.InitValues;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.List;

@SuppressWarnings("unused")
public class UssdAdapter extends RecyclerView.Adapter<UssdAdapter.MyViewHolder> {

    private Context context;
    private int rowLayout, index;
    private List<Ussd> ussdList;
    private UssdAdapterListener listener;
    private DatabaseManager manager;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout call, delete, save;
        private TextView description;
        private TextView code;
        private TextView call_txt;
        private TextView delete_txt;
        private TextView save_txt;
        private TextView source;
        private LinearLayout previous;

        MyViewHolder(View view) {
            super(view);

            code = view.findViewById(R.id.ussd_code);
            description = view.findViewById(R.id.ussd_description);
            call = view.findViewById(R.id.ussd_call);
            delete = view.findViewById(R.id.ussd_delete);
            save = view.findViewById(R.id.ussd_save);
            call_txt = view.findViewById(R.id.ussd_call_txt);
            delete_txt = view.findViewById(R.id.ussd_delete_txt);
            save_txt = view.findViewById(R.id.ussd_save_txt);
            previous = view.findViewById(R.id.previous);
            LinearLayout next = view.findViewById(R.id.next);
            source = view.findViewById(R.id.previousTxt);
        }

    }

    public UssdAdapter(List<Ussd> ussd, int rowLayout, Context context, UssdAdapterListener listener, int i) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.ussdList = ussd;
        this.listener = listener;
        this.index = i;

        manager = new DatabaseManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Ussd ussd = ussdList.get(position);

        holder.description.setText(ussd.getDescription());
        holder.code.setText(ussd.getCode());

        if (index == 0){

            holder.previous.setVisibility(View.GONE);

        }else if(index == 1){

            holder.previous.setVisibility(View.VISIBLE);

            InitValues initValues = InitValues.getInstance();

            String[] fragments = initValues.getFragments(),
                     fragmentsTxt = initValues.getFragmentsTxt();

            for(int i = 0; i < fragments.length; i++){
                if (fragments[i].equals(ussd.getFragment())){
                    holder.source.setText(fragmentsTxt[i]);
                }
            }

        }else if(index == 2){

            holder.previous.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.save.setVisibility(View.GONE);

        }

        if (ussd.getIsNative()) {

            holder.delete.setVisibility(View.GONE);

        } else {
            holder.delete.setVisibility(View.VISIBLE);

            setTextViewDrawable(holder.delete_txt, R.drawable.ic_delete_28px, R.color.colorPrimary);
        }

        setTextViewDrawable(holder.call_txt, R.drawable.ic_call_28px, R.color.colorPrimary);

        setTextViewDrawable(holder.code, R.drawable.ic_label_24px, R.color.colorAccent);

        if (ussd.getIsFavorite()) {

            setTextViewDrawable(holder.save_txt, R.drawable.ic_bookmark_28px, R.color.colorPrimary);

        } else {

            setTextViewDrawable(holder.save_txt, R.drawable.ic_bookmark_border_28px, R.color.colorAccent);

        }

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ussd.getIsFavorite()) {

                    long result = manager.db_updateValue(new Ussd(ussd.getDescription(), ussd.getCode(), ussd.getFragment(), ussd.getParameters(), ussd.getIsNative(), false));

                    if (result != -1) {

                        setTextViewDrawable(holder.save_txt, R.drawable.ic_bookmark_border_28px, R.color.colorAccent);

                        Toast.makeText(context, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                    }

                } else {

                    long result = manager.db_updateValue(new Ussd(ussd.getDescription(), ussd.getCode(), ussd.getFragment(), ussd.getParameters(), ussd.getIsNative(), true));

                    if (result != -1) {

                        setTextViewDrawable(holder.save_txt, R.drawable.ic_bookmark_28px, R.color.colorPrimary);

                        Toast.makeText(context, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                    }

                }

                listener.refresh();
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.askPermission(ussd);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle(R.string.suppression)
                        .setIcon(R.mipmap.mucx_launcher)
                        .setMessage(R.string.delete_this_code)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                long result = manager.db_removeValue(new Ussd(ussd.getDescription(),
                                        ussd.getCode(),
                                        ussd.getFragment(),
                                        ussd.getParameters(),
                                        ussd.getIsNative(),
                                        ussd.getIsFavorite()));

                                if (result != -1){
                                    Toast.makeText(context, R.string.code_deleted, Toast.LENGTH_SHORT).show();
                                    listener.refreshAndScrollToBottom();
                                }else{
                                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .show();
            }
        });
    }

    private void setTextViewDrawable(TextView holder, int res, int color){

        Drawable drawable = ContextCompat.getDrawable(context, res);

        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color));
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            holder.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

    }

    @Override
    public int getItemCount() {
        return ussdList.size();
    }

    public interface UssdAdapterListener{
        void refresh();
        void refreshAndScrollToBottom();
        void askPermission(Ussd ussd);
    }

}

