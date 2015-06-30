package kr.mindwing.camp_exam_sms.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

public class SmsReceiver extends BroadcastReceiver {
	String tag = "MessageReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getAction()) {
		case Telephony.Sms.Intents.SMS_DELIVER_ACTION:
			SmsUtil.smsDelivered(context, intent);
			break;

		case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
			// ConversationActivity 가 Broadcast 를 처리해야 함.
			break;

		default:

		}
	}
}
