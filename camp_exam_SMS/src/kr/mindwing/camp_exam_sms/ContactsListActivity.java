package kr.mindwing.camp_exam_sms;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;

public class ContactsListActivity extends ActionBarActivity {

	RecyclerView rv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contacts_list);

		rv = (RecyclerView) findViewById(R.id.recycler_view);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
