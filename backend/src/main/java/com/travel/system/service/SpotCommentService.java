package com.travel.system.service;

import com.travel.system.entity.SpotComment;

import java.util.List;

public interface SpotCommentService {
    List<SpotComment> findBySpotId(Long spotId);
    SpotComment addComment(SpotComment comment);
    boolean deleteComment(Long id);
    Double getAvgRating(Long spotId);
}