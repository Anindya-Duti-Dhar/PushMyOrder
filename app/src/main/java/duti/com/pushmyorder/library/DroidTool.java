package duti.com.pushmyorder.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;



public class DroidTool {

    private Object objModel = null;
    public Context c;
    public static Context context;
    private View modalView = null;

    private Map<String, String[]> validationMap = new HashMap<String, String[]>();
    private HashMap<String, SpinnerValue[]> spinnerValueMap = new HashMap<>();

    public SweetAlert alert;
    public NetworkChecking droidNet;
    public Pref pref;
    public Ux ui;
    public Mapper mapper;
    public ActivityUI activity;
    public Tools tools;
    public Threading threading;
    public Db db;
    public dynamic dynamic;
    public CheckPermission permission;
    public Validation validation;
    public DateTimeManager dateTime;

    public DroidTool(Context context) {
        c = context;
        this.context = context;
        droidNet = new NetworkChecking(this);
        alert = new SweetAlert(this);
        pref = new Pref(this);
        ui = new Ux(this, spinnerValueMap);
        mapper = new Mapper(this, objModel, spinnerValueMap);
        activity = new ActivityUI(this);
        validation = new Validation(this, validationMap);
        tools = new Tools(this);
        threading = new Threading();
        dateTime = new DateTimeManager(this);
        db = new Db(c);
        dynamic = new dynamic();
        permission = new CheckPermission(this);
    }

    public void setModalView(View view) {
        modalView = view;
        ui.setModalView(modalView);
    }

    public View getModalView() {
        return modalView;
    }

    public void msg(String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    public String gStr(int resId) {
        return c.getString(resId);
    }

    public static String getStr(int resId) {
        return context.getString(resId);
    }

    public String extra() {
        String extraVal = "";
        Bundle extras = ((Activity) c).getIntent().getExtras();
        if (extras != null) {
            extraVal = extras.getString("key");
        }
        return extraVal;
    }

    public class dynamic {
        public void executeMethod(Object o, String methodName, Object val) {
            Method method = null;
            try {
                if (val.getClass() == Integer.class) {
                    method = o.getClass().getMethod(methodName, Integer.TYPE);
                    method.invoke(o, val);
                } else if (val.getClass() == Long.class){
                    method = o.getClass().getMethod(methodName, Long.TYPE);
                    method.invoke(o, val);
                } else {
                    method = o.getClass().getMethod(methodName, new Class[]{String.class});
                    method.invoke(o, new Object[]{val});
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public String getFieldValue(Object o, String fieldName) {
            return "";
        }

        public void setFieldValue(Object o, String fieldName, String value) {

        }
    }
}