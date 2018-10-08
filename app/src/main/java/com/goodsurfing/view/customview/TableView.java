package com.goodsurfing.view.customview;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goodsurfing.constants.Constants;

public class TableView extends ViewGroup {
    private static final int STARTX = 120;// 起始X坐标
    private static final int STARTY = 0;// 起始Y坐标
    private static final int BORDER = 2;// 表格边框宽度
    private static int HEIGHT = 80;//设置高度
    private static int WIDTH = 80; 
    private static final int offset= 50;
     
    private int mRow=17;// 行数
    private int mCol=7;// 列数
    
    private static final String Str[] = {"周一","周二","周三","周四","周五","周六","周天"};
    private static final String StrTime[] = {"00:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00"
    									,"16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};
    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRow = 3;// 默认行数为3
        this.mCol = 3;// 默认列数为3 
        // 添加子控件
        this.addOtherView(context);
    }
     
    public TableView(Context context, int row,int col) {
        super(context);
        if(row>20 || col>20){
            this.mRow = 20;// 大于20行时，设置行数为20行
            this.mCol = 20;// 大于20列时，设置列数为20列
        }else if(row==0 || col==0){
            this.mRow = 3;
            this.mCol = 3;
        }
        else{
            this.mRow = row;
            this.mCol = col;
        }
        // 添加子控件
        this.addOtherView(context);
    }
     
    public void addOtherView(Context context){
        for(int i=0;i<=mRow;i++){
            for(int j=0;j<=mCol;j++){
            	
            	if(i==0&j!=0) {
            		TextView textView = new TextView(context);
            		textView.setText(Str[j-1]);
            		textView.setTextColor(Color.BLACK);
            		textView.setTextSize(12.0f);
            		textView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            		textView.setBackgroundColor(Color.rgb(234, 234, 234));
            		this.addView(textView);
            	}else if(j==0) {
            		TextView textView1 = new TextView(context);
            		textView1.setText(StrTime[i]);
            		textView1.setTextColor(Color.BLACK);
            		textView1.setTextSize(12.0f);
            		textView1.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            		textView1.setBackgroundColor(Color.rgb(234, 234, 234));
            		this.addView(textView1);
            		
            	}else {
            		View view = new View(context);
              
	                if(i%2==0){
	                    view.setBackgroundColor(Color.rgb(250, 250, 250));//FAFA
	                }else{
	                    view.setBackgroundColor(Color.rgb(255, 255, 255));
	                    
	                }
	                this.addView(view);
            	}
               
            }
        }
    }
     
    @Override
    protected void dispatchDraw(Canvas canvas) {
    	   
        Paint paint = new Paint();
        paint.setStrokeWidth(BORDER);
        paint.setColor(Color.rgb(196, 196, 196));
        paint.setStyle(Style.STROKE);
        // 绘制外部边框
        // canvas.drawRect(STARTX, STARTY, getWidth()-STARTX, getHeight()-STARTY, paint);
        // 画列分割线
        for(int i=1;i<mCol;i++){
           //canvas.drawLine((WIDTH)*i+STARTX, STARTY, (WIDTH)*i+STARTX, HEIGHT*mRow-STARTY, paint);
        }
        // 画行分割线
        for(int j=1;j<mRow;j++){
           // canvas.drawLine(STARTX, HEIGHT*j-offset, getWidth(), HEIGHT*j-offset, paint);
        }
        super.dispatchDraw(canvas);
    }
     
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	   
    	 Constants.devWidth = getWidth();
         Constants.devHeight = getHeight();
         
         
        //设置宽度
        WIDTH = (getWidth()-STARTX)/mCol;
        
        //设置高度
         //if(HEIGHT*this.mRow<=getHeight())
         //	HEIGHT = getHeight()/mRow;
        
        if(WIDTH*this.mRow<=getHeight())
        	HEIGHT = getHeight()/mRow;
        else
        	HEIGHT = WIDTH;
        
       // offset =  getWidth()-(WIDTH+BORDER*4)*mCol;

        		
        int x = STARTX+BORDER;
        int y = STARTY+BORDER-offset;
        int i = 0;
        int count = getChildCount();
        for(int j=0; j<count; j++){
            View child = getChildAt(j);
            if(j%8==0) {
            	//child.layout(0, y-BORDER+offset, WIDTH+BORDER*2, y+HEIGHT-BORDER+offset+BORDER*2);
            	child.layout(0, y-BORDER+HEIGHT-24, WIDTH, y+HEIGHT-BORDER+HEIGHT-24);
            }
            else if(j/8==0)
            	child.layout(x-WIDTH, y+offset, x-BORDER*2, y+HEIGHT-BORDER*2+offset);
            else
            	child.layout(x-WIDTH, y, x-BORDER*2, y+HEIGHT-BORDER*2);
            if(i >=(mCol)){
                i = 0;
                x = STARTX+BORDER;
                y += HEIGHT;
            }else{
                i++;
                x += WIDTH;
            }
        }
    }
     
    public void setRow(int row){
        this.mRow = row;
    }
     
    public void setCol(int col){
        this.mCol = col;
    }
 

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	
    }
}
