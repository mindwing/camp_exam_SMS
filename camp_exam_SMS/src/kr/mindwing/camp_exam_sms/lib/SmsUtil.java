package kr.mindwing.camp_exam_sms.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;

public class SmsUtil {

	private static final String[] PROJ_ID = new String[] { "_id" };

	// for API Level 19
	// private static final String[] PROJ_CONVERSATIONS_INFO = new String[] {
	// "_id", TextBasedSmsColumns.DATE, ThreadsColumns.RECIPIENT_IDS,
	// ThreadsColumns.SNIPPET, ThreadsColumns.SNIPPET_CHARSET,
	// ThreadsColumns.MESSAGE_COUNT }; // , "unread_message_count" };

	private static final String ID = "_id";
	private static final String DATE = "date";
	private static final String RECIPIENT_IDS = "recipient_ids";
	private static final String SNIPPET = "snippet";
	private static final String SNIPPET_CHARSET = "snippet_cs";
	private static final String MESSAGE_COUNT = "message_count";
	private static final String TYPE = "type";
	private static final String SUBJECT = "sub";
	private static final String SUBJECT_CHARSET = "sub_cs";
	private static final String BODY = "body";
	private static final String MESSAGE_BOX = "msg_box";

	private static final String[] PROJ_CONVERSATIONS_INFO = new String[] { ID,
			DATE, RECIPIENT_IDS, SNIPPET, SNIPPET_CHARSET, MESSAGE_COUNT };

	private static final String[] PROJ_CONVERSATION = new String[] { ID, TYPE,
			DATE, SUBJECT, SUBJECT_CHARSET, BODY, MESSAGE_BOX };

	private static final Uri CONTENT_URI = Uri.parse("content://mms-sms/");

	private static final Uri CONTENT_URI_CANONICAL_ADDRESS = Uri
			.withAppendedPath(/* MmsSms. */CONTENT_URI, "canonical-address");

	public static final Uri CONTENT_CONVERSATIONS_URI = Uri
			.parse("content://mms-sms/conversations");

	private static final Uri CONTENT_URI_CANONICAL_ADDRESSES = Uri
			.withAppendedPath(/* MmsSms. */CONTENT_URI, "canonical-addresses");

	private static final Uri CONTENT_URI_MMS_SMS_THREAD_ID = Uri
			.parse("content://mms-sms/threadID");

	public static final String ADDRESSES = "ADDRESS_INFO";
	public static final String THREAD_ID = "THREAD_ID";

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void checkDefaultSmsApp(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}

		final String myPackageName = context.getPackageName();

