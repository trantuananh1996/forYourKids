package koiapp.pr.com.koiapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tran Anh on 11/5/2016.
 */
public class DateTimeUtils {
    public Context mContext;
    private static volatile DateTimeUtils instance = null;
    private static boolean mResult;

    public static DateTimeUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (DateTimeUtils.class) {
                if (instance == null) {
                    instance = new DateTimeUtils(context);
                }
            }
        }
        return instance;
    }

    private DateTimeUtils(Context context) {
        mContext = context;
    }

    public static SimpleDateFormat HH_mm = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public static SimpleDateFormat HH_mm_aa = new SimpleDateFormat("HH:mm aa", Locale.getDefault());
    public static SimpleDateFormat dd_MM_yyyy_HH_mm = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static SimpleDateFormat dd_MM = new SimpleDateFormat("dd/MM", Locale.getDefault());
    public static SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static SimpleDateFormat MM = new SimpleDateFormat("MM", Locale.getDefault());
    public static SimpleDateFormat yyyy = new SimpleDateFormat("yyyy", Locale.getDefault());
    public static SimpleDateFormat MM_yyyy = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    public static String getFormatedDateTime(SimpleDateFormat format, long epoch) {
        return format.format(epoch * 1000);
    }


    public  String getCurrentddMMyyyy() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale);
        return format.format(Calendar.getInstance().getTime());
    }

    public  String get_ddMMyyyy_fromEpoch(long epoch) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale);
        return format.format(epoch * 1000);
    }

    public  long getEpochFromddMMyyyy(String str) {
        try {
            return (new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale)).parse(str).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public  String getHourFromEpoch(long epoch) {
        return new SimpleDateFormat("HH:mm"
                , mContext.getResources().getConfiguration().locale).format(epoch * 1000);
    }

    public  boolean isWeekend_ddMMyyyy(String str) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale).parse(str));
            return c.get(Calendar.DAY_OF_WEEK) - 1 == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getEpochFromHour(String hour) {
        try {
            Calendar c = Calendar.getInstance();
            return new SimpleDateFormat("dd.MM.yyyy HH:mm"
                    , mContext.getResources().getConfiguration().locale)
                    .parse(c.get(Calendar.DATE) + "."
                            + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR) + " " + hour).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void showDatePickerGetDateMinToday(final TextView v, Activity activity) {
        // when dialog box is closed, below method will be called.
        DatePickerDialog.OnDateSetListener datePickerListener
                = (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale);
                    String strDate = format.format(calendar.getTime());
                    v.setText(strDate);
                };
        DatePickerDialog datePicker = FragmentUtils.getInstance(mContext).createDateDialog(datePickerListener, activity);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        if (!TextUtils.isEmpty(v.getText().toString())) {
            String date = v.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", activity.getResources().getConfiguration().locale);
            try {
                Date d = format.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        datePicker.show();
    }

    public void addListenerPickDateMinDate(final TextView textView, final long date, final Activity activity) {
        textView.setOnClickListener(v -> showDatePickerGetDateMinDate(textView, date, activity));
    }

    public void showDatePickerGetDateMinDate(final TextView textView, long date, Activity activity) {
        // when dialog box is closed, below method will be called.
        DatePickerDialog.OnDateSetListener datePickerListener
                = (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale);
                    String strDate = format.format(calendar.getTime());
                    textView.setText(strDate);
                };
        DatePickerDialog datePicker = FragmentUtils.getInstance(mContext).createDateDialog(datePickerListener, activity);
        datePicker.getDatePicker().setMinDate(date);
        if (!TextUtils.isEmpty(textView.getText().toString())) {
            String strd = textView.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", activity.getResources().getConfiguration().locale);
            try {
                Date d = format.parse(strd);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        datePicker.show();
    }

    public void addListenerPickDateMinToday(final TextView view, final Activity activity) {
        view.setOnClickListener(v -> showDatePickerGetDateMinToday(view, activity));
    }

    public static void showDatePickerGetDateMaxToday(final TextView v, final Activity activity) {

        // when dialog box is closed, below method will be called.
        DatePickerDialog.OnDateSetListener datePickerListener
                = (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", activity.getResources().getConfiguration().locale);
                    String strDate = format.format(calendar.getTime());
                    v.setText(strDate);
                };
        DatePickerDialog datePicker = FragmentUtils.getInstance(activity).createDateDialog(datePickerListener, activity);
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + 10000);
        if (!TextUtils.isEmpty(v.getText().toString())) {
            String date = v.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", activity.getResources().getConfiguration().locale);
            try {
                Date d = format.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        datePicker.show();
    }

    public static void addListenerPickDateMaxToday(final TextView view, final Activity activity) {
        view.setOnClickListener(v -> showDatePickerGetDateMaxToday(view, activity));
    }

    public void showDatePickerGetDate(final TextView v, Activity activity) {
        // when dialog box is closed, below method will be called.
        DatePickerDialog.OnDateSetListener datePickerListener
                = (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", mContext.getResources().getConfiguration().locale);
                    String strDate = format.format(calendar.getTime());
                    v.setText(strDate);
                };
        DatePickerDialog datePicker = FragmentUtils.getInstance(mContext).createDateDialog(datePickerListener, activity);
        if (!TextUtils.isEmpty(v.getText().toString())) {
            String date = v.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", activity.getResources().getConfiguration().locale);
            try {
                Date d = format.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        datePicker.show();
    }

    public void addListenerPickDate(final TextView view, final Activity activity) {
        view.setOnClickListener(v -> showDatePickerGetDate(view, activity));
    }
}
