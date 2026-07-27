package com.adigital.p2p;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.android.material.progressindicator.CircularProgressIndicator;
    import com.google.firebase.auth.FirebaseAuth;

    public class LoginActivity extends AppCompatActivity {

      private EditText etEmail, etSenha;
      private Button btnEntrar;
      private CircularProgressIndicator progress;
      private FirebaseAuth mAuth;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_login);

          mAuth    = FirebaseAuth.getInstance();
          etEmail  = findViewById(R.id.et_email);
          etSenha  = findViewById(R.id.et_senha);
          btnEntrar = findViewById(R.id.btn_entrar);
          progress  = findViewById(R.id.progress);

          btnEntrar.setOnClickListener(v -> login());

          TextView tvCadastro = findViewById(R.id.tv_cadastro);
          tvCadastro.setOnClickListener(v ->
              startActivity(new Intent(this, CadastroActivity.class)));
      }

      private void login() {
          String email = etEmail.getText().toString().trim();
          String senha = etSenha.getText().toString().trim();

          if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
              Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
              return;
          }

          progress.setVisibility(View.VISIBLE);
          btnEntrar.setEnabled(false);

          mAuth.signInWithEmailAndPassword(email, senha)
              .addOnSuccessListener(r -> {
                  startActivity(new Intent(this, MainActivity.class));
                  finish();
              })
              .addOnFailureListener(e -> {
                  progress.setVisibility(View.GONE);
                  btnEntrar.setEnabled(true);
                  Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_LONG).show();
              });
      }
    }
    