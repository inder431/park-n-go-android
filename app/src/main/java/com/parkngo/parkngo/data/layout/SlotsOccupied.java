package com.parkngo.parkngo.data.layout;

import com.google.firebase.database.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlotsOccupied {
    @PropertyName("columns")
    public Integer column;
    @PropertyName("occupied_by")
    public String occupiedBy;
    @PropertyName("row")
    public Integer row;
    @PropertyName("slot_code")
    public String slotCode;
    @PropertyName("slot_id")
    public String slotId;
    @PropertyName("status")
    public String status;
    @PropertyName("duration")
    public Duration duration;
}
