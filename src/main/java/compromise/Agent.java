package compromise;

import java.util.Map;

public class Agent {
    private int id;
    private String login;
    private String firstName;
    private String secondName;
    private Map<Integer, Integer> ratingList;
    private Algorithm optimalAlgorithm;

    public Agent(int id, String login, String firstName, String secondName) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public Algorithm getOptimalAlgorithm() {
        return optimalAlgorithm;
    }

    public void setOptimalAlgorithm(Algorithm optimalAlgorithm) {
        this.optimalAlgorithm = optimalAlgorithm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Map<Integer, Integer> getRatingList() {
        return ratingList;
    }

    public void setRatingList(Map<Integer, Integer> ratingList) {
        this.ratingList = ratingList;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
