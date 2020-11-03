package com.alexcorp.bloggers.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DoughnutCard<T> extends Card implements Serializable {

    public final static String PER_CENT_TYPE = "pre_cent";
    public final static String DEFAULT = "default";

    private String title;
    private T amount;
    private String type;

    private Map<String, T> values;
}
