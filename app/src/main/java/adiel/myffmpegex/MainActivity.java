package adiel.myffmpegex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final int REQUEST_VIDEO_CAPTURE = 1;
    VideoView  mVideoView;
    Uri videoUri;

    TextView tvResult;
    private Uri _videoFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.activity_main);
            mVideoView = (VideoView) findViewById(R.id.mVideoView);
            tvResult = (TextView) findViewById(R.id.tvResult);
            findViewById(R.id.btnCompress).setOnClickListener(this);
            findViewById(R.id.takeVideo).setOnClickListener(this);
            loadBinary();
        }else {
            startActivity(new Intent(MainActivity.this,PermissionActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.takeVideo:
                    takeVideo();
                break;

            case R.id.btnCompress:
                if(videoUri!=null) {
                    String vidPath = videoUri.getPath();
                    Uri vidPathOutput = generateTimeStampVideoFileUri("FfOutput");
                    String[] cmd = new String[]{"-i", vidPath, "-b:v", "2.4M", "-bufsize", "2.404M", "-maxrate", "5M", "-preset", "fast", vidPathOutput.getPath()};
                    loadBinary();
                    exeCompress(cmd);
                }else {
                    Toast.makeText(this, "first take video", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }



    public void compress(View view) {


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
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
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
                    String s = "exeCompress onSuccess:" + message;
                    Log.d("temp",s);
                    //tvResult.setText(s);
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



    private void takeVideo(){
        _videoFileUri = generateTimeStampVideoFileUri("FfInput");
        if (_videoFileUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _videoFileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            mVideoView.setVideoURI(videoUri);
            tvResult.setText(videoUri.getPath());
            Log.d("temp","vidPath:"+videoUri.getPath() );
        }
    }


    public void play(View view) {
        mVideoView.start();
    }

    private Uri generateTimeStampVideoFileUri(String dirName) {
        Uri photoFileUri = null;
        File outputDir = getVideoDirectory(dirName);

        if(outputDir != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            String photoFileName = "VID_" + timeStamp + ".mp4";

            File photoFile = new File(outputDir, photoFileName);
            photoFileUri = Uri.fromFile(photoFile);
        }


        return photoFileUri;
    }


    private File getVideoDirectory(String dirName) {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            outputDir = new File(pictureDir, dirName);
            if (!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    Log.d("temp","Failed to create directory: " + outputDir.getAbsolutePath());
                    outputDir = null;
                }
            }
        }
        Log.d("temp","outputDir:"+outputDir);
        return outputDir;
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, "onConfigurationChanged", Toast.LENGTH_SHORT).show();
        Log.d("temp","onConfigurationChanged");
    }


}
