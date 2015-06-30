package kr.mindwing.camp_exam_sms;

import kr.mindwing.camp_exam_sms.lib.AddressInfo;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartCompositionActivity extends ActionBarActivity {

	private EditText etAddress;
	private Button btOk, btCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_composition);

		// setTitle(addresses);

		etAddress = (EditText) findViewById(R.id.address);

		btOk = (Button) findViewById(R.id.ok);
		btOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String address = etAddress.getText().toString();

				if (address.length() == 0) {
					Toast.makeText(StartCompositionActivity.this,
							"전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

					return;
				}

				AddressInfo addressInfo = new AddressInfo(
						StartCompositionActivity.this, address);

				Intent conversationIntent = new Intent(
						StartCompositionActivity.this,
						ConversationActivity.class);
				conversationIntent.putExtra(SmsUtil.THREAD_ID,
						addressInfo.getThreadId());
				conversationIntent.putExtra(SmsUtil.ADDRESSES, address);

				startActivity(conversationIntent);
			}
		});

		btCancel = (Button) findViewById(R.id.cancel);
		btCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		etAddress.requestFocus();
	}
}
