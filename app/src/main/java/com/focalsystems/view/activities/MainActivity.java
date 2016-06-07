package com.focalsystems.view.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.focalsystems.R;
import com.focalsystems.Utils.Point;
import com.focalsystems.presenter.callbacks.MainCallback;
import com.focalsystems.presenter.implementations.MainPresenter;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.dflabs.lib.mvp.BaseActivity;
import io.dflabs.lib.mvp.BasePresenter;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MainActivity extends BaseActivity implements MainCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private MainPresenter mMainPresenter;

    @Bind(R.id.act_main_image_view_result)
    ImageViewTouch mImageViewResult;
    @Bind(R.id.act_main_image_draw)
    ImageViewTouch mImageViewPane;
    @Bind(R.id.menu_floating_action_menu)
    FloatingActionMenu mFloatingMenu;

    Bitmap bitmapMaster;
    Canvas canvasMaster;
    Bitmap bitmapDrawingPane;
    Canvas canvasDrawingPane;
    Point points;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarEnabled(false);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.act_main_take_photo)
    public void onClickTakePictureButton() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @OnClick(R.id.menu_draw_photo)
    public void onDrawPhotoClick(){
        mImageViewPane.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.menu_edith_photo)
    public void onEdithPhotoClick(){
        mImageViewPane.setVisibility(View.GONE);
    }


    @OnTouch(R.id.act_main_image_draw)
    public boolean onTouchResultImage(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                points = mMainPresenter.pointXY((ImageView) v, bitmapMaster, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mMainPresenter.initDrawRectangle((ImageView) v, bitmapMaster, x, y, points);
                break;
            case MotionEvent.ACTION_UP:
                mMainPresenter.drawRectangle((ImageView) v, bitmapMaster, x, y);
                finalizeDrawing();
                break;
        }
        return true;

    }

    @Override
    public void onSuccessCreateBitmap(Bitmap imageBitmap, Config config) {
        mImageViewResult.setBackground(null);
        bitmapMaster = imageBitmap;
        mImageViewResult.setImageBitmap(bitmapMaster);

        //bitmapMaster is Mutable bitmap
        bitmapMaster = Bitmap.createBitmap(
                imageBitmap.getWidth(),
                imageBitmap.getHeight(),
                config);

        canvasMaster = new Canvas(bitmapMaster);
        canvasMaster.drawBitmap(imageBitmap, 0, 0, null);

        mImageViewResult.setImageBitmap(bitmapMaster);

        try {
            //Create bitmap of same size for drawing
            bitmapDrawingPane = Bitmap.createBitmap(
                    imageBitmap.getWidth(),
                    imageBitmap.getHeight(),
                    config);
            canvasDrawingPane = new Canvas(bitmapDrawingPane);
//            mImageViewPane.setImageBitmap(bitmapDrawingPane);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_photo, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onSuccessInitDrawRectangle(int x, int y, int projectedX, int projectedY, Paint paint) {
        canvasDrawingPane.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvasDrawingPane.drawRect(x, y, projectedX, projectedY, paint);
        mImageViewPane.invalidate();
    }

    @Override
    public void onSuccessDrawRectangle(int pointX, int pointY, Paint paint) {
        canvasDrawingPane.drawRect(points.x, points.y, pointX, pointY, paint);
        mImageViewPane.invalidate();
    }

    @Override
    public void onErrorCreateBitmap(IOException e) {

    }

    @Override
    public void onErrorInitDrawRectangle() {
        Toast.makeText(this, R.string.error_photo, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onErrorDrawRectangle() {
        Toast.makeText(this, R.string.error_photo, Toast.LENGTH_LONG).show();
    }

    private void finalizeDrawing() {
        canvasMaster.drawBitmap(bitmapDrawingPane, 0, 0, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFloatingMenu.performClick();
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            mMainPresenter.createBitmap(imageUri);
        }
    }

    @Override
    protected BasePresenter getPresenter() {
        return mMainPresenter = new MainPresenter(this, this);
    }

}