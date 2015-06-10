package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.ConversationInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SmsConversationAdapter extends
		RecyclerView.Adapter<SmsConversationViewHolder> {

	private ArrayList<ConversationInfo> conversationList;

	public SmsConversationAdapter(ArrayList<ConversationInfo> _conversationList) {
		conversationList = _conversationList;
	}

	@Override
	public int getItemCount() {
		return conversationList.size();
	}

	@Override
	public void onBindViewHolder(SmsConversationViewHolder holder, int position) {
	}

	@Override
	public SmsConversationViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		LayoutInflater layoutInflater = LayoutInflater
				.from(parent.getContext());

		View view = layoutInflater.inflate(R.layout.listitem_conversation_list,
				parent, false);
		SmsConversationViewHolder viewHolder = new SmsConversationViewHolder(
				view);

		return viewHolder;
	}

}
