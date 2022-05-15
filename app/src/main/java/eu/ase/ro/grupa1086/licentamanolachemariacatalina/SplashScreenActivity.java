package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();

//        new Handler().postDelayed(new Runnable() {
//
//// Using handler with postDelayed called runnable run method
//
//            @Override
//
//            public void run() {
//
//                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
//                i.putExtra("splashScreen", "opened");
//
//                startActivity(i);
//
//                // close this activity
//
//                finish();
//
//            }
//
//        }, 5*1000);
    }
}