package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.adapters.FactorsAdapter;
import us.rst.farmacovigilanza.adapters.TherapiesAdapter;
import us.rst.farmacovigilanza.database.entity.FactorEntity;
import us.rst.farmacovigilanza.database.entity.PatientEntity;
import us.rst.farmacovigilanza.database.entity.TherapyEntity;
import us.rst.farmacovigilanza.databinding.ActivityAddEditPatientBinding;
import us.rst.farmacovigilanza.helpers.KeyboardHelper;
import us.rst.farmacovigilanza.helpers.SnackBarHelper;
import us.rst.farmacovigilanza.models.FiscalCode;
import us.rst.farmacovigilanza.viewmodels.PatientViewModel;

public class AddEditPatientActivity extends BaseActivity implements View.OnClickListener,
        FactorsAdapter.OnDeleteClick, TherapiesAdapter.OnDeleteClick {

    @Override protected int getLayoutId() {
        return R.layout.activity_add_edit_patient;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override protected PatientViewModel getViewModel() {
        if (viewModel == null) {
            PatientViewModel.Factory factory = new PatientViewModel.Factory(getApplication());
            viewModel = ViewModelProviders.of(this, factory).get(PatientViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activityAddEditPatientButtonSave.setOnClickListener(this);
        binding.activityAddEditPatientButtonAddRiskFactor.setOnClickListener(this);
        binding.activityAddEditPatientButtonAddTherapies.setOnClickListener(this);
        binding.activityAddEditPatientButtonSaveRiskFactor.setOnClickListener(this);
        binding.activityAddEditPatientButtonSaveTherapy.setOnClickListener(this);

        String cfFromReportActivity = getIntent().getStringExtra("cf");
        if (cfFromReportActivity != null) {
            getViewModel().setPatient(cfFromReportActivity);
        }

        currentPatient = new PatientEntity();

        getViewModel().getPatient().observe(this, patient -> {
           if (patient == null) {
               return;
           }

           currentPatient = patient;
           binding.activityAddEditPatientEditTextCf.setText(patient.getFiscalCode().toString());
           binding.activityAddEditPatientEditTextBirthday.setText("" + patient.getBirthDate());
           binding.activityAddEditPatientEditTextJob.setText(patient.getJob());
           binding.activityAddEditPatientEditTextProvince.setText(patient.getProvince());
           binding.activityAddEditPatientEditTextCf.setEnabled(false);
           binding.activityAddEditPatientEditTextBirthday.setEnabled(false);
           binding.activityAddEditPatientButtonSave.setVisibility(View.GONE);
        });

        getViewModel().getFactors().observe(this, factors -> {
            List<String> factorNames = new ArrayList<>();
            for (FactorEntity f: factors) {
                factorNames.add(f.getName());
            }

            binding.activityAddEditPatientRiskFactorNames.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, factorNames));
        });

        getViewModel().getPatientFactors().observe(this, factors -> {
            if (factors == null || factors.size() == 0) {
                binding.activityAddEditPatientNoFactorText.setVisibility(View.VISIBLE);
                binding.activityAddEditPatientRecyclerViewFactors.setVisibility(View.GONE);

                return;
            }

            binding.activityAddEditPatientNoFactorText.setVisibility(View.GONE);
            binding.activityAddEditPatientRecyclerViewFactors.setVisibility(View.VISIBLE);
            binding.activityAddEditPatientRecyclerViewFactors.setLayoutManager(new LinearLayoutManager(this));
            binding.activityAddEditPatientRecyclerViewFactors.setAdapter(new FactorsAdapter(factors, this));
        });

        getViewModel().getPatientTherapies().observe(this, therapies -> {
            if (therapies == null || therapies.size() == 0) {
                binding.activityAddEditPatientNoTherapiesText.setVisibility(View.VISIBLE);
                binding.activityAddEditPatientRecyclerViewTherapies.setVisibility(View.GONE);

                return;
            }

            binding.activityAddEditPatientNoTherapiesText.setVisibility(View.GONE);
            binding.activityAddEditPatientRecyclerViewTherapies.setVisibility(View.VISIBLE);
            binding.activityAddEditPatientRecyclerViewTherapies.setLayoutManager(new LinearLayoutManager(this));
            binding.activityAddEditPatientRecyclerViewTherapies.setAdapter(new TherapiesAdapter(therapies, this));
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_add_edit_patient_button_add_risk_factor:
                binding.activityAddEditPatientNewRiskFactorLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_add_edit_patient_button_add_therapies:
                binding.activityAddEditPatientNewTherapyLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_add_edit_patient_button_save:
                String fiscalCode = binding.activityAddEditPatientEditTextCf.getText().toString();
                String birthDate = binding.activityAddEditPatientEditTextBirthday.getText().toString();
                String job = binding.activityAddEditPatientEditTextJob.getText().toString();
                String province = binding.activityAddEditPatientEditTextProvince.getText().toString();
                if(fiscalCode.isEmpty() || birthDate.isEmpty() || job.isEmpty() || province.isEmpty()) {
                    SnackBarHelper.showShort(v, getString(R.string.error_empty_field));
                    return;
                }
                if(fiscalCode.length() != 16) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_cf_less));
                }
                currentPatient.setFiscalCode(FiscalCode.parse(binding.activityAddEditPatientEditTextCf.getText().toString()));
                currentPatient.setBirthDate(Integer.parseInt(binding.activityAddEditPatientEditTextBirthday.getText().toString()));
                currentPatient.setJob(binding.activityAddEditPatientEditTextJob.getText().toString());
                currentPatient.setProvince(binding.activityAddEditPatientEditTextProvince.getText().toString());
                getViewModel().add(currentPatient);
                onBackPressed();
                break;
            case R.id.activity_add_edit_patient_button_save_risk_factor:
                String factorName = binding.activityAddEditPatientRiskFactorNames.getSelectedItem().toString();
                int levelOfRisk = Integer.parseInt(binding.activityAddEditPatientRiskFactorLevel.getText().toString());
                getViewModel().addFactor(factorName, levelOfRisk);

                binding.activityAddEditPatientRiskFactorLevel.setText("");
                binding.activityAddEditPatientNewRiskFactorLayout.setVisibility(View.GONE);
                KeyboardHelper.hideKeyboard(this);
                break;
            case R.id.activity_add_edit_patient_button_save_therapy:
                String medicine = binding.activityAddEditPatientTherapyMedicine.getText().toString();
                int unit = Integer.parseInt(binding.activityAddEditPatientTherapyUnit.getText().toString());
                int frequency = Integer.parseInt(binding.activityAddEditPatientTherapyFrequency.getText().toString());
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);

                TherapyEntity entity = new TherapyEntity();
                entity.setMedicine(medicine);
                entity.setFrequency(frequency);
                entity.setUnit(unit);
                try {
                    entity.setStartDate(format.parse(binding.activityAddEditPatientTherapyStartDate.getText().toString()));
                    entity.setEndDate(format.parse(binding.activityAddEditPatientTherapyEndDate.getText().toString()));
                } catch (ParseException e) {
                    // TODO: mostrare errore
                }

                getViewModel().addTherapy(entity);
                binding.activityAddEditPatientTherapyFrequency.setText("");
                binding.activityAddEditPatientTherapyUnit.setText("");
                binding.activityAddEditPatientTherapyMedicine.setText("");
                binding.activityAddEditPatientTherapyStartDate.setText("");
                binding.activityAddEditPatientTherapyEndDate.setText("");
                KeyboardHelper.hideKeyboard(this);
                binding.activityAddEditPatientNewTherapyLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override public void onFactorDelete(String name) {
        getViewModel().deleteFactor(name);
    }

    @Override public void onTherapyDelete(String name) {
        getViewModel().deleteTherapy(name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ActivityAddEditPatientBinding binding;
    private PatientViewModel viewModel;
    private PatientEntity currentPatient;
}

