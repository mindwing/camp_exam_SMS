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

public class ConversationActivity extends ActionBarActivity {

	private AddressInfo addressInfo;
	private RecyclerView recyclerView;
	private ArrayList<MessageData> conversationList;
	private EditText textInput;

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

		textInput = (EditText) findViewById(R.id.text_input);
		textInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SmsUtil.sendSms(ConversationActivity.this, addressInfo,
						textInput.getText().toString());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
