package eu.ase.ro.grupa1086.licentamanolachemariacatalina.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
            }
        };

        thread.start();
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