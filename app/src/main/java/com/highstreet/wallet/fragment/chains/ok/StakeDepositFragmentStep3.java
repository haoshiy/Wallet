package com.highstreet.wallet.fragment.chains.ok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.highstreet.wallet.R;
import com.highstreet.wallet.activities.chains.ok.StakeDepositActivity;
import com.highstreet.wallet.base.BaseFragment;
import com.highstreet.wallet.dialog.Dialog_Ok_Deposit_warning;
import com.highstreet.wallet.utils.WDp;

import java.math.BigDecimal;

import static com.highstreet.wallet.base.BaseChain.OK_TEST;

public class StakeDepositFragmentStep3 extends BaseFragment implements View.OnClickListener {
    public final static int SELECT_DEPOSIT_CHECK = 9106;

    private TextView        mDepositAmount;
    private TextView        mFeeAmount;
    private TextView        mMemo;
    private Button          mBeforeBtn, mConfirmBtn;
    private TextView        mDepositDenom, mFeeDenom;

    public static StakeDepositFragmentStep3 newInstance(Bundle bundle) {
        StakeDepositFragmentStep3 fragment = new StakeDepositFragmentStep3();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView       = inflater.inflate(R.layout.fragment_stake_deposit_3, container, false);
        mDepositAmount      = rootView.findViewById(R.id.deposit_amount);
        mDepositDenom       = rootView.findViewById(R.id.deposit_amount_denom);
        mFeeAmount          = rootView.findViewById(R.id.fees_amount);
        mFeeDenom           = rootView.findViewById(R.id.fees_denom);
        mMemo               = rootView.findViewById(R.id.memo);
        mBeforeBtn          = rootView.findViewById(R.id.btn_before);
        mConfirmBtn         = rootView.findViewById(R.id.btn_confirm);

        WDp.DpMainDenom(getContext(), getSActivity().mAccount.baseChain, mDepositDenom);
        WDp.DpMainDenom(getContext(), getSActivity().mAccount.baseChain, mFeeDenom);

        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onRefreshTab() {
        BigDecimal toDeleagteAmount = new BigDecimal(getSActivity().mToDepositCoin.amount);
        BigDecimal feeAmount = new BigDecimal(getSActivity().mFee.amount.get(0).amount);

        if (getSActivity().mBaseChain.equals(OK_TEST)) {
            mDepositAmount.setText(WDp.getDpAmount2(getContext(), toDeleagteAmount, 0, 8));
            mFeeAmount.setText(WDp.getDpAmount2(getContext(), feeAmount, 0, 8));

        }
        mMemo.setText(getSActivity().mMemo);
    }


    @Override
    public void onClick(View v) {
        if(v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            Dialog_Ok_Deposit_warning dialog = Dialog_Ok_Deposit_warning.newInstance();
            dialog.setTargetFragment(StakeDepositFragmentStep3.this, SELECT_DEPOSIT_CHECK);
            getFragmentManager().beginTransaction().add(dialog, "dialog").commitNowAllowingStateLoss();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEPOSIT_CHECK && resultCode == Activity.RESULT_OK) {
            getSActivity().onStartDeposit();
        }
    }

    private StakeDepositActivity getSActivity() {
        return (StakeDepositActivity)getBaseActivity();
    }
}
