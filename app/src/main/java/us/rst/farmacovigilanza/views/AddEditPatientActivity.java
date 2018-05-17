package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.databinding.ActivityAddEditPatientBinding;
import us.rst.farmacovigilanza.viewmodels.AddEditPatientViewModel;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;


public class AddEditPatientActivity extends BaseActivity{

    @Override protected int getLayoutId() {
        return R.layout.activity_add_edit_patient;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        if (viewModel == null) {
            AddEditPatientViewModel.Factory factory = new AddEditPatientViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(AddEditPatientViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private ActivityAddEditPatientBinding binding;
    private AddEditPatientViewModel viewModel;
}
