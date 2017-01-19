package customer.glympse.glympse.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

import customer.glympse.glympse.R;

import static com.google.android.gms.internal.zzs.TAG;

public class CommonMethods {

    static ProgressDialog dialog;

    public static boolean isEmailValid(Context context, String email) {
        if (email.isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(context, R.string.title_attention,R.string.message_email_empty).show();
            return false;
        }
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        if (!matcher.matches()) {
            DialogFactory.createSimpleOkErrorDialog(context, R.string.title_attention,R.string.message_email_not_valid).show();
            return false;
        }
        return true;
    }
    public static boolean isValidPhoneNo(Context context, String phoneNo) {
        if (phoneNo.isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(context, R.string.title_attention,R.string.msg_invalid_phone_no).show();
            return false;
        }
        if (phoneNo.length() != 10) {
            DialogFactory.createSimpleOkErrorDialog(context, R.string.title_attention,R.string.msg_invalid_phone_no_length).show();
            return false;
        }
        Matcher matcher = Patterns.PHONE.matcher(phoneNo);
        if (!matcher.matches()) {
            DialogFactory.createSimpleOkErrorDialog(context, R.string.title_attention,R.string.msg_invalid_phone_no).show();
            return false;
        }
        return true;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.d(TAG, manager.getDeviceId());
        return manager.getDeviceId();
    }

    public static ProgressDialog GetDialog(Context context, String title, String Message) {


        if (dialog != null) {
            dialog = null;
        }
        dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(Message);
        dialog.show();
        return dialog;
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }

    public static void errormessageon_Edittext(String message, EditText view) {

        int ecolor = Color.parseColor("#ff0000"); // whatever color you want
        String estring = message;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
        ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        view.setError(ssbuilder);
        view.requestFocus();
    }

    /**
     * method used to get current time
     *
     * @return date and time both
     */
    public static String getFormattedCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        String dateTime = formatter.format(date);
        return dateTime;
    }

    /**
     * method used to show alert dialog
     *
     * @param string get alert message
     */
    public static void showAlert(Context context, String title, String string) {
        // TODO Auto-generated method stub
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(string);
        alert.setCancelable(false);
        alert.setPositiveButton(context.getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        alert.show();
    }

    /**
     * method used to validate email
     *
     * @param email get email
     * @return true or false
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * method used to get age from date of birth
     *
     * @param _month month
     * @param _day   day
     * @param _year  year
     * @return age
     */
    public static int getAge(int _month, int _day, int _year) {

        GregorianCalendar cal = new GregorianCalendar();
        int year, month, date, age;

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        date = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        age = year - cal.get(Calendar.YEAR);
        if ((month < cal.get(Calendar.MONTH))
                || ((month == cal.get(Calendar.MONTH)) && (date < cal.get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }
        if (age < 0)
            throw new IllegalArgumentException("Age < 0");
        return age;
    }

    /**
     * method used to change date format
     *
     * @param date date
     * @return change format date
     */
    public static String changeDateFormat(String date) {
        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat output = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date oneWayTripDate = input.parse(date);  // parse input
            date = output.format(oneWayTripDate);    // format output
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * method used to change the font
     *
     * @param context get context
     * @return changed font
     */
    public static Typeface headerFont(Context context) {
        Typeface headerFont = Typeface.createFromAsset(context.getAssets(), "font/Lato-Regular.ttf");

        return headerFont;
    }

    /**
     * method used to change the font
     *
     * @param context get context
     * @return changed font
     */
    public static Typeface boldFont(Context context) {
        Typeface headerFont = Typeface.createFromAsset(context.getAssets(), "font/Lato-Bold.ttf");

        return headerFont;
    }

    /**
     * method used to change the font
     *
     * @param context get context
     * @return changed font
     */
    public static Typeface normalText(Context context) {
        Typeface headerFont = Typeface.createFromAsset(context.getAssets(), "font/Lato-Light.ttf");

        return headerFont;
    }

    /**
     * method used to remove a comma from last in string
     *
     * @param str contain comma seprated string
     * @return string
     */
    public static String method(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        Log.d("String is" + " String", str);
        return str;
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getWidth(Context mContext) {
        int width = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();  // deprecated
        }
        return width;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getHeight(Context mContext) {
        int height = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            height = display.getHeight();  // deprecated
        }
        return height;
    }



    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd ");
        String strDate = mdformat.format(calendar.getTime());

        Log.d("Current Date:- ", strDate);

        return strDate;
    }

    public static String getDateAndTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        Log.d("Current Date:- ", formattedDate);
        return formattedDate;
    }


    /**
     * method used to get distance in kilometers
     *
     * @param distance contain distance
     * @return distance in kilometer
     */
    public static double getDistanceInKilometers(double distance) {
        return distance * 1.60934;
    }

    /**
     * method used to get distance in meters
     *
     * @param distance contain distance
     * @return distance in meter
     */
    public static double getDistanceInMeters(double distance) {
        return distance * 1609.34;
    }

    /**
     * method used to get distance in meters
     *
     * @param distance contain distance
     * @return distance in meter
     */
    public static double metersToMiles(double distance) {
        return distance / 1609.34;
    }

    /**
     * method used to convert date format
     *
     * @param oldDate contain old date
     * @return converted date
     */
    public static String getConvertedDate(String oldDate) {
        Date date = null;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat write = new SimpleDateFormat("dd-MMM-yyyy");
        write.setTimeZone(TimeZone.getDefault());
        try {
            date = parser.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return write.format(date);
    }
}
