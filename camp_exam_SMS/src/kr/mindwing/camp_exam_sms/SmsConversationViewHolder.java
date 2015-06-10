package kr.mindwing.camp_exam_sms;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class SmsConversationViewHolder extends RecyclerView.ViewHolder {

	private TextView tvAddress;
	private TextView tvSnippet;

	public SmsConversationViewHolder(View smsConversationView) {
		super(smsConversationView);

		tvAddress = (TextView) smsConversationView.findViewById(R.id.address);
		tvSnippet = (TextView) smsConversationView.findViewById(R.id.snippet);
	}

}
