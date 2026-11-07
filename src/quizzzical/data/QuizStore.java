package quizzzical.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import quizzzical.model.Announcement;
import quizzzical.model.Question;
import quizzzical.model.QuestionType;
import quizzzical.model.Quiz;
import quizzzical.model.QuizResult;
import quizzzical.model.Student;

/** Stores student accounts, quizzes, announcements and results in the local QuizStore. */
public class QuizStore
{
    private static final Path DATA_FOLDER = Path.of(System.getProperty("user.home"), ".quizzzical");
    private static final Path DATA_FILE = DATA_FOLDER.resolve("quizstore.dat");
    private AppData data;

    public QuizStore()
    {
        load();
    }

    private void load()
    {
        try
        {
            Files.createDirectories(DATA_FOLDER);
            if(Files.exists(DATA_FILE))
            {
                try(ObjectInputStream input = new ObjectInputStream(Files.newInputStream(DATA_FILE)))
                {
                    data = (AppData) input.readObject();
                    migrateSavedData();
                    save();
                    return;
                }
            }
        }
        catch(Exception exception)
        {
            System.err.println("Could not read the saved Quizzzical store: " + exception.getMessage());
        }
        data = createDefaultData();
        save();
    }

    private void migrateSavedData()
    {
        if(data.quizzes == null) data.quizzes = new ArrayList<>();
        if(data.announcements == null) data.announcements = new ArrayList<>();
        if(data.students == null) data.students = new ArrayList<>();
        if(data.results == null) data.results = new ArrayList<>();
        if(data.quiz != null && data.quizzes.isEmpty()) data.quizzes.add(data.quiz);
        if(data.quiz == null && !data.quizzes.isEmpty()) data.quiz = data.quizzes.get(0);
        if(data.quizzes.isEmpty())
        {
            AppData defaults = createDefaultData();
            data.quiz = defaults.quiz;
            data.quizzes.add(defaults.quiz);
            if(data.adminUsername == null) data.adminUsername = defaults.adminUsername;
            if(data.adminPasswordHash == null) data.adminPasswordHash = defaults.adminPasswordHash;
            if(data.students.isEmpty()) data.students.addAll(defaults.students);
        }
        if(data.adminUsername == null || data.adminPasswordHash == null)
        {
            data.adminUsername = "quizadmin";
            data.adminPasswordHash = PasswordUtil.hash("Qz!Admin2026");
        }
        String firstQuizId = data.quizzes.get(0).getId();
        for(QuizResult result : data.results) result.assignQuizId(firstQuizId);
    }

    private AppData createDefaultData()
    {
        AppData created = new AppData();
        created.adminUsername = "quizadmin";
        created.adminPasswordHash = PasswordUtil.hash("Qz!Admin2026");
        created.students.add(new Student("s1001", PasswordUtil.hash("Stu!1001Q"), "Mokoena", "T.", "2026001"));
        created.students.add(new Student("s1002", PasswordUtil.hash("Stu!1002Q"), "Naidoo", "A.", "2026002"));
        created.students.add(new Student("s1003", PasswordUtil.hash("Stu!1003Q"), "Dlamini", "K.", "2026003"));
        created.students.add(new Student("s1004", PasswordUtil.hash("Stu!1004Q"), "Jacobs", "L.", "2026004"));
        created.students.add(new Student("s1005", PasswordUtil.hash("Stu!1005Q"), "Petersen", "R.", "2026005"));

        List<Question> starterQuestions = new ArrayList<>();
        starterQuestions.add(new Question(QuestionType.SINGLE_CHOICE,
                "Which file extension is used for a Java source file?", 2,
                Arrays.asList(".java", ".class", ".exe", ".html"), new LinkedHashMap<>(), ".java",
                "Think about the file you write before compiling it."));
        starterQuestions.add(new Question(QuestionType.TRUE_FALSE,
                "The JVM executes Java bytecode.", 2,
                Arrays.asList("True", "False"), new LinkedHashMap<>(), "True",
                "The JVM is the runtime engine for Java programs."));
        starterQuestions.add(new Question(QuestionType.FILL_IN,
                "Which Java command compiles a .java source file?", 2,
                new ArrayList<>(), new LinkedHashMap<>(), "javac",
                "The command name begins with 'java' and ends with 'c'."));
        starterQuestions.add(new Question(QuestionType.MULTI_SELECT,
                "Tick all of the following that are Java keywords.", 3,
                Arrays.asList("class", "public", "hello", "static"), new LinkedHashMap<>(), "class;public;static",
                "Choose reserved words that have a special meaning in Java."));
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("JDK", "Development kit");
        pairs.put("JRE", "Runtime environment");
        pairs.put("JVM", "Virtual machine");
        starterQuestions.add(new Question(QuestionType.MATCHING,
                "Match each Java term with its description.", 3,
                new ArrayList<>(), pairs,
                "JDK=Development kit;JRE=Runtime environment;JVM=Virtual machine",
                "Think about which item is a kit, which is a runtime and which executes bytecode."));

        created.quiz = new Quiz("Java Fundamentals - Starter Quiz", 15, 50, 75, true, true, starterQuestions);
        created.quiz.updateDetails("Computer Science", "Java Fundamentals", "Quiz",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusYears(2));
        created.quizzes.add(created.quiz);
        created.announcements.add(new Announcement("Welcome to Quizzzical",
                "Your student dashboard is ready. Check the quiz grid for upcoming and available assessments."));
        return created;
    }

