package com.example.accball;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback  {

    SensorManager mSensorManager;
    Sensor mAccSensor;

    SurfaceHolder mHolder;
    int mSurfaceWidth;
    int mSurfaceHeight;

    static final float RADIUS = 150.0f;
    static final int DIA = (int)RADIUS * 2 ;
    static final float COEF = 1000.0f;

    float mBallX;
    float mBallY;

    float mVX;
    float mVY;

    long mT0;

    Bitmap mBallBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();

        mHolder.addCallback(this);


        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceView.setZOrderOnTop((true));

        Bitmap ball = BitmapFactory.decodeResource(getResources(),R.drawable.ball);
        mBallBitmap = Bitmap.createScaledBitmap(ball , DIA, DIA , false);

    }
    //加速度センサーの値に変化があったときに呼ばれる
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("加速度" , "x=" + sensorEvent.values[0] + "y=" + sensorEvent.values[1] + "z=" + sensorEvent.values[2]);
            float x = -sensorEvent.values[0];
            float y = sensorEvent.values[1];

            //時間tを求める
            if (mT0 == 0) {
                mT0 = sensorEvent.timestamp;
                return;
            }
            float t = sensorEvent.timestamp - mT0;
            mT0 = sensorEvent.timestamp;
            t = t / 1000000000.0f;


            //}

            float dx = (mVX * t) + (x * t * t / 2.0f);
            float dy = (mVY * t) + (y * t * t / 2.0f);

             mBallX = mBallX + dx * COEF;
             mBallY = mBallY + dy * COEF;

            mVX = mVX + (x * t);
            mVY = mVY + (y * t);

            if(mBallX - RADIUS < 0 && mVX < 0 ) {
                 mVX = -mVX / 1.5f;
                 mBallX = RADIUS;
            }else if (mBallX + RADIUS > mSurfaceWidth && mVX > 0 ) {
                 mVX = -mVX / 1.5f;
                 mBallX = mSurfaceWidth - RADIUS;
            }
            if(mBallY - RADIUS < 0 && mVY < 0 ) {
                mVY = -mVY / 1.5f;
                mBallY = RADIUS;
            } else if (mBallY + RADIUS > mSurfaceHeight && mVY < 0 ) {
                mVY = -mVY / 1.5f;
                mBallY = mSurfaceHeight - RADIUS;
            }

         drawCanvas();


        }

}

private void drawCanvas() {

    Canvas c = mHolder.lockCanvas();
    c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    Paint paint = new Paint();
    c.drawBitmap(mBallBitmap, mBallX - RADIUS, mBallY - RADIUS, paint);

    mHolder.unlockCanvasAndPost(c);
}


    //センサーの精度が変わったとき
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume(){
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    //surfaceが作成されたときに呼ばれる
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSensorManager.registerListener(this,mAccSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    //surfaceが変更されたときに呼ばれる
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mSurfaceWidth = i1 ;
        mSurfaceHeight= i2;

        mBallX = mSurfaceWidth /2 ;
        mBallY = mSurfaceHeight /2 ;

        mVX =0 ;
        mVY =0 ;
        mT0 =0 ;


    }

    //surfaceが削除されるときに呼ばれる
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSensorManager.unregisterListener(this);
    }
    }