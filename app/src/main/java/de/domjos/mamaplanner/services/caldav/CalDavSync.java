package de.domjos.mamaplanner.services.caldav;

import android.util.Log;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.GenerateQuery;

import java.util.List;

public class CalDavSync {
    private HttpClient httpClient;
    private String basePath;


    public CalDavSync(CalDavCredentials credentials) {
        this.basePath = credentials.getBasePath();

        this.httpClient = new HttpClient();
        this.httpClient.getHostConfiguration().setHost(credentials.getHostName());
        this.setCredentials(this.httpClient, credentials.getUserName(), credentials.getPassword());
    }

    public void sync() {

    }

    public boolean test() {
        try {
            this.listCalendar(this.basePath);
            return true;
        } catch (Exception ex) {
            Log.v("Exception", ex.toString());
        }
        return false;
    }

    private List<Calendar> listCalendar(String path) throws Exception {
        CalDAVCollection collection = new CalDAVCollection(path,
                (HostConfiguration) httpClient.getHostConfiguration().clone(),
                new CalDAV4JMethodFactory(),
                CalDAVConstants.PROC_ID_DEFAULT
        );

        GenerateQuery gq=new GenerateQuery();
        CalendarQuery calendarQuery = gq.generate();
        return collection.queryCalendars(this.httpClient, calendarQuery);
    }

    private void setCredentials(HttpClient client, String user, String pass) {
        Credentials credentials = new UsernamePasswordCredentials(user, pass);
        client.getState().setCredentials(AuthScope.ANY, credentials);
    }
}
