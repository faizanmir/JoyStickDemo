package avishkaar.com.joystickdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoyStick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{
    public float centerX;
    public JoystickListener joystickCallback;
    public float centerY;
    public float baseRadius;
    public float hatRadius;
    private static final String TAG = "JoyStick";
    interface JoystickListener{
        void onJoystickMoved(float x, float y ,int id);
        void onUserUnpress(float x,float y);
    }


    void setUpDimensions()
    {
        centerX = (float) getWidth()/2;
        centerY = (float) getHeight()/2;
        baseRadius =(float) Math.min(getWidth()/2,getHeight()/2);
        hatRadius = (float) Math.min(getWidth()/2,getHeight()/2)/3;

    }

    public JoyStick(Context context) {
        super(context);

        if(context instanceof JoystickListener)
        {
            joystickCallback =  (JoystickListener)context;
        }

    }

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        Log.e(TAG, "JoyStick: " + "JoyStick Created with id " + getId() );
        if(context instanceof JoystickListener)
        {
            joystickCallback =  (JoystickListener)context;
        }
    }

    public JoyStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
        {
            joystickCallback =  (JoystickListener)context;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        getHolder().addCallback(this);
        setUpDimensions();
        Log.e(TAG, "surfaceCreated: " + baseRadius + "  X  " +centerX + "  Y  " +centerY + "  H  " + getHeight() + "     " + getWidth());
        drawJoyStick(centerX,centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        holder.addCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.addCallback(this);
    }

    void drawJoyStick(float newX,float newY)
    {
          if(getHolder().getSurface().isValid()) {
              Canvas myCanvas = getHolder().lockCanvas();
              Paint colors = new Paint();
              colors.setStyle(Paint.Style.FILL_AND_STROKE);
              colors.setStrokeWidth(3);
              colors.setAntiAlias(true);
              myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
              colors.setARGB(255, 50, 50, 50);
              myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
              colors.setARGB(255, 100, 100, 100);
              myCanvas.drawCircle(newX, newY, hatRadius, colors);
              getHolder().unlockCanvasAndPost(myCanvas);
          }


    }


    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.equals(this))
        {
            if(event.getAction()!=MotionEvent.ACTION_UP)
            {
                float displacement = (float) Math.sqrt(Math.pow(event.getX()-centerX,2)+Math.pow(event.getY()-centerY,2));
                if(displacement<baseRadius) {
                    drawJoyStick(event.getX(), event.getY());
                    joystickCallback.onJoystickMoved((event.getX()-centerX)/baseRadius,(event.getY()-centerY)/baseRadius,getId());
                }else
                {
                    float ratio =  baseRadius/displacement;
                    float constrainedX  = centerX + (event.getX()-centerX)* ratio;
                    float constrainedY = centerY +  (event.getY()-centerY)*ratio;
                    drawJoyStick(constrainedX,constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX-centerX)/baseRadius,(constrainedY-centerY)/baseRadius,getId());

                }
            }
            else
            {
                drawJoyStick(centerX,centerY);
                joystickCallback.onUserUnpress(0,0);
            }

        }
        return true;
    }
}
