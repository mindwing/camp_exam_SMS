package kr.mindwing.camp_exam_sms.lib;

import android.content.Context;
import android.telephony.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class AddressInfo {
	private String mThreadId;

	private HashSet<String> mAddressSet = new HashSet<String>();

	private HashMap<String, ArrayList<String>> mAddressNameListMap = new HashMap<String, ArrayList<String>>();

	private String mSpaceSeparatedExpression, mSpaceSeparatedNameExpression;

	public static ArrayList<AddressInfo> getFilteredAddressInfoList(
			ArrayList<AddressInfo> source, String _partialAddressName) {
		Iterator<AddressInfo> sourceIter = source.listIterator();
		_partialAddressName = _partialAddressName.toLowerCase();
		ArrayList<AddressInfo> result = new ArrayList<AddressInfo>();

		while (sourceIter.hasNext()) {
			AddressInfo addressInfo = sourceIter.next();
			if (addressInfo.isAddressNameMatched(_partialAddressName)) {
				result.add(addressInfo);
			}
		}

		return result;
	}

	private boolean isAddressNameMatched(String _partialAddressName) {
		Collection<ArrayList<String>> addressNameLists = mAddressNameListMap
				.values();
		// if (addressNames.size() == 0) {
		// return;
		// }

		Iterator<ArrayList<String>> addressNameListIter = addressNameLists
				.iterator();

		while (addressNameListIter.hasNext()) {
			ArrayList<String> addressNameList = addressNameListIter.next();
			Iterator<String> addressNameIter = addressNameList.iterator();

			while (addressNameIter.hasNext()) {
				if (addressNameIter.next().toLowerCase()
						.contains(_partialAddressName)) {
					return true;
				}
			}
		}

		return false;
	}

	public AddressInfo(String _threadId) {
		mThreadId = _threadId;
	}

	public AddressInfo(String _addresses, String _threadId) {
		mThreadId = _threadId;

		for (StringTokenizer st = new StringTokenizer(_addresses); st
				.hasMoreTokens();) {
			addAddress(st.nextToken());
		}
	}

	public AddressInfo(Context _ctx, String _addresses) {
		if (_addresses == null) {
			addAddress("Unknown");
		} else {
			for (StringTokenizer st = new StringTokenizer(_addresses); st
					.hasMoreTokens();) {
				addAddress(st.nextToken());
			}
		}

		mThreadId = SmsUtil.getThreadId(_ctx, this, true);
	}

	public String getThreadId() {
		return mThreadId;
	}

	void setThreadId(String _threadId) {
		mThreadId = _threadId;
	}

	public boolean addAddress(String _address) {
		boolean retVal = mAddressSet.add(_address);
		mSpaceSeparatedExpression = null;

		return retVal;
	}

	public int size() {
		return mAddressSet.size();
	}

	public void clear() {
		mAddressSet.clear();
	}

	public Iterator<String> iterator() {
		return mAddressSet.iterator();
	}

	private String[] type = new String[0];

	public String[] toArray() {
		return mAddressSet.toArray(type);
	}

	public String getSpaceSeparatedExpression() {
		if (mSpaceSeparatedExpression != null) {
			return mSpaceSeparatedExpression;
		}

		String retVal = "";

		for (Iterator<String> iter = mAddressSet.iterator(); iter.hasNext();) {
			String address = iter.next();

			if (address != null) {
				retVal += PhoneNumberUtils.formatNumber(address);
			}

			retVal += " ";
		}

		return mSpaceSeparatedExpression = retVal.trim();
	}

	public String getSpaceSeparatedNameExpression() {
		if (mSpaceSeparatedNameExpression != null) {
			return mSpaceSeparatedNameExpression;
		}

		String retVal = "";

		for (Iterator<String> iter = mAddressSet.iterator(); iter.hasNext();) {
			String address = iter.next();
			ArrayList<String> names = getAddressName(address);

			if (names != null) {
				retVal += names.get(0);
				retVal += " ";
			} else if (!"".equals(address)) {
				retVal += PhoneNumberUtils.formatNumber(address);
				retVal += " ";
			}
		}

		if (retVal == "") {
			retVal = "발신번호없음";
		}

		return mSpaceSeparatedNameExpression = retVal.trim();
	}

	public String getChainExpression() {
		if (mSpaceSeparatedExpression == null) {
			getSpaceSeparatedExpression();
		}

		return mSpaceSeparatedExpression.replace(' ', ';');
	}

	public ArrayList<String> getAddressName(String _address) {
		return mAddressNameListMap.get(_address);
	}

	public void setAddressName(String _address,
			ArrayList<String> _addressNameList) {
		if (mAddressSet.contains(_address)) {
			mAddressNameListMap.put(_address, _addressNameList);
		}
	}

	public int getAddressNameListCount() {
		return mAddressSet.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AddressInfo)) {
			return false;
		}

		AddressInfo compAddressInfo = (AddressInfo) obj;

		return compAddressInfo.getThreadId().equals(mThreadId);
	}

	@Override
	public String toString() {
		return getSpaceSeparatedNameExpression();
	}
}
