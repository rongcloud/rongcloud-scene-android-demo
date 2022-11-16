package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

/**
 * @author gyn
 * @date 2021/9/30
 */
public interface IFun {
    int getStatus();

    void setStatus(int status);

    int getIcon();

    String getText();

    abstract class BaseFun implements IFun {

        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }
}
