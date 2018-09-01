package com.goodsurfing.server.net;

import java.util.HashMap;
import java.util.Map;

public class HttpErrorCode {

    private static Map<String, String> errMap = new HashMap<String, String>();

    static {
        errMap.put("1001", "签名错误");
        errMap.put("1002", "参数错误");
        errMap.put("1003", "当前IP超过每日发送次数");
        errMap.put("1004", "当前手机超过每日发送次数");
        errMap.put("1005", "验证码错误");
        errMap.put("1006", "短信发送失败");
        errMap.put("1007", "账户不存在");
        errMap.put("1008", "密码错误");
        errMap.put("1009", "注册错误");
        errMap.put("1010", "切换失败");
        errMap.put("1011", "App不存在");
        errMap.put("1012", "操作失败");
        errMap.put("1013", "孩子设备不在线，无法操作");
        errMap.put("1014", "新注册用户UUID已存在");
        errMap.put("1015", "手机号码已存在");
        errMap.put("1016", "孩子已解除绑定");
        errMap.put("1017", "绑定失败,请检测家长手机或验证码");
        errMap.put("1018", "已绑定过家长");
        errMap.put("1019", "该设备已经解屏");
        errMap.put("1020", "时间间隔不能超过30天");
        errMap.put("1021", "该手机号已存在");
        errMap.put("1022", "号码已经绑定");
        errMap.put("10000", "服务器忙,请稍后再试");
    }


    public static String getCode2String(String code) {

        String codeError = "";
        codeError
                = errMap.get(code);
        if (codeError == null)
            codeError = errMap.get("10000");
        return codeError;
    }
}
