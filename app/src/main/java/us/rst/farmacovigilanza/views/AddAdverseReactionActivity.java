package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.database.entity.AvverseReactionEntity;
import us.rst.farmacovigilanza.databinding.ActivityAddAdverseReactionBinding;
import us.rst.farmacovigilanza.viewmodels.AddAdverseReactionViewModel;

public class AddAdverseReactionActivity extends BaseActivity implements View.OnClickListener{

    @Override protected int getLayoutId() {
        return R.layout.activity_add_adverse_reaction;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected AddAdverseReactionViewModel getViewModel() {
        if (viewModel == null) {
            AddAdverseReactionViewModel.Factory factory = new AddAdverseReactionViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(AddAdverseReactionViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activityAddAdverseReactionSaveButton.setOnClickListener(this);
        avverseReactionEntity = new AvverseReactionEntity();
    }

    @Override
    public void onClick(View v) {
        avverseReactionEntity.setName(binding.activityAddAdverseReactionNameText.getText().toString());
        avverseReactionEntity.setDescription((binding.activityAddAdverseReactionDescriptionText.getText().toString()));
        getViewModel().add(avverseReactionEntity);
    }

    private ActivityAddAdverseReactionBinding binding;
    private AddAdverseReactionViewModel viewModel;
    private AvverseReactionEntity avverseReactionEntity;
}