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
import java.util.Date;
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
                binding.activityAddEditPatientButtonAddRiskFactor.setVisibility(View.GONE);
                binding.activityAddEditPatientNewRiskFactorLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_add_edit_patient_button_add_therapies:
                binding.activityAddEditPatientButtonAddTherapies.setVisibility(View.GONE);
                binding.activityAddEditPatientNewTherapyLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_add_edit_patient_button_save:
                String fiscalCode = binding.activityAddEditPatientEditTextCf.getText().toString();
                String job = binding.activityAddEditPatientEditTextJob.getText().toString();
                String province = binding.activityAddEditPatientEditTextProvince.getText().toString();
                String birthdDateString = binding.activityAddEditPatientEditTextBirthday.getText().toString();
                if(fiscalCode.isEmpty() || birthdDateString.isEmpty() || job.isEmpty() || province.isEmpty()) {
                    SnackBarHelper.showShort(v, getString(R.string.error_empty_field));
                    return;
                }
                if(fiscalCode.length() != 16) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_cf_less));
                    return;
                }
                int birthDate = 0;
                try {
                    birthDate = Integer.parseInt(birthdDateString);
                } catch (NumberFormatException e) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_birthdate_error));
                    return;
                }
                currentPatient.setFiscalCode(FiscalCode.parse(fiscalCode));
                currentPatient.setBirthDate(birthDate);
                currentPatient.setJob(job);
                currentPatient.setProvince(province);
                getViewModel().add(currentPatient);
                onBackPressed();
                break;
            case R.id.activity_add_edit_patient_button_save_risk_factor:
                String factorName = binding.activityAddEditPatientRiskFactorNames.getSelectedItem().toString();
                String levelOfRiskString = binding.activityAddEditPatientRiskFactorLevel.getText().toString();
                if(factorName.isEmpty() || levelOfRiskString.isEmpty()) {
                    SnackBarHelper.showShort(v, getString(R.string.error_empty_field));
                    return;
                }
                int levelOfRisk = 0;
                try {
                    levelOfRisk = Integer.parseInt(levelOfRiskString);
                } catch (NumberFormatException e) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_risck_level_error));
                    return;
                }
                if(levelOfRisk < 1 || levelOfRisk > 5) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_risck_level_error));
                    return;
                }

                getViewModel().addFactor(factorName, levelOfRisk);

                binding.activityAddEditPatientRiskFactorLevel.setText("");
                binding.activityAddEditPatientButtonAddRiskFactor.setVisibility(View.VISIBLE);
                binding.activityAddEditPatientNewRiskFactorLayout.setVisibility(View.GONE);
                KeyboardHelper.hideKeyboard(this);
                break;
            case R.id.activity_add_edit_patient_button_save_therapy:
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
                String medicine = binding.activityAddEditPatientTherapyMedicine.getText().toString();
                String unitString = binding.activityAddEditPatientTherapyUnit.getText().toString();
                String frequencyString = binding.activityAddEditPatientTherapyFrequency.getText().toString();
                String startDateString = binding.activityAddEditPatientTherapyStartDate.getText().toString();
                String endDateString = binding.activityAddEditPatientTherapyEndDate.getText().toString();

                if(medicine.isEmpty() || unitString.isEmpty() || frequencyString.isEmpty() || startDateString.isEmpty() || endDateString.isEmpty()) {
                    SnackBarHelper.showShort(v, getString(R.string.error_empty_field));
                    return;
                }
                int unit = 0;
                int frequency = 0;
                try {
                    unit = Integer.parseInt(unitString);
                    frequency = Integer.parseInt(frequencyString);
                } catch (NumberFormatException e) {
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_edit_patient_snackbar_unit_frequency_error));
                    return;
                }
                Date startDate;
                Date endDate;
                try {
                    startDate = format.parse(startDateString);
                    endDate = format.parse(endDateString);
                } catch (ParseException e) {
                    SnackBarHelper.showShort(v, getString(R.string.error_date));
                    return;
                }

                TherapyEntity entity = new TherapyEntity();
                entity.setMedicine(medicine);
                entity.setFrequency(frequency);
                entity.setUnit(unit);
                entity.setStartDate(startDate);
                entity.setEndDate(endDate);

                getViewModel().addTherapy(entity);
                binding.activityAddEditPatientTherapyFrequency.setText("");
                binding.activityAddEditPatientTherapyUnit.setText("");
                binding.activityAddEditPatientTherapyMedicine.setText("");
                binding.activityAddEditPatientTherapyStartDate.setText("");
                binding.activityAddEditPatientTherapyEndDate.setText("");
                KeyboardHelper.hideKeyboard(this);
                binding.activityAddEditPatientButtonAddTherapies.setVisibility(View.VISIBLE);
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

