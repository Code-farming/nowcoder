package com.lhb.nowcoder.service;

public interface LikeService {
    /**
     * 用户点赞某个实体的行为
     * @param userId
     * @param entityType
     * @param entityId
     */
    void like(int userId,int entityType,int entityId,int entityUserId);

    /**
     * 查找某个实体的点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findEntityLikeCount(int entityType,int entityId);

    /**
     * 查找用户对某个实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int findEntityLikeStatus(int userId,int entityType,int entityId);

    Integer findUserLikeCount(int userId);
}
