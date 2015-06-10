package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.ConversationInfo;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ContactsListActivity extends ActionBarActivity {

	private RecyclerView recyclerView;
	private ArrayList<ConversationInfo> conversationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contacts_list);

		conversationList = SmsUtil.getConversationInfoList(this);

		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(new SmsConversationAdapter(conversationList));

		RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(
				this);
		recyclerView.setLayoutManager(rvLayoutManager);

		SmsUtil.checkDefaultSmsApp(this);

		// AddressInfo addrInfo = conversationList.get(0).getAddressInfo();
		// ArrayList<MessageData> messageList = SmsUtil.getConversation(
		// ContactsListActivity.this, addrInfo);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}
