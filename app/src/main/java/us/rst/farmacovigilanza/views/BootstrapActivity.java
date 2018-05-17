package us.rst.farmacovigilanza.views;

import android.content.Intent;
import android.os.Bundle;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;

public class BootstrapActivity extends BaseActivity {

    @Override protected int getLayoutId() {
        return 0;
    }

    @Override protected void setToolbar() { }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void setBinding() { }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, MainDoctorActivity.class));
    }
}
