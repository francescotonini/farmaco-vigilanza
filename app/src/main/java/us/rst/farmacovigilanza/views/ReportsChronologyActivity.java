package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.Toolbar;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.databinding.ActivityReportsChronologyBinding;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;
import us.rst.farmacovigilanza.viewmodels.ReportsChronologyViewModel;

public class ReportsChronologyActivity extends BaseActivity{

    @Override protected int getLayoutId() {
        return R.layout.activity_reports_chronology;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        if (viewModel == null) {
            ReportsChronologyViewModel.Factory factory = new ReportsChronologyViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(ReportsChronologyViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    private ActivityReportsChronologyBinding binding;
    private ReportsChronologyViewModel viewModel;
}
