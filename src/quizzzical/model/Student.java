package quizzzical.model;

import java.io.Serializable;

public class Student implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String username;
    private String passwordHash;
    private String surname;
    private String initials;
    private final String studentNumber;

    public Student(String username, String passwordHash, String surname,
                   String initials, String studentNumber)
    {
        this.username = username;
        this.passwordHash = passwordHash;
        this.surname = surname;
        this.initials = initials;
        this.studentNumber = studentNumber;
    }

    public String getUsername() { return username; }
    public String getSurname() { return surname; }
    public String getInitials() { return initials; }
    public String getStudentNumber() { return studentNumber; }
    public String getFullName() { return surname + " " + initials; }
    public boolean passwordMatches(String hash) { return passwordHash.equals(hash); }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void updateProfile(String surname, String initials)
    {
        this.surname = surname;
        this.initials = initials;
    }
}

