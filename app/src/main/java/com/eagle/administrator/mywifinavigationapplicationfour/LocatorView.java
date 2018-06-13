package com.eagle.administrator.mywifinavigationapplicationfour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Administrator on 2018/5/16.
 */

public class LocatorView extends View {//自定义LocatorView空间，继承View类，重写其onDraw方法；
    public float bitmapX;//定义Lccatord图片的坐标；
    public float bitmapY;
    public LocatorView(Context context){//重写构造方法，初始化Locator的位置坐标；
        super(context);
        bitmapX=900;
        bitmapY=0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();//创建并实例化Paint的对象；
        Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.start);//根据图片生成位图对象
        canvas.drawBitmap(bitmap,bitmapX,bitmapY,paint);//绘制Locator；
        if(bitmap.isRecycled()){//判断图片是否回收，没回收强制回收图片；
            bitmap.recycle();
        }
    }
}
