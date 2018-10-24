package com.goodsurfing.map;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.base.BasePhotoActivity;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.FriendDao;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class InfoChildrenActivity extends BaseActivity {

	protected static final int REFRESH = 100;

	private static final int request_Code = 1;
	private static final int request_name_Code = 2;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_info_children_et_name)
	private EditText nameEt;

	@ViewInject(R.id.activity_info_children_delete_btn)
	private TextView saveTv;

	@ViewInject(R.id.activity_info_children_head)
	private RoundImageView headIv;

	@ViewInject(R.id.activity_info_children_delete_phone)
	private ImageView deleteIv;

	@ViewInject(R.id.activity_info_children_tv_phone)
	private TextView phoneTv;
	@ViewInject(R.id.activity_info_children_tv_yy)
	private TextView yunyTv;
	@ViewInject(R.id.activity_info_children_tv_address)
	private TextView addressTv;
	
	@ViewInject(R.id.title_layout)
	private View title_layout;
	
	
	private Friend friend;

	private int userIndex;
	private FriendDao friendDao;
	private int id;
	private int oldId;

	private Dialog dialog;

	private Dialog deleteChildrenDialog;

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
		userIndex = getIntent().getExtras().getInt("userIndex");
		friend = Constants.user.get(userIndex);
		id = oldId = friend.getId();
		phoneTv.setText(friend.getPhone());
		nameEt.setText(friend.getNikename());
		addressTv.setText(friend.getAddress());
		yunyTv.setText(ActivityUtil.getPhoneYunyin(friend.getPhone()));
		headIv.setBackgroundResource(Constants.showIds[friend.getId()]);
		friendDao = new FriendDao(this);
	}

	@OnClick(R.id.activity_info_children_et_name)
	private void onClickEditName(View view) {
		Intent intent = new Intent(this, SettingChildrenNameActivity.class);
		intent.putExtra("name", friend.getNikename());
		startActivityForResult(intent, request_name_Code);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@OnClick(R.id.activity_info_children_delete_btn)
	private void onClickDeleteChildren(View view) {
		// TODO 解除绑定
		deleteChildrenDialog = ActivityUtil.getDialog(this, "是否确定解除绑定", new OnClickListener() {

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
		// ChangePictureActivity.luncherPhoto(this, request_Code);
	}

	private void deleteFriend() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		// committype=19& id=11
		String url = Constants.SERVER_URL + "?" + "token=" + Constants.tokenID + "&userid=" + Constants.userId + "&committype=19" + "&id=" + friend.get_id();
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					if ("0".equals(result.code)) {
						ActivityUtil.showPopWindow4Tips(InfoChildrenActivity.this,title_layout, true, "解绑成功");
						friendDao.deleteFriend(Constants.user.get(userIndex).getPhone());
						Constants.user.remove(userIndex);
						Intent intent = getIntent();
						intent.putExtra("isdelete", true);
						setResult(RESULT_OK, intent);
						onBackPressed();
					}else {
						ActivityUtil.showPopWindow4Tips(InfoChildrenActivity.this, title_layout, false, result.extra+"");
					}
				} catch (Exception e) {
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	/**
	 * 更改昵称或头像
	 */
	private void upData4Servers() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		String url = Constants.SERVER_URL + "?" + "token=" + Constants.tokenID + "&userid=" + Constants.userId + "&committype=18" + "&img=" + id + "&name=" + nameEt.getText().toString().trim() + "&id=" + friend.get_id();

		// committype=18& img=1&nickname=nihao&id=11
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					if ("0".equals(result.code)) {
						ActivityUtil.showPopWindow4Tips(InfoChildrenActivity.this,title_layout, true, "更改成功");
						Constants.user.get(userIndex).setId(id);
						friendDao.upData4head(Constants.user.get(userIndex).getPhone(), id);
						dialog.dismiss();
						Intent intent = getIntent();
						intent.putExtra("isdelete", false);
						setResult(RESULT_OK, intent);
						onBackPressed();
					}else {
						ActivityUtil.showPopWindow4Tips(InfoChildrenActivity.this, title_layout, false, result.extra+"");
					}
				} catch (Exception e) {
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
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

	private void saveHeadPhoto() {
		if ((id != 0 && id != oldId) || name != null) {
			dialog = ActivityUtil.getDialog(this, "已更改数据是否保存？", new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					upData4Servers();
				}
			}, new OnClickListener() {

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
			headIv.setBackground(null);
			id = data.getExtras().getInt("id");
			headIv.setBackgroundResource(Constants.showIds[id]);
		}
		if (resultCode == RESULT_OK && requestCode == request_name_Code) {
			name = data.getExtras().getString("name");
			Constants.user.get(userIndex).setNikename(name);
			nameEt.setText(name);
			friendDao.upData4Name(Constants.user.get(userIndex).getPhone(), name);
		}
	}

}
