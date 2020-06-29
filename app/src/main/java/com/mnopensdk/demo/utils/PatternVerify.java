package com.mnopensdk.demo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WIN on 2017/11/9.
 */

public class PatternVerify {

    private static final String  pattern = "^1[3578][0-9]{9}$";
    private static final String emailPattern = "^([a-zA-Z0-9\\._-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
    private static final String illegalChar = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@①#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    private static final String LegalString = "^(?!.*([@|#|=|!|#|/|&|*|(|)|^|<|>|.])).*$";


    /**
     * 验证是否为手机号码
     * @param mobile
     * @return
     */
    public static boolean verifyMobile(String mobile){
        return isNumeric(mobile); // Pattern.matches(pattern, mobile);
    }

    /**
     * 判断字符串是否全是数字
     * @param s
     * @return
     */
    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    /**
     * 验证是否为邮箱
     * @param emial
     * @return
     */
    public static boolean verifyEmial(String emial){
        return Pattern.matches(emailPattern, emial);
    }

    /**
     * 是否是合法字符串
     * @param str
     * @return
     */
    public static boolean isLegalString(String str){
        Pattern pattern = Pattern.compile(/*LegalString*/ illegalChar);
        Matcher m = pattern.matcher(str);

        if (m.find()) {
          return false;
        }
        return true;
    }

    /**
     * 验证身份证号是否符合规则
     *
     * 一、15位身份证号
     * 二、18位身份证号（前17位位数字，最后一位为字母x）
     * 三、18为身份证号（18位都是数字）
     * @param text 身份证号
     * @return
     */
    public static boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }

    /**
     * 判断是否为合法IP
     * @param ipAddress
     * @return
     */
    public static boolean isLegalIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
    /**
     * 通过HashSet踢除重复元素
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 删除ArrayList中重复元素，保持顺序
     * @param list
     */
    public static List removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);

        return list;
    }

    /**
     *
     * @param strDate  "24572399594766"
     * @return "2457 2399 5947 66"
     */
    public static String getFormatString(String strDate){
        int len = strDate.length()/4;
        if(strDate.length()%4 > 0){
            len ++;
        }

        String newDate = "";
        for(int i = 0; i < strDate.length(); i = i + 4){
            String byte4Str = "";
            if(i+4 < strDate.length()){
                byte4Str = strDate.substring(i, i+4);
            }else{
                byte4Str = strDate.substring(i, strDate.length());
            }
            if("".equals(newDate)){
                newDate = byte4Str;
            }else{
                newDate = newDate + " " + byte4Str ;
            }
        }

        return newDate;
    }

    /**
     * 只含有数字、字母 组合
     */
    public static boolean isNumAndChar(String str) {
//		String pattern = "^[a-zA-Z0-9]*$";//只含有数字、字母
        String pattern = "^(?!^\\d+$)(?!^[a-zA-Z]+$)[0-9a-zA-Z]{6,18}+$";
        if (isNull(str))
            return false;
        if (Pattern.matches(pattern, str)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNull(String temp) {
        if (temp.isEmpty())
            return true;
        else
            return false;
    }

}
