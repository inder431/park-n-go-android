package com.parkngo.parkngo.data.rates;

import com.google.firebase.database.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rate {

    @PropertyName("price")
    public Double price;
    @PropertyName("rate_id")
    public String rateId;
    @PropertyName("time")
    public String time;
    @PropertyName("show_per_hr")
    public Boolean showPerHour;
    @PropertyName("type")
    public Long type;

}
