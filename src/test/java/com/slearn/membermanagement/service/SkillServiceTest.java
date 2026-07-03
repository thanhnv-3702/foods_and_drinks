package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.SkillForm;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private SkillService skillService;

    @Test
    void create_linksUserAndLogs() {
        var user = TestEntityFactory.user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));

        var form = SkillForm.builder()
                .name("Java")
                .level("Senior")
                .usedYearNumber(5)
                .userId(1L)
                .build();

        Skill created = skillService.create(form);

        assertThat(created.getUser()).isEqualTo(user);
        assertThat(created.getName()).isEqualTo("Java");
        verify(activityLogService).record(eq("CREATE_SKILL"), contains("Java"));
    }

    @Test
    void create_userNotFound_throws() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        var form = SkillForm.builder().name("Go").userId(5L).build();

        assertThatThrownBy(() -> skillService.create(form))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("5");
    }

    @Test
    void delete_removesAndLogs() {
        var user = TestEntityFactory.user(1L);
        Skill skill = TestEntityFactory.skill(10L, user);
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));

        skillService.delete(10L);

        verify(skillRepository).delete(skill);
        verify(activityLogService).record(eq("DELETE_SKILL"), contains("10"));
    }
}
