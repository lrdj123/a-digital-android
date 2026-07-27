package com.adigital.p2p.services;

    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.content.Context;
    import android.content.Intent;
    import androidx.core.app.NotificationCompat;
    import com.adigital.p2p.MainActivity;
    import com.adigital.p2p.R;
    import com.google.firebase.messaging.FirebaseMessagingService;
    import com.google.firebase.messaging.RemoteMessage;

    public class ADigitalFirebaseService extends FirebaseMessagingService {

      private static final String CHANNEL_ID   = "adigital_alertas";
      private static final String CHANNEL_NAME = "A DIGITAL — Alertas";

      @Override
      public void onMessageReceived(RemoteMessage message) {
          super.onMessageReceived(message);

          String tipo   = message.getData().get("tipo");
          String titulo = "🖐️ A DIGITAL";
          String corpo  = "Novo alerta recebido";

          if ("SOS".equals(tipo)) {
              titulo = "🚨 SOS RECEBIDO";
              corpo  = "Um contato seu acionou o SOS! Verifique imediatamente.";
          } else if ("TRANSACAO".equals(tipo)) {
              titulo = "💰 Nova Transação";
              corpo  = "Uma transação P2P foi iniciada.";
          }

          mostrarNotificacao(titulo, corpo);
      }

      private void mostrarNotificacao(String titulo, String corpo) {
          NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          NotificationChannel ch = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
          nm.createNotificationChannel(ch);

          PendingIntent pi = PendingIntent.getActivity(this, 0,
              new Intent(this, MainActivity.class),
              PendingIntent.FLAG_IMMUTABLE);

          NotificationCompat.Builder notif = new NotificationCompat.Builder(this, CHANNEL_ID)
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContentTitle(titulo)
              .setContentText(corpo)
              .setPriority(NotificationCompat.PRIORITY_HIGH)
              .setAutoCancel(true)
              .setContentIntent(pi);

          nm.notify((int) System.currentTimeMillis(), notif.build());
      }

      @Override
      public void onNewToken(String token) {
          // Salvar token FCM no Firestore para envio de SOS
      }
    }
    