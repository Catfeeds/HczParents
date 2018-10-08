package com.goodsurfing.utils;

import java.util.Comparator;

import com.goodsurfing.beans.Friend;

/**
 * User比较器
 * 
 */
public class UserComparator implements Comparator<Friend> {

	public int compare(Friend o1, Friend o2) {
		if (o1.getPinyinlastname() == null || o2.getPinyinlastname() == null)
			return 1;
		if (o1.getPinyinlastname().equals("@")
				|| o2.getPinyinlastname().equals("#")) {
			return -1;
		} else if (o1.getPinyinlastname().equals("#")
				|| o2.getPinyinlastname().equals("@")) {
			return 1;
		} else {
			return o1.getPinyinlastname().compareTo(o2.getPinyinlastname());
		}
	}

}
