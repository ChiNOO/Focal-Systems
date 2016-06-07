package com.focalsystems.presenter.implementations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.focalsystems.Utils.Point;
import com.focalsystems.presenter.callbacks.MainCallback;
import java.io.IOException;
import io.dflabs.lib.mvp.BasePresenter;

/**
 * Created by christian on 01/06/16.
 * Focal Systems Skill Assessment
 */
public class MainPresenter extends BasePresenter {

    private final MainCallback callback;

    public MainPresenter(Context context, MainCallback callback) {
        super(context);
        this.callback = callback;
    }

    public void createBitmap(Uri imageUri) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        try {
            Bitmap imageRotate = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            Bitmap imageBitmap = Bitmap.createBitmap(imageRotate, 0, 0, imageRotate.getWidth(),
                    imageRotate.getHeight(), matrix, true);


            Bitmap.Config config;
            if(imageBitmap.getConfig() != null){
                config = imageBitmap.getConfig();
            }else{
                config = Bitmap.Config.ARGB_8888;
            }

            callback.onSuccessCreateBitmap(imageBitmap, config);

        } catch (IOException e) {
            callback.onErrorCreateBitmap(e);
        }
    }

    public void initDrawRectangle(ImageView v, Bitmap bitmapMaster, int x, int y, Point point) {
        if (x > 0 || y > 0 || x < v.getWidth() || y < v.getHeight()) {
            int projectedX = (int) ((double) x * ((double) bitmapMaster.getWidth() / (double) v.getWidth()));
            int projectedY = (int) ((double) y * ((double) bitmapMaster.getHeight() / (double) v.getHeight()));

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(20);
            callback.onSuccessInitDrawRectangle(point.x, point.y, projectedX, projectedY, paint);
        } else {
            callback.onErrorInitDrawRectangle();
        }
    }

    public void drawRectangle(ImageView v, Bitmap bitmapMaster, int x, int y) {
        if (x > 0 || y > 0 || x < v.getWidth() || y < v.getHeight()) {
            int pointX = (int) ((double) x * ((double) bitmapMaster.getWidth() / (double) v.getWidth()));
            int pointY = (int) ((double) y * ((double) bitmapMaster.getHeight() / (double) v.getHeight()));

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(20);
            callback.onSuccessDrawRectangle(pointX, pointY, paint);
        } else {
            callback.onErrorDrawRectangle();
        }
    }

    public Point pointXY(ImageView v, Bitmap bitmapMaster, int x, int y) {
        if (x < 0 || y < 0 || x > v.getWidth() || y > v.getHeight()) {
            return null;
        } else {
            int projectedX = (int) ((double) x * ((double) bitmapMaster.getWidth() / (double) v.getWidth()));
            int projectedY = (int) ((double) y * ((double) bitmapMaster.getHeight() / (double) v.getHeight()));

            return new Point(projectedX, projectedY);
        }
    }
}
