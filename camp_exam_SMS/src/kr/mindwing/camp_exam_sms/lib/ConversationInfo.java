package kr.mindwing.camp_exam_sms.lib;

import java.io.UnsupportedEncodingException;

public class ConversationInfo {
	private AddressInfo mAddressInfo;
	private String mSnippet;
	private int mSnippetCharset;
	private long mDate;
	private int mTotalMessageCount;

	ConversationInfo(AddressInfo _addressInfo, String _snippet,
			int _snippetCharset, long _date, int _totalMessageCount) {
		mAddressInfo = _addressInfo;
		mSnippetCharset = _snippetCharset;
		if (mSnippetCharset == 106) { // 106 �� 8859-1 �씤�벏
			try {
				mSnippet = new String(_snippet.getBytes("8859-1"));
			} catch (UnsupportedEncodingException e) {
				mSnippet = _snippet;
				e.printStackTrace();
			}
		} else {
			mSnippet = _snippet;
		}

		mDate = _date;
		mTotalMessageCount = _totalMessageCount;
	}

	public AddressInfo getAddressInfo() {
		return mAddressInfo;
	}

	public String getSnippet() {
		return mSnippet;
	}

	public long getDate() {
		return mDate;
	}

	public int getTotalMessageCount() {
		return mTotalMessageCount;
	}

	public String toString() {
		return String.format("%s(nameList:%d, total:%d)\n%s",
				mAddressInfo.getSpaceSeparatedExpression(),
				mAddressInfo.getAddressNameListCount(), mTotalMessageCount,
				mSnippet);
	}
}
