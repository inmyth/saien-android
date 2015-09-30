package jp.navi.saien.utils;

import jp.navi.saien.R;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {
	
    public static ProgressDialog showIndeterminate(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);

        dialog.setCancelable(false);
        dialog.show();
        dialog.setContentView(R.layout.dialog_progressbar_indeterminate);
        return dialog;
}
}
