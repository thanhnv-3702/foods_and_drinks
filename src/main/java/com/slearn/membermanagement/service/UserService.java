package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.UserForm;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TeamRepository teamRepository,
                       PositionRepository positionRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Position> findAllPositions() {
        return positionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email, Long excludeId) {
        if (excludeId == null) {
            return userRepository.existsByEmail(email);
        }
        return userRepository.existsByEmailAndIdNot(email, excludeId);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + id));
    }

    @Transactional(readOnly = true)
    public UserForm getFormById(Long id) {
        User u = getById(id);
        return UserForm.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .birthday(u.getBirthday())
                .role(u.getRole())
                .teamId(u.getTeam() != null ? u.getTeam().getId() : null)
                .positionId(u.getPosition() != null ? u.getPosition().getId() : null)
                .build();
    }

    @Transactional
    public User create(UserForm form) {
        User user = User.builder()
                .name(form.getName())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .birthday(form.getBirthday())
                .role(form.getRole())
                .team(resolveTeam(form.getTeamId()))
                .position(resolvePosition(form.getPositionId()))
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserForm form) {
        User user = getById(id);
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        if (StringUtils.hasText(form.getPassword())) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        user.setBirthday(form.getBirthday());
        user.setRole(form.getRole());
        user.setTeam(resolveTeam(form.getTeamId()));
        user.setPosition(resolvePosition(form.getPositionId()));
        return userRepository.save(user);
    }

    /**
     * Xóa người dùng. Trả về null nếu thành công, hoặc thông báo lỗi ràng buộc.
     */
    @Transactional
    public String delete(Long id, Long currentUserId) {
        User user = getById(id);
        if (currentUserId != null && currentUserId.equals(id)) {
            return "Không thể xóa tài khoản đang đăng nhập.";
        }
        try {
            userRepository.delete(user);
            userRepository.flush();
            return null;
        } catch (DataIntegrityViolationException ex) {
            return "Không thể xóa \"" + user.getName()
                    + "\" vì đang được tham chiếu (leader team/project, kỹ năng, lịch sử...).";
        }
    }

    private Team resolveTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy team id=" + teamId));
    }

    private Position resolvePosition(Long positionId) {
        if (positionId == null) {
            return null;
        }
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vị trí id=" + positionId));
    }
}
