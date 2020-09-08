package com.neteru.mobileussdcodex.classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.models.Ussd;

@SuppressWarnings("unused")
public class CallOperator {
    private Context context;
    private String description, code, parameters, fragment;
    private EditText[] editTexts;
    private String[] part;

    private CallOperator(Context ctx, Ussd ussd){

        context = ctx;
        description = ussd.getDescription();
        code = ussd.getCode();
        parameters = ussd.getParameters();
        fragment = ussd.getFragment();

    }

    public static CallOperator getInstance(Context ctx, Ussd ussd){
        return new CallOperator(ctx, ussd);
    }

    public void showDialog(){
        if (parameters == null) {

            showConfirmationDialog(code, description, "\n>>>\t\t"+code);

        }else{

            AlertDialog.Builder ussdParameters = new AlertDialog.Builder(context);
            ussdParameters.setTitle(context.getString(R.string.ussd_settings_title, description));
            ussdParameters.setIcon(R.mipmap.mucx_launcher);

            LayoutInflater factory = LayoutInflater.from(context);

            @SuppressLint("InflateParams")
            final View ussdParametersView = factory.inflate(R.layout.parameters_layout, null);

            final EditText editText_1 = ussdParametersView.findViewById(R.id.editor_1),
                           editText_2 = ussdParametersView.findViewById(R.id.editor_2),
                           editText_3 = ussdParametersView.findViewById(R.id.editor_3),
                           editText_4 = ussdParametersView.findViewById(R.id.editor_4),
                           editText_5 = ussdParametersView.findViewById(R.id.editor_5);

            editTexts = new EditText[]{editText_1, editText_2, editText_3, editText_4, editText_5};
            for (EditText editText: editTexts) { editText.setInputType(InputType.TYPE_CLASS_NUMBER); }

            part = parameters.split("\\*");

            configEdit(part.length);

            ussdParameters
                    .setView(ussdParametersView)
                    .setPositiveButton(R.string.launch, (dialogInterface, i) -> buildRequest(part.length))
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(false)
                    .show();
        }
    }

    private void configEdit(int nb){
        for (int i = 0; i < editTexts.length; i++) {
            if (i < nb){
                editTexts[i].setHint(part[i]);
            }else {
                editTexts[i].setVisibility(View.GONE);
            }
        }
    }

    private void buildRequest(int nb){
        boolean proof = true; String req = null;
        for (int i = 0; i < nb; i++){
            if (editTexts[i].getText().toString().isEmpty())
                proof = false;
        }

        if (proof){
            for (int y = 0; y < nb; y++){
                if (y == 0){
                    req = code.replace(part[y], editTexts[y].getText().toString());
                }else {
                    req = req.replace(part[y], editTexts[y].getText().toString());
                }
            }
            showConfirmationDialog(req,context.getString(R.string.confirmation_title, description),"\n>>>\t\t"+req);
        }else {
            Toast.makeText(context, R.string.missing_parameters, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog(final String request, String title, String mes){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.mucx_launcher)
                .setMessage(fragment.equals("utilities") ? mes+"\n\n"+context.getString(R.string.other_code_warning) : mes)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.execute, (dialogInterface, i) -> {
                    Intent intent;

                    if (!fragment.equals("utilities")) {

                        String ussd = request.replaceAll("#+", Uri.encode("#"));
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd));

                    }else {

                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("ussd code",request);

                        if (clipboardManager == null) {

                            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(context, R.string.copied_code, Toast.LENGTH_SHORT).show();

                        intent = new Intent(Intent.ACTION_DIAL);

                    }

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, R.string.auth_denied, Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
