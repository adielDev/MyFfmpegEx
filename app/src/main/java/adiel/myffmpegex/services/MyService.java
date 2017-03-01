package adiel.myffmpegex.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import adiel.myffmpegex.MainActivity;

public class MyService extends Service {

    public static final String TAG =MyService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        loadBinary();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        String [] cmd = intent.getStringArrayExtra("cmd");
        exeCompress(cmd);
        return START_NOT_STICKY;
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void exeCompress(String[] cmd){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG,"exeCompress onStart");
                    String s = "exeCompress onStart:";
                    Log.d(TAG,s);
                }

                @Override
                public void onProgress(String message) {
                    String s = "exeCompress onProgress:" + message;
                    Log.d(TAG,s);
                }

                @Override
                public void onFailure(String message) {
                    String s = "exeCompress onFailure:" + message;
                    Log.d(TAG,s);
                }

                @Override
                public void onSuccess(String message) {
                    String s = "exeCompress onSuccess:" + message;
                    Log.d(TAG,s);
                }

                @Override
                public void onFinish() {
                    String s = "exeCompress onFinish:" ;
                    Log.d(TAG,s);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
            String s = "FFmpegCommandAlreadyRunningException";
            Log.d(TAG,s);
        }finally {
            stopSelf();
        }

    }

    private void loadBinary(){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG,"loadBinary onStart");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG,"loadBinary onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG,"loadBinary onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d(TAG,"loadBinary onFinish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace();
            Log.d(TAG,"FFmpegNotSupportedException");
            stopSelf();
        }
    }
}