    public synchronized void save()
    {
        try
        {
            Files.createDirectories(DATA_FOLDER);
            Path temporaryFile = DATA_FOLDER.resolve("quizstore.tmp");
            try(ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporaryFile)))
            {
                output.writeObject(data);
            }
            try
            {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch(IOException exception)
            {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch(IOException exception)
        {
            throw new IllegalStateException("Could not save Quizzzical data: " + exception.getMessage(), exception);
        }
    }

    public synchronized boolean authenticateAdmin(String username, String password)
    {
        return data.adminUsername.equals(username) && data.adminPasswordHash.equals(PasswordUtil.hash(password));
    }

    public synchronized boolean authenticateStudent(String username, String password)
    {
        Student student = getStudent(username);
        return student != null && student.passwordMatches(PasswordUtil.hash(password));
    }

    public synchronized Student getStudent(String username)
    {
        for(Student student : data.students) if(student.getUsername().equals(username)) return student;
        return null;
    }

    public synchronized List<Student> getStudents() { return new ArrayList<>(data.students); }
    public synchronized List<Quiz> getQuizzes() { return new ArrayList<>(data.quizzes); }
    public synchronized Quiz getQuiz() { return data.quizzes.isEmpty() ? data.quiz : data.quizzes.get(0); }
    public synchronized Quiz getQuizById(String id)
    {
        for(Quiz quiz : data.quizzes) if(quiz.getId().equals(id)) return quiz;
        return null;
    }
    public synchronized String getAdminUsername() { return data.adminUsername; }

    public synchronized void updateAdminCredentials(String username, String password)
    {
        if(username == null || username.trim().isEmpty() || password == null || password.length() < 8)
            throw new IllegalArgumentException("Enter a username and a password of at least 8 characters.");
        data.adminUsername = username.trim();
        data.adminPasswordHash = PasswordUtil.hash(password);
        save();
    }

    public synchronized void registerStudent(String surname, String initials, String studentNumber,
                                             String username, String password)
    {
        addStudent(surname, initials, studentNumber, username, password);
    }

    public synchronized void addStudent(String surname, String initials, String studentNumber,
                                        String username, String password)
    {
        if(surname == null || initials == null || studentNumber == null || username == null || password == null
                || surname.trim().isEmpty() || initials.trim().isEmpty() || studentNumber.trim().isEmpty()
                || username.trim().isEmpty() || password.length() < 8)
            throw new IllegalArgumentException("Complete every field. Passwords must have at least 8 characters.");
        for(Student student : data.students)
        {
            if(student.getUsername().equalsIgnoreCase(username.trim())
                    || student.getStudentNumber().equalsIgnoreCase(studentNumber.trim()))
                throw new IllegalArgumentException("That username or student number is already in use.");
        }
        data.students.add(new Student(username.trim(), PasswordUtil.hash(password), surname.trim(),
                initials.trim(), studentNumber.trim()));
        save();
    }

    public synchronized void updateStudentProfile(String username, String surname, String initials)
    {
        if(surname == null || initials == null || surname.trim().isEmpty() || initials.trim().isEmpty())
            throw new IllegalArgumentException("Surname and initials are required.");
        Student student = getStudent(username);
        if(student == null) throw new IllegalArgumentException("Student account not found.");
        student.updateProfile(surname.trim(), initials.trim());
        save();
    }

    public synchronized void changeStudentPassword(String username, String password)
    {
        resetStudentPassword(username, password);
    }

    public synchronized void removeStudent(String username)
    {
        data.students.removeIf(student -> student.getUsername().equals(username));
        data.results.removeIf(result -> result.getUsername().equals(username));
        save();
    }

    public synchronized void resetStudentPassword(String username, String newPassword)
    {
        if(newPassword == null || newPassword.length() < 8)
            throw new IllegalArgumentException("Password must have at least 8 characters.");
        Student student = getStudent(username);
        if(student == null) throw new IllegalArgumentException("Student account not found.");
        student.setPasswordHash(PasswordUtil.hash(newPassword));
        save();
    }

    public synchronized void addQuiz(Quiz quiz)
    {
        if(quiz == null) throw new IllegalArgumentException("Quiz is required.");
        data.quizzes.add(quiz);
        if(data.quiz == null) data.quiz = quiz;
        save();
    }

    public synchronized void removeQuiz(String quizId)
    {
        if(data.quizzes.size() <= 1) throw new IllegalArgumentException("Keep at least one quiz in Quizzzical.");
        data.quizzes.removeIf(quiz -> quiz.getId().equals(quizId));
        data.results.removeIf(result -> quizId.equals(result.getQuizId()));
        data.quiz = data.quizzes.get(0);
        save();
    }

    public synchronized void saveQuiz(Quiz quiz)
    {
        boolean found = false;
        for(int i = 0; i < data.quizzes.size(); i++)
        {
            if(data.quizzes.get(i).getId().equals(quiz.getId()))
            {
                data.quizzes.set(i, quiz);
                found = true;
                break;
            }
        }
        if(!found) data.quizzes.add(quiz);
        if(data.quiz == null || data.quiz.getId().equals(quiz.getId())) data.quiz = quiz;
        save();
    }

    public synchronized boolean hasAttempted(String username)
    {
        for(QuizResult result : data.results) if(result.getUsername().equals(username)) return true;
        return false;
    }

    public synchronized boolean hasAttempted(String username, String quizId)
    {
        return getResultForStudentAndQuiz(username, quizId) != null;
    }

    public synchronized void addResult(QuizResult result)
    {
        if(hasAttempted(result.getUsername(), result.getQuizId()))
            throw new IllegalStateException("You have already submitted this assessment.");
        data.results.add(result);
        save();
    }

    public synchronized List<QuizResult> getResults() { return new ArrayList<>(data.results); }

    public synchronized List<QuizResult> getResultsForStudent(String username)
    {
        List<QuizResult> found = new ArrayList<>();
        for(QuizResult result : data.results) if(result.getUsername().equals(username)) found.add(result);
        found.sort((a, b) -> a.getSubmittedAt().compareTo(b.getSubmittedAt()));
        return found;
    }

    public synchronized QuizResult getResultForStudent(String username)
    {
        QuizResult latest = null;
        for(QuizResult result : data.results)
            if(result.getUsername().equals(username)
                    && (latest == null || result.getSubmittedAt().isAfter(latest.getSubmittedAt()))) latest = result;
        return latest;
    }

    public synchronized QuizResult getResultForStudentAndQuiz(String username, String quizId)
    {
        for(QuizResult result : data.results)
            if(result.getUsername().equals(username) && quizId.equals(result.getQuizId())) return result;
        return null;
    }

    public synchronized void clearResults() { data.results.clear(); save(); }

    public synchronized void addAnnouncement(String title, String message)
    {
        if(title == null || title.trim().isEmpty() || message == null || message.trim().isEmpty())
            throw new IllegalArgumentException("Enter an announcement title and message.");
        data.announcements.add(0, new Announcement(title.trim(), message.trim()));
        save();
    }

    public synchronized List<Announcement> getAnnouncements() { return new ArrayList<>(data.announcements); }

    private static class AppData implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;
        private String adminUsername;
        private String adminPasswordHash;
        private Quiz quiz;
        private List<Quiz> quizzes = new ArrayList<>();
        private List<Student> students = new ArrayList<>();
        private List<QuizResult> results = new ArrayList<>();
        private List<Announcement> announcements = new ArrayList<>();
    }
}
