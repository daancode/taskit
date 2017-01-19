package code.daan.taskit.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import code.daan.taskit.MainActivity;
import code.daan.taskit.R;
import code.daan.taskit.database.Task;

public class TaskReminder extends BroadcastReceiver {

    NotificationManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        Task task = Task.findById(Task.class, taskId);

        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent r_intent = new Intent(context, MainActivity.class);
        r_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, taskId, r_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.logo2)
                .setContentText("Tap to open Taskit")
                .setContentTitle(task.title)
                .setAutoCancel(true);

        Notification note = builder.build();
        note.defaults |= Notification.DEFAULT_LIGHTS;
        note.defaults |= Notification.DEFAULT_SOUND;
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.priority = Notification.PRIORITY_MAX;
        manager.notify(taskId, note);

        task.isReminderSet = false;
        task.save();
    }
}

