package org.techtown.dbproject;


public class Data{

    public int _id;
    public String type;
    public String toilet;
    public String address;
    public double latitude;
    public double longitude;
    public String number;
    public String time;

    //set function
    public void setId(int _id) { this._id = _id; }
    public void setType(String type){this.type=type;}
    public void setToilet(String toilet){this.toilet=toilet;}
    public void setAddress(String address){this.address=address;}
    public void setLatitude(double latitude){this.latitude=latitude;}
    public void setLongitude(double longitude){this.longitude=longitude;}
    public void setNumber(String number){this.number=number;}
    public void setTime(String time){this.time=time;}

    public int getID(){return this._id;}
    public String getType(){return this.type;}
    public String getToilet(){return this.toilet;}
    public String getAddress(){return this.address;}
    public double getLatitude() {return this.latitude;}
    public double getLongitude() {return this.longitude;}
    public String getNumber() {return this.number;}
    public String getTime() {return this.time;}
}


