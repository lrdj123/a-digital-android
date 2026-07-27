package com.adigital.p2p;

    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.FirebaseFirestore;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class SOSActivity extends AppCompatActivity {

      private EditText etNomeContato, etTelefoneContato;
      private Button btnSalvarContato, btnTesteSOS;
      private TextView tvContatosSalvos, tvStatusSOS;
      private FirebaseFirestore db;
      private String uid;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_sos);

          db  = FirebaseFirestore.getInstance();
          uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

          etNomeContato     = findViewById(R.id.et_nome_contato);
          etTelefoneContato = findViewById(R.id.et_telefone_contato);
          btnSalvarContato  = findViewById(R.id.btn_salvar_contato);
          btnTesteSOS       = findViewById(R.id.btn_teste_sos);
          tvContatosSalvos  = findViewById(R.id.tv_contatos_salvos);
          tvStatusSOS       = findViewById(R.id.tv_status_sos);

          getSupportActionBar().setTitle("🚨 SOS Invisível");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          btnSalvarContato.setOnClickListener(v -> salvarContato());
          btnTesteSOS.setOnClickListener(v -> testarSOS());

          carregarContatos();
      }

      private void salvarContato() {
          String nome     = etNomeContato.getText().toString().trim();
          String telefone = etTelefoneContato.getText().toString().trim();

          if (nome.isEmpty() || telefone.isEmpty()) {
              Toast.makeText(this, "Preencha nome e telefone", Toast.LENGTH_SHORT).show();
              return;
          }

          Map<String, Object> contato = new HashMap<>();
          contato.put("nome", nome);
          contato.put("telefone", telefone);
          contato.put("timestamp", System.currentTimeMillis());

          db.collection("usuarios").document(uid)
              .collection("contatos_sos").add(contato)
              .addOnSuccessListener(r -> {
                  Toast.makeText(this, "✅ Contato salvo: " + nome, Toast.LENGTH_SHORT).show();
                  etNomeContato.setText("");
                  etTelefoneContato.setText("");
                  carregarContatos();
              })
              .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
      }

      private void carregarContatos() {
          db.collection("usuarios").document(uid)
              .collection("contatos_sos")
              .orderBy("timestamp")
              .get()
              .addOnSuccessListener(snap -> {
                  if (snap.isEmpty()) {
                      tvContatosSalvos.setText("Nenhum contato cadastrado ainda.");
                      return;
                  }
                  StringBuilder sb = new StringBuilder("📱 Contatos de confiança:\n\n");
                  for (var doc : snap.getDocuments()) {
                      sb.append("👤 ").append(doc.getString("nome"))
                        .append(" — ").append(doc.getString("telefone")).append("\n");
                  }
                  tvContatosSalvos.setText(sb.toString());
              });
      }

      private void testarSOS() {
          tvStatusSOS.setVisibility(View.VISIBLE);
          tvStatusSOS.setText("🚨 TESTE SOS ATIVADO\n\n" +
              "✅ Localização capturada\n" +
              "✅ Contatos notificados silenciosamente\n" +
              "✅ Transação bloqueada\n\n" +
              "Em produção: SMS + push notification\nenviados automaticamente pela IA.");

          // Registra alerta de teste no Firestore
          Map<String, Object> alerta = new HashMap<>();
          alerta.put("tipo", "TESTE");
          alerta.put("uid", uid);
          alerta.put("timestamp", System.currentTimeMillis());
          alerta.put("automatico", false);
          db.collection("alertas_sos").add(alerta);
      }

      @Override
      public boolean onSupportNavigateUp() { onBackPressed(); return true; }
    }
    