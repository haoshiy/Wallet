package com.highstreet.wallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.highstreet.wallet.R;
import com.highstreet.wallet.activities.HtlcSendActivity;
import com.highstreet.wallet.base.BaseChain;
import com.highstreet.wallet.base.BaseFragment;
import com.highstreet.wallet.dialog.Dialog_Htlc_Warning;
import com.highstreet.wallet.model.type.Fee;
import com.highstreet.wallet.utils.WDp;
import com.highstreet.wallet.utils.WLog;
import com.highstreet.wallet.utils.WUtil;

import java.math.BigDecimal;

import static com.highstreet.wallet.base.BaseConstant.FEE_BEP3_RELAY_FEE;
import static com.highstreet.wallet.base.BaseConstant.TOKEN_HTLC_BINANCE_BNB;
import static com.highstreet.wallet.base.BaseConstant.TOKEN_HTLC_BINANCE_TEST_BNB;
import static com.highstreet.wallet.base.BaseConstant.TOKEN_HTLC_BINANCE_TEST_BTC;
import static com.highstreet.wallet.base.BaseConstant.TOKEN_HTLC_KAVA_BNB;
import static com.highstreet.wallet.base.BaseConstant.TOKEN_HTLC_KAVA_TEST_BNB;

public class HtlcSendStep3Fragment extends BaseFragment implements View.OnClickListener {
    public final static int SELECT_HTLC_CONFIRM = 9104;

    private CardView    mSendCard;
    private ImageView   mSendIcon;
    private TextView    mSendAmountTv, mSendDenomTv;
    private TextView    mSendFeeAmountTv, mSendFeeDenomTv;
    private TextView    mReceiveChainTv, mReceiveAddressTv;
    private CardView    mClaimCard;
    private ImageView   mClaimIcon;
    private TextView    mReceiveAmountTv, mReceiveAmountDenomTv;
    private TextView    mRelayFeeAmountTv, mRelayFeeAmountDenomTv;
    private TextView    mClaimFeeAmountTv, mClaimFeeDenomTv;
    private TextView    mClaimAddressTv;
    private Button      mBeforeBtn, mConfirmBtn;

    private int         mDecimal = 8;
    public String       mToSwapDenom;

