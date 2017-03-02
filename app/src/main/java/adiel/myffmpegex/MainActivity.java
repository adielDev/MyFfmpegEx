package adiel.myffmpegex;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private  Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            findViewById(R.id.btnLoadBinari).setOnClickListener(this);
            findViewById(R.id.btnLoadBinariInSEperateProcess).setOnClickListener(this);
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


}
