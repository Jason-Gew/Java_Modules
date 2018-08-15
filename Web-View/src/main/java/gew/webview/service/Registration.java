package gew.webview.service;

import gew.webview.model.User;

import java.util.List;

/**
 * @author Jason/GeW
 */
public interface Registration {

    int getCurrentUserNumber();

    User getUser(final String username);

    List<User> getAllUsers();

    String addUser(final User user);

    boolean exist(final User user);

    boolean exist(final String name);

    boolean delete(final String username);

    String update(final User user);
}
