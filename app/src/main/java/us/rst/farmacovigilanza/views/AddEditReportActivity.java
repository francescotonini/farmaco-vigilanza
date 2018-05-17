package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.databinding.ActivityAddEditReportsBinding;
import us.rst.farmacovigilanza.viewmodels.AddEditReportViewModel;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;

// dovrebbe essere solo "ADD", non può modificare una segnalazione una volta effetuata e inviata al sistema di gestione delle segnalazini

public class AddEditReportActivity extends BaseActivity{

    @Override protected int getLayoutId() {
        return R.layout.activity_add_edit_reports;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        if (viewModel == null) {
            AddEditReportViewModel.Factory factory = new AddEditReportViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(AddEditReportViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    private ActivityAddEditReportsBinding binding;
    private AddEditReportViewModel viewModel;
}
