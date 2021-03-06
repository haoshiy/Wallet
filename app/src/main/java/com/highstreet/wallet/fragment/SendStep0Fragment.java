package com.highstreet.wallet.fragment;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.highstreet.wallet.R;
import com.highstreet.wallet.activities.SendActivity;
import com.highstreet.wallet.base.BaseChain;
import com.highstreet.wallet.base.BaseFragment;
import com.highstreet.wallet.dialog.Dialog_StarName_Confirm;
import com.highstreet.wallet.network.ApiClient;
import com.highstreet.wallet.network.req.ReqStarNameResolve;
import com.highstreet.wallet.network.res.ResIovStarNameResolve;
import com.highstreet.wallet.utils.WKey;
import com.highstreet.wallet.utils.WUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.highstreet.wallet.base.BaseChain.BAND_MAIN;
import static com.highstreet.wallet.base.BaseChain.BNB_MAIN;
import static com.highstreet.wallet.base.BaseChain.BNB_TEST;
import static com.highstreet.wallet.base.BaseChain.CERTIK_TEST;
import static com.highstreet.wallet.base.BaseChain.COSMOS_MAIN;
import static com.highstreet.wallet.base.BaseChain.IOV_MAIN;
import static com.highstreet.wallet.base.BaseChain.IOV_TEST;
import static com.highstreet.wallet.base.BaseChain.IRIS_MAIN;
import static com.highstreet.wallet.base.BaseChain.KAVA_MAIN;
import static com.highstreet.wallet.base.BaseChain.KAVA_TEST;
import static com.highstreet.wallet.base.BaseChain.OK_TEST;

public class SendStep0Fragment extends BaseFragment implements View.OnClickListener {
    public final static int SELECT_STAR_NAME_ADDRESS = 9102;

    private EditText        mAddressInput;
    private Button          mCancel, mNextBtn;
    private LinearLayout    mStarNameLayer;
    private LinearLayout    mBtnQr, mBtnPaste, mBtnHistory;

    public static SendStep0Fragment newInstance(Bundle bundle) {
        SendStep0Fragment fragment = new SendStep0Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_step0, container, false);
        mAddressInput = rootView.findViewById(R.id.receiver_account);
        mNextBtn = rootView.findViewById(R.id.btn_next);
        mCancel = rootView.findViewById(R.id.btn_cancel);
        mStarNameLayer = rootView.findViewById(R.id.starname_layer);

        mBtnQr = rootView.findViewById(R.id.btn_qr);
        mBtnPaste = rootView.findViewById(R.id.btn_paste);
        mBtnHistory = rootView.findViewById(R.id.btn_history);
        mBtnHistory.setVisibility(View.GONE);

        mCancel.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mBtnQr.setOnClickListener(this);
        mBtnPaste.setOnClickListener(this);
        mBtnHistory.setOnClickListener(this);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, rootView.getResources().getDisplayMetrics());
                rootView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = rootView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;
                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                if (alreadyOpen) {
                    mStarNameLayer.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStarNameLayer.setVisibility(View.VISIBLE);
                        }
                    },100);
                }
            }
        });
        mStarNameLayer.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextBtn)) {
            String userInput = mAddressInput.getText().toString().trim();

            if (WUtil.isValidStarName(userInput.toLowerCase())) {
                onCheckNameService(userInput.toLowerCase(), getSActivity().mBaseChain);
                return;
            }

            if (getSActivity().mAccount.address.equals(userInput)) {
                Toast.makeText(getContext(), R.string.error_self_sending, Toast.LENGTH_SHORT).show();
                return;
            }

            if (getSActivity().mBaseChain.equals(COSMOS_MAIN)) {
                if (userInput.startsWith("cosmos") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(IRIS_MAIN)) {
                if (userInput.startsWith("iaa") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(BNB_MAIN)) {
                if (userInput.startsWith("bnb") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(KAVA_MAIN) || getSActivity().mBaseChain.equals(KAVA_TEST)) {
                if (userInput.startsWith("kava") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(IOV_MAIN) || getSActivity().mBaseChain.equals(IOV_TEST)) {
                if (userInput.startsWith("star") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(BNB_TEST)) {
                if (userInput.startsWith("tbnb") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(BAND_MAIN)) {
                if (userInput.startsWith("band") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(OK_TEST)) {
                if (userInput.startsWith("okexchain") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(CERTIK_TEST)) {
                if (userInput.startsWith("certik") && WKey.isValidBech32(userInput)) {
                    getSActivity().mTagetAddress = userInput;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_address_target, Toast.LENGTH_SHORT).show();
                }

            }


        } else if (v.equals(mCancel)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mBtnQr)) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();

        } else if (v.equals(mBtnPaste)) {
            ClipboardManager clipboard = (ClipboardManager)getSActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                String userPaste = clipboard.getPrimaryClip().getItemAt(0).coerceToText(getSActivity()).toString().trim();
                if(TextUtils.isEmpty(userPaste)) {
                    Toast.makeText(getSActivity(), R.string.error_clipboard_no_data, Toast.LENGTH_SHORT).show();
                    return;
                }
                mAddressInput.setText(userPaste);
                mAddressInput.setSelection(mAddressInput.getText().length());

            } else {
                Toast.makeText(getSActivity(), R.string.error_clipboard_no_data, Toast.LENGTH_SHORT).show();
            }


        } else if (v.equals(mBtnHistory)) {
            Toast.makeText(getSActivity(), R.string.error_prepare, Toast.LENGTH_SHORT).show();

        }
    }

    private SendActivity getSActivity() {
        return (SendActivity)getBaseActivity();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_STAR_NAME_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                getSActivity().mStarName = data.getStringExtra("starname");
                getSActivity().mTagetAddress = data.getStringExtra("originAddress");
                getSActivity().onNextStep();
            }

        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() != null) {
                    mAddressInput.setText(result.getContents().trim());
                    mAddressInput.setSelection(mAddressInput.getText().length());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void onCheckNameService(String userInput, BaseChain chain) {
        ReqStarNameResolve req = new ReqStarNameResolve(userInput);
        ApiClient.getIovChain(getContext()).getStarnameAddress(req).enqueue(new Callback<ResIovStarNameResolve>() {
            @Override
            public void onResponse(Call<ResIovStarNameResolve> call, Response<ResIovStarNameResolve> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResIovStarNameResolve nameResolve = response.body();
                    final String matchAddress = nameResolve.getAddressWithChain(chain);
                    if (TextUtils.isEmpty(matchAddress)) {
                        Toast.makeText(getContext(), R.string.error_no_mattched_starname, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getSActivity().mAccount.address.equals(matchAddress)) {
                        Toast.makeText(getContext(), R.string.error_no_mattched_starname, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("starname", userInput);
                    bundle.putString("originAddress", matchAddress);
                    Dialog_StarName_Confirm dialog = Dialog_StarName_Confirm.newInstance(bundle);
                    dialog.setCancelable(true);
                    dialog.setTargetFragment(SendStep0Fragment.this, SELECT_STAR_NAME_ADDRESS);
                    getFragmentManager().beginTransaction().add(dialog, "dialog").commitNowAllowingStateLoss();

                } else {
                    Toast.makeText(getContext(), R.string.error_invalide_starname, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResIovStarNameResolve> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error_network_error, Toast.LENGTH_SHORT).show();
                return;
            }
        });

    }
}