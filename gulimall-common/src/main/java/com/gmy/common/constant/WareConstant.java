package com.gmy.common.constant;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/29 13:12
 */
public class WareConstant {

    public enum PurchaseStatusEnum{

        // 基本属性
        CREATE(0, "新建"),
        // 销售属性
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINSH(3, "已完成"),
        HASERROR(4, "有异常");

        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg) {
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



    public enum PurchaseDetailStatusEnum{

        // 基本属性
        CREATE(0, "新建"),
        // 销售属性
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINSH(3, "已完成"),
        HASERROR(4, "有异常");

        private int code;
        private String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
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
