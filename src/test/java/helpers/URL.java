package helpers;

public class URL {
    private final static String HOST_TEST = "https://stellarburgers.nomoreparties.site";

    public static String getHost() {
        if (System.getProperty("host") != null) {
            return System.getProperty("host");
        } else {
            return HOST_TEST;
        }
    }
}