		if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {
			Intent intent = new Intent(
					Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
			intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
					myPackageName);
			context.startActivity(intent);
		}
	}

	public static ArrayList<ConversationInfo> getConversationInfoList(
			Context context) {
		ArrayList<ConversationInfo> conversationList = new ArrayList<ConversationInfo>();
		// Uri CONTENT_CONVERSATIONS_URI = Uri
		// .parse("content://mms-sms/conversations");

		Cursor convInfoCursor = context.getContentResolver().query(
				/* MmsSms. */CONTENT_CONVERSATIONS_URI.buildUpon()
						.appendQueryParameter("simple", "true").build(),
				PROJ_CONVERSATIONS_INFO, null, null, null);

		if (convInfoCursor == null) {
			return conversationList;
		}

		for (convInfoCursor.moveToFirst(); !convInfoCursor.isAfterLast(); convInfoCursor
				.moveToNext()) {
			String addrIds = convInfoCursor.getString(convInfoCursor
					.getColumnIndex(RECIPIENT_IDS));
			String threadId = convInfoCursor.getString(convInfoCursor
					.getColumnIndex(ID));

			AddressInfo addrInfo = new AddressInfo(threadId);
			retrieveAddressesByIds(context, addrInfo, addrIds);

			long date = convInfoCursor.getLong(convInfoCursor
					.getColumnIndex(DATE));
			String snippet = convInfoCursor.getString(convInfoCursor
					.getColumnIndex(SNIPPET));
			int snippetCharset = convInfoCursor.getInt(convInfoCursor
					.getColumnIndex(SNIPPET_CHARSET));
			int totalCount = convInfoCursor.getInt(convInfoCursor
					.getColumnIndex(MESSAGE_COUNT));

			conversationList.add(new ConversationInfo(addrInfo, snippet,
					snippetCharset, date, totalCount));
		}

		clearAddressNameCache();
		convInfoCursor.close();

		return conversationList;
	}

	private static HashMap<String, ArrayList<String>> addressNameCache = new HashMap<String, ArrayList<String>>();

	private static void clearAddressNameCache() {
		addressNameCache.clear();
	}

	private static synchronized AddressInfo retrieveAddressesByIds(
			Context _ctx, AddressInfo address, String _addrs) {
		String addressFromDb = null;

		for (StringTokenizer st = new StringTokenizer(_addrs); st
				.hasMoreTokens();) {
			Cursor addrCursor = _ctx.getContentResolver().query(
					Uri.withAppendedPath(CONTENT_URI_CANONICAL_ADDRESS,
							st.nextToken()), null, null, null, null);

			if (addrCursor != null && addrCursor.getCount() > 0) {
				addrCursor.moveToFirst();
				addressFromDb = addrCursor.getString(0);
				address.addAddress(addressFromDb);
				if (addressFromDb != null && addressFromDb.length() > 0) {
					ArrayList<String> addressName = getAddressName(_ctx,
							addressFromDb);
					if (addressName != null && addressName.size() != 0) {
						address.setAddressName(addressFromDb, addressName);
					}
				}

				addrCursor.close();
			}
		}

		// if (result.length() == 0) {
		// return result;
		// } else {
		// return result.substring(0, result.length() - 1);
		// }

		return address;
	}

	private static ArrayList<String> getAddressName(Context context,
			String number) {
		if (number == null) {
			return null;
		}

		ArrayList<String> names;

		if (addressNameCache.containsKey(number)) {
			names = addressNameCache.get(number);
		} else {
			addressNameCache.put(number, names = new ArrayList<String>());

			Uri uri = Uri.withAppendedPath(
					ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(number));
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
					null, null, null);

			if (cursor == null) {
				return null;
			}

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				names.add(cursor.getString(cursor
						.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)));
			}

			cursor.close();
		}

		return names;
	}

	static String getThreadId(Context _ctx, AddressInfo _addressInfo,
			boolean _isCreate) {
		String threadId = "-1";
		String[] addresses = _addressInfo.toArray();

		if (addresses == null || addresses.length == 0) {
			return "-1";
		}

		if (addresses.length == 1) {
			Cursor addrCursor = _ctx.getContentResolver().query(
					CONTENT_URI_CANONICAL_ADDRESSES, null,
					"address = '" + addresses[0] + "'", null, null);

			if ((addrCursor != null && addrCursor.getCount() > 0) || _isCreate) {
				addrCursor.close();
			} else {
				return "-1";
			}
		}

		Iterator<String> iter = _addressInfo.iterator();

		Uri.Builder uriBuilder = CONTENT_URI_MMS_SMS_THREAD_ID.buildUpon();

		while (iter.hasNext()) {
			uriBuilder.appendQueryParameter("recipient", iter.next());
		}

		Cursor threadCursor = _ctx.getContentResolver().query(
				uriBuilder.build(), PROJ_ID, null, null, null);

		if (threadCursor == null) {
			return "-1";
		}

		try {
			threadCursor.moveToFirst();

			for (int i = 0; i < threadCursor.getCount(); i++) {
				threadId = threadCursor.getString(0);
			}

			if (!_isCreate) {
				Cursor delCursor = _ctx.getContentResolver().query(
						CONTENT_URI_MMS_SMS_THREAD_ID, PROJ_ID, null, null,
						null);
				if (delCursor != null && delCursor.getCount() > 0) {
					delCursor.close();
				} else {
					setAllDelete(_ctx, threadId, 0, System.currentTimeMillis());
					threadId = "-1";
				}
			}
		} finally {
			threadCursor.close();
		}

		return threadId;
	}

	private static void setAllDelete(Context _ctx, String threadId,
			long startDate, long endDate) {
		_ctx.getContentResolver().delete(
				CONTENT_URI_MMS_SMS_THREAD_ID,
				"threadID = ? AND date >= " + startDate + " AND date <= "
						+ endDate, new String[] { threadId });
	}

	public static MessageData getMessageData(Context _ctx,
			AddressInfo _addressInfo, long _date) {
		Cursor msgCursor = _ctx.getContentResolver().query(
				Uri.withAppendedPath(CONTENT_CONVERSATIONS_URI,
						_addressInfo.getThreadId()), PROJ_CONVERSATION,
				"date = " + _date, null, null);

		if (msgCursor == null) {
			return null;
		}

		MessageData messageData = null;

		if (msgCursor.moveToFirst()) {
			messageData = MessageData
					.buildFromDb(_ctx, msgCursor, _addressInfo);
		}

		msgCursor.close();

		return messageData;
	}

	public static ArrayList<MessageData> getConversation(Context _ctx,
			AddressInfo _addressInfo) {
		ArrayList<MessageData> dataList = new ArrayList<MessageData>();

		Cursor msgCursor = _ctx.getContentResolver().query(
				Uri.withAppendedPath(CONTENT_CONVERSATIONS_URI,
						_addressInfo.getThreadId()), PROJ_CONVERSATION, null,
				null, null);

		if (msgCursor == null) {
			return dataList;
		}

		for (msgCursor.moveToFirst(); !msgCursor.isAfterLast(); msgCursor
				.moveToNext()) {
			dataList.add(MessageData.buildFromDb(_ctx, msgCursor, _addressInfo));
		}

		msgCursor.close();

		return dataList;
	}
}
