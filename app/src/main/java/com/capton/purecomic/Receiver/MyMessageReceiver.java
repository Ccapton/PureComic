package com.capton.purecomic.Receiver;

import android.content.Context;

//import cn.jpush.android.api.JPushInterface;
//import cn.jpush.android.service.PushReceiver;


/**
 * Created by capton on 2017/4/22.
 */

public class MyMessageReceiver  {
    private final String RECEIVED="cn.jpush.android.intent.MESSAGE_RECEIVED";
    public MyMessageReceiver() {
        super();
    }
  private Context context;
   /* @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context=context;
        if(intent.getAction().equals(RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.i("MyMessageReciver", "onReceive: " + message);

            UpdateForcast forcast= JSON.parseObject(message,UpdateForcast.class);
            if(forcast!=null){
                String character="";
                for (int i = 0; i <forcast.getCharacterList().size(); i++) {
                    character+=forcast.getCharacterList().get(i)+" ";
                }
                Notifiy(forcast.getTitle(),forcast.getVersion(),character,forcast.getUrl());
            }else {

            }

        }
    }

    private void Notifiy(String ticker, String version, String character, String url){
        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification= null;

            notification = new Notification.Builder(context).setTicker(ticker)
                    .setContentTitle("随心漫画 "+ticker+" "+version)
                    .setSmallIcon(R.drawable.logo_s)
                    .setShowWhen(true).setContentText(character)
                    .setContentIntent(pendingIntent).build();
            manager.notify(1,notification);


    }*/
}
