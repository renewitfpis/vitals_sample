package sg.lifecare.vitals2.ui.base;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import butterknife.Unbinder;
import sg.lifecare.vitals2.VitalsApp;
import sg.lifecare.framework.di.module.ActivityModule;
import sg.lifecare.framework.utils.NetworkUtils;
import sg.lifecare.vitals2.di.component.ActivityComponent;
import sg.lifecare.vitals2.di.component.DaggerActivityComponent;

public abstract  class BaseActivity extends AppCompatActivity
        implements MvpView, BaseFragment.Callback {

    private ActivityComponent mActivityComponent;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((VitalsApp)getApplication()).getComponent())
                .build();
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        super.onDestroy();
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    protected  abstract void setup();

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT);

        View view = snackbar.getView();
        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setText(message);
        snackbar.show();
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }
}
