package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.SpotComment;
import com.travel.system.service.SpotCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenic-spot")
public class SpotCommentController {

    @Autowired
    private SpotCommentService spotCommentService;

    @GetMapping("/{id}/comments")
    public Result<List<SpotComment>> getComments(@PathVariable Long id) {
        return Result.success(spotCommentService.findBySpotId(id));
    }

    @GetMapping("/{id}/rating")
    public Result<Double> getRating(@PathVariable Long id) {
        return Result.success(spotCommentService.getAvgRating(id));
    }

    @PostMapping("/{id}/comment")
    public Result<SpotComment> addComment(@PathVariable Long id, @RequestBody SpotComment comment) {
        comment.setSpotId(id);
        SpotComment saved = spotCommentService.addComment(comment);
        return Result.success(saved);
    }

    @DeleteMapping("/comment/{commentId}")
    public Result<?> deleteComment(@PathVariable Long commentId) {
        spotCommentService.deleteComment(commentId);
        return Result.success(null);
    }
}