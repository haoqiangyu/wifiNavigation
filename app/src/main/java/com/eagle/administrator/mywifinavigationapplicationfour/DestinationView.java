package com.eagle.administrator.mywifinavigationapplicationfour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Administrator on 2018/6/9.
 */
//自定义的目的地图片控件；
public class DestinationView extends View {
    public  float bitmapDesX;
    public  float bitmapDesY;
    public DestinationView(Context context){
        super(context);
        bitmapDesX=900;
        bitmapDesY=900;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.end);
        canvas.drawBitmap(bitmap,bitmapDesX,bitmapDesY,paint);
        if(bitmap.isRecycled()){
            bitmap.recycle();
        }
    }
}
