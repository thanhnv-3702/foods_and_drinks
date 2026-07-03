package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.ImportResult;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.util.CsvUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CsvImportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_PASSWORD = "Password@123";

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messages;

    public CsvImportService(UserRepository userRepository,
                            SkillRepository skillRepository,
                            PositionRepository positionRepository,
                            TeamRepository teamRepository,
                            ProjectRepository projectRepository,
                            ProjectMemberRepository projectMemberRepository,
                            PasswordEncoder passwordEncoder,
                            MessageService messages) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.positionRepository = positionRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.messages = messages;
    }

    // ---- Positions: Name, Abbreviation ----
    public ImportResult importPositions(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<List<String>> rows = read(file);
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            int line = i + 1;
            String name = cell(r, 0);
            if (name.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingName"));
                continue;
            }
            positionRepository.save(Position.builder()
                    .name(name).abbreviation(cell(r, 1)).build());
            result.incrementSuccess();
        }
        return result;
    }

    // ---- Skills: Name, Level, UsedYearNumber, UserEmail ----
    public ImportResult importSkills(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<List<String>> rows = read(file);
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            int line = i + 1;
            String name = cell(r, 0);
            String email = cell(r, 3);
            if (name.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingName"));
                continue;
            }
            if (email.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingUserEmail"));
                continue;
            }
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                result.addError(line, messages.get("csv.error.userNotFound", email));
                continue;
            }
            Integer years = null;
            String yearsRaw = cell(r, 2);
            if (!yearsRaw.isEmpty()) {
                try {
                    years = Integer.parseInt(yearsRaw);
                } catch (NumberFormatException ex) {
                    result.addError(line, messages.get("csv.error.invalidUsedYears", yearsRaw));
                    continue;
                }
            }
            skillRepository.save(Skill.builder()
                    .name(name).level(emptyToNull(cell(r, 1)))
                    .usedYearNumber(years).user(user.get()).build());
            result.incrementSuccess();
        }
        return result;
    }

    // ---- Teams: Name, Description, LeaderEmail ----
    public ImportResult importTeams(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<List<String>> rows = read(file);
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            int line = i + 1;
            String name = cell(r, 0);
            if (name.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingName"));
                continue;
            }
            User leader = null;
            String leaderEmail = cell(r, 2);
            if (!leaderEmail.isEmpty()) {
                Optional<User> opt = userRepository.findByEmail(leaderEmail);
                if (opt.isEmpty()) {
                    result.addError(line, messages.get("csv.error.leaderNotFound", leaderEmail));
                    continue;
                }
                leader = opt.get();
            }
            teamRepository.save(Team.builder()
                    .name(name).description(emptyToNull(cell(r, 1))).leader(leader).build());
            result.incrementSuccess();
        }
        return result;
    }

    // ---- Users: Name, Email, Birthday, Role, TeamName, PositionName, Password, Skills ----
    public ImportResult importUsers(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<List<String>> rows = read(file);
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            int line = i + 1;
            String name = cell(r, 0);
            String email = cell(r, 1);
            if (name.isEmpty() || email.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingNameOrEmail"));
                continue;
            }
            if (!email.contains("@")) {
                result.addError(line, messages.get("csv.error.invalidEmail", email));
                continue;
            }
            if (userRepository.existsByEmail(email)) {
                result.addError(line, messages.get("csv.error.emailExists", email));
                continue;
            }

            LocalDate birthday = null;
            String birthdayRaw = cell(r, 2);
            if (!birthdayRaw.isEmpty()) {
                try {
                    birthday = LocalDate.parse(birthdayRaw, DATE);
                } catch (Exception ex) {
                    result.addError(line, messages.get("csv.error.invalidBirthday", birthdayRaw));
                    continue;
                }
            }

            Role role = Role.USER;
            String roleRaw = cell(r, 3);
            if (!roleRaw.isEmpty()) {
                try {
                    role = Role.valueOf(roleRaw.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    result.addError(line, messages.get("csv.error.invalidRole", roleRaw));
                    continue;
                }
            }

            Team team = null;
            String teamName = cell(r, 4);
            if (!teamName.isEmpty()) {
                Optional<Team> opt = teamRepository.findFirstByNameIgnoreCase(teamName);
                if (opt.isEmpty()) {
                    result.addError(line, messages.get("csv.error.teamNotFound", teamName));
                    continue;
                }
                team = opt.get();
            }

            Position position = null;
            String positionName = cell(r, 5);
            if (!positionName.isEmpty()) {
                Optional<Position> opt = positionRepository.findFirstByNameIgnoreCase(positionName);
                if (opt.isEmpty()) {
                    result.addError(line, messages.get("csv.error.positionNotFound", positionName));
                    continue;
                }
                position = opt.get();
            }

            String rawPassword = cell(r, 6);
            String password = rawPassword.isEmpty() ? DEFAULT_PASSWORD : rawPassword;

            User user = userRepository.save(User.builder()
                    .name(name).email(email)
                    .password(passwordEncoder.encode(password))
                    .birthday(birthday).role(role).team(team).position(position)
                    .build());

            // Skills (cột 7): "name|level|years; name|level|years"
            String skillsRaw = cell(r, 7);
            if (!skillsRaw.isEmpty()) {
                for (String item : skillsRaw.split(";")) {
                    String s = item.trim();
                    if (s.isEmpty()) {
                        continue;
                    }
                    String[] parts = s.split("\\|");
                    String sName = parts[0].trim();
                    if (sName.isEmpty()) {
                        continue;
                    }
                    Integer sYears = null;
                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        try {
                            sYears = Integer.parseInt(parts[2].trim());
                        } catch (NumberFormatException ignored) {
                            sYears = null;
                        }
                    }
                    skillRepository.save(Skill.builder()
                            .name(sName)
                            .level(parts.length >= 2 ? emptyToNull(parts[1].trim()) : null)
                            .usedYearNumber(sYears)
                            .user(user)
                            .build());
                }
            }
            result.incrementSuccess();
        }
        return result;
    }

    // ---- Projects: Name, Abbreviation, StartDate, EndDate, TeamName, LeaderEmail, MemberEmails ----
    public ImportResult importProjects(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        List<List<String>> rows = read(file);
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            int line = i + 1;
            String name = cell(r, 0);
            String teamName = cell(r, 4);
            if (name.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingName"));
                continue;
            }
            if (teamName.isEmpty()) {
                result.addError(line, messages.get("csv.error.missingTeamName"));
                continue;
            }
            Optional<Team> team = teamRepository.findFirstByNameIgnoreCase(teamName);
            if (team.isEmpty()) {
                result.addError(line, messages.get("csv.error.teamNotFound", teamName));
                continue;
            }

            LocalDate start = null;
            LocalDate end = null;
            try {
                String s = cell(r, 2);
                String e = cell(r, 3);
                if (!s.isEmpty()) {
                    start = LocalDate.parse(s, DATE);
                }
                if (!e.isEmpty()) {
                    end = LocalDate.parse(e, DATE);
                }
            } catch (Exception ex) {
                result.addError(line, messages.get("csv.error.invalidDate"));
                continue;
            }
            if (start != null && end != null && end.isBefore(start)) {
                result.addError(line, messages.get("csv.error.endBeforeStart"));
                continue;
            }

            User leader = null;
            String leaderEmail = cell(r, 5);
            if (!leaderEmail.isEmpty()) {
                Optional<User> opt = userRepository.findByEmail(leaderEmail);
                if (opt.isEmpty()) {
                    result.addError(line, messages.get("csv.error.leaderNotFound", leaderEmail));
                    continue;
                }
                leader = opt.get();
            }

            // Resolve members trước, fail cả dòng nếu có email không tồn tại
            List<User> members = new ArrayList<>();
            String memberEmails = cell(r, 6);
            boolean memberError = false;
            if (!memberEmails.isEmpty()) {
                for (String em : memberEmails.split(";")) {
                    String e = em.trim();
                    if (e.isEmpty()) {
                        continue;
                    }
                    Optional<User> opt = userRepository.findByEmail(e);
                    if (opt.isEmpty()) {
                        result.addError(line, messages.get("csv.error.memberNotFound", e));
                        memberError = true;
                        break;
                    }
                    members.add(opt.get());
                }
            }
            if (memberError) {
                continue;
            }

            Project project = projectRepository.save(Project.builder()
                    .name(name).abbreviation(emptyToNull(cell(r, 1)))
                    .startDate(start).endDate(end).team(team.get()).leader(leader)
                    .build());
            for (User m : members) {
                projectMemberRepository.save(ProjectMember.builder()
                        .project(project).user(m).build());
            }
            result.incrementSuccess();
        }
        return result;
    }

    private List<List<String>> read(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        return CsvUtil.parse(content);
    }

    private static String cell(List<String> row, int idx) {
        if (idx >= row.size() || row.get(idx) == null) {
            return "";
        }
        return row.get(idx).trim();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
