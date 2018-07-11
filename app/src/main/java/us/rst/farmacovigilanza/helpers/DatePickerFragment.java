package us.rst.farmacovigilanza.helpers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public InterfaceCommunicator interfaceCommunicator;
    private int type;

    public DatePickerFragment() {
    }

    @SuppressLint("ValidFragment")
    public DatePickerFragment(int type) {
        this.type = type;
    }

    public interface InterfaceCommunicator {
        void onResult(int type, int year, int month, int day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    // Call when user click ok
    public void onDateSet(DatePicker view, int year, int month, int day) {
        interfaceCommunicator.onResult(type, year, month+1, day);
    }

    public void setInterfaceCommunicator(InterfaceCommunicator interfaceCommunicator) {
        this.interfaceCommunicator = interfaceCommunicator;
    }
}