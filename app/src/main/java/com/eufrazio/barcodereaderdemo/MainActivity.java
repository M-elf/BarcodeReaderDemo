package com.eufrazio.barcodereaderdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
        final TextView barcodeInfo = (TextView) findViewById(R.id.codeTextView);

        barcodeDetector = new BarcodeDetector.Builder(this).build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    barcodeInfo.post(new Runnable() {

                        @Override
                        public void run() {

                            barcodeInfo.setText(barcodes.valueAt(0).displayValue);

                        }

                    });

                }

            }

        });

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1080,720)
                .build();

        cameraView.getHolder().addCallback(this);

    }

    public void startCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},1);

        } else {

            try {

                if (barcodeDetector.isOperational()) {

                    cameraSource.start(cameraView.getHolder());

                } else {

                    Log.i("Barcode Decoder", "is not functioning properly!");

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        startCamera();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        cameraSource.stop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                try {

                    if (barcodeDetector.isOperational()) {

                        cameraSource.start(cameraView.getHolder());
                        Log.i("CameraSource", "Started!");

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

    }

}
