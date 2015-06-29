package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.AddressInfo;
import kr.mindwing.camp_exam_sms.lib.MessageData;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ConversationActivity extends ActionBarActivity {

	private AddressInfo addressInfo;
	private RecyclerView recyclerView;
	private ArrayList<MessageData> conversationList;
	private EditText etTextInput;
	private TextView btSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);

		String threadId = getIntent().getStringExtra(SmsUtil.THREAD_ID);
		String addresses = getIntent().getStringExtra(SmsUtil.ADDRESSES);
		addressInfo = new AddressInfo(addresses, threadId);

		setTitle(addresses);

		conversationList = SmsUtil.getConversation(this, addressInfo);

		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(new SmsConversationsAdapter(conversationList));

		RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(rvLayoutManager);

		etTextInput = (EditText) findViewById(R.id.text_input);

		btSend = (TextView) findViewById(R.id.send);
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SmsUtil.sendSms(ConversationActivity.this, addressInfo,
						etTextInput.getText().toString());
				etTextInput.setText(null);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		boolean isDefaultSmsApp = SmsUtil.isDefaultSmsApp(this);

		String hint = isDefaultSmsApp ? "대화 입력창" : "(기본 SMS 앱 설정필요)";
		etTextInput.setHint(hint);
		etTextInput.setEnabled(isDefaultSmsApp);

		btSend.setEnabled(isDefaultSmsApp);

		recyclerView.scrollToPosition(conversationList.size() - 1);
	}

}
