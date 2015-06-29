package kr.mindwing.camp_exam_sms.lib;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;

public class SmsUtil {

	private static final String TAG = "SmsUtil";

	private static final String[] PROJ_ID = new String[] { "_id" };

	// for API Level 19
	// private static final String[] PROJ_CONVERSATIONS_INFO = new String[] {
	// "_id", TextBasedSmsColumns.DATE, ThreadsColumns.RECIPIENT_IDS,
	// ThreadsColumns.SNIPPET, ThreadsColumns.SNIPPET_CHARSET,
	// ThreadsColumns.MESSAGE_COUNT }; // , "unread_message_count" };

	public static final String ID = "_id";
	public static final String DATE = "date";
	public static final String RECIPIENT_IDS = "recipient_ids";
	public static final String SNIPPET = "snippet";
	public static final String SNIPPET_CHARSET = "snippet_cs";
	public static final String MESSAGE_COUNT = "message_count";
	public static final String TYPE = "type";
	public static final String SUBJECT = "sub";
	public static final String SUBJECT_CHARSET = "sub_cs";
	public static final String BODY = "body";
	public static final String MESSAGE_BOX = "msg_box";
	public static final String ADDRESS = "address";
	public static final String ADDRESSES = "address_info";
	public static final String THREAD_ID = "thread_id";
	public static final String DATE_SENT = "date_sent";
	public static final String READ = "read";
	public static final String SEEN = "seen";
	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	private static final String[] PROJ_CONVERSATIONS_INFO = new String[] { ID,
			DATE, RECIPIENT_IDS, SNIPPET, SNIPPET_CHARSET, MESSAGE_COUNT };

	private static final String[] PROJ_CONVERSATION = new String[] { ID, TYPE,
			DATE, SUBJECT, SUBJECT_CHARSET, BODY, MESSAGE_BOX };

	private static final Uri CONTENT_URI_MMS_SMS = Uri
			.parse("content://mms-sms/");
	private static final Uri CONTENT_URI_SMS = Uri.parse("content://sms");

	private static final Uri CONTENT_URI_CANONICAL_ADDRESS = Uri
			.withAppendedPath(/* MmsSms. */CONTENT_URI_MMS_SMS,
					"canonical-address");

	public static final Uri CONTENT_CONVERSATIONS_URI = Uri
			.parse("content://mms-sms/conversations");

	private static final Uri CONTENT_URI_CANONICAL_ADDRESSES = Uri
			.withAppendedPath(/* MmsSms. */CONTENT_URI_MMS_SMS,
					"canonical-addresses");

	private static final Uri CONTENT_URI_MMS_SMS_THREAD_ID = Uri
			.parse("content://mms-sms/threadID");

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void checkDefaultSmsApp(Context context) {
		String myPackageName = context.getPackageName();

		if (isDefaultSmsApp(context, myPackageName)) {
			return;
		}

		if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {
			Intent intent = new Intent(
					Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
			intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
					myPackageName);
			context.startActivity(intent);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean isDefaultSmsApp(Context context) {
		return isDefaultSmsApp(context, context.getPackageName());
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean isDefaultSmsApp(Context context, String myPackageName) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return true;
		}

		return (Telephony.Sms.getDefaultSmsPackage(context)
				.equals(myPackageName));
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

		String threadId = _addressInfo.getThreadId();

		if (Integer.parseInt(threadId) < 1) {
			return dataList;
		}

		Cursor msgCursor = _ctx.getContentResolver().query(
				Uri.withAppendedPath(CONTENT_CONVERSATIONS_URI, threadId),
				PROJ_CONVERSATION, null, null, null);

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

	public static CharSequence getDateString(long _date) {
		return DateFormat.format("MM/dd HH:mm:ss", _date);
	}

	public static boolean sendSms(Context _ctx, AddressInfo _addressInfo,
			String _text) {
		if (_text == null || _text.length() <= 0) {
			Log.d(TAG, "sendSms: _text is null");

			return false;
		}

		try {
			if (_text == null || _text.getBytes("KSC5601").length > 80) {
				Log.d(TAG, "sendSms: _text length is longer than 80: " + _text);

				return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return false;
		}

		SmsManager smsManager = SmsManager.getDefault();

		String address;
		long sentTime = System.currentTimeMillis();

		for (Iterator<String> iter = _addressInfo.iterator(); iter.hasNext();) {
			smsManager.sendTextMessage((address = iter.next()), null, _text,
					null, null);

			Log.d("sendSms", address + ": " + _text);
		}

		insertSmsToDb(_ctx, _addressInfo, _text, sentTime, false, false);

		return true;
	}

	private static void insertSmsToDb(Context _ctx, AddressInfo _addressInfo,
			String body, long date, boolean isFrom, boolean isSpam) {
		ContentValues value = new ContentValues(7);
		value.put(THREAD_ID, _addressInfo.getThreadId());
		value.put(ADDRESS, _addressInfo.getChainExpression());
		value.put(DATE, date);
		value.put(READ, 0);
		value.put(SEEN, 0);
		value.put(TYPE, !isFrom ? MESSAGE_TYPE_SENT : MESSAGE_TYPE_INBOX);
		value.put(BODY, body);

		_ctx.getContentResolver().insert(CONTENT_URI_SMS, value);
	}
}
