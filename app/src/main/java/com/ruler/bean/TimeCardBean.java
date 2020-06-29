package com.ruler.bean;

import java.util.List;

/**
 * Created by WIN on 2017/11/29.
 */

public class TimeCardBean {
    /**
     * found : 10
     * info : [{"channel":0,"endtime":"2017-11-29 00:45:04","filesize":2633728,"index":0,"number":0,"starttime":"2017-11-29 00:44:58","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:46:44","filesize":44400640,"index":0,"number":1,"starttime":"2017-11-29 00:45:04","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:47:04","filesize":2496512,"index":0,"number":2,"starttime":"2017-11-29 00:46:58","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:48:44","filesize":43876352,"index":0,"number":3,"starttime":"2017-11-29 00:47:04","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:50:59","filesize":99328,"index":0,"number":4,"starttime":"2017-11-29 00:50:59","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:52:44","filesize":84180992,"index":0,"number":5,"starttime":"2017-11-29 00:50:59","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:52:59","filesize":93184,"index":0,"number":6,"starttime":"2017-11-29 00:52:59","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:54:44","filesize":84377600,"index":0,"number":7,"starttime":"2017-11-29 00:52:59","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:54:59","filesize":39936,"index":0,"number":8,"starttime":"2017-11-29 00:54:59","videotype":4294967295},{"channel":0,"endtime":"2017-11-29 00:56:44","filesize":84312064,"index":0,"number":9,"starttime":"2017-11-29 00:54:59","videotype":4294967295}]
     */

    private int found;
    private int channel;
    private int code;
    private String msg = "";
    private List<InfoBean> info;

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * channel : 0
         * endtime : 2017-11-29 00:45:04
         * filesize : 2633728
         * index : 0
         * number : 0
         * starttime : 2017-11-29 00:44:58
         * videotype : 4294967295
         */

        private int channel;
        private String endtime;
        private int filesize;
        private int index;
        private int number;
        private String starttime;
        private int videotype;

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public int getFilesize() {
            return filesize;
        }

        public void setFilesize(int filesize) {
            this.filesize = filesize;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public int getVideotype() {
            return videotype;
        }

        public void setVideotype(int videotype) {
            this.videotype = videotype;
        }
    }
}
