package com.horovod.timecountfxprobe.user;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllUsers {

    private static volatile AtomicInteger IDCounterAllUsers = new AtomicInteger(0);

    private static Map<Integer, User> users = new ConcurrentHashMap<>();

    // Заводим отдельный список для удаленных пользователей, чтобы иметь возможность
    // подставлять вместо них обозначение "пользователь удален" в подборках статистики
    private static Map<Integer, User> deletedUsers = new ConcurrentHashMap<>();



    /** Стандартные геттеры и сеттеры */

    public static int getIDCounterAllUsers() {
        return IDCounterAllUsers.get();
    }

    public static synchronized void setIDCounterAllUsers(int newIDCounterAllUsers) {
        AllUsers.IDCounterAllUsers.set(newIDCounterAllUsers);
    }

    public static synchronized int incrementIdNumberAndGet() {
        return IDCounterAllUsers.incrementAndGet();
    }

    public static Map<Integer, User> getUsers() {
        return users;
    }

    public static synchronized void setUsers(Map<Integer, User> users) {
        AllUsers.users = users;
    }

    public static Map<Integer, User> getDeletedUsers() {
        return deletedUsers;
    }

    public static synchronized void setDeletedUsers(Map<Integer, User> deletedUsers) {
        AllUsers.deletedUsers = deletedUsers;
    }

    /** Геттеры отдельных юзеров из мапы
     * @return null
     * */

    public static User getOneUser(int idUser) {
        if (isUserExist(idUser)) {
            return users.get(idUser);
        }
        return null;
    }

    public static User getOneUser(String userNameLogin) {
        if (isNameLoginExist(userNameLogin)) {
            for (User u : users.values()) {
                if (u.getNameLogin().equals(userNameLogin)) {
                    return u;
                }
            }
        }
        return null;
    }

    public static User getOneDeletedUser(int deletedUser) {
        if (deletedUsers.containsKey(deletedUser)) {
            return deletedUsers.get(deletedUser);
        }
        return null;
    }



    /** Добавление и удаление пользователя */

    public static synchronized boolean addUser(User user) {
        if (!users.containsKey(user.getIDNumber()) && !isNameLoginExist(user.getNameLogin())) {
            users.put(user.getIDNumber(), user);
            return true;
        }
        return false;
    }

    public static synchronized boolean deleteUser(int idUser) {
        if (isUserExist(idUser)) {
            deletedUsers.put(idUser, users.get(idUser));
            users.remove(idUser);
            return true;
        }
        return false;
    }

    public static synchronized boolean resurrectUser(int idUser) {
        if (isUserDeleted(idUser)) {
            users.put(idUser, deletedUsers.get(idUser));
            deletedUsers.remove(idUser);
            return true;
        }
        return false;
    }



    /** Методы проверки существования юзера
     * * с данным ID и nameLogin
     */
    public static boolean isUserExist(int idNumber) {
        if (idNumber <= 0 || idNumber > getIDCounterAllUsers()) {
            return false;
        }
        return users.containsKey(idNumber);
    }

    public static boolean isNameLoginExist(String nameLog) {
        if (nameLog == null || nameLog.isEmpty()) {
            return false;
        }

        Collection<User> tmpUsers = users.values();

        for (User u : tmpUsers) {
            if (nameLog.equals(u.getNameLogin())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserDeleted(int idNumber) {
        if (idNumber <= 0 || idNumber > getIDCounterAllUsers()) {
            return false;
        }
        return deletedUsers.containsKey(idNumber);
    }

}