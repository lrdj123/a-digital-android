package com.adigital.p2p;

    import android.os.Bundle;
    import android.view.View;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.Query;

    public class HistoricoActivity extends AppCompatActivity {

      private TextView tvHistorico;
      private ProgressBar progress;
      private FirebaseFirestore db;
      private String uid;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_historico);

          db        = FirebaseFirestore.getInstance();
          uid       = FirebaseAuth.getInstance().getCurrentUser().getUid();
          tvHistorico = findViewById(R.id.tv_historico);
          progress    = findViewById(R.id.progress);

          getSupportActionBar().setTitle("📋 Histórico de Transações");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          carregarHistorico();
      }

      private void carregarHistorico() {
          progress.setVisibility(View.VISIBLE);
          db.collection("transacoes")
              .whereEqualTo("compradorId", uid)
              .orderBy("timestamp", Query.Direction.DESCENDING)
              .limit(20)
              .get()
              .addOnSuccessListener(snap -> {
                  progress.setVisibility(View.GONE);
                  if (snap.isEmpty()) {
                      tvHistorico.setText("Nenhuma transação encontrada.\nFaça sua primeira transação P2P!");
                      return;
                  }
                  StringBuilder sb = new StringBuilder();
                  for (var doc : snap.getDocuments()) {
                      Double valor    = doc.getDouble("valor");
                      String hash     = doc.getString("hash");
                      String status   = doc.getString("status");
                      String vendedor = doc.getString("vendedorId");
                      Long ts         = doc.getLong("timestamp");

                      sb.append("──────────────────────\n");
                      sb.append("💰 R$ ").append(String.format("%.2f", valor)).append("\n");
                      sb.append("👤 Vendedor: ").append(vendedor != null ? vendedor : "—").append("\n");
                      sb.append("✅ Status: ").append(status).append("\n");
                      sb.append("🔐 Hash: ").append(hash != null ? hash.substring(0, 16) : "—").append("...\n");
                      if (ts != null) {
                          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault());
                          sb.append("📅 ").append(sdf.format(new java.util.Date(ts))).append("\n");
                      }
                      sb.append("\n");
                  }
                  tvHistorico.setText(sb.toString());
              })
              .addOnFailureListener(e -> {
                  progress.setVisibility(View.GONE);
                  tvHistorico.setText("Erro ao carregar histórico: " + e.getMessage());
              });
      }

      @Override
      public boolean onSupportNavigateUp() { onBackPressed(); return true; }
    }
    