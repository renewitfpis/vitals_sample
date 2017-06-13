package sg.lifecare.vitals2.ui;


import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import sg.lifecare.vitals2.R;
import timber.log.Timber;

public class BarcodeBottomSheetFragment extends BottomSheetDialogFragment
        implements ZXingScannerView.ResultHandler{

    public static final String TAG = BarcodeBottomSheetFragment.class.getSimpleName();

    private ZXingScannerView mScannerView;

    public static BarcodeBottomSheetFragment newInstance() {
        BarcodeBottomSheetFragment fragment = new BarcodeBottomSheetFragment();

        return fragment;
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
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.bottomsheet_barcode_scanner, null);

        mScannerView = new ZXingScannerView(getActivity());
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.content_frame);
        viewGroup.addView(mScannerView);
        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if ((behavior != null) && (behavior instanceof BottomSheetBehavior)) {
            Timber.d("behavior");
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallback);
            ((BottomSheetBehavior) behavior).setHideable(false);
            ((BottomSheetBehavior) behavior).setPeekHeight(1200);
        }

        //ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        Timber.d("Contents = " + result.getText() +
                ", Format = " + result.getBarcodeFormat().toString());

        Toast.makeText(getActivity(), "Contents = " + result.getText() +
                ", Format = " + result.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

    }
}
