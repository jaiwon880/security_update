package io.playdata.security.comment.service;

import io.playdata.security.board.service.BoardService;
import io.playdata.security.comment.model.CommentDTO;
import io.playdata.security.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardService boardService;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Transactional
    public void deleteAllByUsername(String username) {
        List<CommentDTO> userComment = commentRepository.findByUsername(username);
        if ( userComment == null || userComment.isEmpty() == true ) {
            return;
        }
        commentRepository.deleteAllByUsername(username);
    }

    public CommentDTO createComment(Long postId, String text) {
        CommentDTO comment = new CommentDTO();
        comment.setPostId(postId);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUsername(boardService.getCurrentUsername());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

}
