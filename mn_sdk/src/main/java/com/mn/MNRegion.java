package com.mn;

/**
 * @WYU-WIN
 * @date 2020/8/17 0017.
 * description：
 */
public enum MNRegion {
    CN("cn"),
    US("us"),
    TS("ts"),
    DS("ds"),
    ;

    private String region;

    MNRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region;
    }
}
