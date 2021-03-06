
package com.bs.bsims.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bs.bsims.R;
import com.bs.bsims.adapter.ApprovlaNewIdeaAdapter;
import com.bs.bsims.adapter.HeadAdapter;
import com.bs.bsims.application.BSApplication;
import com.bs.bsims.constant.Constant;
import com.bs.bsims.fragment.ApprovalViewFragment;
import com.bs.bsims.model.EmployeeVO;
import com.bs.bsims.model.OvertimeDetailResultVO;
import com.bs.bsims.model.OvertimeDetailVO;
import com.bs.bsims.model.SuppliesItemVO;
import com.bs.bsims.utils.CommonUtils;
import com.bs.bsims.utils.DateUtils;
import com.bs.bsims.utils.HttpClientUtil;
import com.bs.bsims.utils.UrlUtil;
import com.bs.bsims.view.BSCircleImageView;
import com.bs.bsims.view.BSGridView;
import com.bs.bsims.view.BSMoveLayout;
import com.bs.bsims.view.BSMoveLayout.DeleteListeren;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApprovalSuppliesDetailActivity extends BaseActivity {
    private TextView mPersonTitle01, mPersonTitle02, mPersonTitle03, mPersonTitle04;
    private BSCircleImageView mHeadIcon;

    private String mUid;
    private String mAlid;
    private String mType;

    private BSGridView mApproverGv, mInformGv;
    private HeadAdapter mApproverAdapter, mInformAdapter;
    private TextView mApproverTv, mInformTv;
    private LinearLayout mApproverLayout, mInformLayout;

    private ListView mListView;
    private ApprovlaNewIdeaAdapter mApprovlaIdeaAdapter;
    private TextView mApprovalIdeaTv;
    private ApprovalViewFragment mApprovalViewFragment;
    private int mCurrent = 0;

    private OvertimeDetailVO mOvertimeDetialVO;
    private TextView mStratTimeTv, mFeeTotal, mContentReason;

    private ListView mListViewItem;
    private MyAdapter adapter;
    private String mMessageid;
    private OvertimeDetailResultVO mOvertimeDetailResultVO;

    @Override
    public void baseSetContentView() {
        View layout = View.inflate(this, R.layout.approval_supplies_detail, null);
        mContentLayout.addView(layout);
    }

    @Override
    public boolean getDataResult() {
        return getData();
    }

    @Override
    public void updateUi() {
    }

    @Override
    public void executeSuccess() {
        super.executeSuccess();
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = CommonUtils.initImageLoaderOptions();
        imageLoader.displayImage(mOvertimeDetialVO.getHeadpic(), mHeadIcon, options);
        mHeadIcon.setUserId(mOvertimeDetialVO.getUserid());// 获取头像对应的用户ID
        mHeadIcon.setUserName(mOvertimeDetialVO.getFullname());
        mHeadIcon.setUrl(mOvertimeDetialVO.getHeadpic());
        mPersonTitle01.setText(mOvertimeDetialVO.getFullname());
        mPersonTitle02.setText(mOvertimeDetialVO.getPname());

        long time = Long.parseLong(mOvertimeDetialVO.getAddtime()) * 1000;
        mPersonTitle03.setText(DateUtils.parseDate(time));
        mPersonTitle04.setText(mOvertimeDetialVO.getDname());
        mStratTimeTv.setText(DateUtils.parseDate(Long.valueOf(mOvertimeDetialVO.getAtime()) * 1000));
        mFeeTotal.setText(mOvertimeDetialVO.getTotalprice());
        mContentReason.setText(mOvertimeDetialVO.getReason());

        if (mOvertimeDetialVO.getAppUser() != null) {
            mApproverAdapter.updateData(mOvertimeDetialVO.getAppUser());
            // mApproverLayout.setVisibility(View.VISIBLE);
            // mApproverTv.setVisibility(View.VISIBLE);
            // mApproverAdapter.setApproval(true);// 圆圈显示类型判断
            // mApproverAdapter.setStatus(mLeaveDetailVO.getStatus());

            // 刷新状态列表
            for (int i = 0; i < mOvertimeDetialVO.getAppUser().size(); i++) {
                EmployeeVO vo = mOvertimeDetialVO.getAppUser().get(i);
                if ("0".equals(vo.getStatus())) {
                    mCurrent++;
                }
            }

            if (mOvertimeDetialVO.getOpinion() != null) {
                List<EmployeeVO> appUserList = new ArrayList<EmployeeVO>();
                appUserList.addAll(mOvertimeDetialVO.getAppUser());
                for (int i = 0; i < mOvertimeDetialVO.getAppUser().size(); i++) {
                    for (int j = 0; j < mOvertimeDetialVO.getOpinion().size(); j++) {
                        if (mOvertimeDetialVO.getOpinion().get(j).getUserid().equals(mOvertimeDetialVO.getAppUser().get(i).getUserid())) {
                            appUserList.remove(i);
                            appUserList.add(i, mOvertimeDetialVO.getOpinion().get(j));
                            continue;
                        }
                    }
                }
                mApprovlaIdeaAdapter.updateData(appUserList);
                mApprovalIdeaTv.setVisibility(View.VISIBLE);
                mApprovlaIdeaAdapter.setApprovalType(mOvertimeDetialVO.getGenre());
                mApprovlaIdeaAdapter.setApprovalId(mOvertimeDetialVO.getMaid());
            }
            else{
                mApprovlaIdeaAdapter.updateData(mOvertimeDetialVO.getAppUser());
                mApprovalIdeaTv.setVisibility(View.VISIBLE);
                mApprovlaIdeaAdapter.setApprovalType(mOvertimeDetialVO.getGenre());
                mApprovlaIdeaAdapter.setApprovalId(mOvertimeDetialVO.getMaid());
            }
            
            if(!BSApplication.getInstance().getUserId().equals(mOvertimeDetialVO.getUserid())){
                mApprovlaIdeaAdapter.setViewCui(true);//不显示
            }
            else{
                mApprovlaIdeaAdapter.setViewCui(false);//显示
            }
        }
        if (mOvertimeDetialVO.getInsUser() != null) {
            mInformTv.setVisibility(View.VISIBLE);
            mInformLayout.setVisibility(View.VISIBLE);
            mInformAdapter.updateData(mOvertimeDetialVO.getInsUser());
        } else {
            mInformTv.setVisibility(View.GONE);
            mInformLayout.setVisibility(View.GONE);
        }
        if (mOvertimeDetialVO.getMaterial() != null) {
            adapter.list = mOvertimeDetialVO.getMaterial();
            adapter.notifyDataSetChanged();
        }

      

        try {
            if ("1".equals(mOvertimeDetialVO.getApproval())) {
                // 显示界面
                mApprovalViewFragment = new ApprovalViewFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.bottom_view, mApprovalViewFragment);
                transaction.commitAllowingStateLoss();
                mApprovalViewFragment.setApprovalid(mAlid);
                mApprovalViewFragment.setType(mType);
                mApprovalViewFragment.setCount(mCurrent);
            }
        } catch (Exception e) {
        }
        // 判断审核状态
        CommonUtils.setApprovalImg(Integer.parseInt(mOvertimeDetialVO.getStatus()), (ImageView) findViewById(R.id.approval_status), this);
    }

    @Override
    public void executeFailure() {
        if (mOvertimeDetailResultVO == null) {
            super.executeSuccess();
            return;
        } else {
            super.setNonetIcon(mOvertimeDetailResultVO.getRetinfo());
        }
    }

    @Override
    public void initView() {
        mHeadIcon = (BSCircleImageView) findViewById(R.id.head_icon);
        mPersonTitle01 = (TextView) findViewById(R.id.person_title01);
        mPersonTitle02 = (TextView) findViewById(R.id.person_title02);
        mPersonTitle03 = (TextView) findViewById(R.id.person_title03);
        mPersonTitle04 = (TextView) findViewById(R.id.person_title04);
        mStratTimeTv = (TextView) findViewById(R.id.start_time_tv);

        mFeeTotal = (TextView) findViewById(R.id.fee_total);
        mContentReason = (TextView) findViewById(R.id.item_use);

        mApproverGv = (BSGridView) findViewById(R.id.approver_gv);
        mInformGv = (BSGridView) findViewById(R.id.inform_gv);
        mApproverAdapter = new HeadAdapter(this, false);
        mInformAdapter = new HeadAdapter(this, false, true);
        mApproverGv.setAdapter(mApproverAdapter);
        mInformGv.setAdapter(mInformAdapter);

        mApproverTv = (TextView) findViewById(R.id.approver_tv);
        mInformTv = (TextView) findViewById(R.id.inform_people_tv);
        mApproverLayout = (LinearLayout) findViewById(R.id.approver_layout);
        mInformLayout = (LinearLayout) findViewById(R.id.inform_people_layout);

        initData();

        mListView = (ListView) findViewById(R.id.list_view);
        mApprovlaIdeaAdapter = new ApprovlaNewIdeaAdapter(this);
        mListView.setAdapter(mApprovlaIdeaAdapter);
        mApprovalIdeaTv = (TextView) findViewById(R.id.approval_idea_tv);

        mListViewItem = (ListView) findViewById(R.id.listview);
        adapter = new MyAdapter();
        mListViewItem.setAdapter(adapter);
    }

    @Override
    public void bindViewsListener() {

    }

    public void initData() {
        Intent intent = getIntent();
        mUid = intent.getStringExtra("uid");
        mAlid = intent.getStringExtra("alid");
        mType = intent.getStringExtra("type");
        mTitleTv.setText("物资详情");
        mMessageid = intent.getStringExtra("messageid");
    }

    public boolean getData() {

        try {

            String strUlr = UrlUtil.getSuppliesDetailUrl(Constant.APPROVAL_SUPPLIES_DETAIL_URL, mUid, mAlid, mMessageid);
            String jsonStr = HttpClientUtil.get(strUlr, Constant.ENCODING).trim();
            Gson gson = new Gson();
            mOvertimeDetailResultVO = gson.fromJson(jsonStr, OvertimeDetailResultVO.class);

            if (Constant.RESULT_CODE.endsWith(mOvertimeDetailResultVO.getCode())) {
                mOvertimeDetialVO = mOvertimeDetailResultVO.getArray();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("isread", "1");
                map.put("approvalid", mAlid);
                CommonUtils.sendBroadcast(this, Constant.HOME_MSG, map);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class MyAdapter extends BaseAdapter {
        private List<SuppliesItemVO> list;

        public MyAdapter() {
            list = new ArrayList<SuppliesItemVO>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                // convertView = new BSMoveLayout(ApprovalSuppliesDetailActivity.this, false);
                View view = View.inflate(ApprovalSuppliesDetailActivity.this, R.layout.lv_supplies_itme, null);
                convertView = new BSMoveLayout(ApprovalSuppliesDetailActivity.this, false, view, listeren);
                holder.text01 = (TextView) convertView.findViewById(R.id.text01);
                holder.text02 = (TextView) convertView.findViewById(R.id.text02);
                holder.text03 = (TextView) convertView.findViewById(R.id.text03);
                holder.text04 = (TextView) convertView.findViewById(R.id.text04);
                holder.delete = (TextView) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SuppliesItemVO vo = list.get(position);
            holder.text01.setText(vo.getName());
            holder.text02.setText(vo.getPrice());
            holder.text03.setText(vo.getNum());
            holder.text04.setText(vo.getUnit());
            return convertView;
        }

        DeleteListeren listeren = new DeleteListeren() {

            @Override
            public void deleteItem(int position) {

            }
        };

        class ViewHolder {
            private TextView delete, text01, text02, text03, text04;
        }
    }

}
