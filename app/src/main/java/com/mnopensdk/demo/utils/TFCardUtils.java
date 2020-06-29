package com.mnopensdk.demo.utils;


import com.mn.bean.setting.TFStateBean;
import com.mn.bean.setting.TFStateConfigBean;

/**
 * Created by Administrator on 2019/3/29 0029.
 */

public class TFCardUtils {

  /*
  State:
    1. AutoFormat  格式正常并且只能自动格式化
    2. UnSupported 格式不正常并且不能手动格式化
    3. Normal     格式正常并且支持格式化
    4. NeedFormat 格式不正常支持手动格式化并且需要手动格式化
    5. NULL 没有存储卡
*/

    public static String getTFCardState(TFStateConfigBean bean) {
        if (bean == null) {
            return "NULL";
        }
        if (!bean.isResult()) {
            if (bean.getCode() == 407) {
                return "NULL";
            } else if (bean.getCode() == 414) {
                return "TimeOut";
            } else if (bean.getCode() == 415) {
                return "EtsNotOnline";
            } else if (bean.getCode() == 405) {
                return "Reinsert";
            } else if (bean.getCode() == 403) {
                return "ProtocolRequestFailed";
            } else if (bean.getCode() != 0) {
                return "Failed";
            }
        }
        if (bean.getParams() == null || bean.getParams().size() == 0) {
            return "NULL";
        } else {
            if (bean.getParams().size() > 1) {
                TFStateConfigBean.ParamsBean paramsBean = bean.getParams().get(0);
                TFStateConfigBean.ParamsBean paramsBean1 = bean.getParams().get(1);

                String state = paramsBean.getState();
                String state1 = paramsBean1.getState();

                if (("Normal".equals(state) && "Normal".equals(state1))) {
                    return "Normal";
                }

                if (("Normal".equals(state) || "Normal".equals(state1)) && !state.equals(state1)) {
                    return "NeedFormat";
                }

                if ("NeedFormat".equals(state) || "NeedFormat".equals(state1)) {
                    return "NeedFormat";
                }

                if ("AutoFormat".equals(state) || "AutoFormat".equals(state1)) {
                    return "AutoFormat";
                }

                if ("UnSupported".equals(state) || "UnSupported".equals(state1)) {
                    return "UnSupported";
                }

                return "UnSupported";
            } else {
                TFStateConfigBean.ParamsBean paramsBean = bean.getParams().get(0);
                return paramsBean.getState();
            }
        }
    }

    public static String getTFCardState(TFStateBean bean) {
        if (bean == null) {
            return "NULL";
        } else {
            if (!bean.isResult()) {
                if (bean.getCode() == 407) {
                    return "NULL";
                } else if (bean.getCode() == 414) {
                    return "TimeOut";
                } else if (bean.getCode() == 415) {
                    return "EtsNotOnline";
                } else if (bean.getCode() == 405) {
                    return "Reinsert";
                } else if (bean.getCode() == 403) {
                    return "ProtocolRequestFailed";
                } else if (bean.getCode() != 0) {
                    return "Failed";
                }
            }
            if (bean.getParams() == null) {
                return "NULL";
            } else {
                String state = bean.getParams().getState();

                if (("Normal".equals(state))) {
                    return "Normal";
                }

                if ("Normal".equals(state)) {
                    return "NeedFormat";
                }

                if ("NeedFormat".equals(state)) {
                    return "NeedFormat";
                }

                if ("AutoFormat".equals(state)) {
                    return "AutoFormat";
                }

                if ("UnSupported".equals(state)) {
                    return "UnSupported";
                }

                return "UnSupported";
            }
        }
    }
}
