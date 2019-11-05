package de.domjos.mamaplanner.model.objects;

public interface IDatabaseObject {

    void setID(long ID);
    long getID();

    void setTimeStamp(long timeStamp);
    long getTimeStamp();

    String getTable();
}
