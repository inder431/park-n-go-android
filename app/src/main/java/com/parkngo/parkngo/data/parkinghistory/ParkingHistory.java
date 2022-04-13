package com.parkngo.parkngo.data.parkinghistory;

import com.google.firebase.database.PropertyName;
import com.parkngo.parkngo.data.layout.Duration;

import java.util.UUID;

public class ParkingHistory {

    @PropertyName("confirmed_on")
    public Long confirmedOn;
    @PropertyName("date")
    public Long date;
    @PropertyName("duration")
    public Duration duration;
    @PropertyName("parking_id")
    public String parkingId;
    @PropertyName("booking_id")
    public String bookingId;
    @PropertyName("slot_code")
    public String slotCode;
    @PropertyName("status")
    public String status;

}
