package com.travel.system.service.impl;

import com.travel.system.entity.SpotComment;
import com.travel.system.mapper.SpotCommentMapper;
import com.travel.system.service.SpotCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotCommentServiceImpl implements SpotCommentService {

    @Autowired
    private SpotCommentMapper spotCommentMapper;

    @Override
    public List<SpotComment> findBySpotId(Long spotId) {
        return spotCommentMapper.findBySpotId(spotId);
    }

    @Override
    public SpotComment addComment(SpotComment comment) {
        spotCommentMapper.insert(comment);
        return comment;
    }

    @Override
    public boolean deleteComment(Long id) {
        return spotCommentMapper.deleteById(id) > 0;
    }

    @Override
    public Double getAvgRating(Long spotId) {
        return spotCommentMapper.avgRatingBySpotId(spotId);
    }
}