package com.jrf.backend.model;

import org.springframework.lang.Nullable;

public record Pokemon(int id, String name, @Nullable String sprite) {
}
