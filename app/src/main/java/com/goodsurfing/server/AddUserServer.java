package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.FriendDao;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONObject;

/**
 * 获取网络授权时间
 * 
 * @author xzj
 * 
 */
public class AddUserServer extends BaseDataService {
	Context context;

	public AddUserServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context);
		this.context = context;
	}


	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				JSONObject object = jsonObject.getJSONObject("adduser");
				Friend friend = new Friend();
				friend.setId(object.getInt("imgnum"));
				friend.set_id(object.getLong("id"));
				friend.setLatitude(object.getDouble("latitude"));
				friend.setLongitude(object.getDouble("longitude"));
				friend.setPhone(object.getString("mobile"));
				try {
					friend.setNikename(object.getString("name"));
				} catch (Exception e) {
					friend.setNikename(object.getString("ExpirationTime"));
				}
				friend.setTime(System.currentTimeMillis());
				Constants.user.add(friend);
				new FriendDao(context).insert(Constants.user);
				result.code = "0";
			} else {
				String errMsg = PropertiesUtil.getProperties(context).getProperty(errCode);
				result.code = "1";
				result.extra = errMsg;
			}

		} catch (Exception e) {
			LogUtil.logError(e);
			result.code = "1";
			result.extra = "服务器忙，请稍候再试";
		}

		return super.parseResponse(entity, responder, result);
	}

}
