package com.goodsurfing.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.TimeControllerActivity;

@SuppressLint("NewApi")
public class TimerController {

    private Context mContext;
    private TableLayout mTab;
    private LinearLayout mLinear;
    private LinearLayout clearLayout;
    private TextView mTv;
    private TextView clearTv;
    private TextView[] textViews;
    private int[] textViewIds = new int[]{R.id.clear_tv_1, R.id.clear_tv_2, R.id.clear_tv_3, R.id.clear_tv_4, R.id.clear_tv_5, R.id.clear_tv_6, R.id.clear_tv_7,};
    private static final int MAXFLA = 50;
    private static final String Str[] = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String StrTime[] = {"0-7:59", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

    private static char[] result = new char[119];
    private static int WIDTH = 0; // Constants.devWidth;
    private static int HEIGHT = 0; // Constants.devHeight;

    private static int defultW = 135;
    private String clickColor = "#F08B22";
    public TimerController() {
    }

    public void setColor(String clickColor) {
        this.clickColor = clickColor;
    }

    public TimerController(Context context, TableLayout tab, LinearLayout clearLayout, LinearLayout linear, TextView tv) {
        mContext = context;
        mTab = tab;
        mLinear = linear;
        this.clearLayout = clearLayout;
        mTv = tv;
        WIDTH = (Constants.devWidth) / 8;
        defultW = WIDTH;
        HEIGHT = (Constants.devHeight) / 18;
        if (HEIGHT % 2 != 0)
            HEIGHT = HEIGHT + 1;
    }

    @SuppressLint("ResourceType")
    public void initTabBackGround() {
        for (int i = 0; i < result.length; i++) {
            result[i] = '0';
        }
        textViews = new TextView[7];
        mTab.setStretchAllColumns(true);
        mLinear.setBackgroundColor(Color.parseColor("#dddddd"));
        for (int i = 0; i < 18; i++) {
            // view.setWidth(100);
            if (i == 0) {
                TextView view1 = new TextView(mContext);
                LayoutParams lp = new LayoutParams();
                lp.setMargins(0, 0, 1, 1);
                view1.setLayoutParams(lp);
                view1.setWidth(WIDTH - 1);
                view1.setHeight(HEIGHT + 1);
                view1.setGravity(Gravity.CENTER);
                view1.setBackgroundColor(Color.parseColor("#e5e5e5"));
                mLinear.addView(view1);
            } else {
                // 左边背景处理
                TextView view = new TextView(mContext);
                view.setText(StrTime[i - 1]);

                view.setTextSize(12);
                LayoutParams lp = new LayoutParams();
                lp.setMargins(0, 1, 1, 1);
                view.setLayoutParams(lp);
                view.setWidth(WIDTH - 1);
                view.setHeight(HEIGHT);
                view.setGravity(Gravity.CENTER);
                view.setTextColor(Color.parseColor("#7c7c7c"));
                view.setBackgroundColor(Color.parseColor("#e5e5e5"));
                mLinear.addView(view);
            }

            // table处理
            TableRow tablerow = new TableRow(mContext);
            tablerow.setBackgroundColor(Color.parseColor("#dddddd"));

            for (int j = 0; j < 7; j++) {

                if (i == 0) {
                    TextView testview = new TextView(mContext);
                    // testview.setBackgroundResource(R.drawable.shape);
                    testview.setText(Str[j]);
                    LayoutParams lp = new LayoutParams();
                    lp.setMargins(1, 0, 1, 1);
                    testview.setLayoutParams(lp);
                    testview.setWidth(WIDTH - 2);
                    testview.setHeight(HEIGHT + 1);
                    testview.setGravity(Gravity.CENTER);
                    testview.setBackgroundColor(Color.parseColor("#f4f4f4"));
                    testview.setTextColor(Color.parseColor("#343434"));
                    // testview.setFocusable(true);
                    tablerow.addView(testview);
                    // tablerow.addView(bu);
                } else {
                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setBackgroundColor(Color.WHITE);

                    TextView testview = new TextView(mContext);
                    // testview.setBackgroundResource(R.drawable.shape);
                    // testview.setText("选择");
                    LayoutParams lp = new LayoutParams();
                    lp.setMargins(1, 1, 1, 1);
                    layout.setLayoutParams(lp);
                    lp.height = HEIGHT;
                    lp.width = WIDTH - 2;
                    android.view.ViewGroup.LayoutParams tvParams = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
                    testview.setLayoutParams(tvParams);
                    if (i % 2 == 0)
                        testview.setBackgroundColor(Color.rgb(250, 250, 250));
                    else
                        testview.setBackgroundColor(Color.WHITE);
                    layout.setId((i - 1) * 7 + j + MAXFLA);
                    // testview.setFocusable(true);
                    layout.setOnClickListener(onClickListener);
                    layout.setPadding(2, 2, 2, 2);
                    layout.setGravity(Gravity.CENTER);
                    layout.addView(testview);
                    tablerow.addView(layout);
                }

            }
            mTab.addView(tablerow, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
        // 清楚功能
        clearTv = (TextView) clearLayout.findViewById(R.id.clear_tv_0);
        clearTv.setText("重置");
        clearTv.setGravity(Gravity.CENTER);
        clearTv.setId(7);
        clearTv.setOnClickListener(onClickListener1);
        // view1.setBackgroundColor(Color.rgb(230, 230, 230));
        clearTv.setBackgroundColor(Color.parseColor("#343434"));
        for (int j = 0; j < 7; j++) {
            textViews[j] = (TextView) clearLayout.findViewById(textViewIds[j]);
            textViews[j].setText("清除");
            textViews[j].setGravity(Gravity.CENTER);
            textViews[j].setBackgroundColor(Color.parseColor("#343434"));
            textViews[j].setId(j);
            textViews[j].setOnClickListener(onClickListener1);
        }
    }

    public void refreshTable(String str) {

        for (int i = 0; i < mTab.getChildCount(); i++) {
            TableRow tablerow = (TableRow) mTab.getChildAt(i);
            for (int j = 0; j < tablerow.getChildCount(); j++) {
                View view = tablerow.getChildAt(j);

                int count = view.getId();
                if (count < MAXFLA)
                    continue;
                int position = getPosition(getRow(count - 50), getCol(count - 50));
                LinearLayout layout = (LinearLayout) view;
                View text = layout.getChildAt(0);
                if ((count >= MAXFLA) && (count <= 169)) {
                    if (str.length() < position + 1)
                        continue;

                    if ("1".equals(str.charAt(position) + "")) {
                        result[position] = '1';
                        text.setBackgroundColor(Color.parseColor(clickColor));
                    } else {
                        result[position] = '0';
                        if (getRow(count - 50) % 2 == 0)
                            text.setBackgroundColor(Color.parseColor("#ffffff"));
                        else
                            text.setBackgroundColor(Color.parseColor("#fafafa"));
                    }

                }
            }
        }
        for (int i = 0; i < 7; i++) {
            if (isClearCol(getCol(i))) {
                textViews[getCol(i)].setBackgroundColor(Color.parseColor("#f4f4f4"));
            } else {
                textViews[getCol(i)].setBackgroundColor(Color.WHITE);
            }
        }
        if (isClearAllCol()) {
            clearTv.setBackgroundColor(Color.parseColor("#f4f4f4"));
        } else {
            clearTv.setBackgroundColor(Color.WHITE);
        }
        mTv.setEnabled(false);
        mTv.setTextColor(Color.parseColor("#dddddd"));

    }

    private boolean isClearAllCol() {
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 17; i++) {
                if (result[j * 17 + i] == '1')
                    return true;
            }
        }
        return false;
    }

    public static String getSelectResult() {
        return String.valueOf(result);
    }

    // Row 行数
    private int getRow(int cnt) {
        return cnt / 7;
    }

    // Col 列号
    private int getCol(int cnt) {
        return cnt % 7;
    }

    // 字符串定位
    private int getPosition(int row, int col) {
        return 17 * col + row;
    }

    OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (Constants.isDrawerOut)
                return;
            LinearLayout layout = (LinearLayout) view;
            int count = view.getId();
            View text = layout.getChildAt(0);
            if ((count >= MAXFLA) && (count <= 175)) {
                int position = getPosition(getRow(count - 50), getCol(count - 50));

                if (result[position] == '1') {
                    result[position] = '0';
                    if (getRow(count - 50) % 2 == 0)
                        text.setBackgroundColor(Color.rgb(255, 255, 255));
                    else
                        text.setBackgroundColor(Color.rgb(250, 250, 250));
                } else {
                    result[position] = '1';
                    text.setBackgroundColor(Color.parseColor(clickColor));
                }
            }
            if (isClearCol(getCol(count - 50))) {
                textViews[getCol(count - 50)].setBackgroundColor(Color.parseColor("#f4f4f4"));
            } else {
                textViews[getCol(count - 50)].setBackgroundColor(Color.WHITE);
            }
            if (isClearAllCol()) {
                clearTv.setBackgroundColor(Color.parseColor("#f4f4f4"));
            } else {
                clearTv.setBackgroundColor(Color.WHITE);
            }
            saveDatas();
        }
    };

    OnClickListener onClickListener1 = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (Constants.isDrawerOut)
                return;

            int count = view.getId();
            if (count < 7) {
                clearCol(count);
            } else {
                for (int i = 0; i < 7; i++) {
                    clearCol(i);
                }
            }
            refreshTable(getSelectResult());
            // mTv.setVisibility(View.VISIBLE);
            saveDatas();
        }
    };

    private void clearCol(int col) {
        for (int i = 0; i < 17; i++)
            result[col * 17 + i] = '0';
    }

    private boolean isClearCol(int col) {
        for (int i = 0; i < 17; i++) {
            if (result[col * 17 + i] == '1')
                return true;
        }
        return false;
    }

    private void saveDatas() {
        if (!TimeControllerActivity.timerStr.equals(String.valueOf(result))) {
            mTv.setEnabled(true);
            mTv.setTextColor(Color.parseColor("#5a667d"));
        } else {
            mTv.setEnabled(false);
            mTv.setTextColor(Color.parseColor("#dddddd"));
        }
    }

}
