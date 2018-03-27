package yonky.remotecontroltest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Administrator on 2018/3/27.
 */

public class MyDragShadowBuilder extends View.DragShadowBuilder {
    private static Drawable shadow;
    private int width,height;
    public MyDragShadowBuilder(View v){
        super(v);
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(true));
        shadow = new BitmapDrawable(null,bitmap);
        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(false);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        width = getView().getWidth();
        height = getView().getHeight();

//        设置阴影大小
        shadow.setBounds(0,0,width,height);
//        设置长宽值，通过outShadowSize参数返回给系统。
        outShadowSize.set(width,height);
//        把触摸点的位置设为拖动阴影的中心
        outShadowTouchPoint.set(width/2,height/2);

    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
