package com.adigital.p2p;

    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.view.animation.AlphaAnimation;
    import android.view.animation.Animation;
    import android.widget.TextView;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.firebase.auth.FirebaseAuth;

    public class SplashActivity extends AppCompatActivity {

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_splash);

          TextView tvLogo = findViewById(R.id.tv_logo);
          TextView tvSlogan = findViewById(R.id.tv_slogan);

          AlphaAnimation fade = new AlphaAnimation(0f, 1f);
          fade.setDuration(1200);
          tvLogo.startAnimation(fade);
          tvSlogan.startAnimation(fade);

          new Handler(Looper.getMainLooper()).postDelayed(() -> {
              FirebaseAuth auth = FirebaseAuth.getInstance();
              Intent next;
              if (auth.getCurrentUser() != null) {
                  next = new Intent(this, MainActivity.class);
              } else {
                  next = new Intent(this, LoginActivity.class);
              }
              startActivity(next);
          }, 2200);
      }
    }
    