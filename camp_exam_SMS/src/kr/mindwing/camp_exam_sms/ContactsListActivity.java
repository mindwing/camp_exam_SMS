package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.ConversationInfo;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ContactsListActivity extends ActionBarActivity {

	private RecyclerView recyclerView;
	private ImageView ball;
	private ArrayList<ConversationInfo> conversationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contacts_list);

		conversationList = SmsUtil.getConversationInfoList(this);

		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(new SmsConversationInfosAdapter(
				conversationList));

		RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(rvLayoutManager);

		ball = (ImageView) findViewById(R.id.ball);
		ball.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent compositionIntent = new Intent(
						ContactsListActivity.this,
						StartCompositionActivity.class);
				startActivity(compositionIntent);
			}
		});

		SmsUtil.checkDefaultSmsApp(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}
