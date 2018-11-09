package com.goodsurfing.addchild;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.adpter.ChildListAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.ExpireBean;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetBindVipNet;
import com.goodsurfing.server.net.HczGetChildsNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

public class ChildListActivity extends BaseActivity {

    protected static final int REFRESH = 100;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_about_tv)
    private TextView versionTv;

    @ViewInject(R.id.child_list_rc)
    private RecyclerView childRecyclerView;

    @ViewInject(R.id.activity_child_nodata)
    private View nodataView;

    private List<ChildBean> childs = new ArrayList<>();
    private ChildListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("孩子设备");
        right.setText("添加");
        right.setVisibility(View.VISIBLE);
        adapter = new ChildListAdapter(this, childs);
        childRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        childRecyclerView.setAdapter(adapter);
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildListActivity.this, ChildInfoActivity.class);
                intent.putExtra("childId", (int) v.getTag());
                startActivity(intent);
            }
        });
    }


    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.tv_title_right)
    public void onHeadRightClick(View view) {
        if(Constants.child==null) {
            startActivity(new Intent(this, AddChildActivity.class));
        }else {
            ActivityUtil.showPopWindow4Tips(this, nodataView, false, "暂时只支持绑定一个设备");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Constants.userId.equals("")) {
            getChildList();
            getBindVip();
        }
    }

    private void getBindVip() {
        HczGetBindVipNet getChildNet = new HczGetBindVipNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ExpireBean bean = (ExpireBean) msg.obj;
                        if (bean.isNotice()) {
                            showNoticeDialog(bean);
                        }
                        try {
                            Constants.dealTime = bean.getExpiredate()+"";
                            User user = CommonUtil.getUser(ChildListActivity.this);
                            user.setEditTime(Constants.dealTime);
                            CommonUtil.setUser(ChildListActivity.this, user);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        break;
                }
            }
        });
        getChildNet.putParams();
        getChildNet.sendRequest();
    }

    private void getChildList() {
        HczGetChildsNet getChildNet = new HczGetChildsNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        childs.clear();
                        childs.addAll(Constants.childs);
                        adapter.notifyDataSetChanged();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        break;
                }
                setNodataView();
            }
        });
        getChildNet.putParams();
        getChildNet.sendRequest();
    }

    private void showNoticeDialog(final ExpireBean about) {
        final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
        View view = View.inflate(this, R.layout.layout_vip_dialog, null);
        TextView content = (TextView) view.findViewById(R.id.layout_vip_content_tv);
        TextView rightView = (TextView) view.findViewById(R.id.layout_vip_time_tv);
        Button btn = view.findViewById(R.id.layout_vip_btn);
        content.setText(about.getMsg());
        rightView.setText("有效期至: "+about.getExpiredate());
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ActivityUtil.goMainActivity(ChildListActivity.this);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void setNodataView(){
        if(childs.size()==0){
            nodataView.setVisibility(View.VISIBLE);
        }else {
            nodataView.setVisibility(View.GONE);
        }
    }

}
