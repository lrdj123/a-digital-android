package com.adigital.p2p;

    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.TextView;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.FirebaseFirestore;

    public class MainActivity extends AppCompatActivity {

      private FirebaseAuth mAuth;
      private FirebaseFirestore db;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          mAuth = FirebaseAuth.getInstance();
          db    = FirebaseFirestore.getInstance();

          FirebaseUser user = mAuth.getCurrentUser();
          if (user == null) {
              startActivity(new Intent(this, LoginActivity.class));
              finish();
              return;
          }

          // Carrega nome do usuário
          db.collection("usuarios").document(user.getUid()).get()
              .addOnSuccessListener(doc -> {
                  if (doc.exists()) {
                      String nome = doc.getString("nome");
                      TextView tvBemVindo = findViewById(R.id.tv_bem_vindo);
                      if (tvBemVindo != null && nome != null)
                          tvBemVindo.setText("Olá, " + nome.split(" ")[0] + " 🖐️");
                  }
              });

          // Cards de funcionalidades
          CardView cardTransacao = findViewById(R.id.card_transacao);
          CardView cardSos       = findViewById(R.id.card_sos);
          CardView cardHistorico = findViewById(R.id.card_historico);
          CardView cardSair      = findViewById(R.id.card_sair);

          if (cardTransacao != null)
              cardTransacao.setOnClickListener(v ->
                  startActivity(new Intent(this, TransacaoActivity.class)));

          if (cardSos != null)
              cardSos.setOnClickListener(v ->
                  startActivity(new Intent(this, SOSActivity.class)));

          if (cardHistorico != null)
              cardHistorico.setOnClickListener(v ->
                  startActivity(new Intent(this, HistoricoActivity.class)));

          if (cardSair != null)
              cardSair.setOnClickListener(v -> {
                  mAuth.signOut();
                  startActivity(new Intent(this, LoginActivity.class));
                  finish();
              });
      }
    }
    