package avishkaar.com.joystickdemo;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements JoyStick.JoystickListener {
    private static final String TAG = "MainActivity";
    JoyStick rightStick,leftStick;
    static Socket socket;
    static PrintWriter printWriter;
    int flagForSectorOne,flagForSectorTwo,flagForSectorThree,flagForSectorFour;
    ExecutorService executorService;
    TextView connectionStatus;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftStick = findViewById(R.id.leftStick);
        connectionStatus = findViewById(R.id.connectionStatus);

        //Making joystick background translucent

        leftStick.setZOrderOnTop(true);
        SurfaceHolder surfaceHolder = leftStick.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
         rightStick = findViewById(R.id.rightStick);
        rightStick.setZOrderOnTop(true);
        SurfaceHolder holder =  rightStick.getHolder();
        holder.setFormat(PixelFormat.TRANSLUCENT);
        connectionStatus.setText("Not Connected");



        executorService = Executors.newSingleThreadExecutor();

    }



    public void connectServer(View view) {
        new StartTCPConnection(MainActivity.this).execute();
    }

    /** Async call to TCP connection establishment **/

    static class StartTCPConnection extends AsyncTask<Void,Void,Void>{
        WeakReference <MainActivity> reference;

         StartTCPConnection(MainActivity mainActivity) {
             reference = new WeakReference<>(mainActivity);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = reference.get();
            if(socket.isConnected()) {
                activity.connectionStatus.setText("Socket Connected");
            }else {
                activity.connectionStatus.setText("Not Connected");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                socket = new Socket("192.168.4.1",900);
                Log.e(TAG, "Is Socket Connected? " + socket.isConnected() );
                printWriter = new PrintWriter(socket.getOutputStream());
                Log.i(TAG, "Checking if socket is really connected " + (socket.getLocalSocketAddress()));
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


    /**
     Making logic for dividing the circle in sectors here
     THIS PART CORRESPONDS TO THE INTERFACE CALLS FROM THE VIEW
     */



    @Override
    public void onJoystickMoved(float x, float y,int id) {
        int intX =Math.round(x*100);
        int intY = Math.round(y*100);





        if(id == leftStick.getId())
        {

            if((intX<70&&intX>-70))
            {
                if(y<=0)
                {
                    if(flagForSectorOne==0)
                    {

                        sendToBrain("A");
                        resetValues(0);

                    }

                }
                else if  (y>0)
                {
                    if (flagForSectorThree==0){

                        sendToBrain("W");
                        resetValues(2);

                    }
                }

            }

            if(intY<70&&intY>-70)
            {
                if(x>0){
                    if(flagForSectorTwo==0)
                    {

                        sendToBrain("G");
                        resetValues(1);

                    }
                }
                else if(x<0)
                {
                    if(flagForSectorFour==0)
                    {

                        sendToBrain("B");
                        resetValues(3);

                    }


                }

            }


        }
        else if(id == rightStick.getId()){
            Log.e(TAG, "onJoystickMoved: " + "Moving the right joystick" );

        }
    }

    @Override
    public void onUserUnpress(float x, float y) {
        Log.e(TAG, "onUserUnpress: Center ");
        sendToBrain("X");
        resetValues(90);
    }

    @Override
    public void joystickAngle(int angle) {




    }


    /**This method finally sends the data to TCP server **/

    void sendToBrain(final String instruction)
    {

        if (socket != null && socket.isConnected()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run:Runnable Instruction sent   " + instruction);
                    printWriter.println(instruction);
                    printWriter.flush();
                }
            };
            executorService.submit(r);


        }



    }

/**
 *       This method is used to make a function call only one time as the input is continuous
*       Further fine tuning is required for making th joy stick functional
*       Author : Faizan Mir
*
*
*
* */
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
       else if(identifier == 2)
       {
           flagForSectorOne=0;
           flagForSectorTwo =0;
           flagForSectorThree=1;
           flagForSectorFour=0;
       }
       else if(identifier ==3)
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
/** Closing socket and Executor service to release resources **/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        try {
            if(socket!=null){
                socket.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
