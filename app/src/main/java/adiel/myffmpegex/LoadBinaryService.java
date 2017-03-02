package adiel.myffmpegex;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LoadBinaryService extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_LOAD_BINARY = "adiel.myffmpegex.action.FOO";

    public LoadBinaryService()
    {
        super();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionLoadBinary(Context context,Intent intent) {
        intent.setAction(ACTION_LOAD_BINARY);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
            final String action = intent.getAction();
            if (ACTION_LOAD_BINARY.equals(action)) {
                handleActionFoo(resultReceiver);
            }
        }
        return START_NOT_STICKY;
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     * @param resultReceiver
     */
    private void handleActionFoo(ResultReceiver resultReceiver) {
        // TODO: Handle action Foo
        loadBinary(resultReceiver);
    }

    private void loadBinary(final ResultReceiver resultReceiver){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("temp","loadBinary from seperate process onStart");
                    resultReceiver.send(1,null);
                }

                @Override
                public void onFailure() {
                    Log.d("temp","loadBinary from seperate process onFailure");
                    resultReceiver.send(2,null);
                }

                @Override
                public void onSuccess() {
                    Log.d("temp","loadBinary from seperate process onSuccess");
                    resultReceiver.send(3,null);
                }

                @Override
                public void onFinish() {
                    Log.d("temp","loadBinary from seperate process onFinish");
                    resultReceiver.send(4,null);
                    stopSelf();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("temp","onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
