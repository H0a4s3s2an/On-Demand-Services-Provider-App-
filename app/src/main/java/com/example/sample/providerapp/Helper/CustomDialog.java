package com.example.sample.providerapp.Helper;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;

import com.example.sample.providerapp.R;

/**
 * Created by Hassan Javaid on 7/7/2018.
 */

public class CustomDialog extends Dialog {
    public CustomDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customdialog);
    }
}