    public static HtlcSendStep3Fragment newInstance(Bundle bundle) {
        HtlcSendStep3Fragment fragment = new HtlcSendStep3Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_htlc_send_step3, container, false);
        mSendCard = rootView.findViewById(R.id.send_swap_card);
        mSendIcon = rootView.findViewById(R.id.send_icon);
        mSendAmountTv = rootView.findViewById(R.id.send_amount);
        mSendDenomTv = rootView.findViewById(R.id.send_amount_denom);
        mSendFeeAmountTv = rootView.findViewById(R.id.send_fee);
        mSendFeeDenomTv = rootView.findViewById(R.id.send_fee_denom);
        mReceiveChainTv = rootView.findViewById(R.id.recipient_chain);
        mReceiveAddressTv = rootView.findViewById(R.id.recipient_address);
        mClaimCard = rootView.findViewById(R.id.claim_swap_card);
        mClaimIcon = rootView.findViewById(R.id.claim_icon);
        mReceiveAmountTv = rootView.findViewById(R.id.receive_amount);
        mReceiveAmountDenomTv = rootView.findViewById(R.id.receive_amount_denom);
        mRelayFeeAmountTv = rootView.findViewById(R.id.relay_fee);
        mRelayFeeAmountDenomTv = rootView.findViewById(R.id.relay_fee_denom);
        mClaimFeeAmountTv = rootView.findViewById(R.id.claim_fee);
        mClaimFeeDenomTv = rootView.findViewById(R.id.claim_fee_denom);
        mClaimAddressTv = rootView.findViewById(R.id.claim_address);
        mBeforeBtn = rootView.findViewById(R.id.btn_before);
        mConfirmBtn = rootView.findViewById(R.id.btn_confirm);
        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onRefreshTab() {
        mToSwapDenom = getSActivity().mToSwapDenom;
        Fee sendFee = getSActivity().onInitSendFee();
        Fee claimFee = getSActivity().onInitClaimFee();

        BigDecimal toSendAmount         = new BigDecimal(getSActivity().mToSendCoins.get(0).amount);
        BigDecimal sendFeeAmount        = new BigDecimal(sendFee.amount.get(0).amount);
        BigDecimal claimFeeAmount       = new BigDecimal(claimFee.amount.get(0).amount);

        // set send card view
        mSendIcon.setColorFilter(WDp.getChainColor(getContext(), getSActivity().mBaseChain), android.graphics.PorterDuff.Mode.SRC_IN);
        if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN) || getSActivity().mBaseChain.equals(BaseChain.BNB_TEST)) {
            if (mToSwapDenom.equals(TOKEN_HTLC_BINANCE_BNB) || mToSwapDenom.equals(TOKEN_HTLC_BINANCE_TEST_BNB)) {
                mSendDenomTv.setText(getString(R.string.str_bnb_c));
                mSendDenomTv.setTextColor(getResources().getColor(R.color.colorBnb));
            } else if (mToSwapDenom.equals(TOKEN_HTLC_BINANCE_TEST_BTC)) {
                mSendDenomTv.setText(getString(R.string.str_btc_c));
            }
            WDp.DpMainDenom(getContext(), getSActivity().mBaseChain.getChain(), mSendFeeDenomTv);

            mSendAmountTv.setText(WDp.getDpAmount2(getContext(), toSendAmount, 0, 8));
            mSendFeeAmountTv.setText(WDp.getDpAmount2(getContext(), sendFeeAmount, 0, 8));
            WLog.w("mRecipientChain " + getSActivity().mRecipientChain.getChain());
            mReceiveChainTv.setText(WDp.getDpChainName(getContext(), getSActivity().mRecipientChain));
            mReceiveAddressTv.setText(getSActivity().mRecipientAccount.address);

        } else if (getSActivity().mBaseChain.equals(BaseChain.KAVA_MAIN) || getSActivity().mBaseChain.equals(BaseChain.KAVA_TEST)) {
            mDecimal = WUtil.getKavaCoinDecimal(getSActivity().mToSwapDenom);
            mSendDenomTv.setText(getSActivity().mToSwapDenom.toUpperCase());
            WDp.DpMainDenom(getContext(), getSActivity().mBaseChain.getChain(), mSendFeeDenomTv);

            mSendAmountTv.setText(WDp.getDpAmount2(getContext(), toSendAmount, mDecimal, mDecimal));
            mSendFeeAmountTv.setText(WDp.getDpAmount2(getContext(), sendFeeAmount, 6, 6));
            mReceiveChainTv.setText(WDp.getDpChainName(getContext(), getSActivity().mRecipientChain));
            mReceiveAddressTv.setText(getSActivity().mRecipientAccount.address);
        }

        //set claim card view
        mClaimIcon.setColorFilter(WDp.getChainColor(getContext(), getSActivity().mRecipientChain), android.graphics.PorterDuff.Mode.SRC_IN);
        if (getSActivity().mRecipientChain.equals(BaseChain.BNB_MAIN) || getSActivity().mRecipientChain.equals(BaseChain.BNB_TEST)) {
            mReceiveAmountDenomTv.setText(getSActivity().mToSwapDenom.toUpperCase());
            mRelayFeeAmountDenomTv.setText(getSActivity().mToSwapDenom.toUpperCase());
            if (mToSwapDenom.equals(TOKEN_HTLC_KAVA_BNB) || mToSwapDenom.equals(TOKEN_HTLC_KAVA_TEST_BNB)) {
                mReceiveAmountDenomTv.setTextColor(getResources().getColor(R.color.colorBnb));
                mRelayFeeAmountDenomTv.setTextColor(getResources().getColor(R.color.colorBnb));
            }
            WDp.DpMainDenom(getContext(), getSActivity().mRecipientChain.getChain(), mClaimFeeDenomTv);

            BigDecimal relayFee = new BigDecimal(FEE_BEP3_RELAY_FEE).movePointRight(mDecimal);
            mReceiveAmountTv.setText(WDp.getDpAmount2(getContext(), toSendAmount.subtract(relayFee), mDecimal, mDecimal));
            mRelayFeeAmountTv.setText(WDp.getDpAmount2(getContext(), relayFee, mDecimal, mDecimal));
            mClaimFeeAmountTv.setText(WDp.getDpAmount2(getContext(), claimFeeAmount, 0, mDecimal));
            mClaimAddressTv.setText(getSActivity().mRecipientAccount.address);


        } else if (getSActivity().mRecipientChain.equals(BaseChain.KAVA_MAIN) || getSActivity().mRecipientChain.equals(BaseChain.KAVA_TEST)) {
            if (mToSwapDenom.equals(TOKEN_HTLC_BINANCE_BNB) || mToSwapDenom.equals(TOKEN_HTLC_BINANCE_TEST_BNB)) {
                mReceiveAmountDenomTv.setText(getString(R.string.str_bnb_c));
                mRelayFeeAmountDenomTv.setText(getString(R.string.str_bnb_c));

            } else if (mToSwapDenom.equals(TOKEN_HTLC_BINANCE_TEST_BTC)) {
                mReceiveAmountDenomTv.setText(getString(R.string.str_btc_c));
                mRelayFeeAmountDenomTv.setText(getString(R.string.str_btc_c));
            }
            WDp.DpMainDenom(getContext(), getSActivity().mRecipientChain.getChain(), mClaimFeeDenomTv);

            BigDecimal relayFee = new BigDecimal(FEE_BEP3_RELAY_FEE);
            mReceiveAmountTv.setText(WDp.getDpAmount2(getContext(), toSendAmount.subtract(relayFee), 0, 8));
            mRelayFeeAmountTv.setText(WDp.getDpAmount2(getContext(), relayFee, 0, 8));
            mClaimFeeAmountTv.setText(WDp.getDpAmount2(getContext(), claimFeeAmount, 6, 6));
            mClaimAddressTv.setText(getSActivity().mRecipientAccount.address);

        }

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            Dialog_Htlc_Warning dialog = Dialog_Htlc_Warning.newInstance();
            dialog.setTargetFragment(HtlcSendStep3Fragment.this, SELECT_HTLC_CONFIRM);
            getFragmentManager().beginTransaction().add(dialog, "dialog").commitNowAllowingStateLoss();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_HTLC_CONFIRM && resultCode == Activity.RESULT_OK) {
            getSActivity().onStartHtlcSend();

        }
    }

    private HtlcSendActivity getSActivity() {
        return (HtlcSendActivity)getBaseActivity();
    }
}
