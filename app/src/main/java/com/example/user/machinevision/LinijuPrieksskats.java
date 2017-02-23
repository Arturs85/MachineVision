package com.example.user.machinevision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by user on 2016.05.31..
 */
public class LinijuPrieksskats extends View {
    public byte[] atelaDati;
 //   ArrayList<HorizontalaLinija> linijas;
    int spilgtumsDelta = 20;
    Paint paint = new Paint();
    Canvas canvas = new Canvas();
    Bitmap bitmap;
    Bitmap bitmapOtrs;
    boolean uzzimetsPirmais = true;
    public LinijuPrieksskats(Context context) {
        super(context);
        paint.setAntiAlias(true);
        paint.setColor(Color.MAGENTA);


    }
public void mSetBitmap(Bitmap bitmap){
    if(uzzimetsPirmais)
    this.bitmap = bitmap;
    else
    bitmapOtrs = bitmap;
    invalidate();

}
    boolean noteiktMalu(int a, int a1, int a2, int a3) {
        boolean irMala = false;
        if (a + a1 < a2 + a3 - spilgtumsDelta || a + a1 > a2 + a3 + spilgtumsDelta)
            irMala = true;


        return irMala;
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // Rect rect = new Rect(0,0,479,400);
    // synchronized (bitmap) {
        // canvas.scale(1.25f,1f);
        //synchronized (bitmap) {
            if (uzzimetsPirmais && bitmapOtrs != null) {
                //synchronized (bitmapOtrs) {
                    canvas.drawBitmap(bitmapOtrs, 0, 0, paint);
                    Log.d("linPr", " otrais ");
                //}
                uzzimetsPirmais = false;
            } else {

                if (bitmap != null) {
                    // canvas.drawColor(Color.WHITE);
                    // canvas.drawColor(Color.TRANSPARENT);//, Mode.CLEAR);
                    //synchronized (bitmap) {
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        //canvas.drawBitmap(bitmapOtrs,null,rect,paint);
                      //  Log.d("linPr", " pirmais ");
                        uzzimetsPirmais = true;
                    //}
                }

            }
            super.onDraw(canvas);
          /*


            if (atelaDati != null) {
                int kolonna = 1;
                int vKolonna = 1;
                int uvRinda = 385;
                int rinda = 1;
                int u, v, y;

               for (int a = 0; a < atelaDati.length/3*2 - 1; a = a + 1) { //640x384


                    if (noteiktMalu(atelaDati[a], atelaDati[a + 1], atelaDati[a + 2], atelaDati[a + 3])) {
                        canvas.drawPoint(kolonna, rinda, paint);

                    }
                    kolonna++;
                    if (kolonna >= 641) {
                        rinda++;
                        kolonna = 1;
                    }
                }

            } else
                canvas.drawText("null", 55, 55, paint);
*/
       // }
       // invalidate();
    }
    public Paint yuvToRGB(int y, int u, int v) {
        int red = (y + (v) * 114 / 100)/2 ;
        int green = ((y - 39 * (u) / 100 - (v) * 58 / 100) + 255)/2 ;
        int blue = (y + 2 * (u)) / 3;
        //int red = (int)((y + (1.370705 * (v-128)))/2.4)+73;
        // int green = (int)((y - (0.698001 * (v-128)) - (0.337633 * (u-128)))/2.1)+66;
        //  int blue = (int)((y + (1.732446 * (u-128)))/2.8)+83;

        paint.setColor(Color.rgb(red,green,blue));


        return paint;
    }
    boolean[] atrastEkstremus() {
        boolean[] ekstremi = new boolean[245760];
        for (int a = 0; a < 245760 - 1; a = a + 1) {
            if (noteiktMalu(atelaDati[a], atelaDati[a + 640], atelaDati[a + 640 * 2], atelaDati[a + 640 * 3])) {
                ekstremi[a] = true;
            } else
                ekstremi[a] = false;

        }
        return ekstremi;
    }


}
