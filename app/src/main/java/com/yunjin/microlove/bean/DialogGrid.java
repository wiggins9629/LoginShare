package com.yunjin.microlove.bean;

/**
 * @Description dialog数据集合
 * @Author 一花一世界
 */
public class DialogGrid {

    private int imageId;
    private String name;

    public DialogGrid(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DialogGrid{" +
                "imageId=" + imageId +
                ", name='" + name + '\'' +
                '}';
    }
}
