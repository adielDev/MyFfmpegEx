package adiel.myffmpegex;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static adiel.myffmpegex.Util.generateTimeStampVideoFileUri;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final int IMAGE_PICKER_SELECT = 1;
    private  Handler uiHandler;
    Uri selectedMediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            findViewById(R.id.btnLoadBinari).setOnClickListener(this);
            findViewById(R.id.btnLoadBinariInSEperateProcess).setOnClickListener(this);
            findViewById(R.id.btnPickVideo).setOnClickListener(this);
        uiHandler = new Handler();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnLoadBinari:
                    loadBinary();
                break;

            case R.id.btnLoadBinariInSEperateProcess:
                MyResultReceiver myResultReciever = new MyResultReceiver(null);
                Intent intent =new Intent(MainActivity.this,LoadBinaryService.class);
                intent.putExtra("receiver",myResultReciever);
                LoadBinaryService.startActionLoadBinary(this,intent);
                break;
            case R.id.btnPickVideo:
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/* video/*");
                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);

                break;

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            selectedMediaUri = data.getData();

            if (selectedMediaUri.toString().contains("images")) {
                //handle image
            } else if (selectedMediaUri.toString().contains("video")) {
                //handle video
                Uri vidPathOutput = generateTimeStampVideoFileUri("FfOutput");
                String[] cmd= new String[]{"-i",selectedMediaUri.getPath(),"-b:v","2.4M","-bufsize","2.404M","-maxrate","5M","-preset","fast",vidPathOutput.getPath()};
                exeCompress(cmd);
                Toast.makeText(this, "start compress", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void loadBinary(){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("temp","loadBinary onStart");
                }

                @Override
                public void onFailure() {
                    Log.d("temp","loadBinary onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d("temp","loadBinary onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d("temp","loadBinary onFinish");
                    Toast.makeText(MainActivity.this, "open app again\nthe layout reverse", Toast.LENGTH_LONG).show();
                    MainActivity.super.onBackPressed();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    public class MyResultReceiver extends ResultReceiver {

        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String result ="-1";
            switch (resultCode){
                case 1:
                    result =   "on start";
                    break;
                case 2:
                    result =   "on failure";
                    break;
                case 3:
                    result =   "on succees";
                    break;
                case 4:
                    result =   "on finish";
                    break;
            }
            Log.d("temp","onReceiveResult:resultCode"+result);
            final String finalResult = result;
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                   // tvSTate.setText(finalResult +" from seperate process");

                    Toast.makeText(MainActivity.this, "open app again\nthe layout OK", Toast.LENGTH_LONG).show();
                    MainActivity.super.onBackPressed();
                }
            });
           // Toast.makeText(MainActivity.this, "result:"+result, Toast.LENGTH_SHORT).show();

        }
    }

    public void compress(Uri videoUri) {
        String vidPath = videoUri.getPath();
        Uri vidPathOutput = generateTimeStampVideoFileUri("FfOutput");
        String[] cmd= new String[]{"-i",vidPath,"-b:v","2.4M","-bufsize","2.404M","-maxrate","5M","-preset","fast",vidPathOutput.getPath()};
        loadBinary();
        exeCompress(cmd);

    }


    private void exeCompress(String[] cmd){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("temp","exeCompress onStart");
                    String s = "exeCompress onStart:";
                    Log.d("temp",s);
                }

                @Override
                public void onProgress(String message) {
                    String s = "exeCompress onProgress:" + message;
                    Log.d("temp",s);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    String s = "exeCompress onFailure:" + message;
                    Log.d("temp",s);
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    Log.d("temp","exeCompress onSuccess:"+message);
                    String s = "exeCompress onSuccess:" + message;
                    Log.d("temp",s);
                }

                @Override
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "onFinish", Toast.LENGTH_SHORT).show();
                    String s = "exeCompress onFinish:" ;
                    Log.d("temp",s);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
            Log.d("temp","FFmpegCommandAlreadyRunningException");
            String s = "FFmpegCommandAlreadyRunningException";
            Log.d("temp",s);
        }

    }

}
