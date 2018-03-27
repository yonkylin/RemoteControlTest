package yonky.remotecontroltest;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/26.
 */

public class DraggableInfo implements Serializable {
    /**
     * 0 : 1 * 1 图片
     * 1 : 1 * 1 文字
     * 2 : 3 * 3 图片
     * 3 : 1 * 2 图片
     */
    private int type;
    private String text;
    private int pic;
    private int id;

    public DraggableInfo( String text, int pic, int id,int type) {
        this.type = type;
        this.text = text;
        this.pic = pic;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
