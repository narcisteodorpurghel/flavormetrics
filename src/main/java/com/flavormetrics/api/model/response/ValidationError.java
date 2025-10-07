package com.flavormetrics.api.model.response;

import java.util.Map;

public record ValidationError(int code, String message, Map<String, String> details, String path) {}
