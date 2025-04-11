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

}
