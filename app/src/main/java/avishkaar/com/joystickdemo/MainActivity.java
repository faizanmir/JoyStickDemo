package avishkaar.com.joystickdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity implements JoyStick.JoystickListener {
    private static final String TAG = "MainActivity";
    JoyStick rightStick,leftStick;
    static Socket socket;
    static PrintWriter printWriter;
    Thread networkThread;
    boolean shouldISend= false;
    int flag =0;
    int i =0;
    boolean isInSectorOne,isInSectorTwo,isInSectorThree,isInSector4;
    int flagForSectorOne,flagForSectorTwo,flagForSectorThree,flagForSectorFour;
    ExecutorService executorService;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftStick = findViewById(R.id.leftStick);
        leftStick.setZOrderOnTop(true);
        SurfaceHolder surfaceHolder = leftStick.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
         rightStick = findViewById(R.id.rightStick);
        rightStick.setZOrderOnTop(true);
        SurfaceHolder holder =  rightStick.getHolder();
        holder.setFormat(PixelFormat.TRANSLUCENT);
        executorService = Executors.newSingleThreadExecutor();

    }

    public void connectClient(View view) {
        new StartTCPConnection().execute();
    }

    static class StartTCPConnection extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket("192.168.4.1",900);
                Log.e(TAG, "doInBackground: " + socket.isConnected() );
                printWriter = new PrintWriter(socket.getOutputStream());
                Log.e(TAG, "doInBackground: " + (socket.getLocalSocketAddress()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onJoystickMoved(float x, float y,int id) {
        int intX =Math.round(x*100);
        int intY = Math.round(y*100);

        if(id == leftStick.getId())
        {
            Log.e(TAG, "onJoystickMoved: " + "Moving the left joystick" );

            if((intX<70&&intX>-70))
            {
                if(y<0)
                {
                    Log.e(TAG, "onJoystickMoved: " + flagForSectorOne );
                    Log.e(TAG, "onJoystickMoved: " + "WE MOVE FORWARD" );
                    if(flagForSectorOne==0)
                    {   shouldISend = true;
                        sendToBrain(shouldISend,"A");
                        resetValues(0);
                    }



                }
                else if  (y>0)
                {   Log.e(TAG, "onJoystickMoved: " +"WE MOVE BACKWARD" );
                    if (flagForSectorThree==0){
                        shouldISend=true;
                        sendToBrain(true,"B");
                        resetValues(2);
                    }
                }

            }

            if(intY<70&&intY>-70)
            {
                if(x>0){
                    Log.e(TAG, "onJoystickMoved: " + "WE GO RIGHT" );
                    if(flagForSectorTwo==0)
                    {
                        shouldISend=true;
                        sendToBrain(true,"C");
                        resetValues(1);
                    }
                }
                else if(x<0)
                {
                    Log.e(TAG, "onJoystickMoved: " + "WE GO LEFT" );
                    if(flagForSectorFour==0)
                    { shouldISend=true;
                        sendToBrain(true,"C");
                        resetValues(3);
                    }


                }

            }


        }
        else if(id == rightStick.getId()){
            Log.e(TAG, "onJoystickMoved: " + "Moving the right joystick" );

        }
        /*
        Making logic for dividing the circle in sectors here
         */


    }

    @Override
    public void onUserUnpress(float x, float y) {
        Log.e(TAG, "onUserUnpress:Center "  +  x*100 + "Y:"  +  y*100 );
        sendToBrain(true,"X");
        resetValues(90);
    }

    void sendToBrain(boolean bool, final String instruction)
    {   int j = 0;
        j++;

        if(shouldISend)
        {
            Log.e(TAG, "sendToBrain: " + "j = " +  j + "instruction" + instruction);
            if(socket!=null){
                Log.e(TAG, "sendToBrain: " + "IS THIS EXECUTED" );
                Runnable  r = new Runnable() {
                    @Override
                    public void run() {
                        printWriter.println(instruction);
                        printWriter.flush();
                    }
                };
                executorService.submit(r);




            }
        }
        shouldISend = false;

    }


    void resetValues(int identifier)
    {
       if(identifier == 0)
       {
           flagForSectorOne=1;
           flagForSectorTwo =0;
           flagForSectorThree=0;
           flagForSectorFour=0;
       }
       else if(identifier == 1)
       {
           flagForSectorOne=0;
           flagForSectorTwo =1;
           flagForSectorThree=0;
           flagForSectorFour=0;
       }
       else if(identifier==2)
       {
           flagForSectorOne=0;
           flagForSectorTwo =0;
           flagForSectorThree=1;
           flagForSectorFour=0;
       }
       else if(identifier ==4)
       {
           flagForSectorOne=0;
           flagForSectorTwo =0;
           flagForSectorThree=0;
           flagForSectorFour=1;
       }
       else
       {
           flagForSectorOne=0;
           flagForSectorTwo =0;
           flagForSectorThree=0;
           flagForSectorFour=0;
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
