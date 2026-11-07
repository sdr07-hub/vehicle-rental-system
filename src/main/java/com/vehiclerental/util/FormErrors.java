package com.vehiclerental.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small helper that turns form validation errors into a simple Map,
 * so a JSP page can show them with ${errors.fieldName}.
 *
 * Example: errors.get("email") -> "Enter a valid email address"
 */
public final class FormErrors {

    // private constructor: this class only has static methods, so no objects are needed
    private FormErrors() {
    }

    public static Map<String, String> toMap(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            // A "binding failure" means the text could not be converted (for example "abc" typed into a number box)
            String message = error.isBindingFailure()
                    ? "Please enter a valid value"
                    : error.getDefaultMessage();
            // keep only the first message for each field
            errors.putIfAbsent(error.getField(), message);
        }
        return errors;
    }
}
