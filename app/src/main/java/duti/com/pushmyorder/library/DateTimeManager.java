package duti.com.pushmyorder.library;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import duti.com.pushmyorder.R;

import static org.joda.time.LocalDate.fromCalendarFields;

public class DateTimeManager {

    // context variable
    private Context mContext;
    // valid list of months name
    public List<String> monthList = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
    ;
    // Year setup listener variable
    private onYearSetListener myYearSetListener = null;
    // Month setup listener variable
    private onMonthSetListener myMonthSetListener = null;
    // Date setup listener variable
    private onDateSetListener myDateSetListener = null;
    // Date setup listener variable
    private onTimeSetListener myTimeSetListener = null;
    // final string of selected date
    private String mSelectedDate;
    // final string of selected time
    private String mSelectedTime;

    // empty constructor
    public DateTimeManager(Context context) {
        mContext = context;
    }

    DroidTool dt;

    public DateTimeManager(DroidTool droidTool) {
        dt = droidTool;
        mContext = dt.c;
    }

    // year setup listener interface
    public interface onYearSetListener {
        void onYearSet(String year, EditText tvDate);
    }

    // year setup response listener
    public void yearSetResponseListener(onYearSetListener listener) {
        this.myYearSetListener = listener;
    }

    // call TextView along with it's listener to show date picker dialog
    public EditText yearPicker(final int resId, final String pickedYear, final int minYear, final int maxYear, int hintRes) {
        // init EditText
        final EditText etDate = dt.ui.editText.getRes(resId);
        etDate.setText(pickedYear);
        etDate.setHint(dt.gStr(hintRes));
        // init listener of this edit text
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call picker
                showYearDialog(etDate, pickedYear, minYear, maxYear);
            }
        });
        return etDate;
    }

    public void showYearDialog(final EditText etDate, String pickedYear, int minYear, int maxYear) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (!TextUtils.isEmpty(pickedYear)) year = Integer.parseInt(pickedYear);


        View dialogView = View.inflate(dt.c, R.layout.dialog_year_picker, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(dt.c);
        builder.setTitle(dt.gStr(R.string.hint_year_picker))
                .setView(dialogView)
                .setCancelable(true);

        TextView year_text = (TextView) dialogView.findViewById(R.id.selected_year);
        year_text.setText("" + year);

        final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);
        yearPicker.setMaxValue(year + maxYear);
        yearPicker.setMinValue(year - minYear);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVa2, int newVal2) {
                etDate.setText(String.valueOf(newVal2));
            }
        });

        builder.setPositiveButton(dt.gStr(R.string.select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etDate.setText(String.valueOf(yearPicker.getValue()));
                dialogInterface.dismiss();
                // integrate with our custom listener to send result to the activity
                if (myYearSetListener != null)
                    myYearSetListener.onYearSet(String.valueOf(yearPicker.getValue()), etDate);
            }
        });

        builder.setNegativeButton(dt.gStr(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

/*        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                etDate.setText(String.valueOf(yearPicker.getValue()));
                d.dismiss();
                // integrate with our custom listener to send result to the activity
                if (myYearSetListener != null) myYearSetListener.onYearSet(String.valueOf(yearPicker.getValue()), etDate);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });*/

        builder.show();
    }

    // Month setup listener interface
    public interface onMonthSetListener {
        void onMonthSet(String month, TextView tvDate);
    }

    // Month setup response listener
    public void monthSetResponseListener(onMonthSetListener listener) {
        this.myMonthSetListener = listener;
    }

    MonthYearPickerDialog monthPicker;

    private String[] monthNameList = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    // call TextView along with it's listener to show date picker dialog
    public TextView monthPicker(final int resId) {

        // init TextView
        final TextView tvMonth = dt.ui.textView.getObject(resId);

        DateFormat tvDateFormat = new SimpleDateFormat("MMMM", Locale.US);
        Date date = new Date();
        String tvSelectedDate = tvDateFormat.format(date);
        tvMonth.setText(tvSelectedDate);

        monthPicker = new MonthYearPickerDialog();

        monthPicker.setListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String date = String.valueOf(dayOfMonth);
                String month = String.valueOf(monthOfYear);
                if (dayOfMonth < 10) date = "0" + dayOfMonth;
                if (monthOfYear < 10) month = "0" + monthOfYear;
                String mSelectedActualDate = year + "-" + month + "-" + date;

                tvMonth.setText(monthNameList[monthOfYear - 1]);
                // integrate with our custom listener to send result to the activity
                if (myMonthSetListener != null)
                    myMonthSetListener.onMonthSet(mSelectedActualDate, tvMonth);
            }
        });

        // init listener of this edit text
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call picker
                monthPicker.show(((FragmentActivity) (Activity) dt.c).getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        return tvMonth;
    }

    // Date setup listener interface
    public interface onDateSetListener {
        void onDateSet(String date, EditText etDate);
    }

    // Date setup response listener
    public void DateSetResponseListener(onDateSetListener listener) {
        this.myDateSetListener = listener;
    }

    // 1.1.1
    // call Edit text along with it's listener to show date picker dialog
    public EditText datePicker(final int resID, final boolean hasClearButton, final String maxDate, final String minDate, final String pickedDate, final int hintRes) {

        // init edit text
        final EditText etDate = dt.ui.editText.getRes(resID);

        // set text
        etDate.setText(pickedDate);
        etDate.setHint(hintRes);

        // init listener of this edit text
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call date picker
                DatePickerManager(etDate, hasClearButton, maxDate, minDate, etDate.getText().toString().trim(), hintRes);
            }
        });
        return etDate;
    }

    // 1.1.2
    // call Edit text along with it's listener to show date picker dialog
    public EditText datePicker(final View view, final int resID, final boolean hasClearButton, final String maxDate, final String minDate, final String pickedDate, final int hintRes) {

        // init edit text
        //final EditText etDate = dt.ui.editText.getRes(resID);
        final EditText etDate = (EditText) view.findViewById(resID);

        // set text
        etDate.setText(pickedDate);
        etDate.setHint(hintRes);

        // init listener of this edit text
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call date picker
                DatePickerManager(etDate, hasClearButton, maxDate, minDate, etDate.getText().toString().trim(), hintRes);
            }
        });
        return etDate;
    }

    // 1.1.3
    // call only date picker dialog
    public void datePicker(final boolean hasClearButton, final String maxDate, final String minDate) {
        DatePickerManager(hasClearButton, maxDate, minDate);
    }

    // 1.2.1
    // Date picker main method
    public void DatePickerManager(final EditText etDate, boolean hasClearButton, String maxDate, String minDate, String pickedDate, final int hintRes) {
        Activity activity = (Activity) mContext;
        Calendar now;
        if (!TextUtils.isEmpty(pickedDate)) now = calenderFromString(pickedDate);
        else now = Calendar.getInstance();
        ClearableDatePicker dpd = ClearableDatePicker.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        String date = String.valueOf(dayOfMonth);
                        String month = String.valueOf((monthOfYear + 1));
                        if (dayOfMonth < 10) date = "0" + dayOfMonth;
                        if (monthOfYear < 10) month = "0" + (monthOfYear + 1);

                        mSelectedDate = date + "-" + getNameOfTheMonth(monthOfYear + 1) + "-" + year;
                        String mSelectedActualDate = date + "-" + month + "-" + year;

                        etDate.setText(mSelectedDate);
                        etDate.setHint(hintRes);
                        // integrate with our custom listener to send result to the activity
                        if (myDateSetListener != null)
                            myDateSetListener.onDateSet(mSelectedActualDate, etDate);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // listener for clear action in date picker dialog
        if (hasClearButton) {
            dpd.setOnDateClearedListener(new ClearableDatePicker.OnDateClearedListener() {
                @Override
                public void onDateCleared(ClearableDatePicker view) {
                    etDate.setText("");
                    etDate.setHint(hintRes);
                    // integrate with our custom listener to send result to the activity
                    if (myDateSetListener != null) {
                        myDateSetListener.onDateSet("", etDate);
                    }
                }
            });
        }

        //check if has date max validator
        if (!TextUtils.isEmpty(maxDate)) dpd.setMaxDate(getValidDate(maxDate));

        //check if has date min validator
        if (!TextUtils.isEmpty(minDate)) dpd.setMinDate(getValidDate(minDate));

        // bind text and color for ok button
        dpd.setOkText(mContext.getString(R.string.do_select));
        dpd.setOkColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind text and color for cancel button
        dpd.setCancelText(mContext.getString(R.string.do_cancel));
        dpd.setCancelColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind color
        dpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        // finally show the dialog
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    // 1.2.1
    // Date picker main method
    public void DatePickerManager(boolean hasClearButton, String maxDate, String minDate) {
        Activity activity = (Activity) mContext;
        Calendar now = Calendar.getInstance();
        ClearableDatePicker dpd = ClearableDatePicker.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth);
                        if (dayOfMonth < 10) date = "0" + dayOfMonth;
                        // integrate with our custom listener to send result to the activity
                        if (myDateSetListener != null)
                            myDateSetListener.onDateSet(date + "-" + getNameOfTheMonth(monthOfYear + 1) + "-" + year, null);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // listener for clear action in date picker dialog
        if (hasClearButton) {
            dpd.setOnDateClearedListener(new ClearableDatePicker.OnDateClearedListener() {
                @Override
                public void onDateCleared(ClearableDatePicker view) {
                    // integrate with our custom listener to send result to the activity
                    if (myDateSetListener != null) {
                        myDateSetListener.onDateSet("", null);
                    }
                }
            });
        }

        //check if has date max validator
        if (!TextUtils.isEmpty(maxDate)) dpd.setMaxDate(getValidDate(maxDate));

        //check if has date min validator
        if (!TextUtils.isEmpty(minDate)) dpd.setMinDate(getValidDate(minDate));

        // bind text and color for ok button
        dpd.setOkText(mContext.getString(R.string.do_select));
        dpd.setOkColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind text and color for cancel button
        dpd.setCancelText(mContext.getString(R.string.do_cancel));
        dpd.setCancelColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind color
        dpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        // finally show the dialog
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    // 1.3
    // get exact name of the selected month
    public String getNameOfTheMonth(int monthNo) {
        if (monthNo != 0)
            return monthList.get(monthNo - 1);
        return null;
    }

    // 1.4
    // get Valid date
    public Calendar getValidDate(String date) {
        Calendar output = calenderFromString(date);
        output.set(Calendar.YEAR, output.get(Calendar.YEAR));
        output.set(Calendar.DAY_OF_MONTH, output.get(Calendar.DAY_OF_MONTH));
        output.set(Calendar.MONTH, output.get(Calendar.MONTH));
        return output;
    }

    // 1.5
    // get calendar object from server given string
    public Calendar calenderFromString(String stringDate) {
        Calendar calendar = null;
        if (!TextUtils.isEmpty(stringDate)) {
            SimpleDateFormat format = null;
            if (stringDate.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            else format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = null;
            try {
                date = format.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        return calendar;
    }

    public String fineFormatDateFromString(String string) {
        Calendar calendar = null;
        String stringDate = "";
        if (!TextUtils.isEmpty(string)) {
            SimpleDateFormat format = null;
            if (string.matches(".*[a-zA-Z]+.*")) {
                format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            }
            Date date = null;
            try {
                date = format.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            if (calendar != null) {
                calendar.add(Calendar.DATE, 0);  // number of days to add
                calendar.add(Calendar.MONTH, 0);// number of months to add
                calendar.add(Calendar.YEAR, 0);  // number of years to add
                stringDate = format2.format(calendar.getTime());
            }
        }
        return stringDate;
    }

    public String sqliteDateFromString(String string) {
        Calendar calendar = null;
        String stringDate = "";
        if (!TextUtils.isEmpty(string)) {
            SimpleDateFormat format = null;

            if (string.matches(".*[a-zA-Z]+.*")) {
                format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            }

            Date date = null;
            try {
                date = format.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }

            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            if (calendar != null) {
                calendar.add(Calendar.DATE, 0);  // number of days to add
                calendar.add(Calendar.MONTH, 0);// number of months to add
                calendar.add(Calendar.YEAR, 0);  // number of years to add
                stringDate = format2.format(calendar.getTime());
            }
        }
        return stringDate;
    }

    // Time setup listener interface
    public interface onTimeSetListener {
        void onTimeSet(String time, EditText etTime);
    }

    // Time setup response listener
    public void TimeSetResponseListener(onTimeSetListener listener) {
        this.myTimeSetListener = listener;
    }

    // 2.1.1
    // call Edit text along with it's listener to show time picker dialog
    public EditText timePicker(int resID, final boolean hasClearButton, final String maxTime, final String minTime, final String pickedTime) {
        // init edit text
        final EditText etTime = dt.ui.editText.getRes(resID);
        // set text
        etTime.setText(pickedTime);
        etTime.setHint(R.string.select);
        // init listener of this edit text
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call time picker
                TimePickerManager(etTime, hasClearButton, maxTime, minTime, etTime.getText().toString().trim());
            }
        });
        return etTime;
    }

    // 2.1.2
    // call Edit text along with it's listener to show time picker dialog
    public EditText timePicker(View view, int resID, final boolean hasClearButton, final String maxTime, final String minTime, final String pickedTime) {
        // init edit text
        final EditText etTime = (EditText) view.findViewById(resID);
        // set text
        etTime.setText(pickedTime);
        // init listener of this edit text
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call time picker
                TimePickerManager(etTime, hasClearButton, maxTime, minTime, etTime.getText().toString().trim());
            }
        });
        return etTime;
    }

    // 2.2
    // Time picker main method
    public void TimePickerManager(final EditText etTime, boolean hasClearButton, String maxTime, String minTime, String pickedTime) {
        Activity activity = (Activity) mContext;
        Calendar now;
        if (!TextUtils.isEmpty(pickedTime)) now = timeCalenderFromString(pickedTime);
        else now = Calendar.getInstance();

        ClearableTimePicker tpd = ClearableTimePicker.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        String hourString = getExactHour(hourOfDay) < 10 ? "0" + getExactHour(hourOfDay) : "" + getExactHour(hourOfDay);
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString = second < 10 ? "0" + second : "" + second;
                        mSelectedTime = hourString + ":" + minuteString + " " + getTimeFormat(hourOfDay);
                        String mSelectedActualTime = hourOfDay + "-" + minute + "-" + second;
                        etTime.setText(mSelectedTime);
                        // integrate with our custom listener to send result to the activity
                        if (myTimeSetListener != null) {
                            myTimeSetListener.onTimeSet(mSelectedActualTime, etTime);
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                false
        );

        // listener for clear action in time picker dialog
        if (hasClearButton) {
            tpd.setOnTimeClearedListener(new ClearableTimePicker.OnTimeClearedListener() {
                @Override
                public void onTimeCleared(ClearableTimePicker view) {
                    etTime.setText("");
                    // integrate with our custom listener to send result to the activity
                    if (myTimeSetListener != null) {
                        myTimeSetListener.onTimeSet("", etTime);
                    }
                }
            });
        }

        //check if has time max validator
        if (!TextUtils.isEmpty(maxTime)) tpd.setMaxTime(validTime(maxTime));

        //check if has time min validator
        if (!TextUtils.isEmpty(minTime)) tpd.setMinTime(validTime(minTime));

        // bind text and color for ok button
        tpd.setOkText(mContext.getString(R.string.do_select));
        tpd.setOkColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind text and color for cancel button
        tpd.setCancelText(mContext.getString(R.string.do_cancel));
        tpd.setCancelColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind color
        tpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        // finally show the dialog
        tpd.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    // 2.3
    // calender from time string
    public Calendar timeCalenderFromString(String stringTime) {
        Calendar calendar = null;
        if (!TextUtils.isEmpty(stringTime)) {
            SimpleDateFormat format = null;
            if (stringTime.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("h:mm a", Locale.US);
            else format = new SimpleDateFormat("h:mm a", Locale.US);
            Date date = null;
            try {
                date = format.parse(stringTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        return calendar;
    }

    // 2.4
    // time point from string
    public Timepoint validTime(String time) {
        Calendar output = timeCalenderFromString(time);
        int hour = output.get(Calendar.HOUR_OF_DAY);
        int minute = output.get(Calendar.MINUTE);
        int second = output.get(Calendar.SECOND);
        return new Timepoint(hour, minute, second);
    }

    // 2.5
    // get time format for time picker
    public String getTimeFormat(int hourOfDay) {
        String amOrPm = "am";
        if (hourOfDay >= 12) amOrPm = "pm";
        return amOrPm;
    }

    // 2.6
    // get exact hour of time picker
    public int getExactHour(int hourOfDay) {
        int exactHour = hourOfDay;
        if (hourOfDay > 12) exactHour = hourOfDay - 12;
        return exactHour;
    }

    public String getAge(int day, int month, int year) {
        String ageYear;
        String ageMonth;
        String ageDay;
        LocalDate birthday = new LocalDate(year, month, day);
        LocalDate now = new LocalDate();//Today's date
        Period period = new Period(birthday, now, PeriodType.yearMonthDay());
        // years
        if (period.getYears() < 10 && period.getYears() >= 0) {
            ageYear = "0" + period.getYears();
        } else if (period.getYears() < 0) {
            ageYear = "00";
        } else {
            ageYear = String.valueOf(period.getYears());
        }
        // months
        if (period.getMonths() < 10 && period.getMonths() >= 0) {
            ageMonth = "0" + period.getMonths();
        } else if (period.getMonths() < 0) {
            ageMonth = "00";
        } else {
            ageMonth = String.valueOf(period.getMonths());
        }
        // days
        if (period.getDays() < 10 && period.getDays() >= 0) {
            ageDay = "0" + period.getDays();
        } else if (period.getDays() < 0) {
            ageDay = "00";
        } else {
            ageDay = String.valueOf(period.getDays());
        }
        String actualAge = ageYear + "-" + ageMonth + "-" + ageDay;
        return actualAge;
    }

    public int getAgeMonthFromDOB(String date, Calendar calendar, View view, int resId) {
        int ageInMonth = 0;
        String ageInAllUnit = "";
        if (!TextUtils.isEmpty(date)) {
            ageInAllUnit = getAge(date, calendar);
            List<String> ageList = Arrays.asList(ageInAllUnit.split("-"));
            String year = "00";
            String month = "00";
            String day = "00";
            if (!ageList.get(0).equals("00")) year = ageList.get(0);
            if (!ageList.get(1).equals("00")) month = ageList.get(1);
            ageInMonth = (Integer.parseInt(year) * 12) + Integer.parseInt(month);
        }

        if (resId != 0) {
            EditText etDOB = null;
            if (view == null) etDOB = (EditText) ((Activity) dt.c).findViewById(resId);
            else etDOB = (EditText) view.findViewById(resId);
            etDOB.setText(ageInMonth);
        }

        return ageInMonth;
    }

    public String getAge(String benDOB, Calendar toDate) {
        int day = 0, month = 0, year = 0;
        if (!TextUtils.isEmpty(benDOB)) {
            List<String> dateListBen = Arrays.asList(benDOB.split("-"));
            if (benDOB.matches(".*[a-zA-Z]+.*")) {
                day = Integer.parseInt(dateListBen.get(0));
                month = monthList.indexOf(dateListBen.get(1)) + 1;
                year = Integer.parseInt(dateListBen.get(2));
            } else {
                day = Integer.parseInt(dateListBen.get(2));
                month = Integer.parseInt(dateListBen.get(1));
                year = Integer.parseInt(dateListBen.get(0));
            }
        }
        LocalDate targetDate = null;
        if (toDate != null) {
            targetDate = fromCalendarFields(toDate);
        } else {
            targetDate = new LocalDate(); // Current date
        }
        String ageYear;
        String ageMonth;
        String ageDay;
        LocalDate birthday = new LocalDate(year, month, day);
        Period period = new Period(birthday, targetDate, PeriodType.yearMonthDay());
        // years
        if (period.getYears() < 10 && period.getYears() >= 0) {
            ageYear = "0" + period.getYears();
        } else if (period.getYears() < 0) {
            ageYear = "00";
        } else {
            ageYear = String.valueOf(period.getYears());
        }
        // months
        if (period.getMonths() < 10 && period.getMonths() >= 0) {
            ageMonth = "0" + period.getMonths();
        } else if (period.getMonths() < 0) {
            ageMonth = "00";
        } else {
            ageMonth = String.valueOf(period.getMonths());
        }
        // days
        if (period.getDays() < 10 && period.getDays() >= 0) {
            ageDay = "0" + period.getDays();
        } else if (period.getDays() < 0) {
            ageDay = "00";
        } else {
            ageDay = String.valueOf(period.getDays());
        }
        String actualAge = ageYear + "-" + ageMonth + "-" + ageDay;
        return actualAge;
    }

    public String getAgeInMonth(String benDOB, Calendar toDate) {
        int day = 0, month = 0, year = 0;
        if (!TextUtils.isEmpty(benDOB)) {
            List<String> dateListBen = Arrays.asList(benDOB.split("-"));
            if (benDOB.matches(".*[a-zA-Z]+.*")) {
                day = Integer.parseInt(dateListBen.get(0));
                month = monthList.indexOf(dateListBen.get(1)) + 1;
                year = Integer.parseInt(dateListBen.get(2));
            } else {
                day = Integer.parseInt(dateListBen.get(2));
                month = Integer.parseInt(dateListBen.get(1));
                year = Integer.parseInt(dateListBen.get(0));
            }
        }
        LocalDate targetDate = null;
        if (toDate != null) {
            targetDate = fromCalendarFields(toDate);
        } else {
            targetDate = new LocalDate(); // Current date
        }
        String ageYear;
        String ageMonth;
        String ageDay;
        LocalDate birthday = new LocalDate(year, month, day);
        Period period = new Period(birthday, targetDate, PeriodType.yearMonthDay());
        // years
        if (period.getYears() < 10 && period.getYears() >= 0) {
            ageYear = "0" + period.getYears();
        } else if (period.getYears() < 0) {
            ageYear = "00";
        } else {
            ageYear = String.valueOf(period.getYears());
        }
        // months
        if (period.getMonths() < 10 && period.getMonths() >= 0) {
            ageMonth = "0" + period.getMonths();
        } else if (period.getMonths() < 0) {
            ageMonth = "00";
        } else {
            ageMonth = String.valueOf(period.getMonths());
        }
        // days
        if (period.getDays() < 10 && period.getDays() >= 0) {
            ageDay = "0" + period.getDays();
        } else if (period.getDays() < 0) {
            ageDay = "00";
        } else {
            ageDay = String.valueOf(period.getDays());
        }
        String actualAge = ageYear + "-" + ageMonth + "-" + ageDay;
        return actualAge;
    }

    // get edd date in string
    public String getEDDFromCalender(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String stringDate = "";
        if (calendar != null) {
            calendar.add(Calendar.DATE, 7);  // number of days to add
            calendar.add(Calendar.MONTH, 8);  // number of months to add
            calendar.add(Calendar.YEAR, 0);  // number of years to add
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    // get minimum LMP date
    public String getMinLmpDate(String today) {
        String minDate = "";
        Calendar calendar = null;
        if (!TextUtils.isEmpty(today)) {
            SimpleDateFormat format = null;
            if (today.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            else format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = null;
            try {
                date = format.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        if (calendar != null) {
            calendar.add(Calendar.DATE, 0);
            calendar.add(Calendar.MONTH, -10);
            calendar.add(Calendar.YEAR, 0);
            minDate = format.format(calendar.getTime());
        }
        return minDate;
    }

    // get Min Date limit from today (one month earlier)
    public String getOneMonthEarlierThanToday(String today) {
        String minDate = "";
        Calendar calendar = null;
        if (!TextUtils.isEmpty(today)) {
            SimpleDateFormat format = null;
            if (today.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            else format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = null;
            try {
                date = format.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        if (calendar != null) {
            calendar.add(Calendar.DATE, 0);
            calendar.add(Calendar.MONTH, -1);
            calendar.add(Calendar.YEAR, 0);
            minDate = format.format(calendar.getTime());
        }
        return minDate;
    }

    // get Date Of Birth string from day, month, year
    public String getDateOfBirthFromSplitAgeStrings(String yearString, String monthString, String dayString) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String stringDate = null;
        int day = 0, month = 0, year = 0;
        if (!TextUtils.isEmpty(dayString)) {
            day = Integer.parseInt(dayString);
        }
        if (!TextUtils.isEmpty(monthString)) {
            month = Integer.parseInt(monthString);
        }
        if (!TextUtils.isEmpty(yearString)) {
            year = Integer.parseInt(yearString);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);  // number of days to subtract
        calendar.add(Calendar.MONTH, -month);  // number of months to subtract
        calendar.add(Calendar.YEAR, -year);  // number of years to subtract

        if (calendar != null) {
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    public String getStringDateByAddingWithCurrentDate(int day, int month, int year) {
        String stringDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        if (calendar != null) {
            calendar.add(Calendar.DATE, day);  // number of days to add
            calendar.add(Calendar.MONTH, month);  // number of months to add
            calendar.add(Calendar.YEAR, year);  // number of years to add
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    public String getStringDateBySubtractingWithCurrentDate(int day, int month, int year) {
        String stringDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        if (calendar != null) {
            calendar.add(Calendar.DATE, -day);  // number of days to subtract
            calendar.add(Calendar.MONTH, -month);  // number of months to subtract
            calendar.add(Calendar.YEAR, -year);  // number of years to subtract
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    // get Age in String
    public String getAgeStringFromStringDate(String date) {
        String ageInString = "";
        String ageInYears = "";
        if (!TextUtils.isEmpty(date)) {
            List<String> dateList = Arrays.asList(date.split("-"));
            if (date.matches(".*[a-zA-Z]+.*")) {
                ageInYears = getAge(Integer.parseInt(dateList.get(0)), (monthList.indexOf(dateList.get(1)) + 1), Integer.parseInt(dateList.get(2)));
            } else {
                ageInYears = getAge(Integer.parseInt(dateList.get(2)), Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(0)));
            }
            List<String> ageList = Arrays.asList(ageInYears.split("-"));
            String year = "";
            String month = "";
            String day = "";
            if (!ageList.get(0).equals("00"))
                year = ageList.get(0) + " " + dt.gStr(R.string.year) + " ";
            if (!ageList.get(1).equals("00"))
                month = ageList.get(1) + " " + dt.gStr(R.string.month) + " ";
            day = ageList.get(2) + " " + dt.gStr(R.string.day);
            ageInString = year + month + day;
        }
        return ageInString;
    }

    // set Age in Views from Date of Birth
    public String setAgeFromDOB(String date, View view, int resId) {
        String ageInString = "";
        String ageInYears = "";
        if (!TextUtils.isEmpty(date)) {
            List<String> dateList = Arrays.asList(date.split("-"));
            if (date.matches(".*[a-zA-Z]+.*")) {
                ageInYears = getAge(Integer.parseInt(dateList.get(0)), (monthList.indexOf(dateList.get(1)) + 1), Integer.parseInt(dateList.get(2)));
            } else {
                ageInYears = getAge(Integer.parseInt(dateList.get(2)), Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(0)));
            }
            List<String> ageList = Arrays.asList(ageInYears.split("-"));
            String year = "";
            String month = "";
            String day = "";
            if (!ageList.get(0).equals("00"))
                year = ageList.get(0) + " " + dt.gStr(R.string.year) + " ";
            if (!ageList.get(1).equals("00"))
                month = ageList.get(1) + " " + dt.gStr(R.string.month) + " ";
            day = ageList.get(2) + " " + dt.gStr(R.string.day);
            ageInString = year + month + day;
        }
        EditText etDOB = null;
        if (view == null) etDOB = (EditText) ((Activity) dt.c).findViewById(resId);
        else etDOB = (EditText) view.findViewById(resId);
        etDOB.setText(ageInString);
        return ageInString;
    }

    public int getAgeMonthFromDOB(String date, View view, int resId) {
        int ageInMonth = 0;
        String ageInAllUnit = "";
        if (!TextUtils.isEmpty(date)) {
            List<String> dateList = Arrays.asList(date.split("-"));
            if (date.matches(".*[a-zA-Z]+.*")) {
                ageInAllUnit = getAge(Integer.parseInt(dateList.get(0)), (monthList.indexOf(dateList.get(1)) + 1), Integer.parseInt(dateList.get(2)));
            } else {
                ageInAllUnit = getAge(Integer.parseInt(dateList.get(2)), Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(0)));
            }
            List<String> ageList = Arrays.asList(ageInAllUnit.split("-"));
            String year = "00";
            String month = "00";
            String day = "00";
            if (!ageList.get(0).equals("00")) year = ageList.get(0);
            if (!ageList.get(1).equals("00")) month = ageList.get(1);
            ageInMonth = (Integer.parseInt(year) * 12) + Integer.parseInt(month);
            if (resId != 0) {
                EditText etDOB = null;
                if (view == null) etDOB = (EditText) ((Activity) dt.c).findViewById(resId);
                else etDOB = (EditText) view.findViewById(resId);
                etDOB.setText(ageInMonth);
            }
        }
        return ageInMonth;
    }

    public int getAgeYearFromDOB(String date, View view, int resId) {
        int ageInYear = 0;
        String ageInAllUnit = "";
        if (!TextUtils.isEmpty(date)) {
            List<String> dateList = Arrays.asList(date.split("-"));
            if (date.matches(".*[a-zA-Z]+.*")) {
                ageInAllUnit = getAge(Integer.parseInt(dateList.get(0)), (monthList.indexOf(dateList.get(1)) + 1), Integer.parseInt(dateList.get(2)));
            } else {
                ageInAllUnit = getAge(Integer.parseInt(dateList.get(2)), Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(0)));
            }
            List<String> ageList = Arrays.asList(ageInAllUnit.split("-"));
            String year = "00";
            if (!ageList.get(0).equals("00")) year = ageList.get(0);
            ageInYear = Integer.parseInt(year);
            if (resId != 0) {
                EditText etDOB = null;
                if (view == null) etDOB = (EditText) ((Activity) dt.c).findViewById(resId);
                else etDOB = (EditText) view.findViewById(resId);
                etDOB.setText(ageInYear);
            }
        }
        return ageInYear;
    }

    // date smaller than another date
    public boolean dateSmallerThanAnotherDate(String gmpDate, String childrenDOB){
        boolean isSmaller = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date dateChild = sdf.parse(childrenDOB);
            Date dateGmp = sdf.parse(gmpDate);
            if(dateChild.compareTo(dateGmp) < 0) isSmaller = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSmaller;
    }

    // get current system date
    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        return currentDate;
    }

    // get current system SQLite date
    public String getSqlCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        return currentDate;
    }

    // get current system time
    public String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        Date date = new Date();
        String currentTime = timeFormat.format(date);
        return currentTime;
    }

    // get current system time
    public String getCurrentSqlTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM", Locale.US);
        Date date = new Date();
        String currentTime = timeFormat.format(date);
        return currentTime;
    }

    // get current system time
    public String getSqlTimeFromString(String time) {
        String sqlTime = "";
        if(time!=null) {
            if (!TextUtils.isEmpty(time)) {
                sqlTime = time;
                if (time.contains("AM") || time.contains("PM")) sqlTime = time.substring(0, time.indexOf(' ') - 1);
            }
        }
        return sqlTime;
    }

    //compare time
    public boolean CompareTimeIsSmaller(String bigTime, String smallTime){
        boolean isSmaller = false;
        if(bigTime!=null && smallTime!=null){
            if(!TextUtils.isEmpty(bigTime) && !TextUtils.isEmpty(bigTime)){
                try {
                    String time1 = getSqlTimeFromString(bigTime);
                    String time2 = getSqlTimeFromString(smallTime);
                    DateFormat f = new SimpleDateFormat("HH:mm");
                    Date d1 = f.parse(time1);
                    Date d2 = f.parse(time2);
                    long difference = d1.getTime() - d2.getTime();
                    if(difference>0)isSmaller = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSmaller;
    }

    //compare time
    public boolean CompareTimeWithDifference(String bigTime, String smallTime, int minutesDifference){
        boolean isSmaller = false;
        if(bigTime!=null && smallTime!=null){
            if(!TextUtils.isEmpty(bigTime) && !TextUtils.isEmpty(bigTime)){
                try {
                    String time1 = getSqlTimeFromString(bigTime);
                    String time2 = getSqlTimeFromString(smallTime);
                    DateFormat f = new SimpleDateFormat("HH:mm");
                    Date d1 = f.parse(time1);
                    Date d2 = f.parse(time2);
                    long difference = d1.getTime() - d2.getTime();
                    long shouldGap = ( 1000 * (minutesDifference * 60));
                    if(difference>=shouldGap)isSmaller = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSmaller;
    }

    // get current system month
    public String getCurrentMonth() {
        DateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", Locale.US);
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        String currentMonth = "01-" + currentDate;
        return currentMonth;
    }

    // get system current date and time
    public String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a", Locale.US);
        String formattedDate = dateFormat.format(new Date()).toString();
        System.out.println(formattedDate);
        return formattedDate;
    }

    // get system current date and time mili seconds
    public String getCurrentDateTimeMils() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yymmddhhmmssss", Locale.US);
        String formattedDate = dateFormat.format(new Date()).toString();
        System.out.println(formattedDate);
        return formattedDate;
    }

    // get current system year
    public String getCurrentYear() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    // get year from string
    public String getYearFromString(String stringDate) {
        String year = "";
        if (!TextUtils.isEmpty(stringDate)) {
            if (stringDate.matches(".*[a-zA-Z]+.*")) year = stringDate.substring(7, stringDate.length()-1);
            else year = stringDate.substring(0, 4);
        }
        return year;
    }

    // get month from selected date
    public String getMonthFromSelectedDate(String date) {
        String month = "";
        if(!TextUtils.isEmpty(date)) {
            if (date.matches(".*[a-zA-Z]+.*")) {
                List<String> dateList = Arrays.asList(date.split("-"));
                for (int i = 0; i < monthList.size(); i++) {
                    if (monthList.get(i).equals(dateList)) {
                        month = String.valueOf(i + 1);
                        break;
                    }
                }
            } else {
                List<String> dateList = Arrays.asList(date.split("-"));
                month = getNameOfTheMonth(Integer.parseInt(dateList.get(1)));
            }
        }
        return month;
    }

    // get year from string
    public String getYearMonthFromString(String date) {
        String yearMonth = "";
        if(!TextUtils.isEmpty(date)){
            if (yearMonth.matches(".*[a-zA-Z]+.*")) {
                List<String> dateList = Arrays.asList(date.split("-"));
                String month = "";
                for (int i = 0; i<monthList.size(); i++){
                    if(monthList.get(i).equals(dateList.get(1))){
                        month = String.valueOf(i+1);
                        break;
                    }
                }
                yearMonth = dateList.get(2)+"-"+month+"-";
            } else {
                List<String> dateList = Arrays.asList(date.split("-"));
                yearMonth = dateList.get(0)+"-"+dateList.get(1)+"-";
            }
        }
        return yearMonth;
    }

}
