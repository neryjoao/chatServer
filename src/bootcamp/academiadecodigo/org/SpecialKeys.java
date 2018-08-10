package bootcamp.academiadecodigo.org;

/**
 * Created by codecadet on 25/06/2018.
 */
public enum SpecialKeys {
    who("/who"),
    privateChat("privateChat");

    String comand;

    SpecialKeys(String comand) {
        this.comand = comand;
    }

    public String getComand() {
        return comand;
    }

}
