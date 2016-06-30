package gagan.com.communities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import gagan.com.communities.activites.MainTabActivity;
import gagan.com.communities.activites.SplashActivity;
import gagan.com.communities.activites.fragment.HomeFragment;
import gagan.com.communities.activites.fragment.NotificationTabFragment;
import gagan.com.communities.adapters.NotificationAdapter;
import gagan.com.communities.utills.GlobalConstants;
import gagan.com.communities.utills.Notification;
import gagan.com.communities.utills.SharedPrefHelper;

public class GCMIntentService extends GCMBaseIntentService
{

    static int flag = 0;

    Bundle bun;

    public GCMIntentService()
    {
        super(GlobalConstants.SENDER_ID);
    }


    private void showNotification(Context context, String message, Intent intent)
    {


        Intent notificationIntent;

        if (isLoggedIn(context))
        {
            notificationIntent = intent;
        }
        else
        {
            notificationIntent = new Intent(context, SplashActivity.class);
        }


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);


//        if (!taskData.getImage().isEmpty())
//        {
//
//            try
//            {
//                Uri imageUri = Uri.parse(taskData.getImage());
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
////        }
//                builder.setLargeIcon(bitmap);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.grey_bg));
//        }

        builder.setContentTitle("Neibr");
        builder.setContentText(message);
//        builder.setSubText("Tap to open EasyBeezee and set reminders..!");


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (defaultSound == null)
        {
            defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (defaultSound == null)
            {
                defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }

        builder.setSound(defaultSound);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
//		Log.i(TAG, "Device registered: regId = " + registrationId);

    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
//		Log.i(TAG, "Device unregistered");

    }


    @Override
    protected void onMessage(Context context, Intent intent)
    {
        try
        {
            bun = intent.getExtras();
            final String message = intent.getStringExtra("message");
            Log.e("++++GCM message++++", message);

//            Bundle[{content=28, message=Gagan Deep sent you a message, title=message recieved, collapse_key=do_not_collapse, status=9, sound=1, senderID=13, message_id=524, vibrate=1, from=679103909775, datetime=2016-05-25 22:45:16}]
//            {"message":"You have successfully liked the post title: \"for liki dislike test post\"",
//                    "title":"Post Liked","status":2,"vibrate":1,"sound":1,"post_id":"45"}

//            JSONObject jobj = new JSONObject(message);
            try
            {
                if (intent.getStringExtra("status").equals(Notification.MessageRecieved.getValue() + ""))
                {
                    sendBroadcast(new Intent(GlobalConstants.UPDATE_CHAT));
                    sendBroadcast(new Intent(GlobalConstants.UPDATE_MSG_FRAGMENT));

                    MainTabActivity.tabToOpen = 2;
                    NotificationTabFragment.msgTab=true;
                    showNotification(context, message, new Intent(context, MainTabActivity.class));

                }
                else if (intent.getStringExtra("status").equals(Notification.PostAdded.getValue() + ""))
                {
                    if (HomeFragment.homeFragment != null)
                    {
                        (HomeFragment.homeFragment).notifier(0, "0");
                    }
                }

                else if (intent.getStringExtra("status").equals(Notification.PostLiked.getValue() + ""))
                {
                    NotificationAdapter.oneUnread=true;
                    MainTabActivity.tabToOpen = 2;
                    showNotification(context, message, new Intent(context, MainTabActivity.class));
                }
                else if (intent.getStringExtra("status").equals(Notification.CommentAdded.getValue() + ""))
                {
                    NotificationAdapter.oneUnread=true;
                    MainTabActivity.tabToOpen = 2;
                    showNotification(context, message, new Intent(context, MainTabActivity.class));
                }
                else if (intent.getStringExtra("status").equals(Notification.Userfollow.getValue() + ""))
                {
                    NotificationAdapter.oneUnread=true;
                    MainTabActivity.tabToOpen = 2;
                    showNotification(context, message, new Intent(context, MainTabActivity.class));
                }
                else if (intent.getStringExtra("status").equals(Notification.GroupInvitation.getValue() + ""))
                {
                    NotificationAdapter.oneUnread=true;
                    MainTabActivity.tabToOpen = 2;
                    showNotification(context, message, new Intent(context, MainTabActivity.class));
                }


                if (!intent.getStringExtra("status").equals("11"))
                {

                    SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);

                    sharedPrefHelper.SetbadgeCount(sharedPrefHelper.GetbadgeCount() + 1);

                    sendBroadcast(new Intent(GlobalConstants.UPDATE_BADGE));

                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        catch (Exception | Error e)
        {
            showNotification(context, "new notification", new Intent(context, SplashActivity.class));
            e.printStackTrace();
        }


    }


    private boolean isLoggedIn(Context context)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);

        return sharedPrefHelper.isLoggedIn() && sharedPrefHelper.getEmailVerified();
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {

//		String message = "";
        // generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId)
    {

    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        // log message

        return super.onRecoverableError(context, errorId);
    }
}
