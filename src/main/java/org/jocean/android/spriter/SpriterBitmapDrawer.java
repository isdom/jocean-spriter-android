package org.jocean.android.spriter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;

import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Timeline.Key.Object;

public class SpriterBitmapDrawer extends Drawer<Bitmap>{
    
    private Canvas _canvas;
    private final Paint _geomPaint;
    private final ColorMatrix _colorMatrix;
    private final Paint _bitmapPaint;
    
    public SpriterBitmapDrawer(final Loader<Bitmap> loader) {
        super(loader);
        
        this._geomPaint = new Paint();
        this._geomPaint.setAntiAlias(true);
        this._colorMatrix = new ColorMatrix();
        this._bitmapPaint = new Paint();
        this._bitmapPaint.setAntiAlias(true);
    }
    
    public void setCanvas(final Canvas canvas) {
        this._canvas = canvas;
    }
    
    public Canvas getCanvas() {
        return _canvas;
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        float[] src = new float[]{
                    r, 0, 0, 0, 0, 
                    0, g, 0, 0, 0,
                    0, 0, b, 0, 0, 
                    0, 0, 0, a, 0};
        _colorMatrix.set(src);
        _geomPaint.setColorFilter(new ColorMatrixColorFilter(_colorMatrix));
    }

    @Override
    public void line(float x1, float y1, float x2, float y2) {
        _canvas.drawLine(x1, y1, x2, y2, _geomPaint);
    }

    @Override
    public void rectangle(float x, float y, float width, float height) {
        _canvas.drawRect(x, y, x+width, y+height, _geomPaint);          
    }

    @Override
    public void circle(float x, float y, float radius) {
        _canvas.drawCircle(x, y, radius, _geomPaint);
    }

//    @Override
//    public void draw(Object object) {
//        final Bitmap bitmap = loader.get(object.ref);
//        int _w = bitmap.getWidth();
//        int _h = bitmap.getHeight();
//        int _h_w = _w / 2;
//        int _h_h = _h / 2;
//        
//        float newPivotX = _w * object.pivot.x;
//        float newX = object.position.x - newPivotX*Math.signum(object.scale.x);
//        float newPivotY = _h * object.pivot.y;
//        float newY = object.position.y - newPivotY*Math.signum(object.scale.y);
//        
//        _matrix.reset();
//        
//        _matrix.postScale(object.scale.x, -object.scale.y, _h_w, _h_h);
//        _matrix.postRotate(object.angle, _w * object.pivot.x, _h * object.pivot.y);
//        _matrix.postTranslate(newX, newY);
//        
//        final int saveCount = this._canvas.save();
//        _canvas.scale(1, -1);
//        
//        _canvas.drawBitmap(bitmap, _matrix, null);
//        
//        this._canvas.restoreToCount(saveCount);
//    }
    
    @Override
    public void draw(Object object) {
        
        final int saveCount = this._canvas.save();
        
        this._canvas.scale(1,  -1);
        this._canvas.translate(0, -_canvas.getHeight());
        
        final Bitmap bitmap = loader.get(object.ref);
        float newPivotX = bitmap.getWidth() * object.pivot.x;
        float newX = object.position.x - newPivotX*Math.signum(object.scale.x);
        float newPivotY = bitmap.getHeight() * object.pivot.y;
        float newY = object.position.y - newPivotY*Math.signum(object.scale.y);
        this._canvas.rotate( object.angle, object.position.x, object.position.y);
        
        float width = bitmap.getWidth()*object.scale.x;
        float height = -bitmap.getHeight()*object.scale.y;
        
        final RectF rectf = new RectF(newX, newY-height, newX + width, newY);
        
        adjustRectAndScaleCanvas(rectf, _canvas);

        this._bitmapPaint.setAlpha( (int)(object.alpha * 255) );
        
        _canvas.drawBitmap(bitmap, null, rectf, this._bitmapPaint);
        
        this._canvas.restoreToCount(saveCount);
    }

    private void adjustRectAndScaleCanvas(final RectF rectf, final Canvas canvas) {
        if ( rectf.top > rectf.bottom ) {
            this._canvas.scale(1,  -1, 0, (rectf.top + rectf.bottom) / 2);
            final float tmp = rectf.top;
            rectf.top = rectf.bottom;
            rectf.bottom = tmp;
        }
        if ( rectf.left > rectf.right ) {
            this._canvas.scale(-1,  1, (rectf.left + rectf.right) / 2, 0);
            final float tmp = rectf.left;
            rectf.left = rectf.right;
            rectf.right = tmp;
        }
    }
}

