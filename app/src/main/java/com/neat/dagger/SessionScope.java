package com.neat.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by f.gatti.gomez on 02/02/2017.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionScope {
}
