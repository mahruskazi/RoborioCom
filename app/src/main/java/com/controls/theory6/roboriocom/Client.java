package com.controls.theory6.roboriocom;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends AsyncTask<Socket, Void, Socket> {
    String host = "localhost";
    int port = 3000;
    String oldMessage;

    private static final String TAG = "ClientFilter";

    Client(){
    }

    @Override
    protected Socket doInBackground(Socket... arg) {
        Socket socket = arg[0];
        Log.d("Test", "In null");
        try {
            if (socket == null) {
                socket = new Socket(host, port);
                socket.setSoTimeout(100);
                Log.d("Test", "In Create Sock");
            } else if(messageToSend.message!=null){
                if(!socket.isConnected())
                    socket = new Socket(host, port);

                Log.d(TAG, "State " + socket.isConnected());
                OutputStream os = socket.getOutputStream();
                os.write(messageToSend.message.getBytes());
                oldMessage = messageToSend.message;
            }
        } catch (IOException e) {
            MainActivity.clientRun = false;
        }
        return socket;
    }

    //@Override
    /*protected void onPostExecute(Socket result){
        MainActivity.runClient(result);
    }*/
}
