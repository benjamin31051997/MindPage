package thinkfactory.android.app.mindpage.model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Benjamin J on 26-09-2018.
 */
public class Subject implements Comparable {
    private int catgryId;
    private int id;
    private String name;
    private boolean isHidden;
    private Long createdTime;
    private Long updatedTime;
    private List<Long> topicIdList;
    private List<TopicDetails> topicDetailsList;
    private int priority;

    public Subject() {
    }

    public Subject(int catgryId, int id, String name, boolean isHidden, Long createdTime, Long updatedTime, List<Long> topicIdList, List<TopicDetails> topicDetailsList, int priority) {
        this.catgryId = catgryId;
        this.id = id;
        this.name = name;
        this.isHidden = isHidden;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.topicIdList = topicIdList;
        this.topicDetailsList = topicDetailsList;
        this.priority = priority;
    }

    public int getCatgryId() {
        return catgryId;
    }

    public void setCatgryId(int catgryId) {
        this.catgryId = catgryId;
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

    public List<Long> getTopicIdList() {
        return topicIdList;
    }

    public void setTopicIdList(List<Long> topicIdList) {
        this.topicIdList = topicIdList;
    }

    public List<TopicDetails> getTopicDetailsList() {
        return topicDetailsList;
    }

    public void setTopicDetailsList(List<TopicDetails> topicDetailsList) {
        this.topicDetailsList = topicDetailsList;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFields() {
        return "Subject{" +
                "catgryId=" + catgryId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", isHidden=" + isHidden +
                ", createdTime='" + createdTime + '\'' +
                ", updatedTime='" + updatedTime + '\'' +
                ", topicIdList=" + topicIdList +
                ", topicDetailsList=" + topicDetailsList +
                ", priority=" + priority +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.name.compareTo(((Subject)o).getName());
    }
}
