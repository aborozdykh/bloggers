package com.alexcorp.bloggers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommandStatus {

    private String command;
    private boolean done;
    private String output;

}
