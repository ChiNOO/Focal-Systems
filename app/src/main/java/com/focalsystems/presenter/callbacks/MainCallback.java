package com.focalsystems.presenter.callbacks;

import android.graphics.Bitmap;
import android.graphics.Paint;

import java.io.IOException;

/**
 * Created by christian on 01/06/16.
 * Focal Systems Skill Assessment
 */
public interface MainCallback {
    void onSuccessCreateBitmap(Bitmap imageBitmap, Bitmap.Config config);

    void onErrorCreateBitmap(IOException e);

    void onErrorInitDrawRectangle();

    void onSuccessInitDrawRectangle(int x, int y, int projectedX, int projectedY, Paint paint);

    void onSuccessDrawRectangle(int pointX, int pointY, Paint paint);

    void onErrorDrawRectangle();

}
