package kr.mindwing.camp_exam_sms;

import kr.mindwing.camp_exam_sms.lib.MessageData;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class SmsConversationsViewHolder extends RecyclerView.ViewHolder {

	private TextView tvConversation;

	public SmsConversationsViewHolder(View smsConversationView) {
		super(smsConversationView);

		tvConversation = (TextView) smsConversationView
				.findViewById(R.id.conversation);
	}

	public void updateContent(MessageData messageData) {
		tvConversation.setText(messageData.getBody());
	}

}
