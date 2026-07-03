package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
public class SeedDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SeedDataLoader.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TeamMemberHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;
    private final String seedDataLocation;

    public SeedDataLoader(ResourceLoader resourceLoader,
                          ObjectMapper objectMapper,
                          UserRepository userRepository,
                          TeamRepository teamRepository,
                          PositionRepository positionRepository,
                          SkillRepository skillRepository,
                          ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          TeamMemberHistoryRepository historyRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.seed-data-location:classpath:data/seed-data.json}") String seedDataLocation) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.positionRepository = positionRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.historyRepository = historyRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedDataLocation = seedDataLocation;
    }

    @Transactional
    public void loadIfNeeded() throws IOException {
        SeedData data = readSeedData();
        ensureDefaultAccounts(data.accounts());

        if (userRepository.existsByEmail(data.markerEmail())) {
            log.debug("Seed data already present (marker: {}), skipping demo dataset", data.markerEmail());
            return;
        }

        log.info("Loading demo seed data from {}", seedDataLocation);

        Map<String, Position> positionsByName = savePositions(data.positions());
        Map<String, User> usersByKey = saveUsers(data.users(), data.demoPassword());
        Map<String, Team> teamsByKey = saveTeams(data.teams(), usersByKey);
        assignMembers(data.teamAssignments(), usersByKey, teamsByKey, positionsByName);
        saveSkills(data.skills(), usersByKey);
        saveProjects(data.projects(), usersByKey, teamsByKey);
    }

    private SeedData readSeedData() throws IOException {
        Resource resource = resourceLoader.getResource(seedDataLocation);
        return objectMapper.readValue(resource.getInputStream(), SeedData.class);
    }

    private void ensureDefaultAccounts(List<SeedAccount> accounts) {
        for (SeedAccount account : accounts) {
            if (!userRepository.existsByEmail(account.email())) {
                userRepository.save(User.builder()
                        .name(account.name())
                        .email(account.email())
                        .password(passwordEncoder.encode(account.password()))
                        .role(Role.valueOf(account.role()))
                        .build());
            }
        }
    }

    private Map<String, Position> savePositions(List<SeedPosition> positions) {
        Map<String, Position> byName = new HashMap<>();
        for (SeedPosition seed : positions) {
            Position position = positionRepository.save(Position.builder()
                    .name(seed.name())
                    .abbreviation(seed.abbreviation())
                    .build());
            byName.put(seed.name(), position);
        }
        return byName;
    }

    private Map<String, User> saveUsers(List<SeedUser> users, String demoPassword) {
        Map<String, User> byKey = new HashMap<>();
        for (SeedUser seed : users) {
            User user = userRepository.save(User.builder()
                    .name(seed.name())
                    .email(seed.email())
                    .password(passwordEncoder.encode(demoPassword))
                    .birthday(LocalDate.parse(seed.birthday()))
                    .role(Role.valueOf(seed.role()))
                    .build());
            byKey.put(seed.key(), user);
        }
        return byKey;
    }

    private Map<String, Team> saveTeams(List<SeedTeam> teams, Map<String, User> usersByKey) {
        Map<String, Team> byKey = new HashMap<>();
        for (SeedTeam seed : teams) {
            Team team = teamRepository.save(Team.builder()
                    .name(seed.name())
                    .description(seed.description())
                    .leader(usersByKey.get(seed.leaderKey()))
                    .build());
            byKey.put(seed.key(), team);
        }
        return byKey;
    }

    private void assignMembers(List<SeedTeamAssignment> assignments,
                               Map<String, User> usersByKey,
                               Map<String, Team> teamsByKey,
                               Map<String, Position> positionsByName) {
        LocalDateTime baseJoined = LocalDateTime.now().minusMonths(3);
        for (SeedTeamAssignment assignment : assignments) {
            User user = usersByKey.get(assignment.userKey());
            Team team = teamsByKey.get(assignment.teamKey());
            Position position = positionsByName.get(assignment.positionName());
            user.setTeam(team);
            user.setPosition(position);
            userRepository.save(user);
            historyRepository.save(TeamMemberHistory.builder()
                    .team(team)
                    .user(user)
                    .joinedAt(baseJoined.plusDays(assignment.joinedAtOffsetDays()))
                    .build());
        }
    }

    private void saveSkills(List<SeedSkill> skills, Map<String, User> usersByKey) {
        for (SeedSkill seed : skills) {
            skillRepository.save(Skill.builder()
                    .name(seed.name())
                    .level(seed.level())
                    .usedYearNumber(seed.usedYearNumber())
                    .user(usersByKey.get(seed.userKey()))
                    .build());
        }
    }

    private void saveProjects(List<SeedProject> projects,
                              Map<String, User> usersByKey,
                              Map<String, Team> teamsByKey) {
        for (SeedProject seed : projects) {
            Project project = projectRepository.save(Project.builder()
                    .name(seed.name())
                    .abbreviation(seed.abbreviation())
                    .startDate(LocalDate.parse(seed.startDate()))
                    .endDate(LocalDate.parse(seed.endDate()))
                    .team(teamsByKey.get(seed.teamKey()))
                    .leader(usersByKey.get(seed.leaderKey()))
                    .build());
            for (String memberKey : seed.memberKeys()) {
                projectMemberRepository.save(ProjectMember.builder()
                        .project(project)
                        .user(usersByKey.get(memberKey))
                        .build());
            }
        }
    }
}
