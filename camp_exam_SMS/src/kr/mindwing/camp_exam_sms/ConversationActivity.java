package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.AddressInfo;
import kr.mindwing.camp_exam_sms.lib.MessageData;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ConversationActivity extends ActionBarActivity {

	private RecyclerView recyclerView;
	private ArrayList<MessageData> conversationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);

		String threadId = getIntent().getStringExtra(SmsUtil.THREAD_ID);
		String addresses = getIntent().getStringExtra(SmsUtil.ADDRESSES);
		AddressInfo addressInfo = new AddressInfo(addresses, threadId);

		conversationList = SmsUtil.getConversation(this, addressInfo);

		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(new SmsConversationsAdapter(conversationList));

		RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(rvLayoutManager);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}