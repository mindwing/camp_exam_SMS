package kr.mindwing.camp_exam_sms;

import java.util.ArrayList;

import kr.mindwing.camp_exam_sms.lib.ConversationInfo;
import kr.mindwing.camp_exam_sms.lib.SmsUtil;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class SmsConversationInfosAdapter extends
		RecyclerView.Adapter<SmsConversationInfosViewHolder> {

	private ArrayList<ConversationInfo> conversationList;

	public SmsConversationInfosAdapter(
			ArrayList<ConversationInfo> _conversationInfoList) {
		conversationList = _conversationInfoList;
	}

	@Override
	public int getItemCount() {
		return conversationList.size();
	}

	@Override
	public void onBindViewHolder(SmsConversationInfosViewHolder holder,
			int position) {
		holder.updateContent(conversationList.get(position));
	}

	@Override
	public SmsConversationInfosViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		LayoutInflater layoutInflater = LayoutInflater
				.from(parent.getContext());

		View view = layoutInflater.inflate(R.layout.listitem_conversation_list,
				parent, false);
		SmsConversationInfosViewHolder viewHolder = new SmsConversationInfosViewHolder(
				view, conversationList);

		return viewHolder;
	}
}
