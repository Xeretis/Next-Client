package me.lor3mipsum.next.client.core.social;

import java.util.ArrayList;
import java.util.List;

public class SocialManager {
    private final List<Friend> friends;
    private final List<Enemy> enemies;

    public SocialManager() {
        friends  = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<String> getFriendNames() {
        ArrayList<String> friendNames = new ArrayList<>();

        getFriends().forEach(friend -> friendNames.add(friend.getName()));
        return friendNames;
    }

    public List<String> getEnemyNames() {
        ArrayList<String> enemyNames = new ArrayList<>();

        getEnemies().forEach(enemy -> enemyNames.add(enemy.getName()));
        return enemyNames;
    }

    public boolean isFriend(String name) {
        boolean value = false;

        for (Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }

        return value;
    }

    public boolean isEnemy(String name) {
        boolean value = false;

        for (Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }

        return value;
    }

    public Friend getFriend(String name) {
        return getFriends().stream().filter(friend -> friend.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Enemy getEnemy(String name) {
        return getEnemies().stream().filter(enemy -> enemy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void addFriend(Friend friend) {
        getFriends().add(friend);
    }

    public void deleteFriend(String name) {
        getFriends().remove(getFriend(name));
    }

    public void addEnemy(Enemy enemy) {
        getEnemies().add(enemy);
    }

    public void deleteEnemy(String name) {
        getEnemies().remove(getEnemy(name));
    }
}
