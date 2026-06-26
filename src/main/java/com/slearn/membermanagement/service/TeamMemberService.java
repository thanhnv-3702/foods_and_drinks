package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.TeamMemberHistory;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.TeamMemberHistoryRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamMemberService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberHistoryRepository historyRepository;

    public TeamMemberService(TeamRepository teamRepository,
                             UserRepository userRepository,
                             TeamMemberHistoryRepository historyRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true)
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy team id=" + teamId));
    }

    @Transactional(readOnly = true)
    public List<User> getMembers(Long teamId) {
        return userRepository.findByTeamIdOrderByNameAsc(teamId);
    }

    @Transactional(readOnly = true)
    public List<User> getCandidates(Long teamId) {
        return userRepository.findCandidatesForTeam(teamId);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberHistory> getHistory(Long teamId) {
        return historyRepository.findByTeamIdOrderByJoinedAtDesc(teamId);
    }

    /**
     * Thêm hoặc di chuyển user vào team. Mỗi user chỉ thuộc 1 team tại 1 thời điểm:
     * nếu user đang ở team khác, đóng bản ghi lịch sử cũ (set left_at) rồi tạo bản ghi mới.
     */
    @Transactional
    public void addOrMoveMember(Long teamId, Long userId) {
        Team team = getTeam(teamId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + userId));

        // Nếu đã ở đúng team này thì bỏ qua.
        if (user.getTeam() != null && user.getTeam().getId().equals(teamId)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        // Đóng bản ghi lịch sử đang mở (nếu user đang ở team khác).
        closeOpenHistory(userId, now);

        // Gán team mới.
        user.setTeam(team);
        userRepository.save(user);

        // Mở bản ghi lịch sử mới.
        TeamMemberHistory history = TeamMemberHistory.builder()
                .team(team)
                .user(user)
                .joinedAt(now)
                .build();
        historyRepository.save(history);
    }

    /**
     * Gỡ user khỏi team hiện tại (đóng lịch sử).
     */
    @Transactional
    public void removeMember(Long teamId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + userId));
        if (user.getTeam() == null || !user.getTeam().getId().equals(teamId)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        closeOpenHistory(userId, now);
        user.setTeam(null);
        userRepository.save(user);
    }

    private void closeOpenHistory(Long userId, LocalDateTime when) {
        historyRepository.findFirstByUserIdAndLeftAtIsNull(userId).ifPresent(open -> {
            open.setLeftAt(when);
            historyRepository.save(open);
        });
    }
}
