package cn.rc.community.message.sysmsg;

public enum UpdateUserInfoType {
    none(0),
    /**
     * 1: 更新昵称
     */
    nickName(1),

    /**
     * 2: 更新了头像
     */
    portrait(2);

    private final int type;

    UpdateUserInfoType(int v) {
        this.type = v;
    }

    public int getType() {
        return type;
    }

    public static UpdateUserInfoType typeOf(int type) {
        if (1 == type) {
            return nickName;
        } else if (2 == type) {
            return portrait;
        } else {
            return none;
        }
    }

}