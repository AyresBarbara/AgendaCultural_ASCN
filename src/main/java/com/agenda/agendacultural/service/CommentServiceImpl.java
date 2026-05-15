package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.CommentDTO;
import com.agenda.agendacultural.exception.ResourceNotFoundException;
import com.agenda.agendacultural.model.Comment;
import com.agenda.agendacultural.model.Event;
import com.agenda.agendacultural.model.User;
import org.springframework.stereotype.Service;
import com.agenda.agendacultural.repository.CommentRepository;
import com.agenda.agendacultural.repository.EventRepository;
import com.agenda.agendacultural.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + commentDTO.getUserId()));

        Event event = eventRepository.findById(commentDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + commentDTO.getEventId()));

        Comment comment = convertToEntity(commentDTO, user, event);

        Comment saved = commentRepository.save(comment);
        return convertToDto(saved);
    }

    @Override
    public List<CommentDTO> getCommentsByEventId(UUID eventId) {
        List<Comment> comments = commentRepository.findByEvent_IdEventOrderByDateDesc(eventId);
        return comments.stream()
                       .map(this::convertToDto)
                       .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comentário não encontrado com ID: " + id);
        }
        commentRepository.deleteById(id);
    }

    // Helper method for DTO conversion
    private CommentDTO convertToDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setIdComment(comment.getIdComment());
        dto.setText(comment.getText());
        dto.setDate(comment.getDate());
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getIdUser());
        }
        if (comment.getEvent() != null) {
            dto.setEventId(comment.getEvent().getIdEvent());
        }
        return dto;
    }

    // Helper method for Entity conversion
    private Comment convertToEntity(CommentDTO commentDTO, User user, Event event) {
        Comment comment = new Comment();
        comment.setIdComment(commentDTO.getIdComment() != null ? commentDTO.getIdComment() : UUID.randomUUID());
        comment.setText(commentDTO.getText());
        comment.setDate(commentDTO.getDate() != null ? commentDTO.getDate() : java.time.LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);
        return comment;
    }
}
