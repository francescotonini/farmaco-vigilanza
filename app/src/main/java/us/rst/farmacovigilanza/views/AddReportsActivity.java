package us.rst.farmacovigilanza.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.pixplicity.easyprefs.library.Prefs;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import us.rst.farmacovigilanza.FarmacoVigilanzaApp;
import us.rst.farmacovigilanza.Logger;
import us.rst.farmacovigilanza.R;
import us.rst.farmacovigilanza.database.entity.AdverseReactionEntity;
import us.rst.farmacovigilanza.database.entity.PatientEntity;
import us.rst.farmacovigilanza.database.entity.PatientFactorEntity;
import us.rst.farmacovigilanza.database.entity.TherapyEntity;
import us.rst.farmacovigilanza.databinding.ActivityAddReportsBinding;
import us.rst.farmacovigilanza.helpers.KeyboardHelper;
import us.rst.farmacovigilanza.helpers.SnackBarHelper;
import us.rst.farmacovigilanza.viewmodels.ReportViewModel;

/**
 * Binds the UI with the code-behind
 */
public class AddReportsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddReportsBinding binding;
    private ReportViewModel viewModel;
    private PatientEntity patient;
    private boolean doubleBackToExitPressedOnce = false;

    @Override protected int getLayoutId() {
        return R.layout.activity_add_reports;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected ReportViewModel getViewModel() {
        if (viewModel == null) {
            ReportViewModel.Factory factory = new ReportViewModel.Factory(getApplication(),
                    ((FarmacoVigilanzaApp)getApplication()).getDataRepository().getReportsRepository());
            viewModel = ViewModelProviders.of(this, factory).get(ReportViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.activity_main_menu_logout:
                Prefs.putBoolean("isLoggedIn", true);
                // TODO: clear backstack
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.menu_doctor_show_reports:
                startActivity(new Intent(this, ReportsChronologyActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activityAddReportsAddPatient.setOnClickListener(this);
        binding.activityAddReportsEditPatient.setOnClickListener(this);
        binding.activityAddReportsButtonSave.setOnClickListener(this);
        binding.activityAddReportsAddAdverseReaction.setOnClickListener(this);

        binding.activityAddReportsInputCf.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void afterTextChanged(Editable s) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 16) {
                    Logger.v(AddReportsActivity.class.getSimpleName(), "CF is " + s);
                    getViewModel().findPatient(s.toString());
                    getViewModel().findFactors(s.toString());
                    getViewModel().findTherapies(s.toString());

                    return;
                }

                hideAllSection();
            }
        });

        getViewModel().getPatient(this).observe(AddReportsActivity.this, patientEntity -> {
            patient = patientEntity;
            if (patientEntity == null) {
                binding.activityAddReportsNoPatientLayout.setVisibility(View.VISIBLE);
                binding.activityAddReportsPatientLayout.setVisibility(View.GONE);
            }
            else {
                binding.activityAddReportsCardViewAdverseReaction.setVisibility(View.VISIBLE);
                binding.activityAddReportsPatientLayout.setVisibility(View.VISIBLE);
                binding.activityAddReportsNoPatientLayout.setVisibility(View.GONE);
                binding.activityAddReportsCardViewTherapy.setVisibility(View.VISIBLE);
                binding.activityAddReportsButtonSave.setVisibility(View.VISIBLE);
                binding.activityAddReportsYearJob.setText(patientEntity.getBirthDate() + " - " + patientEntity.getJob());
            }
        });

        getViewModel().getFactors(this).observe(AddReportsActivity.this, riskFactors -> {
            if (riskFactors == null) {
                binding.activityAddReportsRiskFactors.setText("Nessun fattore di rischio");
            }
            else {
                String output = "";
                for(PatientFactorEntity f: riskFactors) {
                    output += f.getFactorName() + " con rischio " + f.getLevelOfRisk() + " \n";
                }
                binding.activityAddReportsRiskFactors.setText(output);
            }
        });

        getViewModel().getAdversReactions(this).observe(this, adverseReactionEntities -> {
            List<String> adverseReactionNames = new ArrayList<>();
            for (AdverseReactionEntity a: adverseReactionEntities) {
                adverseReactionNames.add(a.getName());
            }

            binding.activityAddReportsAdverseReactionNames.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, adverseReactionNames));
        });

        getViewModel().getTherapies(this).observe(this, therapyEntities -> {
            List<String> therapyNames = new ArrayList<>();
            for (TherapyEntity a: therapyEntities) {
                therapyNames.add(a.getMedicine() + " (" + a.getUnit() + ") ogni " + a.getFrequency() + " giorni");
            }

            binding.activityAddReportsTherapiesNames.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, therapyNames));
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activity_add_reports_add_patient:
                KeyboardHelper.hideKeyboard(AddReportsActivity.this);
                binding.activityAddReportsInputCf.setText("");
                hideAllSection();
                startActivity(new Intent(this, AddEditPatientActivity.class));
                break;
            case R.id.activity_add_reports_edit_patient:
                KeyboardHelper.hideKeyboard(AddReportsActivity.this);
                hideAllSection();
                Intent intent = new Intent(this, AddEditPatientActivity.class);
                intent.putExtra("cf", patient.getFiscalCode().toString());
                startActivity(intent);
                break;
            case R.id.activity_add_reports_add_adverse_reaction:
                startActivity(new Intent(this, AddAdverseReactionActivity.class));
                break;
            case R.id.activity_add_reports_button_save:
                String adverseReactionName = binding.activityAddReportsAdverseReactionNames.getSelectedItem().toString();
                String adverseReactionDate = binding.activityAddReportsAdverseReactionDate.getText().toString();
                int therapyId = getViewModel().getTherapies(this).getValue().get(binding.activityAddReportsTherapiesNames.getSelectedItemPosition()).getId();
                String levelOfGravityString = binding.activityAddReportsAdverseReactionLevelOfRisk.getText().toString();

                if(adverseReactionDate.isEmpty() || adverseReactionName.isEmpty() || levelOfGravityString.isEmpty()) {
                    KeyboardHelper.hideKeyboard(this);
                    SnackBarHelper.showShort(v, getString(R.string.error_empty_field));
                    return;
                }

                int levelOfGravity = 0;
                try {
                    levelOfGravity = Integer.parseInt(levelOfGravityString);
                } catch (NumberFormatException e) {
                    KeyboardHelper.hideKeyboard(this);
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_reports_snackbar_gravity_level_error));
                    return;
                }
                if(levelOfGravity < 1 || levelOfGravity > 6) {
                    KeyboardHelper.hideKeyboard(this);
                    SnackBarHelper.showShort(v, getString(R.string.activity_add_reports_snackbar_gravity_level_error));
                    return;
                }

                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
                try {
                    getViewModel().saveReport(patient, adverseReactionName, levelOfGravity, format.parse(adverseReactionDate), therapyId);
                } catch (ParseException e) {
                    KeyboardHelper.hideKeyboard(this);
                    SnackBarHelper.showShort(v, getString(R.string.error_date));
                    return;
                }

                KeyboardHelper.hideKeyboard(this);
                binding.activityAddReportsAdverseReactionDate.setText("");
                binding.activityAddReportsAdverseReactionLevelOfRisk.setText("");
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.activity_add_reports_report_add_correctly))
                        .setPositiveButton(getString(R.string.button_ok), (dialog, which) -> {
                            // do nothing
                        })
                        .show();
                break;
        }
    }

    public void hideAllSection() {
        binding.activityAddReportsNoPatientLayout.setVisibility(View.GONE);
        binding.activityAddReportsPatientLayout.setVisibility(View.GONE);
        binding.activityAddReportsCardViewAdverseReaction.setVisibility(View.GONE);
        binding.activityAddReportsNoPatientLayout.setVisibility(View.GONE);
        binding.activityAddReportsCardViewTherapy.setVisibility(View.GONE);
        binding.activityAddReportsButtonSave.setVisibility(View.GONE);
    }

    // Control back press to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            // Close application
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.toast_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2500);
    }
}
