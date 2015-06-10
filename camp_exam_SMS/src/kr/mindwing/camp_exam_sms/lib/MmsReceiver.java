package kr.mindwing.camp_exam_sms.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

public class MmsReceiver extends BroadcastReceiver {
	String tag = "MessageReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION)) {
			// MsgUtil.mmsDelivered(context, intent);
		}

		if (intent.getAction().equals(
				Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION)) {
			// MsgUtil.mmsReceived(intent);
		}
	}
}
