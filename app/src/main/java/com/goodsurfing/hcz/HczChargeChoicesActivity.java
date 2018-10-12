package com.goodsurfing.hcz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.constants.What;
import com.goodsurfing.adpter.ChargeChoicesAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.beans.OrederBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.ChargeListActivity;
import com.goodsurfing.server.ChargeChoicesServer;
import com.goodsurfing.server.net.HczChargeListNet;
import com.goodsurfing.server.net.HczCreatOrderNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 好成长 套餐列表
 */
public class HczChargeChoicesActivity extends BaseActivity {

    private final static String TAG = "HczChargeChoicesActivity";

    protected static final int REFRESH = 100;
    protected static final int SEND = 101;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_charge_choices_lv)
    private ListView mPullListView;
    @ViewInject(R.id.title_layout)
    private View title_layout;
    private ChargeChoicesAdapter Adapter;
    public List<ChargeIDBean> listAdapter = new ArrayList<ChargeIDBean>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            listAdapter.clear();
            switch (msg.what) {
                case What.HTTP_REQUEST_CURD_SUCCESS:
                    ActivityUtil.dismissPopWindow();
                    listAdapter.addAll((List<ChargeIDBean>) msg.obj);
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    ActivityUtil.showPopWindow4Tips(HczChargeChoicesActivity.this, title_layout, false, msg.obj.toString() + "");
                    break;
                case SEND:
                    getChoicesInfo();
                    break;
            }
            Adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_choices);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("套餐服务选择");
        right.setVisibility(View.INVISIBLE);
        Adapter = new ChargeChoicesAdapter(this, R.layout.item_choices_cell, listAdapter);
        mPullListView.setAdapter(Adapter);
        mPullListView.setDivider(null);
        mPullListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                creatOrder(listAdapter.get(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.isNetWork)
            mHandler.sendEmptyMessageDelayed(SEND, 500);
        else
            Toast.makeText(this, "您的网络已断开，请检查网络", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }


    private void creatOrder(ChargeIDBean chargeIDBean) {
        HczCreatOrderNet creatOrderNet = new HczCreatOrderNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        OrederBean orederBean = (OrederBean) msg.obj;
                        Intent intent = new Intent(HczChargeChoicesActivity.this, HczChargeListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("packgeId", orederBean.getTransId()+"");
                        bundle.putString("NonceStr", orederBean.getNonceStr()+"");
                        bundle.putString("CHARGE_TYPE",orederBean.getTitle());
                        bundle.putString("CHARGE_SUM", orederBean.getPrice() + "元");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczChargeChoicesActivity.this, title, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        creatOrderNet.putParams(chargeIDBean.getName(), chargeIDBean.getId(), chargeIDBean.getMoney());
        creatOrderNet.sendRequest();

    }

    private void getChoicesInfo() {
        HczChargeListNet hczChargeListNet = new HczChargeListNet(this, mHandler);
        hczChargeListNet.sendRequest();
    }

}
