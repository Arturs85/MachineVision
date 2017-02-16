package com.example.user.machinevision;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 2016.04.16..
 */

public class AndroidCameraExample extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture, switchCamera, buttonPlus, buttonMinus, buttonMainit;
    private Context myContext;
    private LinearLayout cameraPreview;
    private RelativeLayout cameraPreview2;

    private boolean cameraFront = false;
    private Camera.Parameters mCameraParam;
    private int previewSizeWidth = 200;
    private TextView tekstaLauks;
    private int previewSizeHeight = 300;
    private Intent intent;
    byte[] data1;
    boolean izsauktsSaglabat = false;
    boolean ieslegtsEkstremuSkats = true;
    LinijuPrieksskats skats;
    int spilgtumsDelta = 25;
    byte[] callbackBuffer;
    byte[] callbackBuffer2;
    Handler handler;
    BitmapThread bitmapThread;
    // private final RawPictureCallback mRawPictureCallback = new RawPictureCallback();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera_example);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        //@TargetApi(19)
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        myContext = this;
        handler = new HandlerExtension(this);
        bitmapThread = new BitmapThread();
        bitmapThread.isRunning = true;
        bitmapThread.start();

        initialize();


    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            //mCamera.setDisplayOrientation(90);

            //mCamera.addCallbackBuffer();
            //ByteBuffer buffer = new ByteBuffer();
            //mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            String mString = parameters.getFocusMode();
            int mFormat = parameters.getPreviewFormat();

            //for (String string:parameters.getFocusMode()) {
//mString = mString+" "+string;
            // }
            Toast.makeText(this, mString + " tips: " + mFormat, Toast.LENGTH_LONG).show();
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }


            mCamera.setParameters(parameters);
            previewSizeHeight = size.height;
            previewSizeWidth = size.width;
            int buferaIzmers = previewSizeHeight * previewSizeWidth;
            buferaIzmers = buferaIzmers / 2 + buferaIzmers;
            Toast toast = Toast.makeText(myContext, Integer.toString(previewSizeWidth) + "x" + Integer.toString(previewSizeHeight), Toast.LENGTH_LONG);
            toast.show();
            callbackBuffer = new byte[buferaIzmers];
            callbackBuffer2 = new byte[buferaIzmers];
            mCamera.setPreviewCallbackWithBuffer(getPrevCallback());
            mCamera.addCallbackBuffer(callbackBuffer);

        }
    }

    public void initialize() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.TRANSPARENT);//.setAlpha(0.0f);
        setSupportActionBar(myToolbar);
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        cameraPreview2 = (RelativeLayout) findViewById(R.id.camera_preview2);

        mPreview = new CameraPreview(myContext, mCamera);

        skats = new LinijuPrieksskats(myContext);//MySurfaceView(myContext,data1);//

        // skats.atelaDati = data1;

        cameraPreview.addView(mPreview);
        // cameraPreview.addView(skats);
        cameraPreview2.addView(skats);
       // skats.setRotation(90);
        //skats.setTranslationX(480);
        //cameraPreview2.setTranslationZ(-1);
        //

        tekstaLauks = (TextView) findViewById(R.id.textView);
        tekstaLauks.setText(Integer.toString(spilgtumsDelta));
        tekstaLauks.setMaxWidth(90);
        tekstaLauks.bringToFront();
        buttonPlus = (Button) findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(plusListener);
        buttonPlus.bringToFront();
        buttonMinus = (Button) findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(minusListener);
        buttonMinus.bringToFront();
        buttonMainit = (Button) findViewById(R.id.buttonMainit);
        buttonMainit.setOnClickListener(mainitListener);
        buttonMainit.bringToFront();
        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);
        capture.bringToFront();
        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);
        switchCamera.bringToFront();
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.bringToFront();
        myToolbar.bringToFront();
        seekBar.setAlpha(0.3f);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tekstaLauks.setText(Integer.toString(progress));
                bitmapThread.attels.vSlieksnis = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //intent = new Intent(this, LinijuMekletajs.class);
        //intent.putExtra("vards",data1);

    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                //  mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
        bitmapThread.isRunning = false;
        bitmapThread.interrupt();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Toast toast = Toast.makeText(myContext, "autofocus done", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    private Camera.PreviewCallback getPrevCallback() {
        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // Log.d("MVision", " onPreview called ");

                // Camera.Parameters parameters = camera.getParameters();
                // int imageFormat = parameters.getPreviewFormat();
                // if (imageFormat == ImageFormat.NV21) {
                // Rect rect = new Rect(0, 0, PreviewSizeWidth, PreviewSizeHeight);
                //YuvImage img = new YuvImage(data, ImageFormat.NV21, PreviewSizeWidth, PreviewSizeHeight, null);


                mCamera.addCallbackBuffer(callbackBuffer);
                callbackBuffer2 = data;

                //intent.putExtra("vards", data);
                //skats = new LinijuPrieksskats(myContext);
                bitmapThread.setDati(callbackBuffer2);

                bitmapThread.spilgtumsDelta = spilgtumsDelta;
                //skats.refreshDrawableState();

                //startActivity(intent);
                // LinijuMekletajs mekletajs = new LinijuMekletajs();
                // int ekstremuSkaits = mekletajs.skaitiitEkstremus(data);
                // tekstaLauks.setText("Nevienmērība "+ekstremuSkaits);

                // Toast toast = Toast.makeText(myContext, "Nevienmerība "+ ekstremuSkaits, Toast.LENGTH_SHORT);
                // toast.show();
/*
                 OutputStream outStream = null;
                // File file = new File(String.valueOf(getOutputMediaFile()));
                 try {
                     outStream = new FileOutputStream(getOutputMediaFile());
                    //img.compressToJpeg(rect, 100, outStream);
                    outStream.write(data);
                     outStream.flush();
                     outStream.close();

                     Toast toast = Toast.makeText(myContext, "Picture saved: ", Toast.LENGTH_LONG);
                     toast.show();

                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
*/
                //  }
                //  mPreview.refreshCamera(mCamera);

            }
        };
        return previewCallback;
    }
