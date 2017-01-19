package customer.glympse.glympse.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import customer.glympse.glympse.GlympseApplication;

/**
 * Created by admin on 11/23/2016.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static DatePickerFragment getInstance(int args) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        //0 for start date and 1 for end date
        bundle.putInt("date_type",args);
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        GlympseApplication.getBus().register(this);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
//            Toast.makeText(getActivity(),"date is "+day+"/"+(month+1)+"/"+year,Toast.LENGTH_SHORT).show();

        String dateString = (day)+"/"+(month + 1)+"/" + year;


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        //TimeZone tz = TimeZone.getTimeZone("UTC");
        Date date = new Date(year,month,day);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //df.setTimeZone(tz);
        Log.i("dsfs",dateString + ":" + df.format(date));
        GlympseApplication.getBus().post(new DateData(getArguments().getInt("date_type"),dateString  + ":" + df.format(date)));

    }

    public class DateData{
        public int type;
        public String date;

        public DateData(int type, String date) {
            this.type = type;
            this.date = date;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlympseApplication.getBus().unregister(this);
    }
}
