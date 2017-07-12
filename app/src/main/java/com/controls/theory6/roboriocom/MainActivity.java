package com.controls.theory6.roboriocom;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class MainActivity extends AppCompatActivity {

    public static boolean clientRun;
    public final String host = "localhost";
    private static final String TAG = "Main";

    private String message;

    private Socket socket;

    NetworkTable table;

    TextView text;

    Client myClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.loadLibrary("ntcore");
        try {
            Process p = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageToSend.message = "start";
        /*socket = null;
        myClient = new Client();
        myClient.execute(socket);*/

        text = (TextView) findViewById(R.id.textView);
        enableUsbTethering(this);
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("roborio-1285-frc.local");
        NetworkTable.initialize();

        table = NetworkTable.getTable("Test");
        text.setText("roboRIO connected: "+ table.isConnected());
    }

    public void onClick(View view){
        final EditText input = (EditText) findViewById(R.id.inputText);
        messageToSend.message = input.getText().toString();
        table.putString("Phone Message",messageToSend.message);
        Toast.makeText(this, messageToSend.message,
                Toast.LENGTH_LONG).show();
        text.setText("roboRIO connected: "+ table.isConnected());
    }

    /*public static void runClient(Socket clientSocket){
        if(clientRun){
            Client myClient = new Client();
            myClient.execute(clientSocket);
        }
        else {
            clientRun = true;
        }
    }*/

    /**
     * Uses root to automatically enable USB tethering. This allows the entire startup sequence to
     * be completely automatic (as soon as the robot is powered, the phone boots, the app starts,
     * and it enables tethering by itself) which reduces the potential for field setup mistakes
     */
    public static void enableUsbTethering(final Context context) {
        Thread usbSetupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // keep polling until USB is up
                while(true) {
                    if (isUsbConnected(context) && findUsbTetheringInterface() == null) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // http://stackoverflow.com/a/24346101/1021196
                        try {
                            Log.i(TAG, "Enabling tethering");
                            Runtime.getRuntime().exec("su -c service call connectivity 33 i32 1");
                        } catch(Exception e) {
                            Log.e(TAG, "Error enabling tethering", e);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        usbSetupThread.setName("USB Tethering Setup Thread");
        usbSetupThread.setDaemon(true);
        usbSetupThread.start();
    }

    protected static NetworkInterface findUsbTetheringInterface() {
        try {
            Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = allInterfaces.nextElement();
                if(networkInterface.getName().equals("rndis0") && networkInterface.isUp()) {
                    return networkInterface;
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "Network error finding rndis0", e);
        }
        return null;
    }

    public static boolean isUsbConnected(Context context) {
        Intent intent = context.registerReceiver(null,
                new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }

}
