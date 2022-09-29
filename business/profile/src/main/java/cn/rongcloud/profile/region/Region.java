package cn.rongcloud.profile.region;

import java.io.Serializable;

import cn.rongcloud.profile.region.sort.Cn2Spell;


public class Region implements Serializable, Comparable<Region> {
    String region;
    Locale locale;

    public String getRegion() {
        return region;
    }

    public Locale getLocale() {
        return locale;
    }


    public String getFirstLetter() {
        return null == locale ? "#" : locale.firstLetter;
    }

    @Override
    public int compareTo(Region o) {
        return this.locale.compareTo(o.locale);
    }

    public static class Locale implements Serializable, Comparable<Locale> {
        private String en;
        private String zh;
        // 业务字段
        private String pinyin; // 姓名对应的拼音
        private String firstLetter;
        private boolean isZh;


        public void initPinYin(boolean isZh) {
            this.isZh = isZh;
            pinyin = isZh ? Cn2Spell.getPinYin(zh) : en;
            firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
            if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                firstLetter = "#";
            }
        }

        public String getLocal() {
            return isZh ? zh : en;
        }

        public String getEn() {
            return en;
        }

        public String getZh() {
            return zh;
        }

        @Override
        public int compareTo(Locale o) {
            if (firstLetter.equals("#") && !o.firstLetter.equals("#")) {
                return 1;
            } else if (!firstLetter.equals("#") && o.firstLetter.equals("#")) {
                return -1;
            } else {
                return pinyin.compareToIgnoreCase(o.pinyin);
            }
        }

    }
}
