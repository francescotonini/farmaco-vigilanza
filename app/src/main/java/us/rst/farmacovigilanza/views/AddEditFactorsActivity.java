package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.Toolbar;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.databinding.ActivityAddEditFactorsBinding;
import us.rst.farmacovigilanza.viewmodels.AddEditFactorsViewModel;
import us.rst.farmacovigilanza.viewmodels.BaseViewModel;

public class AddEditFactorsActivity extends BaseActivity{

    @Override protected int getLayoutId() {
        return R.layout.activity_add_edit_factors;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        if (viewModel == null) {
            AddEditFactorsViewModel.Factory factory = new AddEditFactorsViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(AddEditFactorsViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    private ActivityAddEditFactorsBinding binding;
    private AddEditFactorsViewModel viewModel;
}
