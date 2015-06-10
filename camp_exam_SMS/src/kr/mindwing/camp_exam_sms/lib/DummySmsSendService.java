package kr.mindwing.camp_exam_sms.lib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DummySmsSendService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
