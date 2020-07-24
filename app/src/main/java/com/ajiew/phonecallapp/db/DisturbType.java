package com.ajiew.phonecallapp.db;

public enum DisturbType {

    DISTURB_ADDRESS(1,"归属地拦截"),
    DISTURB_CALL(2,"电话拦截");

    private int type;
    private String msg;
    DisturbType(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

     public static DisturbType value(int type){
        for (DisturbType c : DisturbType.values()) {
            if (type == c.getType()) {
                return c;
            }
        }
        return DISTURB_ADDRESS;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
