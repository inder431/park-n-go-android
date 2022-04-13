package com.parkngo.parkngo.data.layout;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Layout implements Serializable {
    @PropertyName("columns")
    public Integer columns;
    @PropertyName("layout_id")
    public String layoutId;
    @PropertyName("rows")
    public Integer rows;
    @PropertyName("layout_title")
    public String layoutTitle;
    @PropertyName("active")
    public Boolean active;
}
