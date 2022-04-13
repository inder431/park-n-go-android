package com.parkngo.parkngo.data.user;

import com.google.firebase.database.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @PropertyName("admin")
    public Boolean admin;
    @PropertyName("email")
    public String email;
    @PropertyName("name")
    public String name;
    @PropertyName("uid")
    public String uid;
}
