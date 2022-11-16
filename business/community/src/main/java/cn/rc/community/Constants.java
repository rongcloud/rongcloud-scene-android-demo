package cn.rc.community;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/24
 * @time 5:48 下午
 */
public class Constants {

    /**
     * 创建群组的唯一自定义标识
     */
    public static final String Add_Action = "tag_create_community";
    public static final String SHUT_UP = "1";//禁言
    public static final String NOT_SHUT_UP = "0";//不禁言
    public static final int markMessageRequestCode = 20001;
    public static final int markMessageResultCode = 20002;

    public enum UpdateType {
        UPDATE_TYPE_GROUP(1),
        UPDATE_TYPE_CHANNEL(2),
        UPDATE_TYPE_ALL(3);

        private final int updateTypeCode;

        public int getUpdateTypeCode() {
            return updateTypeCode;
        }

        UpdateType(int updateTypeCode) {
            this.updateTypeCode = updateTypeCode;
        }
    }

    public enum NeedAuditType {
        NOT_NEED_AUDIT_TYPE(0),//不需要审核
        NEED_AUDIT_TYPE(1);//需要审核

        private final int needAuditCode;

        public int getNeedAuditCode() {
            return needAuditCode;
        }

        NeedAuditType(int needAuditCode) {
            this.needAuditCode = needAuditCode;
        }
    }

    public enum MemberType {
        ALL(0),//在线或离线全部
        ONLINE(1),//在线
        OFFLINE(2),//离线
        BLOCKED(3),//封禁
        SHUTUP(4);//禁言

        private final int code;

        public int getCode() {
            return code;
        }

        MemberType(int code) {
            this.code = code;
        }
    }

    public enum MemberStatus {
        AUDITING(1),//审核中
        NOT_PASS_AUDIT(2),//审核未通过
        PASS_AUDIT(3),//审核通过
        QUIT(4),//退出
        KNOCKOUT(5);//踢出

        private final int code;

        public int getCode() {
            return code;
        }

        MemberStatus(int code) {
            this.code = code;
        }
    }

    public enum MessageEditStatus {
        EDIT_NORMAL_MESSAGE(1),//正在编辑正常的消息

        EDIT_QUOTE_MESSAGE(2),//引用消息

        EDIT_REEDIT_MESSAGE(3);//重新编辑消息

        int type;

        MessageEditStatus(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }


    public enum AuditStatus {
        NOT_JOIN(0),//未加入

        AUDITING(1),//审核中

        AUDIT_FAILED(2),//未通过审核

        JOINED(3);//已加入
        int code;

        AuditStatus(int type) {
            this.code = type;
        }

        public int getCode() {
            return code;
        }
    }
}
