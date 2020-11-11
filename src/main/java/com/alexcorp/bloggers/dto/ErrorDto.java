package com.alexcorp.bloggers.dto;

import com.alexcorp.bloggers.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {

    @JsonView(Views.UserPublickProfile.class)
    private String errorCode;

}
