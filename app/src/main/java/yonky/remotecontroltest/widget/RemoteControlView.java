package yonky.remotecontroltest.widget;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yonky.remotecontroltest.DraggableInfo;

import static yonky.remotecontroltest.Tools.ONE_BY_ONE_PIC;
import static yonky.remotecontroltest.Tools.ONE_BY_ONE_TEXT;
import static yonky.remotecontroltest.Tools.ONE_BY_TWO_PIC;
import static yonky.remotecontroltest.Tools.THREE_BY_THREE_PIC;

/**
 * Created by Administrator on 2018/3/26.
 */

public class RemoteControlView extends FrameLayout implements View.OnDragListener {
    private final static String TAG = "RemoteControlView";

    private Paint mPhonePaint;

    private Path mBackPath;

    private int mPhoneWidth;
    private int mPhoneContentHeight;
    private int mPhoneContentWidth;

    private int startX;
    private List<Rect> mRectList = new ArrayList<>();

    private View dragView;
    private Rect dragRect;

    private boolean isOut;

    private Rect shadowRect;

    private DraggableInfo info;
    Rect mTextRect = new Rect();

    private Bitmap shadowBitmap;

    private Bitmap getShadowBitmap;

    private Rect mRect= new Rect();

    private FrameLayout frameLayout;
    private TextView mTextView;
    private final static int WIDTH_COUNT = 4;
    private final static int HEIGHT_COUNT=7;
    private final static String BORDER_COLOR = "#70ffffff";
    private final static String SOLID_COLOR="#30FFFFFF";
    private final static String DASHED_COLOR="#20ffffff";
    private final static String CONTENT_COLOR="#0e000000";

    private DashPathEffect mDashPathEffect = new DashPathEffect(new float[]{10,10},0);


    public RemoteControlView(@NonNull Context context) {
        this(context,null);
    }

    public RemoteControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RemoteControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setWillNotDraw(false);
        mPhonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPath = new Path();
//        关闭硬件加速，否则虚线显示不出
        setLayerType(LAYER_TYPE_SOFTWARE,null);
//        拖拽有效区域
        frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Color.parseColor(CONTENT_COLOR));
        frameLayout.setOnDragListener(this);
        addView(frameLayout);

        mTextView = new TextView(context);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setText("长按并拖拽下方按钮到这里");
        LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        mTextView.setLayoutParams(fl);
        mTextView.measure(0,0);
        addView(mTextView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
        // 手机高度为View高度减去上下间隔24dp
        int phoneHeight = getMeasuredHeight() - dp2px(24);
        // 手机内容区域高 ：手机高度 - 手机头尾（48dp）- 手机屏幕间距（5dp） * 2）
        mPhoneContentHeight = phoneHeight - dp2px(58);
        // 手机内容区域宽 ：手机内容区域高/ 7 * 4（手机内容区域为4：7）
        mPhoneContentWidth = mPhoneContentHeight / HEIGHT_COUNT * WIDTH_COUNT;
        // 手机宽度为手机内容区域宽 + 手机屏幕间距 * 2
        mPhoneWidth = mPhoneContentWidth + dp2px(10);
        // 绘制起始点
        startX = (getMeasuredWidth() - mPhoneWidth) / 2;
    }

    private int measure(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch(specMode){
            case MeasureSpec.AT_MOST:
//                子容器可以是声明大小内的任意大小
                result = specSize;
                break;
            case MeasureSpec.EXACTLY:
//                父容器已经为子容器设置了尺寸，子容器应当服从这些边界，不论子容器想要多大的空间
                result= specSize;
                break;
                case MeasureSpec.UNSPECIFIED:
//                    父容器对于子容器没有任何限制,子容器想要多大就多大
                    result = dp2px(300);
                break;
                default:
                    break;

        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        frameLayout.layout(startX,dp2px(36),getMeasuredWidth()-startX,getMeasuredHeight()-dp2px(36));
        if(frameLayout.getChildCount()>0){
            for(int i=0;i<frameLayout.getChildCount();i++){
                Rect rect = mRectList.get(i);
                frameLayout.getChildAt(i).layout(rect.left,rect.top,rect.right,rect.bottom);
            }
        }
    }

    private RectF mRectF = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPhonePaint.setColor(Color.parseColor(BORDER_COLOR));
        mPhonePaint.setStyle(Paint.Style.STROKE);
        mPhonePaint.setStrokeWidth(2);
        int i = dp2px(12);
//        绘制手机外壳
        mRectF.left = startX;
        mRectF.right = getMeasuredWidth()-startX;
        mRectF.top= i;
        mRectF.bottom = getMeasuredHeight()-i;
        canvas.drawRoundRect(mRectF,i,i,mPhonePaint);

//        绘制手机上下两条线
        canvas.drawLine(startX,i*3,getMeasuredWidth()-startX,i*3,mPhonePaint);
        canvas.drawLine(startX,getMeasuredHeight()-i*3,getMeasuredWidth()-startX,getMeasuredHeight()-i*3,mPhonePaint);
//        绘制手机上方听筒、摄像头
        mRectF.left = getMeasuredWidth()/2 -dp2px(25);
        mRectF.right= getMeasuredWidth()/2+dp2px(25);
        mRectF.top = dp2px(22);
        mRectF.bottom= dp2px(26);
        canvas.drawRoundRect(mRectF,dp2px(2),dp2px(2),mPhonePaint);
        canvas.drawCircle(getMeasuredWidth()/2-dp2px(40),i*2,dp2px(4),mPhonePaint);
        canvas.drawCircle(getMeasuredWidth()/2+dp2px(40),i*2,dp2px(4),mPhonePaint);
//绘制下方按键
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()-i*2,i/2,mPhonePaint);
        canvas.drawRect(getMeasuredWidth()-startX-mPhoneWidth/5-dp2px(10), getMeasuredHeight() - dp2px(29), getMeasuredWidth()-startX - mPhoneWidth / 5 , getMeasuredHeight() - dp2px(19), mPhonePaint);
        mBackPath.moveTo(startX+mPhoneWidth/5,getMeasuredHeight()-i*2);
        mBackPath.lineTo(startX+mPhoneWidth/5+dp2px(10),getMeasuredHeight()-dp2px(29));
        mBackPath.lineTo(startX+mPhoneWidth/5+dp2px(10),getMeasuredHeight()-dp2px(19));
        mBackPath.close();
        canvas.drawPath(mBackPath,mPhonePaint);
        // 绘制网格（4 * 7的田字格）田字格外框为实线，内侧为虚线
        // 手机屏幕间距5pd
        int j = dp2px(5);
        // 格子的宽高
        int size = mPhoneContentHeight/HEIGHT_COUNT;
        //横线
        for(int z=0;z<=HEIGHT_COUNT;z++){
            mPhonePaint.setPathEffect(null);
            mPhonePaint.setColor(Color.parseColor(SOLID_COLOR));
            mPhonePaint.setStrokeWidth(1);
//            实线
            canvas.drawLine(startX+j,dp2px(41)+z*size,getMeasuredWidth()-startX-j,dp2px(41)+z*size,mPhonePaint);
//              虚线
            if(z!=HEIGHT_COUNT){
                mPhonePaint.setPathEffect(mDashPathEffect);
                mPhonePaint.setColor(Color.parseColor(DASHED_COLOR));
                canvas.drawLine(startX+j,dp2px(41)+z*size+size/2,getMeasuredWidth()-startX-j,dp2px(41)+z*size+size/2,mPhonePaint);
            }
        }
