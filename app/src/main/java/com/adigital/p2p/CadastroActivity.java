package com.adigital.p2p;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.android.material.progressindicator.CircularProgressIndicator;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.FirebaseFirestore;
    import java.util.HashMap;
    import java.util.Map;

    public class CadastroActivity extends AppCompatActivity {

      private EditText etNome, etEmail, etSenha, etTelefone;
      private Button btnCadastrar;
      private CircularProgressIndicator progress;
      private FirebaseAuth mAuth;
      private FirebaseFirestore db;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_cadastro);

          mAuth      = FirebaseAuth.getInstance();
          db         = FirebaseFirestore.getInstance();
          etNome     = findViewById(R.id.et_nome);
          etEmail    = findViewById(R.id.et_email);
          etSenha    = findViewById(R.id.et_senha);
          etTelefone = findViewById(R.id.et_telefone);
          btnCadastrar = findViewById(R.id.btn_cadastrar);
          progress     = findViewById(R.id.progress);

          btnCadastrar.setOnClickListener(v -> cadastrar());
          findViewById(R.id.tv_login).setOnClickListener(v -> finish());
      }

      private void cadastrar() {
          String nome     = etNome.getText().toString().trim();
          String email    = etEmail.getText().toString().trim();
          String senha    = etSenha.getText().toString().trim();
          String telefone = etTelefone.getText().toString().trim();

          if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email)
                  || TextUtils.isEmpty(senha) || TextUtils.isEmpty(telefone)) {
              Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
              return;
          }
          if (senha.length() < 6) {
              Toast.makeText(this, "Senha deve ter ao menos 6 caracteres", Toast.LENGTH_SHORT).show();
              return;
          }

          progress.setVisibility(View.VISIBLE);
          btnCadastrar.setEnabled(false);

          mAuth.createUserWithEmailAndPassword(email, senha)
              .addOnSuccessListener(r -> {
                  String uid = r.getUser().getUid();
                  Map<String, Object> user = new HashMap<>();
                  user.put("nome", nome);
                  user.put("email", email);
                  user.put("telefone", telefone);
                  user.put("uid", uid);
                  user.put("criadoEm", System.currentTimeMillis());
                  user.put("sos_ativo", true);

                  db.collection("usuarios").document(uid).set(user)
                      .addOnSuccessListener(a -> {
                          startActivity(new Intent(this, MainActivity.class));
                          finish();
                      });
              })
              .addOnFailureListener(e -> {
                  progress.setVisibility(View.GONE);
                  btnCadastrar.setEnabled(true);
                  Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
              });
      }
    }
    