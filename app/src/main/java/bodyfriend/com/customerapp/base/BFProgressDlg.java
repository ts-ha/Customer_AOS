package bodyfriend.com.customerapp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import bodyfriend.com.customerapp.R;


public class BFProgressDlg extends ProgressDialog {

    public BFProgressDlg(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
    }
}
