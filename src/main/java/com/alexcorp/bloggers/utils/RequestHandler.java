package com.alexcorp.bloggers.utils;

import java.util.Map;

public interface RequestHandler {

    Map<String,Object> handle(Map<String,Object> response);

}
