package com.example.user.machinevision;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2017.02.15..
 */
public class SaglabatFailaThread extends Thread {
    byte[] attelaDati;
    Context context;
    public SaglabatFailaThread(byte[] attelaDati,Context context){
        this.attelaDati =attelaDati;
        this.context =context;
    }
     private File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "JCG Camera");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".raw");

        return mediaFile;
    }
    private  void saglabatRaw(byte[] data){

        OutputStream outStream = null;
        // File file = new File(String.valueOf(getOutputMediaFile()));
        try {
            outStream = new FileOutputStream(getOutputMediaFile());
            //img.compressToJpeg(rect, 100, outStream);
            outStream.write(data);
            outStream.flush();
            outStream.close();
            Log.d("MVisionThreadSaglabat", " savedRaw ");
//            Toast toast = Toast.makeText(context, "Picture saved: ", Toast.LENGTH_LONG);
  //          toast.show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
public void run(){
    synchronized (attelaDati){
        saglabatRaw(attelaDati);

    }

}

}
