package com.jrf.backend.model;

import java.util.Map;

public record GraphPostRequestBody(String query, Map<String, Object> variables, String operationName) { }
