package com.example.barcodescanner.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jaison.
 */

public class OverlayView extends View {

    private static final int TEXT_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 35.0f;

    Paint paint;
    Paint paintGreenRectangle;
    Path pathGreenRectangle;

    Paint paintBlackBorder;
    Path pathBrackBorderRight;
    Path pathBrackBorderBottom;
    Path pathBrackBorderTop;
    Path pathBrackBorderLeft;


    Canvas canvas;
    Context context;

    final float spaceBetweenCorners=100.0f;
    final float paddingBorders=5.0f;

    int viewPortHeight = 0;
    int viewPortWidth = 0;

    int oldViewPortHeight = 0;
    int oldViewPortWidth = 0;

    int viewHeight = 800;
    int viewWidth = 500;

    int horizontalPadding = 50;
    int verticalPadding = 350;

    Rect overlayRect;
    Region overlayRegion;
    int initialYPosition = 0;

    boolean initialDraw = false;
    boolean canImageMove = false;

    int prevX;
    int prevY;

    int downY = 0;
    int downX = 0;
    int currentX = 0;
    int currentY = 0;

    Rect oveylayRect=new Rect();

    boolean isMovableView;

    public static String TAG = "OverlayView";

    public OverlayView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public boolean isMovableView() {
        return isMovableView;
    }

    public void setMovableView(boolean movableView) {
        isMovableView = movableView;
    }

    private void init(Context context) {

        this.context = context;
        setFocusable(true); // necessary for getting the touch events

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        paint.setColor(Color.parseColor("#AA000000"));


        paintGreenRectangle = new Paint();
        paintGreenRectangle.setStyle(Paint.Style.STROKE);
        paintGreenRectangle.setColor(Color.parseColor("#45f442"));
        paintGreenRectangle.setStrokeWidth(10);
        paintGreenRectangle.setStrokeJoin(Paint.Join.ROUND);
        paintGreenRectangle.setStrokeCap(Paint.Cap.ROUND);

        paintBlackBorder = new Paint();
        paintBlackBorder.setStyle(Paint.Style.STROKE);
        paintBlackBorder.setColor(Color.GRAY);
        paintBlackBorder.setStrokeWidth(10);

        pathGreenRectangle=new Path();
        pathBrackBorderBottom=new Path();
        pathBrackBorderLeft=new Path();
        pathBrackBorderRight=new Path();
        pathBrackBorderTop=new Path();

        canvas = new Canvas();
        overlayRect = new Rect(0, 0, 0, 0);
        overlayRegion = new Region(overlayRect);
    }

    public Rect getOverlayRect()
    {
        return overlayRect;
    }

    public void moveOverlay(final int top) {
        if (top >= verticalPadding) {

            if((top + viewHeight)<=(viewPortHeight-verticalPadding))
            {
                overlayRect.top = top;
                overlayRect.bottom = top + viewHeight;
                overlayRegion.set(overlayRect);
                prevY = top;
                invalidate();
                downY = top;
            }
            else
            {
                overlayRect.top = (viewPortHeight-verticalPadding)-viewHeight;
                overlayRect.bottom = viewPortHeight-verticalPadding;
                overlayRegion.set(overlayRect);
                prevY = overlayRect.top;
                invalidate();
                downY = overlayRect.top;
            }

        }
        else
        {
            overlayRect.top = verticalPadding;
            overlayRect.bottom = verticalPadding + viewHeight;
            overlayRegion.set(overlayRect);
            prevY = verticalPadding;
            invalidate();
            downY = verticalPadding;

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //viewHeight=getHeight();
        //viewWidth=getWidth();


            if (verticalPadding > 0){
                viewHeight = viewPortHeight - (2 * verticalPadding);
            }
            else {
                viewHeight = viewPortHeight;
            }

        canvas.save();
        if (!initialDraw) {
            if(viewPortHeight>viewPortWidth){
                initialYPosition = (viewPortHeight / 2) - (viewHeight / 2);
                initialDraw = true;
                int lLeft=horizontalPadding;
                int lTop=initialYPosition;
                int lRight=(viewPortWidth - horizontalPadding);
                int lBottom=(initialYPosition + viewHeight);

                float flLeft=horizontalPadding;
                float flTop=initialYPosition;
                float flRight=(viewPortWidth - horizontalPadding);
                float flBottom=(initialYPosition + viewHeight);

                overlayRect.set(lLeft, lTop, lRight,lBottom);

                pathGreenRectangle.addRect(flLeft,flTop,flRight,flBottom, Path.Direction.CW);

                pathBrackBorderTop.addRect(lLeft+spaceBetweenCorners,flTop,lRight-spaceBetweenCorners,flTop+paddingBorders,Path.Direction.CW);
                pathBrackBorderLeft.addRect(lLeft,flTop+spaceBetweenCorners,lLeft+paddingBorders,flBottom-spaceBetweenCorners,Path.Direction.CW);
                pathBrackBorderRight.addRect(lLeft+paddingBorders,flTop+spaceBetweenCorners,lRight,flBottom-spaceBetweenCorners,Path.Direction.CW);
                pathBrackBorderBottom.addRect(lLeft+spaceBetweenCorners,flBottom-paddingBorders,lRight-spaceBetweenCorners,flBottom,Path.Direction.CW);
            }else{
                initialYPosition = (viewPortHeight / 2) - (viewHeight / 2);
                initialDraw = true;
                int lLeft=horizontalPadding;
                int lTop=initialYPosition;
                int lRight=viewPortWidth-horizontalPadding;
                int lBottom=(initialYPosition +(viewPortHeight-verticalPadding-initialYPosition) );

                float flLeft=lLeft;
                float flTop=lTop;
                float flRight=lRight;
                float flBottom=lBottom;

                overlayRect.set(lLeft, lTop, lRight,lBottom);
                pathGreenRectangle.addRect(flLeft,flTop,flRight,flBottom, Path.Direction.CW);

            }
        }

        canvas.clipRect(overlayRect, Region.Op.XOR);
        canvas.drawPaint(paint);
        canvas.drawPath(pathGreenRectangle,paintGreenRectangle);
        canvas.drawPath(pathBrackBorderTop,paintBlackBorder);
        canvas.drawPath(pathBrackBorderLeft,paintBlackBorder);
        canvas.drawPath(pathBrackBorderRight,paintBlackBorder);
        canvas.drawPath(pathBrackBorderBottom,paintBlackBorder);

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        viewPortWidth = xNew;
        viewPortHeight = yNew;
        oldViewPortWidth=xOld;
        oldViewPortHeight=yOld;
        if(viewPortHeight>viewPortWidth){
            verticalPadding=(int)(viewPortHeight*0.3);
            horizontalPadding=viewPortWidth/10;
        }else{
            verticalPadding=(viewPortHeight/10);
            horizontalPadding=(int)(viewPortWidth*0.3);
        }
    }
}