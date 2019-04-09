// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.barcodescanner.barcodescanning;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.barcodescanner.VisionProcessorBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.example.barcodescanner.common.CameraImageGraphic;
import com.example.barcodescanner.common.FrameMetadata;
import com.example.barcodescanner.common.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Barcode Detector Demo.
 */
public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

    private static final String TAG = "BarcodeScanProc";

    private final FirebaseVisionBarcodeDetector detector;

    private BarcodeListener mBarcodeListener;

    public BarcodeScanningProcessor(BarcodeListener barcodeListener) {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        // new FirebaseVisionBarcodeDetectorOptions.Builder()
        //     .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
        //     .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        mBarcodeListener=barcodeListener;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Barcode Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionBarcode>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionBarcode> barcodes,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {

        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        graphicOverlay.postInvalidate();

        List<FirebaseVisionBarcode> lBarcodes=selectFocus(barcodes,frameMetadata);
        if(lBarcodes.size()>0){
            mBarcodeListener.onSuccess(barcodes.get(0));

        }
    }

    public List<FirebaseVisionBarcode> selectFocus(List<FirebaseVisionBarcode> barcodes,FrameMetadata metadata) {

        List<FirebaseVisionBarcode> finalListBarcode=new ArrayList<FirebaseVisionBarcode>();
        double nearestDistance = distanceSelectedAreaToCenter(metadata);


        for (int i = 0; i < barcodes.size(); i++) {
            FirebaseVisionBarcode barcode = barcodes.get( i);
            float topLefttDx = Math.abs((metadata.getWidth() / 2) - barcode.getBoundingBox().top);
            float topLeftDy = Math.abs((metadata.getHeight() / 2) - barcode.getBoundingBox().left);
            float bottomRightDx = Math.abs((metadata.getWidth() / 2) - barcode.getBoundingBox().bottom);
            float bottomRightDy = Math.abs((metadata.getHeight() / 2) - barcode.getBoundingBox().right);

            double distanceFromTopLeftToCenter =  Math.sqrt((topLefttDx * topLefttDx) + (topLeftDy * topLeftDy));
            double distanceFromBottomRightToCenter =  Math.sqrt((bottomRightDx * bottomRightDx) + (bottomRightDy * bottomRightDy));
            if (distanceFromTopLeftToCenter <= nearestDistance && distanceFromBottomRightToCenter<= nearestDistance) {
                finalListBarcode.add(barcode);
            }
        }
        return finalListBarcode;
    }

    public double distanceSelectedAreaToCenter(FrameMetadata frameMetadata){
        int width=0;
        int height=0;



        int widthCenter=frameMetadata.getWidth()/2;
        int heightCenter=frameMetadata.getHeight()/2;
        if(frameMetadata.getRotation()==90){
            width=(int)(frameMetadata.getWidth()*0.3);
            height=(int)(frameMetadata.getHeight()/10);
        }else{
            width=(int)(frameMetadata.getWidth()/10);
            height=(int)(frameMetadata.getHeight()*0.3);
        }

        int dx=Math.abs((widthCenter-width*2));
        int dy=Math.abs((heightCenter-height*2));

        return Math.sqrt((dx*dx)+(dy*dy));
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }

    public static interface BarcodeListener{
        public void onSuccess(FirebaseVisionBarcode barcode);
        public void onFailure();
    }
}
