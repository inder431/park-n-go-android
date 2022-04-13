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
public class Duration {
    @PropertyName("start_time")
    public Long startTime;
    @PropertyName("end_time")
    public Long endTime;
}
