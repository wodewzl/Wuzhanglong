
package com.bs.bsims.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bs.bsims.R;
import com.bs.bsims.adapter.ScheduleTrendAdapter;
import com.bs.bsims.application.BSApplication;
import com.bs.bsims.constant.Constant;
import com.bs.bsims.model.ScheduleTrendVO;
import com.bs.bsims.utils.HttpClientUtil;
import com.bs.bsims.utils.ThreadUtil;
import com.bs.bsims.view.BSIndexEditText;
import com.bs.bsims.view.BSRefreshListView;
import com.bs.bsims.view.BSRefreshListView.OnRefreshListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleTrendActivity extends BaseActivity implements OnClickListener {

    private BSRefreshListView mRefreshListView;
    private ScheduleTrendAdapter mTrendAdapter;
    private ScheduleTrendVO mTrendVO;

    // 下拉上拉刷新部分
    private View mFootLayout;
    private TextView mMoreTextView;
    private ProgressBar mProgressBar;
    private int mState = 0; // 0为首次,1为上拉刷新 ，2为下拉刷新

    private boolean mFlag = true;
    private String mFristid, mLastid;
    private List<View> mViewList;// 筛选选中的布局
    private BSIndexEditText mBSBsIndexEditText;
    private Boolean canClickFlag = true;// 解决连续点“更多”可能会出现的异常
    private String mUnRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void baseSetContentView() {
        View.inflate(this, R.layout.schedule_trend, mContentLayout);
    }

    @Override
    public boolean getDataResult() {
        return getData();
    }

    @Override
    public void updateUi() {

    }

    @Override
    public void initView() {
        mTitleTv.setText("日程动态");
        mOkTv.setText("发布");
        mRefreshListView = (BSRefreshListView) findViewById(R.id.lv_refresh);
        mTrendAdapter = new ScheduleTrendAdapter(this);
        mRefreshListView.setAdapter(mTrendAdapter);
        initData();
        initFoot();
        setLeadClass("ScheduleTrendActivity");
    }

    public void initData() {
        Intent intent = this.getIntent();
        mUnRead = intent.getStringExtra("unread");
    }

    @Override
    public void bindViewsListener() {
        mRefreshListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mTrendAdapter.mList.size() > 0) {
                    match(1, mTrendAdapter.mList.get(0).getId());
                } else {
                    mFristid = "";
                    match(1, "");
                }
            }
        });
        mFootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClickFlag) {
                    canClickFlag = false;
                    mMoreTextView.setText("正在加载...");
                    mProgressBar.setVisibility(View.VISIBLE);
                    match(2, mTrendAdapter.mList.get(mTrendAdapter.mList.size() - 1).getId());
                }
            }
        });

        mOkTv.setOnClickListener(this);
    }

    @Override
    public void executeSuccess() {
        super.executeSuccess();

        if (1 == mState) {
            mTrendAdapter.updateDataFrist(mTrendVO.getArray());
        } else if (2 == mState) {
            mTrendAdapter.updateDataLast(mTrendVO.getArray());
        } else {
            mTrendAdapter.updateData(mTrendVO.getArray());
        }

        mTrendAdapter.notifyDataSetChanged();
        mRefreshListView.onRefreshComplete();
        if ("".equals(mFristid) || mState != 1) {
            footViewIsVisibility();
        }
        mState = 0;
        canClickFlag = true;
    }

    @Override
    public void executeFailure() {

        // 列表展示的时候不能调用父类
        super.isRequestFinish();
        mTrendAdapter.notifyDataSetChanged();
        mRefreshListView.onRefreshComplete();
        footViewIsVisibility();

        // 不适合只隐藏列表，适合隐藏整个布局;
        if (mTrendVO == null) {
            super.showNoNetView();
        } else {
            if (mState == 0) {
                mTrendAdapter.updateData(new ArrayList<ScheduleTrendVO>());
                mFootLayout.setVisibility(View.GONE);
            }
        }
        mState = 0;
        canClickFlag = true;
    }

    public void footViewIsVisibility() {
        if (mTrendVO == null || mTrendVO.getCount() == null) {
            return;
        }
        if (Integer.parseInt(mTrendVO.getCount()) <= 15) {
            mFootLayout.setVisibility(View.GONE);
        } else {
            mFootLayout.setVisibility(View.VISIBLE);
            mMoreTextView.setText("更多");
            mProgressBar.setVisibility(View.GONE);
        }
    }

    // 加载更多数据
    public void initFoot() {
        mFootLayout = LayoutInflater.from(this).inflate(R.layout.listview_bottom_more, null);
        mMoreTextView = (TextView) mFootLayout.findViewById(R.id.txt_loading);
        mMoreTextView.setText("更多");
        mProgressBar = (ProgressBar) mFootLayout.findViewById(R.id.progressBar);
        mFootLayout.setVisibility(View.GONE);
        mRefreshListView.addFooterView(mFootLayout);
    }

    public boolean getData() {
        try {
            Gson gson = new Gson();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userid", BSApplication.getInstance().getUserId());
            if (0 == mState) {
                mFristid = "";
                mLastid = "";
            }
            map.put("firstid", mFristid);
            map.put("lastid", mLastid);
            map.put("unread", mUnRead);
            map.put(Constant.FTOKEN_PARAMS, BSApplication.getInstance().getmCompany());
            String jsonStrList = HttpClientUtil.getRequest(BSApplication.getInstance().getHttpTitle() + Constant.CRM_SCHEDULE_TREND, map);
            mTrendVO = gson.fromJson(jsonStrList, ScheduleTrendVO.class);
            if (Constant.RESULT_CODE.equals(mTrendVO.getCode())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void match(int key, String value) {
        switch (key) {
            case 1:
                mFristid = value;
                mLastid = "";
                mState = 1;
                break;
            case 2:
                mLastid = value;
                mFristid = "";
                mState = 2;
                break;
            default:
                break;
        }
        mRefreshListView.changeHeaderViewByState(BSRefreshListView.REFRESHING);
        new ThreadUtil(this, this).start();
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = new Intent();
        intent.setClass(this, ScheduleActivity.class);// 日程
        this.startActivity(intent);
    }
}
