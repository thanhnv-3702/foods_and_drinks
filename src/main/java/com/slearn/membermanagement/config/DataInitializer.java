package com.slearn.membermanagement.config;

import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.TeamMemberHistory;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.TeamMemberHistoryRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Khởi tạo tài khoản mặc định và dữ liệu mẫu để demo toàn bộ chức năng.
 * Chỉ chạy một lần (kiểm tra marker email {@code demo_a@slearn.local}).
 */
@Configuration
@Profile("!test")
public class DataInitializer {

    private static final String MARKER_EMAIL = "demo_a@slearn.local";
    private static final String ADMIN_EMAIL = "admin@slearn.local";
    private static final String ADMIN_PASSWORD = "Admin@12345";
    private static final String DEMO_PASSWORD = "User@12345";

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      TeamRepository teamRepository,
                                      PositionRepository positionRepository,
                                      SkillRepository skillRepository,
                                      ProjectRepository projectRepository,
                                      ProjectMemberRepository projectMemberRepository,
                                      TeamMemberHistoryRepository historyRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            ensureDefaultAccounts(userRepository, passwordEncoder);

            if (userRepository.existsByEmail(MARKER_EMAIL)) {
                return;
            }

            LocalDateTime joined = LocalDateTime.now().minusMonths(3);

            Position posBe = savePosition(positionRepository, "Backend Developer", "BE");
            Position posFe = savePosition(positionRepository, "Frontend Developer", "FE");
            Position posQa = savePosition(positionRepository, "QA Engineer", "QA");
            Position posPm = savePosition(positionRepository, "Project Manager", "PM");

            User userA = saveUser(userRepository, passwordEncoder, "Nguyen Van A", MARKER_EMAIL,
                    LocalDate.of(1992, 3, 15), Role.USER, null, null);
            User userB = saveUser(userRepository, passwordEncoder, "Tran Thi B", "demo_b@slearn.local",
                    LocalDate.of(1994, 7, 20), Role.USER, null, null);
            User userC = saveUser(userRepository, passwordEncoder, "Le Van C", "demo_c@slearn.local",
                    LocalDate.of(1993, 11, 8), Role.USER, null, null);
            User userD = saveUser(userRepository, passwordEncoder, "Pham Thi D", "demo_d@slearn.local",
                    LocalDate.of(1990, 1, 25), Role.USER, null, null);
            User userE = saveUser(userRepository, passwordEncoder, "Hoang Van E", "demo_e@slearn.local",
                    LocalDate.of(1996, 9, 12), Role.USER, null, null);

            Team teamAlpha = teamRepository.save(Team.builder()
                    .name("Team Alpha")
                    .description("Nhóm phát triển web")
                    .leader(userA)
                    .build());
            Team teamBeta = teamRepository.save(Team.builder()
                    .name("Team Beta")
                    .description("Nhóm phát triển mobile")
                    .leader(userD)
                    .build());

            assignToTeam(userRepository, historyRepository, userA, teamAlpha, posBe, joined);
            assignToTeam(userRepository, historyRepository, userB, teamAlpha, posFe, joined.plusDays(5));
            assignToTeam(userRepository, historyRepository, userC, teamBeta, posBe, joined.plusDays(10));
            assignToTeam(userRepository, historyRepository, userD, teamBeta, posPm, joined);
            assignToTeam(userRepository, historyRepository, userE, teamBeta, posQa, joined.plusDays(15));

            saveSkill(skillRepository, "Java", "Advanced", 5, userA);
            saveSkill(skillRepository, "Spring Boot", "Advanced", 4, userA);
            saveSkill(skillRepository, "React", "Intermediate", 3, userB);
            saveSkill(skillRepository, "TypeScript", "Intermediate", 2, userB);
            saveSkill(skillRepository, "Kotlin", "Intermediate", 2, userC);
            saveSkill(skillRepository, "Agile", "Intermediate", 4, userD);
            saveSkill(skillRepository, "Selenium", "Beginner", 1, userE);

            Project projWeb = projectRepository.save(Project.builder()
                    .name("Website Bán Hàng")
                    .abbreviation("WEB")
                    .startDate(LocalDate.of(2026, 1, 5))
                    .endDate(LocalDate.of(2026, 8, 30))
                    .team(teamAlpha)
                    .leader(userA)
                    .build());
            addProjectMember(projectMemberRepository, projWeb, userA);
            addProjectMember(projectMemberRepository, projWeb, userB);

            Project projApp = projectRepository.save(Project.builder()
                    .name("App Mobile Giao Hàng")
                    .abbreviation("APP")
                    .startDate(LocalDate.of(2026, 3, 1))
                    .endDate(LocalDate.of(2026, 12, 31))
                    .team(teamBeta)
                    .leader(userD)
                    .build());
            addProjectMember(projectMemberRepository, projApp, userC);
            addProjectMember(projectMemberRepository, projApp, userD);
            addProjectMember(projectMemberRepository, projApp, userE);
        };
    }

    private void ensureDefaultAccounts(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            userRepository.save(User.builder()
                    .name("System Admin")
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .role(Role.ADMIN)
                    .build());
        }
        if (!userRepository.existsByEmail("user@slearn.local")) {
            userRepository.save(User.builder()
                    .name("Demo User")
                    .email("user@slearn.local")
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .role(Role.USER)
                    .build());
        }
    }

    private Position savePosition(PositionRepository repo, String name, String abbr) {
        return repo.save(Position.builder().name(name).abbreviation(abbr).build());
    }

    private User saveUser(UserRepository repo, PasswordEncoder encoder,
                          String name, String email, LocalDate birthday,
                          Role role, Team team, Position position) {
        return repo.save(User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(DEMO_PASSWORD))
                .birthday(birthday)
                .role(role)
                .team(team)
                .position(position)
                .build());
    }

    private void assignToTeam(UserRepository userRepository,
                              TeamMemberHistoryRepository historyRepository,
                              User user, Team team, Position position,
                              LocalDateTime joinedAt) {
        user.setTeam(team);
        user.setPosition(position);
        userRepository.save(user);
        historyRepository.save(TeamMemberHistory.builder()
                .team(team)
                .user(user)
                .joinedAt(joinedAt)
                .build());
    }

    private void saveSkill(SkillRepository repo, String name, String level, int years, User user) {
        repo.save(Skill.builder()
                .name(name)
                .level(level)
                .usedYearNumber(years)
                .user(user)
                .build());
    }

    private void addProjectMember(ProjectMemberRepository repo, Project project, User user) {
        repo.save(ProjectMember.builder().project(project).user(user).build());
    }
}
