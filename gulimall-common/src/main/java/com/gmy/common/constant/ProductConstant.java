package com.gmy.common.constant;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/24 15:22
 */
public class ProductConstant {
    public enum AttrEnum{
        // 基本属性
        ATTR_TYPE_BASE(1, "基本属性"),
        // 销售属性
        ATTR_ENUM_SALE(0, "销售属性");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public enum StatusEnum{

        // 商品状态
        NEW_SPU(0, "新建"),
        UP_SPU(1, "商品上架"),
        DOWN_SPU(3, "商品下架");

        private int code;
        private String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }

}
