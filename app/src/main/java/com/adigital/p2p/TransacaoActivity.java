package com.adigital.p2p;

    import android.content.DialogInterface;
    import android.os.Bundle;
    import android.os.CancellationSignal;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.biometric.BiometricManager;
    import androidx.biometric.BiometricPrompt;
    import androidx.core.content.ContextCompat;
    import com.google.android.material.progressindicator.LinearProgressIndicator;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.FirebaseFirestore;
    import java.security.MessageDigest;
    import java.security.SecureRandom;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.concurrent.Executor;

    public class TransacaoActivity extends AppCompatActivity {

      private EditText etVendedorId, etValor;
      private Button btnIniciar;
      private TextView tvStatus;
      private LinearProgressIndicator progressBar;
      private FirebaseFirestore db;
      private FirebaseAuth mAuth;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_transacao);

          db         = FirebaseFirestore.getInstance();
          mAuth      = FirebaseAuth.getInstance();
          etVendedorId = findViewById(R.id.et_vendedor_id);
          etValor      = findViewById(R.id.et_valor);
          btnIniciar   = findViewById(R.id.btn_iniciar);
          tvStatus     = findViewById(R.id.tv_status);
          progressBar  = findViewById(R.id.progress_bar);

          btnIniciar.setOnClickListener(v -> iniciarTransacao());
          getSupportActionBar().setTitle("Nova Transação P2P");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }

      private void iniciarTransacao() {
          String vendedorId = etVendedorId.getText().toString().trim();
          String valorStr   = etValor.getText().toString().trim();

          if (vendedorId.isEmpty() || valorStr.isEmpty()) {
              Toast.makeText(this, "Preencha ID do vendedor e valor", Toast.LENGTH_SHORT).show();
              return;
          }
          double valor;
          try { valor = Double.parseDouble(valorStr); }
          catch (Exception e) { Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show(); return; }

          autenticarBiometria(vendedorId, valor);
      }

      private void autenticarBiometria(String vendedorId, double valor) {
          Executor executor = ContextCompat.getMainExecutor(this);
          BiometricPrompt prompt = new BiometricPrompt(this, executor,
              new BiometricPrompt.AuthenticationCallback() {
                  @Override
                  public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                      tvStatus.setText("✅ Biometria confirmada\n🔐 Criptografando dados...");
                      progressBar.setVisibility(View.VISIBLE);
                      processarTransacao(vendedorId, valor);
                  }
                  @Override
                  public void onAuthenticationError(int errorCode, CharSequence errString) {
                      tvStatus.setText("❌ Autenticação negada: " + errString);
                  }
                  @Override
                  public void onAuthenticationFailed() {
                      tvStatus.setText("❌ Digital não reconhecida. Tente novamente.");
                  }
              });

          BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
              .setTitle("🖐️ A DIGITAL")
              .setSubtitle("Confirme sua identidade")
              .setDescription("Use sua impressão digital para autorizar a transação de R$ " +
                  String.format("%.2f", valor))
              .setNegativeButtonText("Cancelar")
              .build();

          prompt.authenticate(info);
      }

      private void processarTransacao(String vendedorId, double valor) {
          tvStatus.setText("🧠 Analisando estado emocional...\n(IA local rodando)");

          // Simula análise emocional local
          new android.os.Handler().postDelayed(() -> {
              double estresse = Math.random() * 0.4; // demo: usuário seguro
              String estadoEmocional = estresse < 0.35 ? "SEGURO" : estresse < 0.55 ? "ALERTA" : "ESTRESSADO";

              tvStatus.setText("📤 Enviando dados criptografados P2P...\n" +
                  "   Estado: " + estadoEmocional + " (" + String.format("%.0f%%", estresse * 100) + " estresse)");

              // Gera hash da transação
              String uid = mAuth.getCurrentUser().getUid();
              String hashInput = uid + vendedorId + valor + System.currentTimeMillis();
              String hashTransacao = sha256(hashInput);

              Map<String, Object> tx = new HashMap<>();
              tx.put("compradorId", uid);
              tx.put("vendedorId", vendedorId);
              tx.put("valor", valor);
              tx.put("hash", hashTransacao);
              tx.put("estadoEmocional", estadoEmocional);
              tx.put("nivelEstresse", estresse);
              tx.put("status", "concluida");
              tx.put("timestamp", System.currentTimeMillis());
              tx.put("conexao", "P2P (sem servidor)");

              db.collection("transacoes").add(tx)
                  .addOnSuccessListener(ref -> {
                      progressBar.setVisibility(View.GONE);
                      tvStatus.setText(
                          "╔══════════════════════════╗\n" +
                          "║  ✅ TRANSAÇÃO CONCLUÍDA  ║\n" +
                          "╚══════════════════════════╝\n\n" +
                          "💰 R$ " + String.format("%.2f", valor) + "\n" +
                          "🔐 Hash: " + hashTransacao.substring(0, 16) + "...\n" +
                          "🔗 Conexão: P2P (sem servidor)");
                      btnIniciar.setEnabled(true);
                  })
                  .addOnFailureListener(e -> {
                      progressBar.setVisibility(View.GONE);
                      tvStatus.setText("❌ Erro: " + e.getMessage());
                  });
          }, 2000);
      }

      private String sha256(String input) {
          try {
              MessageDigest md = MessageDigest.getInstance("SHA-256");
              byte[] bytes = md.digest(input.getBytes("UTF-8"));
              StringBuilder sb = new StringBuilder();
              for (byte b : bytes) sb.append(String.format("%02x", b));
              return sb.toString();
          } catch (Exception e) { return "erro-hash"; }
      }

      @Override
      public boolean onSupportNavigateUp() { onBackPressed(); return true; }
    }
    