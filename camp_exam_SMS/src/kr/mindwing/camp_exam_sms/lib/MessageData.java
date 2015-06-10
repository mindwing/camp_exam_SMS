package kr.mindwing.camp_exam_sms.lib;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.database.Cursor;

public class MessageData {

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_DRAFT = 3;

	public static final String ADDRESS = "address";
	public static final String DATE = "date";
	public static final String BODY = "body";
	public static final String TYPE = "type";

	private String mId;
	private AddressInfo mAddressInfo;
	private int mType;
	private long mDate;
	private String mBody;
	private String mSubject;

	private String mChset;

	MessageData(String _id, AddressInfo _addressInfo, int _type, String _body,
			long _date) {
		mId = _id;
		mAddressInfo = _addressInfo;
		mType = _type;
		mBody = _body;
	}

	public String getId() {
		return mId;
	}

	void setType(int _type) {
		mType = _type;
	}

	public AddressInfo getAddressInfo() {
		return mAddressInfo;
	}

	public String getBody() {
		return mBody;
	}

	void setBody(String _body) {
		mBody = _body;
	}

	public boolean isReceived() {
		return mType == MESSAGE_TYPE_INBOX;
	}

	public boolean isDraft() {
		return mType == MESSAGE_TYPE_DRAFT;
	}

	public long getDate() {
		return mDate;
	}

	public String getChset() {
		return mChset;
	}

	void setChset(String _mChset) {
		mChset = _mChset;
	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String _subject, int _subjectCharset) {
		if (_subjectCharset == 106) {
			try {
				mSubject = new String(_subject.getBytes("8859-1"));
			} catch (UnsupportedEncodingException e) {
				mSubject = _subject;
				e.printStackTrace();
			}
		} else {
			mSubject = _subject;
		}
	}

	static MessageData buildFromDb(Context _ctx, Cursor _msgCursor) {
		String address = _msgCursor.getString(_msgCursor
				.getColumnIndex(ADDRESS));

		return buildFromDb(_ctx, _msgCursor, new AddressInfo(_ctx, address));
	}

	static MessageData buildFromDb(Context _ctx, Cursor _msgCursor,
			AddressInfo _addressInfo) {
		String id = _msgCursor.getString(_msgCursor.getColumnIndex("_id"));

		long date = _msgCursor.getLong(_msgCursor.getColumnIndex(DATE));

		String body = null;

		body = _msgCursor.getString(_msgCursor.getColumnIndex(BODY));

		int type = _msgCursor.getInt(_msgCursor.getColumnIndex(TYPE));

		MessageData messageData = new MessageData(id, _addressInfo, type, body,
				date);

		return messageData;
	}
}
