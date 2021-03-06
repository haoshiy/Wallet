package com.highstreet.wallet.activities.chains.starname;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.highstreet.wallet.R;
import com.highstreet.wallet.activities.PasswordCheckActivity;
import com.highstreet.wallet.base.BaseActivity;
import com.highstreet.wallet.base.BaseChain;
import com.highstreet.wallet.base.BaseFragment;
import com.highstreet.wallet.fragment.chains.starname.RenewStarName0Fragment;
import com.highstreet.wallet.fragment.chains.starname.RenewStarName1Fragment;
import com.highstreet.wallet.fragment.chains.starname.RenewStarName2Fragment;
import com.highstreet.wallet.fragment.chains.starname.RenewStarName3Fragment;
import com.highstreet.wallet.model.type.Fee;

import java.util.ArrayList;

import static com.highstreet.wallet.base.BaseConstant.CONST_PW_PURPOSE;
import static com.highstreet.wallet.base.BaseConstant.CONST_PW_TX_RENEW_ACCOUNT;
import static com.highstreet.wallet.base.BaseConstant.CONST_PW_TX_RENEW_DOMAIN;
import static com.highstreet.wallet.base.BaseConstant.IOV_MSG_TYPE_RENEW_DOMAIN;

public class ReNewStarNameActivity extends BaseActivity {

    private RelativeLayout  mRootView;
    private Toolbar         mToolbar;
    private TextView        mTitle;
    private ImageView       mIvStep;
    private TextView        mTvStep;
    private ViewPager       mViewPager;

    private RenewStarNamePageAdapter    mPageAdapter;
    public String                       mRenewType = "";
    public boolean                      mIsOpendomain;
    public String                       mToRenewDomain;
    public String                       mToRenewAccount;
    public long                         mValidTime;
    public String                       mMemo;
    public Fee                          mFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        mRootView   = findViewById(R.id.root_view);
        mToolbar    = findViewById(R.id.tool_bar);
        mTitle      = findViewById(R.id.toolbar_title);
        mIvStep     = findViewById(R.id.send_step);
        mTvStep     = findViewById(R.id.send_step_msg);
        mViewPager  = findViewById(R.id.view_pager);

        mRenewType = getIntent().getStringExtra("ToRenewType");
        mIsOpendomain = getIntent().getBooleanExtra("IsOpen", true);
        mToRenewDomain = getIntent().getStringExtra("ToRenewDomain");
        mToRenewAccount = getIntent().getStringExtra("ToRenewAccount");
        mValidTime = getIntent().getLongExtra("Time", -1);

        if (mRenewType.equals(IOV_MSG_TYPE_RENEW_DOMAIN)) {
            mTitle.setText(getString(R.string.str_renew_domain));
        } else {
            mTitle.setText(getString(R.string.str_renew_account));
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIvStep.setImageDrawable(getDrawable(R.drawable.step_4_img_1));
        mTvStep.setText(getString(R.string.str_renew_starname_step_0));

        mAccount = getBaseDao().onSelectAccount(getBaseDao().getLastUser());
        mBaseChain = BaseChain.getChain(mAccount.baseChain);

        mPageAdapter = new RenewStarNamePageAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                if(i == 0) {
                    mIvStep.setImageDrawable(getDrawable(R.drawable.step_4_img_1));
                    mTvStep.setText(getString(R.string.str_renew_starname_step_0));
                } else if (i == 1 ) {
                    mIvStep.setImageDrawable(getDrawable(R.drawable.step_4_img_2));
                    mTvStep.setText(getString(R.string.str_renew_starname_step_1));
                } else if (i == 2 ) {
                    mIvStep.setImageDrawable(getDrawable(R.drawable.step_4_img_3));
                    mTvStep.setText(getString(R.string.str_renew_starname_step_2));
                    mPageAdapter.mCurrentFragment.onRefreshTab();
                } else if (i == 3 ) {
                    mIvStep.setImageDrawable(getDrawable(R.drawable.step_4_img_4));
                    mTvStep.setText(getString(R.string.str_renew_starname_step_3));
                    mPageAdapter.mCurrentFragment.onRefreshTab();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
        mViewPager.setCurrentItem(0);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideKeyboard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAccount == null) finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        onHideKeyboard();
        if(mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
        } else {
            super.onBackPressed();
        }
    }

    public void onNextStep() {
        if(mViewPager.getCurrentItem() < mViewPager.getChildCount()) {
            onHideKeyboard();
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }

    public void onBeforeStep() {
        if(mViewPager.getCurrentItem() > 0) {
            onHideKeyboard();
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
        } else {
            onBackPressed();
        }
    }

    public void onRenewStarName() {
        Intent intent = new Intent(ReNewStarNameActivity.this, PasswordCheckActivity.class);
        if (mRenewType.equals(IOV_MSG_TYPE_RENEW_DOMAIN)) {
            intent.putExtra(CONST_PW_PURPOSE, CONST_PW_TX_RENEW_DOMAIN);
        } else {
            intent.putExtra(CONST_PW_PURPOSE, CONST_PW_TX_RENEW_ACCOUNT);
        }
        intent.putExtra("domain", mToRenewDomain);
        intent.putExtra("name", mToRenewAccount);
        intent.putExtra("memo", mMemo);
        intent.putExtra("fee", mFee);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out);
    }

    private class RenewStarNamePageAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> mFragments = new ArrayList<>();
        private BaseFragment mCurrentFragment;

        public RenewStarNamePageAdapter(FragmentManager fm) {
            super(fm);
            mFragments.clear();
            mFragments.add(RenewStarName0Fragment.newInstance(null));
            mFragments.add(RenewStarName1Fragment.newInstance(null));
            mFragments.add(RenewStarName2Fragment.newInstance(null));
            mFragments.add(RenewStarName3Fragment.newInstance(null));
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((BaseFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        public BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public ArrayList<BaseFragment> getFragments() {
            return mFragments;
        }

    }
}
