package com.slearn.membermanagement.support;

import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.TeamMemberHistory;
import com.slearn.membermanagement.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class TestEntityFactory {

    private TestEntityFactory() {
    }

    public static User user(Long id) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email("user" + id + "@test.local")
                .password("encoded")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .build();
    }

    public static User admin(Long id) {
        User user = user(id);
        user.setEmail("admin" + id + "@test.local");
        user.setRole(Role.ADMIN);
        return user;
    }

    public static Team team(Long id) {
        return Team.builder()
                .id(id)
                .name("Team " + id)
                .description("Description " + id)
                .build();
    }

    public static Position position(Long id) {
        return Position.builder()
                .id(id)
                .name("Position " + id)
                .abbreviation("P" + id)
                .build();
    }

    public static Skill skill(Long id, User user) {
        return Skill.builder()
                .id(id)
                .name("Java")
                .level("Senior")
                .usedYearNumber(5)
                .user(user)
                .build();
    }

    public static Project project(Long id, Team team) {
        return Project.builder()
                .id(id)
                .name("Project " + id)
                .abbreviation("PRJ" + id)
                .team(team)
                .build();
    }

    public static ProjectMember projectMember(Project project, User user) {
        return ProjectMember.builder()
                .project(project)
                .user(user)
                .build();
    }

    public static TeamMemberHistory openHistory(Team team, User user) {
        return TeamMemberHistory.builder()
                .team(team)
                .user(user)
                .joinedAt(LocalDateTime.now().minusDays(30))
                .build();
    }
}
