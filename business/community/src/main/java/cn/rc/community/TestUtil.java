package cn.rc.community;


import java.util.ArrayList;
import java.util.List;

import cn.rc.community.bean.CommunityBean;

public class TestUtil {

    public static List<CommunityBean> getTestList(String pre, int count) {
        List<CommunityBean> data = new ArrayList<>();
        CommunityBean bean;
        for (int i = 0; i < count; i++) {
            bean = new CommunityBean(pre + "_" + i, System.nanoTime() + "");
            data.add(bean);
        }
        return data;
    }


    public static List<String> getTestStringList(String pre, int count) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(pre + "_" + i);
        }
        return data;
    }

    public static List<Data> getChannelGroups() {
        List<Data> dataList = new ArrayList<>();
        dataList.add(new Channel("文字频道0"));
        dataList.add(new Channel("语音频道0"));
//        dataList.add(new Channel("帖子频道0"));
        for (int i = 1; i < 3; i++) {
            String name = "test分组" + i;
            Group group = new Group(name);
            group.channels = new ArrayList<>();
            group.channels.add(new Channel("文字频道" + i));
            group.channels.add(new Channel("语音频道" + i));
//            group.channels.add(new Channel("帖子频道" + i));
            dataList.add(group);
        }
        return dataList;
    }

    public static class Data {
        public String name;
        public String setting;

        public Data(String name) {
            this.name = name;
        }
    }

    public static class Group extends Data {
        public List<Channel> channels;

        public Group(String name) {
            super(name);
        }
    }

    public static class Channel extends Data {
        public Channel(String name) {
            super(name);
        }
    }
}
