package kr.mindwing.camp_exam_sms;

import kr.mindwing.camp_exam_sms.lib.MessageData;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

class SmsConversationsViewHolder extends RecyclerView.ViewHolder {

	private TextView tvConversation;
	private TextView tvDate;

	public SmsConversationsViewHolder(View smsConversationView) {
		super(smsConversationView);

		tvConversation = (TextView) smsConversationView
				.findViewById(R.id.conversation);
		tvDate = (TextView) smsConversationView
				.findViewById(R.id.date);
	}

	public void updateContent(MessageData messageData) {
		tvConversation.setText(messageData.getBody());
		tvDate.setText(messageData.getDateString());
	}
}
