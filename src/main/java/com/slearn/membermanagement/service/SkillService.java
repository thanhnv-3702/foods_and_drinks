package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.SkillForm;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final MessageService messages;

    public SkillService(SkillRepository skillRepository, UserRepository userRepository,
                        ActivityLogService activityLogService,
                        MessageService messages) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.messages = messages;
    }

    @Transactional(readOnly = true)
    public Page<Skill> findAll(Pageable pageable) {
        return skillRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Skill getById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messages.get("error.skill.notFound", id)));
    }

    @Transactional(readOnly = true)
    public SkillForm getFormById(Long id) {
        Skill s = getById(id);
        return SkillForm.builder()
                .id(s.getId())
                .name(s.getName())
                .level(s.getLevel())
                .usedYearNumber(s.getUsedYearNumber())
                .userId(s.getUser().getId())
                .build();
    }

    @Transactional
    public Skill create(SkillForm form) {
        User user = findUser(form.getUserId());
        Skill skill = Skill.builder()
                .name(form.getName())
                .level(form.getLevel())
                .usedYearNumber(form.getUsedYearNumber())
                .user(user)
                .build();
        skillRepository.save(skill);
        activityLogService.record("CREATE_SKILL",
                messages.get("activity.skill.created", skill.getName(), user.getId()));
        return skill;
    }

    @Transactional
    public Skill update(Long id, SkillForm form) {
        Skill skill = getById(id);
        skill.setName(form.getName());
        skill.setLevel(form.getLevel());
        skill.setUsedYearNumber(form.getUsedYearNumber());
        skill.setUser(findUser(form.getUserId()));
        skillRepository.save(skill);
        activityLogService.record("UPDATE_SKILL",
                messages.get("activity.skill.updated", skill.getName(), skill.getId()));
        return skill;
    }

    @Transactional
    public void delete(Long id) {
        Skill skill = getById(id);
        skillRepository.delete(skill);
        activityLogService.record("DELETE_SKILL",
                messages.get("activity.skill.deleted", skill.getName(), id));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(messages.get("error.user.notFound", userId)));
    }
}
