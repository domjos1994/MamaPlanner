package de.domjos.mamaplanner.model.family;

import java.util.Date;

import de.domjos.mamaplanner.model.objects.IDatabaseObject;

public final class Family implements IDatabaseObject {
    private long ID, timeStamp;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;
    private byte[] profilePicture;
    private int color;

    public Family() {
        super();

        this.ID = 0L;
        this.timeStamp = 0L;
        this.firstName = "";
        this.lastName = "";
    }

    @Override
    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public long getID() {
        return this.ID;
    }

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String getTable() {
        return "family";
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public byte[] getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.firstName==null ? "" : this.firstName, this.lastName==null ? "" : this.lastName);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
