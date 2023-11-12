package com.apps.um.alarmplus.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apps.um.alarmplus.R;

public class DialogEditLabel extends DialogFragment {
    
    private ClickListener clickListener;
    private EditText editText;
    private AlertDialog alertDialog;

    private TextView textView;


    public interface ClickListener {
        void onClickPositiveButton(String label);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View editTextLayout = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("アラームのラベルの変更")
                .setView(editTextLayout)
                .setPositiveButton("保存", (dialog, which) -> clickListener.onClickPositiveButton(editText.getText().toString()))
                .setNegativeButton("キャンセル", null)
                .create();

        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        editText = requireDialog().findViewById(R.id.editTextDialog);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 15) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAlpha((float) 0.5);
                    Toast.makeText(getContext(), R.string.alarm_edit_text_error, Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAlpha((float) 1.0);
                }
            }
        });
    }

    public void setClickListener(ClickListener listener) {
        this.clickListener = listener;
    }
}