//        竖线
        for(int z=0;z<=WIDTH_COUNT;z++){
            mPhonePaint.setPathEffect(null);
            mPhonePaint.setColor(Color.parseColor(SOLID_COLOR));
            mPhonePaint.setStrokeWidth(1);
            canvas.drawLine(startX+j+z*size,dp2px(41),startX+j+z*size,getMeasuredHeight()-dp2px(41),mPhonePaint);
            //              虚线
            if(z!=WIDTH_COUNT){
                mPhonePaint.setPathEffect(mDashPathEffect);
                mPhonePaint.setColor(Color.parseColor(DASHED_COLOR));
                canvas.drawLine(startX+j+z*size+size/2,dp2px(41),startX+j+z*size+size/2,getMeasuredHeight()-dp2px(41),mPhonePaint);
            }
        }
        if(shadowRect !=null){
            int type = info.getType();
            mPhonePaint.setStyle(Paint.Style.FILL);
            mPhonePaint.setColor(Color.WHITE);
            shadowRect.left = shadowRect.left+startX;
            shadowRect.right = shadowRect.right+startX;
            shadowRect.top = shadowRect.top+dp2px(36);
            shadowRect.bottom = shadowRect.bottom+dp2px(36);

            if(type ==ONE_BY_ONE_TEXT){
                int width = shadowRect.right-shadowRect.left;
                String text = info.getText();
                mPhonePaint.setTextSize(width/4);
                mPhonePaint.getTextBounds(text,0,text.length(),mTextRect);

                int textHeight = mTextRect.bottom-mTextRect.top;
                int textWidth = mTextRect.right-mTextRect.left;
                canvas.drawText(text,shadowRect.left+width/2-textWidth/2,shadowRect.top+width/2+textHeight/2,mPhonePaint);
            }else{
                if(type==ONE_BY_ONE_PIC){
                    //1*1方格
                    int padding = dp2px(12);
                    shadowRect.left=shadowRect.left+padding;
                    shadowRect.right = shadowRect.right-padding;
                    shadowRect.top = shadowRect.top+padding;
                    shadowRect.bottom= shadowRect.bottom-padding;
                }else if(type == THREE_BY_THREE_PIC){
                    //3*3方格
                    int padding =dp2px(10);
                    shadowRect.left = shadowRect.left+padding;
                    shadowRect.right = shadowRect.right-padding;
                    shadowRect.top = shadowRect.top +padding;
                    shadowRect.bottom = shadowRect.bottom-padding;
                }else if(type == ONE_BY_TWO_PIC){
                    int padding = dp2px(4);
                    shadowRect.left=shadowRect.left+padding;
                    shadowRect.right=shadowRect.right-padding;
                }
                canvas.drawBitmap(shadowBitmap,null,shadowRect,mPhonePaint);
            }
        }
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch(action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // 判断是否是需要接收的数据
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
                    Log.e(TAG, "开始拖动");
                }else {
                    return false;
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.e(TAG, "进入区域");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.e(TAG, "移出区域");
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.e(TAG, "停止拖动");
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.e(TAG, "停留在区域中");
                break;
            case DragEvent.ACTION_DROP:
                Log.e(TAG, "释放拖动View");
                break;
            default:
                return false;
        }
        return true;
    }


    private int dp2px(int dp){
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float density = displayMetrics.scaledDensity;
        return (int)(dp*density+0.5f);
    }
}
