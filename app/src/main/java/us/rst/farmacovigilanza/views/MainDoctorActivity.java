package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.databinding.ActivityMainDoctorBinding;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;
import us.rst.farmacovigilanza.viewmodels.MainDoctorViewModel;

public class MainDoctorActivity extends BaseActivity implements View.OnClickListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_main_doctor;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        if (viewModel == null) {
            MainDoctorViewModel.Factory factory = new MainDoctorViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(MainDoctorViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activityMainDoctorManagePatientButton.setOnClickListener(this);
        binding.activityMainDoctorManageFactorsButton.setOnClickListener(this);
        binding.activityMainDoctorManageReportsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_main_doctor_manage_patient_button) {
            Intent i = new Intent(MainDoctorActivity.this, AddEditPatientActivity.class);
            startActivity(i);
        }
        else if(v.getId() == R.id.activity_main_doctor_manage_factors_button){
            Intent i = new Intent(MainDoctorActivity.this, AddEditFactorsActivity.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(MainDoctorActivity.this, AddEditReportActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_doctor_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case (R.id.activity_main_doctor_menu_logout):
                // return to login page
                return true;
            case(R.id.activity_main_doctor_menu_reports_cronology):
                Intent i = new Intent(MainDoctorActivity.this, ReportsChronologyActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActivityMainDoctorBinding binding;
    private MainDoctorViewModel viewModel;
}
