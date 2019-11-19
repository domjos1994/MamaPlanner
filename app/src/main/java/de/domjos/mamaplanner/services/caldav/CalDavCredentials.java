package de.domjos.mamaplanner.services.caldav;

public class CalDavCredentials {
    private String userName;
    private String password;
    private String hostName;
    private String basePath;

    public final static String CAL_USER = "cal_userName";
    public final static String CAL_PWD = "cal_password";
    public final static String CAL_HOST = "cal_hostName";
    public final static String CAL_BASE = "cal_basePath";

    public CalDavCredentials(String userName, String password, Type type) {
        this.userName = userName;
        this.password = password;

        switch (type) {
            case GOOGLE:
                this.hostName = "https://www.google.com";
                this.basePath = String.format("/calendar/dav/%s/", this.userName);
                break;
            case YAHOO:
                this.hostName = "https://caldav.calendar.yahoo.com";
                this.basePath = String.format("/dav/%s/Calendar/", this.userName);
                break;
            default:
                this.hostName = "";
                this.basePath = "";
                break;
        }
    }

    public CalDavCredentials(String userName, String password, String hostName, String basePath) {
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
        this.basePath = basePath;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public enum Type {
        YAHOO,
        GOOGLE
    }
}
