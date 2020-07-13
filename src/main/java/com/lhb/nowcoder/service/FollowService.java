package com.lhb.nowcoder.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    /**
     * 关注实体
     * @param userId
     * @param entityId
     * @param entityType
     */
    void follow(int userId,int entityType,int entityId);

    /**
     * 取消关注实体
     * @param userId
     * @param entityId
     * @param entityType
     */
    void unFollow(int userId, int entityType, int entityId);

    /**
     * 查找用户关注的实体数量
     * @param userId
     * @param entityType
     * @return
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 查找实体对象被关注的数量
     * @param entityId
     * @param entityType
     * @return
     */
    long findFollowerCount(int entityId,int entityType);


    /**
     * 查找用户关注用户实体的列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String,Object>> findFolloweeList(int userId, int offset, int limit);

    boolean hasFollowed(int userId,int entityType, int entityId);

    List<Map<String, Object>> findFollowerList(int userId, int offset, int limit);
}
