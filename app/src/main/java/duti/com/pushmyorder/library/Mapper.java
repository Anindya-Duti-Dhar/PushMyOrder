package duti.com.pushmyorder.library;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Mapper<T> {
    DroidTool dt;
    Object objModel;
    HashMap<String, SpinnerValue[]> spinnerValueMap = new HashMap<>();
    public Mapper(DroidTool droidTool, Object o, HashMap<String, SpinnerValue[]> spinnerValueMap) {
        dt = droidTool;
        this.spinnerValueMap = spinnerValueMap;
        objModel = o;
    }

    //region ModelFromUI
    public Object ModelFromUI(Object model) {
        objModel = model;

        ViewGroup viewGroup = (ViewGroup) ((Activity) dt.c).getWindow().getDecorView();
        getAllChildren(viewGroup);
        return objModel;
    }

    public Object ModelFromUI(Object model, View v) {
        objModel = model;

        //ViewGroup viewGroup = (ViewGroup) ((ActivityUI) c).getWindow().getDecorView();
        getAllChildren(v);
        return objModel;
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            boolean isSkipModelValue = false;

            View tv = (View) child;
            if (tv.getTag() != null)
                if (tv.getTag().toString().contains("exclude")) {
                    isSkipModelValue = true;
                }
            if (!isSkipModelValue) {
                if (child instanceof EditText) {
                    EditText editText = (EditText) child;
                    if (editText.getId() != -1) {
                        String editTextName = dt.c.getResources().getResourceEntryName(editText.getId());
                        String editTextValue = editText.getText().toString();
                        setModelValue(editTextName, editTextValue);
                    }
                } else if (child instanceof Spinner) {
                    Spinner spinner = (Spinner) child;
                    if (spinner.getId() != -1) {
                        String spinnerName = dt.c.getResources().getResourceEntryName(spinner.getId());
                        SpinnerValue spinnerValue = (SpinnerValue) spinner.getSelectedItem();
                        if(spinnerValue!=null)setModelValue(spinnerName, spinnerValue.valueText);
                    }
                } else if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.getId() != -1) {
                        String checkBoxName = dt.c.getResources().getResourceEntryName(checkBox.getId());
                        String checkBoxValue = "0";
                        if(checkBox.isChecked()) checkBoxValue = "1";
                        setModelValue(checkBoxName, checkBoxValue);
                    }
                } else if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    if (textView.getId() != -1) {
                        String textViewName = dt.c.getResources().getResourceEntryName(textView.getId());
                        String textViewValue = textView.getText().toString();
                        setModelValue(textViewName, textViewValue);
                    }
                }
            }
            result.addAll(viewArrayList);
        }
        return result;
    }

    private void setModelValue(String fieldName, String fieldValue) {
        String sFieldType = "";
        try {
            Field field = objModel.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            sFieldType = field.getType().toString();

            if (sFieldType.equalsIgnoreCase("int")) {
                int lValue = 0;
                if(!TextUtils.isEmpty(fieldValue)) lValue = Integer.parseInt(fieldValue);
                field.set(objModel, lValue);
            }
            if (sFieldType.equalsIgnoreCase("class java.lang.Long")) {
                Long iValue = Long.parseLong(fieldValue);
                field.set(objModel, iValue);
            }
            if (sFieldType.equalsIgnoreCase("class java.lang.String")) {
                field.set(objModel, fieldValue);
            }
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    //endregion

    //region UIFromModel
    public void UIFromModel(Object model) {

        String sFieldName = "", sFieldValue = "";
        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true); // if you want to modify private fields

                sFieldName = field.getName().toString();
                sFieldValue = field.get(model) != null ? field.get(model).toString() : "";

                int resID = dt.c.getResources().getIdentifier(sFieldName, "id", dt.c.getPackageName());
                View v = (View) ((Activity) dt.c).findViewById(resID);
                setValues(v, sFieldName, sFieldValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int g = 0;
    }

    public void UIFromModel(Object model, View view) {

        String sFieldName = "", sFieldValue = "";
        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true); // if you want to modify private fields

                sFieldName = field.getName().toString();
                sFieldValue = field.get(model) != null ? field.get(model).toString() : "";

                int resID = view.getResources().getIdentifier(sFieldName, "id", dt.c.getPackageName());
                View v = view.findViewById(resID);
                setValues(v, sFieldName, sFieldValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int g = 0;
    }

    private void setValues(View v, String sFieldName, String sFieldValue) {
        if (v instanceof TextView) {
            TextView et = (TextView) v;
            et.setText(sFieldValue);
        }
        if (v instanceof EditText) {
            EditText et = (EditText) v;
            et.setText(sFieldValue);
        }
        if (v instanceof Spinner) {
            Spinner sp = (Spinner) v;
            if(!TextUtils.isEmpty(sFieldValue))sp.setSelection(getSpinnerValueIndex(Integer.parseInt(sFieldValue), spinnerValueMap.get(sFieldName)));
        }
        if (v instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) v;
            if(!TextUtils.isEmpty(sFieldValue))checkBox.setChecked(getCheckboxState(Integer.parseInt(sFieldValue)));
        }
    }

    private int getSpinnerValueIndex(int val, SpinnerValue[] spinnerValue) {
        for (int i = 0; i < spinnerValue.length; i++) {
            if (Integer.parseInt(spinnerValue[i].valueText) == val) {
                return i;
            }
        }
        return -1;
    }

    private boolean getCheckboxState(int value) {
        boolean checkboxState = false;
        if(value == 1)checkboxState = true;
        return checkboxState;
    }
    //endregion

    public ArrayList<T> JsonToModel(List<T> list, Class<?> cls) {
        ArrayList<T> modelList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.setDateFormat("dd-MMM-yyyy");
            Gson gson = gsonBuilder.create();
            LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) list.get(i);
            JsonObject jsonObject = gson.toJsonTree(map).getAsJsonObject();
            //"AncVisitDate": "2018-04-17T00:00:00"
            Object obj = gson.fromJson(jsonObject.toString().replace("T00:00:00+06:00", "").replace("T00:00:00", ""), cls);
            modelList.add((T) obj);
        }
        return modelList;
    }

    public T JsonToModel(Object list, Class<?> cls) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        JsonObject jsonObject = gson.toJsonTree(list).getAsJsonObject();
        Object obj = gson.fromJson(jsonObject.toString().replace("T00:00:00+06:00", "").replace("T00:00:00", ""), cls);

        return (T) obj;
    }

    public Object getNewInstance(Class refModel){
        Object obj = null;
        try {
            Class<?> cls = refModel.forName(refModel.getCanonicalName());
            obj = cls.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String getFieldValueFromView(String resIdName){
        String value = "";
        int resId = dt.c.getResources().getIdentifier(resIdName, "id", dt.c.getPackageName());
        View view = ((Activity) dt.c).findViewById(resId);
        if(view!=null) value = getControlValue(view);
        return value;
    }

    public String getControlValue(View view) {
        String val = "";
        if (view != null) {
            if (view.getId() != -1) {
                if (view instanceof EditText) {
                    val = ((EditText) view).getText().toString();
                } else if (view instanceof TextView) {
                    val = ((TextView) view).getText().toString();
                } else if (view instanceof Spinner) {
                    SpinnerValue spinnerValue = (SpinnerValue) ((Spinner) view).getSelectedItem();
                    val = spinnerValue.valueText;
                }
            }
        }
        return val;
    }

}
