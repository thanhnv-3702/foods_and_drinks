package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.ProfileView;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProfileService(UserRepository userRepository,
                          SkillRepository skillRepository,
                          ProjectMemberRepository projectMemberRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional(readOnly = true)
    public ProfileView getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + userId));

        List<Project> projects = projectMemberRepository.findByUserId(userId).stream()
                .map(pm -> pm.getProject())
                .collect(Collectors.toList());

        return ProfileView.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .role(user.getRole().name())
                .teamName(user.getTeam() != null ? user.getTeam().getName() : null)
                .positionName(user.getPosition() != null ? user.getPosition().getName() : null)
                .skills(skillRepository.findByUserId(userId))
                .projects(projects)
                .build();
    }
}
