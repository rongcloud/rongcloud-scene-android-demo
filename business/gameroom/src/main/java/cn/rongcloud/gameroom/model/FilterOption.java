package cn.rongcloud.gameroom.model;

/**
 * @author gyn
 * @date 2022/3/18
 */
public class FilterOption<T> {
    private String title;
    private boolean isSelect;
    private T data;

    public FilterOption() {
    }

    public FilterOption(String title, boolean isSelect, T data) {
        this.title = title;
        this.isSelect = isSelect;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
