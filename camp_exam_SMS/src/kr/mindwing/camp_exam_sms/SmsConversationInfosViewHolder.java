package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.AddressInfo;
import kr.mindwing.camp_exam_sms.lib.ConversationInfo;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

class SmsConversationInfosViewHolder extends RecyclerView.ViewHolder {

	private ArrayList<ConversationInfo> conversationList;

	private TextView tvAddress;
	private TextView tvSnippet;
	private TextView tvDate;

	public SmsConversationInfosViewHolder(View smsConversationView,
			ArrayList<ConversationInfo> _conversationList) {
		super(smsConversationView);

		conversationList = _conversationList;

		smsConversationView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent viewConversationIntent = new Intent(itemView
						.getContext(), ConversationActivity.class);
				int itemPosition = getAdapterPosition();
				ConversationInfo convInfo = conversationList.get(itemPosition);

				AddressInfo addressInfo = convInfo.getAddressInfo();
				viewConversationIntent.putExtra(SmsUtil.THREAD_ID,
						addressInfo.getThreadId());
				viewConversationIntent.putExtra(SmsUtil.ADDRESSES,
						addressInfo.getSpaceSeparatedExpression());

				v.getContext().startActivity(viewConversationIntent);
			}
		});

		tvAddress = (TextView) smsConversationView.findViewById(R.id.address);
		tvSnippet = (TextView) smsConversationView.findViewById(R.id.snippet);
		tvDate = (TextView) smsConversationView.findViewById(R.id.date);
	}

	public void updateContent(ConversationInfo conversationInfo) {
		tvAddress.setText(conversationInfo.getAddressInfo()
				.getSpaceSeparatedNameExpression());
		tvSnippet.setText(conversationInfo.getSnippet());
		tvDate.setText(conversationInfo.getDateString());
	}
}
