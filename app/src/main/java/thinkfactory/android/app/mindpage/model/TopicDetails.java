package thinkfactory.android.app.mindpage.model;

import android.support.annotation.NonNull;

/**
 * Created by Benjamin J on 25-11-2018.
 */
public class TopicDetails implements Comparable {
    private int subId;
    private int id;
    private String title;
    private String desc;
    private boolean isHidden;
    private Long createdTime;
    private Long updatedTime;
    private int priority;

    public TopicDetails() {
    }

    public TopicDetails(int subId, int id, String title, String desc, boolean isHidden, Long createdTime, Long updatedTime, int priority) {
        this.subId = subId;
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.isHidden = isHidden;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.priority = priority;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getData() {
        return "TopicDetails{" +
                "subId=" + subId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", isHidden=" + isHidden +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", priority=" + priority +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.title.compareTo(((TopicDetails)o).getTitle());
    }
}
