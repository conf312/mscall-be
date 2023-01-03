package com.admin.message;

public enum MessageType {
    SUCCESS("No errors have occurred."),
    ERROR("Please contact the administrator."),
    NOT_MATCH("Not match x-client-key."),
    FAIL_TOKEN("reCaptchaToken Validation failed.");

    private final String label;

    MessageType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
