package sg.lifecare.vitals2.ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;

import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import sg.lifecare.vitals2.R;
import timber.log.Timber;

public class BarcodeBottomSheetFragment extends BottomSheetDialogFragment
        implements ZXingScannerView.ResultHandler{

    public static final String TAG = BarcodeBottomSheetFragment.class.getSimpleName();

    public static final String PARAM_BARCODE = "barcode";
    public static final String PARAM_FORMAT = "format";

    private ZXingScannerView mScannerView;
    private BottomSheetDialog mDialog;
    private BottomSheetBehavior mBehavior;

    public static BarcodeBottomSheetFragment newInstance() {
        BarcodeBottomSheetFragment fragment = new BarcodeBottomSheetFragment();

        return fragment;
    }

    public static String getBarcode(Intent intent) {
        return intent.getStringExtra(PARAM_BARCODE);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.bottomsheet_barcode_scanner, null);

        mScannerView = new ZXingScannerView(getActivity());
        mScannerView.setAutoFocus(true);
        mScannerView.setShouldScaleToFill(false);
        FrameLayout viewGroup = (FrameLayout) view.findViewById(R.id.content_frame);
        viewGroup.addView(mScannerView);
        mDialog.setContentView(view);

        //CoordinatorLayout.LayoutParams params =
        //        (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        //CoordinatorLayout.Behavior behavior = params.getBehavior();
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        //if ((behavior != null) && (behavior instanceof BottomSheetBehavior)) {
        //    Timber.d("behavior");
        //    ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallback);
        //    ((BottomSheetBehavior) behavior).setHideable(false);

            //int height = view.getMeasuredHeight();
            //((BottomSheetBehavior) behavior).setPeekHeight(height);

            //((BottomSheetBehavior) behavior).setPeekHeight(1200);
        //}

        //ButterKnife.bind(this, view);

        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);
        return mDialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void handleResult(Result result) {
        Timber.d("Contents = " + result.getText() +
                ", Format = " + result.getBarcodeFormat().toString());

        Toast.makeText(getActivity(), "Contents = " + result.getText() +
                ", Format = " + result.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra(PARAM_BARCODE, result.getText());
        intent.putExtra(PARAM_FORMAT, result.getBarcodeFormat().toString());
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                    intent);
        }
        dismiss();

    }
}
