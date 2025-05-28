package com.atguigu.common.constant;

public class ProductConstant {
    public enum AttrEnum {

        ATTR_TYPE_BASE("base", 1), ATTR_TYPE_SALES("sales", 0);
        private String type;
        private int code;
        AttrEnum(String type, int code) {
            this.code = code;
            this.type = type;
        }

        public int getCode() {
            return code;
        }

        public String getType() {
            return type;
        }
    }
    public enum StatusEnum {
        SPU_NEW(0, "新建"), SPU_UP(1, "上架"), SPU_DOWN(2, "下架");

        private int code;

        private String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
