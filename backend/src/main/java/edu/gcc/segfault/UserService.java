package edu.gcc.segfault;

public class UserService {
    private final User defaultUser = new User();

    public User getUser() {
        return defaultUser;
    }
}
