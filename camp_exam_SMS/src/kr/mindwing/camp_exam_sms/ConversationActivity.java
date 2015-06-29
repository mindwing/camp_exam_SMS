package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.AddressInfo;
import kr.mindwing.camp_exam_sms.lib.MessageData;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ConversationActivity extends ActionBarActivity {

	private AddressInfo addressInfo;
	private RecyclerView recyclerView;
	private ArrayList<MessageData> conversationList;
	private SmsConversationsAdapter adapter;
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
		recyclerView.setAdapter(adapter = new SmsConversationsAdapter(
				conversationList));

		RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(rvLayoutManager);

		etTextInput = (EditText) findViewById(R.id.text_input);
		etTextInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				btSend.setText(String.format("보내기\n(%d/140)", s.length()));
			}
		});

		btSend = (TextView) findViewById(R.id.send);
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = etTextInput.getText().toString();
				SmsUtil.sendSms(ConversationActivity.this, addressInfo, message);

				etTextInput.setText(null);
				adapter.notifyMessageAdded(addressInfo, message);
				recyclerView.scrollToPosition(adapter.getItemCount() - 1);
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

		recyclerView.scrollToPosition(adapter.getItemCount() - 1);
	}

}
