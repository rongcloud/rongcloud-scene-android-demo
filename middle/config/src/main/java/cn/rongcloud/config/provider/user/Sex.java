package cn.rongcloud.config.provider.user;

/**
 * 用户性别
 */
public enum Sex {
    unknown(0),
    man(1),
    woman(2);

    private int value = 0;

   public int getSex() {
        return value;
    }

    Sex(int value) {
        this.value = value;
    }

    public static Sex sexOf(String value) {
        return ("".equals(value) || "0".equals(value))
                ? unknown :
                "1".equals(value) || "男".equals(value) ? man : woman;
    }
}