/*
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }
*/

    View.OnClickListener plusListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.autoFocus(mAutoFocusCallback);
            if (spilgtumsDelta <= 49 && spilgtumsDelta >= 1)
                spilgtumsDelta++;
            tekstaLauks.setText(Integer.toString(spilgtumsDelta));

        }
    };
    View.OnClickListener mainitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ieslegtsEkstremuSkats) {
                ieslegtsEkstremuSkats = false;
                bitmapThread.ieslegtsEkstremuSkats = false;
                buttonMainit.setText("<");

            } else {
                ieslegtsEkstremuSkats = true;
                bitmapThread.ieslegtsEkstremuSkats = true;
                buttonMainit.setText(">");
            }
        }
    };
    View.OnClickListener minusListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (spilgtumsDelta <= 50 && spilgtumsDelta >= 2)
                spilgtumsDelta--;
            tekstaLauks.setText(Integer.toString(spilgtumsDelta));
        }
    };
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // mCameraParam = mCamera.getParameters();
            // mCameraParam.set("rawsave-mode", "on");
            // mCameraParam.set("rawfname", "rawfails.raw");
            //  mCamera.setParameters(mCameraParam);
            //   Bitmap plainImage = BitmapFactory.decodeByteArray(rawPlainImage, 0, rawPlainImage.length);
            if (izsauktsSaglabat) {  // sāk saglabāt tikai pec otrās pogas nospiešanas reizes
                synchronized (callbackBuffer) {
                    SaglabatFailaThread saglabatFailaThread = new SaglabatFailaThread(callbackBuffer, myContext);
                    saglabatFailaThread.start();
                    Log.d("MVision", " ThreadSaglabāt started ");
                }
                izsauktsSaglabat = false;
            }
            mCamera.addCallbackBuffer(callbackBuffer);

            mCamera.setPreviewCallbackWithBuffer(getPrevCallback());//setOneShotPreviewCallback(getPrevCallback());//
            //mCamera.takePicture(null,null ,mPicture );
            izsauktsSaglabat = true;


        }
    };

    //make picture and save to a folder
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

    private void saglabatRaw(byte[] data) {

        OutputStream outStream = null;
        // File file = new File(String.valueOf(getOutputMediaFile()));
        try {
            outStream = new FileOutputStream(getOutputMediaFile());
            //img.compressToJpeg(rect, 100, outStream);
            outStream.write(data);
            outStream.flush();
            outStream.close();

            Toast toast = Toast.makeText(myContext, "Picture saved: ", Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ekstremi2kartaMenuItem:
               bitmapThread.ieslegtsEkstremuSkats=true;//newGame();
                return true;
            case R.id.ekstremMenuItemi:
                //showHelp();
                return true;
            case R.id.planeExtractionMenuItem:
                bitmapThread.ieslegtsEkstremuSkats = false;
                //showHelp();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private static class HandlerExtension extends Handler {

        private final WeakReference<AndroidCameraExample> currentActivity;

        public HandlerExtension(AndroidCameraExample activity) {
            currentActivity = new WeakReference<AndroidCameraExample>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            AndroidCameraExample activity = currentActivity.get();
            if (activity != null) {
                if (message.obj != null) {
                    //  activity.updateResults(message.getData().getString("result") + " ir");

                    activity.skats.mSetBitmap((Bitmap) message.obj);
//activity.skats.postInvalidate();
                }

            }
        }
    }

    public class BitmapThread extends Thread {

        byte[] dati;
        byte[] datiOtrs;
        Paint paint = new Paint();
        Paint linijuKrasa = new Paint();
        boolean izsauktsSaglabat = false;
        final Bitmap bitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        boolean isRunning = false;
        boolean ieslegtsEkstremuSkats = true;
        int platums = 0;
        boolean uzzimetsPirmais = true;
        int spilgtumsDelta = 10;
        boolean updated = true;
        Attels attels = new Attels();
        boolean[] ekstremi = new boolean[307200];
        boolean[][] ekstremi160x120 = new boolean[160][120];
        boolean[][] krasuLaukums = new boolean[640][480];
        int[] krasuHist = new int[362];

        public void setDati(byte[] dati) {
            if (uzzimetsPirmais)
                this.dati = dati;
            else
                datiOtrs = dati;
            updated = true;
        }

        void zimetBitmap(byte[] dati) {
            int kolonna = 1;
            int rinda = 1;


            attels.spilgtumsDelta = spilgtumsDelta;

            // ekstremi=attels.atrastEkstremus(dati,ekstremi);
            canvas = attels.atrastVirsmuLoRes(canvas, paint, attels.atrastKrasuHistogrammu(krasuHist, dati), dati);
 /*
            for (int a = 0; a < ekstremi.length - 1; a = a + 1) { //640x384


                if (ekstremi[a]) {
                    canvas.drawPoint(kolonna, rinda, paint);

                }
                kolonna= kolonna+1;
                if (kolonna >= 641) {
                    rinda= rinda +1 ;
                    kolonna = 1;
                }
            }
*/
            //  ekstremi160x120=attels.atrastEkstremus160x120(dati,ekstremi160x120);

/*
krasuLaukums= attels.atrastKrasuLaukumus(dati,krasuLaukums); // krasu laukumu tests
            for (int x = 0; x <= 639; x = x +1) { // 640x384
                for (int y = 0; y <= 479; y++) {
                    if(krasuLaukums[x][y] == true)
                        canvas.drawPoint(x, y,linijuKrasa );
              }
              }
*/
            krasuHist = attels.atrastKrasuHistogrammu(krasuHist, dati);
            for (int i = 0; i < krasuHist.length; i++) {
                int j = krasuHist[i];
                float[] hsv = {(float) i, 0.9f, 0.9f};
                paint.setColor(Color.HSVToColor(hsv));
                //canvas.setColor(Color.getHSBColor((float)i/360, 0.8f, 0.9f));
                //g.setColor(yuvToRGB(40, -120, 120));

                canvas.drawLine(1 * i + 5, 5, 1 * i + 5, j, paint);
                //canvas.drawLine(2*i+6, 5, 2*i+6, j,paint);


            }


        }

        void zimetBitmap2(byte[] dati) {
            int kolonna = 1;

            int updatedPlatums = 0;
            int rinda = 1;
            boolean atrasts = false;
            Attels attels = new Attels();
            attels.spilgtumsDelta = spilgtumsDelta;
            ArrayList<Attels.HorizontalaLinija> horizontalasLinijas = attels.mekletHorLinijas(dati);
            horizontalasLinijas = attels.atlasitLinijas(horizontalasLinijas);

            for (int a = 0; a <= horizontalasLinijas.size() - 2; a++) {
                //canvas.drawLine(horizontalasLinijas.get(a).sakumaX,horizontalasLinijas.get(a).y, horizontalasLinijas.get(a).beiguX,horizontalasLinijas.get(a).y,linijuKrasa);

                if (atrasts == false && horizontalasLinijas.get(a).sakumaX + 10 > horizontalasLinijas.get(a + 1).sakumaX &&
                        horizontalasLinijas.get(a).sakumaX - 10 < horizontalasLinijas.get(a + 1).sakumaX &&
                        horizontalasLinijas.get(a).y + 30 < horizontalasLinijas.get(a + 1).y &&
                        horizontalasLinijas.get(a).getGarums() + 15 > horizontalasLinijas.get(a + 1).getGarums() &&
                        horizontalasLinijas.get(a).getGarums() - 15 < horizontalasLinijas.get(a + 1).getGarums()) {
                    platums = horizontalasLinijas.get(a + 1).y - horizontalasLinijas.get(a).y;
                    atrasts = true;
                    canvas.drawRect(horizontalasLinijas.get(a).sakumaX, horizontalasLinijas.get(a).y, horizontalasLinijas.get(a).beiguX, horizontalasLinijas.get(a + 1).y, paint);
                    //canvas.drawText(Integer.toString(horizontalasLinijas.get(a+1).y-horizontalasLinijas.get(a).y),55,55,paint );
                }
                //platums = (platums +updatedPlatums)/2;
                //canvas.drawText(Integer.toString(platums),55,55,paint );

            }

            //platums = (platums +updatedPlatums)/2;
            canvas.drawText(Integer.toString(platums), 55, 55, paint);
        }

        public void run() {
            try {
                while (isRunning) {
                    //sleep(2) ;

                    if (updated) {

                        paint.setAntiAlias(true);
                        linijuKrasa.setColor(Color.RED);
                        // linijuKrasa.setDither(false);

                        paint.setColor(Color.MAGENTA);
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        // canvas.drawColor(Color.WHITE);

                        synchronized (bitmap) {
                            if (dati != null) {
                                if (uzzimetsPirmais && datiOtrs != null) {
                                    if (ieslegtsEkstremuSkats) {
                                        zimetBitmap(datiOtrs);

                                    } else
                                        zimetBitmap2(datiOtrs);

                                    uzzimetsPirmais = false;
                                } else {
                                    if (ieslegtsEkstremuSkats)
                                        zimetBitmap(dati);
                                        // if (izsauktsSaglabat){
                                        //    saglabatRaw(dati);
                                        //   izsauktsSaglabat=false;
                                        //  }
                                    else
                                        zimetBitmap2(dati);

                                    uzzimetsPirmais = true;
                                }
                            } else
                                canvas.drawText("null", 55, 55, paint);
                            updated = false;

                            Message msg = new Message();

                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Throwable t) {
// just end the background thread
            }
        }

        boolean noteiktMalu(int a, int a1, int a2, int a3) {
            boolean irMala = false;
            if (a + a1 < a2 + a3 - spilgtumsDelta || a + a1 > a2 + a3 + spilgtumsDelta)
                irMala = true;


            return irMala;
        }
    }

}
