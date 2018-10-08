package com.goodsurfing.addchild;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BasePhotoActivity;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.map.ChangePictureActivity;
import com.goodsurfing.map.SettingChildrenNameActivity;
import com.goodsurfing.server.net.HczSetChildInfoNet;
import com.goodsurfing.server.net.HczUnBindChildNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ChildInfoActivity extends BasePhotoActivity {

    protected static final int REFRESH = 100;

    private static final int request_Code = 1;
    private static final int request_name_Code = 2;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_info_children_et_name)
    private EditText nameEt;


    @ViewInject(R.id.activity_info_children_head)
    private RoundImageView headIv;

    @ViewInject(R.id.activity_info_children_delete_phone)
    private ImageView deleteIv;

    @ViewInject(R.id.activity_info_children_tv_phone)
    private TextView phoneTv;
    @ViewInject(R.id.activity_info_children_tv_yy)
    private TextView yunyTv;
    @ViewInject(R.id.activity_info_children_save_btn)
    private TextView saveBtn;
//    @ViewInject(R.id.activity_info_children_tv_address)
//    private TextView addressTv;

    @ViewInject(R.id.title_layout)
    private View title_layout;
    private int userIndex;
    private int id;
    private int oldId;
    private Dialog dialog;
    private Dialog deleteChildrenDialog;
    private ChildBean friend;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_children);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("孩子手机设置");
        right.setVisibility(View.GONE);
        headIv.setmBorderOutsideColor(0xFFcccccc);
        headIv.setmBorderThickness(15);
        userIndex = getIntent().getExtras().getInt("childId");
        friend = Constants.childs.get(userIndex);
        id = oldId = friend.getClientDeviceId();
        phoneTv.setText(friend.getMobile());
        nameEt.setText(friend.getName());
        yunyTv.setText(friend.getDevice());
        headIv.setBackgroundResource(Constants.showIds[friend.getImg()]);
        saveBtn.setEnabled(false);
    }

    @OnClick(R.id.activity_info_children_et_name)
    private void onClickEditName(View view) {
        Intent intent = new Intent(this, SettingChildrenNameActivity.class);
        intent.putExtra("name", friend.getName());
        startActivityForResult(intent, request_name_Code);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    @OnClick(R.id.activity_info_children_delete_btn)
    private void onClickDeleteChildren(View view) {
        deleteChildrenDialog = ActivityUtil.getDialog(this, "是否确定解除绑定", new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                deleteFriend();
                deleteChildrenDialog.dismiss();
            }
        }, null);
        deleteChildrenDialog.show();
    }

    @OnClick(R.id.activity_info_children_head)
    private void onClickUpHead(View view) {
        startActivityForResult(new Intent(this, ChangePictureActivity.class), request_Code);
        overridePendingTransition(R.anim.base_slide_bottom_in, R.anim.base_slide_remain);
    }

    private void deleteFriend() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        HczUnBindChildNet unBindChildNet = new HczUnBindChildNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(ChildInfoActivity.this, title_layout, true, "解绑成功");
                        Constants.childs.remove(userIndex);
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(ChildInfoActivity.this, title_layout, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        unBindChildNet.putParams();
        unBindChildNet.sendRequest();
    }

    /**
     * 更改昵称或头像
     */
    private void upData2Servers() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        ActivityUtil.showPopWindow4Tips(ChildInfoActivity.this, title_layout, false,false, "正在更改信息...",-1);
        HczSetChildInfoNet setChildInfoNet = new HczSetChildInfoNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(ChildInfoActivity.this, title_layout, true, "更改成功");
                        Constants.childs.get(userIndex).setImg(friend.getImg());
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(ChildInfoActivity.this, title_layout, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        setChildInfoNet.putParams(friend.getGender() + "", nameEt.getText().toString().trim(), id);
        setChildInfoNet.sendRequest();

    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        saveHeadPhoto();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveHeadPhoto();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.activity_info_children_save_btn)
    public void onSaveClick(View view) {
        upData2Servers();
    }

    private void saveHeadPhoto() {
        if ((id != 0 && id != oldId) || name != null) {
            dialog = ActivityUtil.getDialog(this, "已更改数据是否保存？", new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    upData2Servers();
                    dialog.dismiss();
                }
            }, new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    onBackPressed();
                }
            });
            dialog.show();
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == request_Code) {
            id = data.getExtras().getInt("id");
            headIv.setBackgroundResource(Constants.showIds[id]);
            friend.setImg(id);
            saveBtn.setEnabled(true);
        }
        if (resultCode == RESULT_OK && requestCode == request_name_Code) {
            name = data.getExtras().getString("name");
            Constants.childs.get(userIndex).setName(name);
            nameEt.setText(name);
            Constants.child.setName(name);
            saveBtn.setEnabled(true);
        }
    }
}
