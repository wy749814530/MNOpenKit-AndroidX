package com.mnopensdk.demo.utils;

import com.mnopensdk.demo.bean.CountryCodeBean;

import java.util.Comparator;

/**
 *
 */
public class PinyinComparator implements Comparator<CountryCodeBean.AreasBean> {

	public int compare(CountryCodeBean.AreasBean o1, CountryCodeBean.AreasBean o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
