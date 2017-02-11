package com.kironono.androidthings.blinkled;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class BlinkLEDActivity extends AppCompatActivity {
    private static final String TAG = BlinkLEDActivity.class.getSimpleName();
    private static final String LED_PIN_NAME = "BCM6";
    private static final int BLINK_INTERVAL = 1000;

    private Gpio mLedGpio;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIO: " + service.getGpioList());

        try {
            mLedGpio = service.openGpio(LED_PIN_NAME);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio.setValue(false);

            mHandler.post(mBlinkRunnable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_blink_led);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mBlinkRunnable);

        try {
            mLedGpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mLedGpio = null;
        }
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio == null) {
                return;
            }

            try {
                mLedGpio.setValue(!mLedGpio.getValue());
                mHandler.postDelayed(mBlinkRunnable, BLINK_INTERVAL);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}
