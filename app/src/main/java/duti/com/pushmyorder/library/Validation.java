package duti.com.pushmyorder.library;

import android.content.Context;
import android.graphics.Color;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import duti.com.pushmyorder.R;


public class Validation {

    DroidTool dt;
    Map<String, String[]> validationMap = new HashMap<String, String[]>();
    String[] _msg;
    int[] _length;

    public Validation(DroidTool dt, Map<String, String[]> validationMap) {
        this.dt = dt;
        this.validationMap = validationMap;
    }


    public void setEditTextIsNotEmpty(String[] controls, String[] msgs) {
        if (msgs == null) {
            validationMap.put("EditText", controls);
            return;
        }
        if (controls.length == msgs.length) {
            validationMap.put("EditText", controls);
            _msg = msgs;
        } else
            dt.msg("Validation messages and mapping controls are not equal");
    }

    public void setEditTextLength(String[] controls, int[] length) {
        if (controls.length == length.length) {
            validationMap.put("EditTextLength", controls);
            _length = length;
        } else
            dt.msg("Missing controls length?");
    }

    public void setSpinnerIsNotEmpty(String[] controls) {
        validationMap.put("Spinner", controls);
    }

    public void setCheckBoxIsNotEmpty(String prefix, int count, String fieldName) {
        String[] val = new String[]{prefix, Integer.toString(count), fieldName};
        validationMap.put("CheckBox", val);
    }

    public void setRadioButtonIsNotEmpty(String prefix, int count, String fieldName) {
        String[] val = new String[]{prefix, Integer.toString(count), fieldName};
        validationMap.put("RadioButton", val);
    }

    public boolean isValid() {
        boolean isValid = true;
        for (Map.Entry<String, String[]> entry : validationMap.entrySet()) {
            if (entry.getKey() == "EditText") {
                int iMsg = 0;
                for (String controlName : entry.getValue()) {
                    int resID = dt.c.getResources().getIdentifier(controlName, "id", dt.c.getPackageName());
                    //EditText et = (EditText) ((android.app.Activity) dt.c).findViewById(resID);
                    EditText et = dt.ui.editText.getRes(resID);
                    if(et!=null){
                        if (et.getText().toString().isEmpty()) {
                            et.requestFocus();
                            InputMethodManager imm = (InputMethodManager) ((android.app.Activity) dt.c).getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                            if (_msg != null)
                                et.setError(_msg[iMsg]);
                            else
                                et.setError("This value is important.");
                            //msg(controlName + " is important.");
                            isValid = false;
                            break;
                        }
                        iMsg++;
                    }
                }
            }
            if (entry.getKey() == "EditTextLength") {
                int iPos = 0;
                for (String controlName : entry.getValue()) {
                    int resID = dt.c.getResources().getIdentifier(controlName, "id", dt.c.getPackageName());
                    //EditText et = (EditText) ((android.app.Activity) dt.c).findViewById(resID);
                    EditText et = dt.ui.editText.getRes(resID);
                    if (et.length() != _length[iPos]) {
                        et.requestFocus();
                        InputMethodManager imm = (InputMethodManager) ((android.app.Activity) dt.c).getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        et.setError(dt.gStr(R.string.minimum) + " " + _length[iPos] + " " + dt.gStr(R.string.letter_require));
                        isValid = false;
                        break;
                    }
                    iPos++;
                }
            }
            if (entry.getKey() == "Spinner") {
                for (String controlName : entry.getValue()) {
                    int resID = dt.c.getResources().getIdentifier(controlName, "id", dt.c.getPackageName());
                    //Spinner sp = (Spinner) ((android.app.Activity) dt.c).findViewById(resID);
                    Spinner sp = dt.ui.spinner.get(resID);
                    if(sp!=null){
                        SpinnerValue spinnerValue = (SpinnerValue) sp.getSelectedItem();
                        if (spinnerValue.valueText == "0") {
                            sp.requestFocus();
                            TextView errorText = (TextView) sp.getSelectedView();
                            ;
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            errorText.setText(dt.gStr(R.string.select_correct_data));
                            isValid = false;
                            break;
                        }
                    }
                }
            }
            if (entry.getKey() == "CheckBox") {
                String[] val = entry.getValue();
                boolean isChecked = false;
                for (int i = 1; i <= Integer.parseInt(val[1]); i++) {
                    String ctlName = val[0] + i;
                    int resID = dt.c.getResources().getIdentifier(ctlName, "id", dt.c.getPackageName());
                    CheckBox chkCtl = (CheckBox) ((android.app.Activity) dt.c).findViewById(resID);
                    isChecked = chkCtl.isChecked();
                    chkCtl.requestFocus();

                    if (!isChecked) {
                        //isValid = isChecked;
                        //msg(val[2] + " is important.");
                        chkCtl.setError(val[2] + " is important.");
                        break;
                    }
                }
                isValid = isChecked;
            }
            if (entry.getKey() == "RadioButton") {
                String[] val = entry.getValue();
                boolean isChecked = false;
                for (int i = 1; i <= Integer.parseInt(val[1]); i++) {
                    String ctlName = val[0] + i;
                    int resID = dt.c.getResources().getIdentifier(ctlName, "id", dt.c.getPackageName());
                    RadioButton chkCtl = (RadioButton) ((android.app.Activity) dt.c).findViewById(resID);
                    if (chkCtl.isChecked() == true) {
                        isChecked = true;
                    }
                }
                if (!isChecked) {
                    isValid = isChecked;
                    dt.msg(val[2] + " is important.");
                    break;
                }
                isValid = isChecked;
            }
        }
        return isValid;
    }
}
