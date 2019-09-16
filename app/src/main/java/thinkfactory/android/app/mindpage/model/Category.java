package thinkfactory.android.app.mindpage.model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Benjamin J on 26-09-2018.
 */
public class Category implements Comparable {
    private int id;
    private String name;
    private List<Long> subjectIdList;
    private boolean isHidden;
    private Long createdTime;
    private Long updatedTime;
    private int priority;

    public Category() {
    }

    public Category(int id, String name, List<Long> subjectIdList, boolean isHidden, Long createdTime, Long updatedTime, int priority) {
        this.id = id;
        this.name = name;
        this.subjectIdList = subjectIdList;
        this.isHidden = isHidden;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getSubjectIdList() {
        return subjectIdList;
    }

    public void setSubjectIdList(List<Long> subjectIdList) {
        this.subjectIdList = subjectIdList;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFields() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subjectIdList=" + subjectIdList +
                ", isHidden=" + isHidden +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", priority=" + priority +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.name.compareTo(((Category)o).getName());
    }
}
