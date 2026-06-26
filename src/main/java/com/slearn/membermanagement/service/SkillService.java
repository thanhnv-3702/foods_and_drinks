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

    public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kỹ năng id=" + id));
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
        return skillRepository.save(skill);
    }

    @Transactional
    public Skill update(Long id, SkillForm form) {
        Skill skill = getById(id);
        skill.setName(form.getName());
        skill.setLevel(form.getLevel());
        skill.setUsedYearNumber(form.getUsedYearNumber());
        skill.setUser(findUser(form.getUserId()));
        return skillRepository.save(skill);
    }

    @Transactional
    public void delete(Long id) {
        Skill skill = getById(id);
        skillRepository.delete(skill);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + userId));
    }
}
