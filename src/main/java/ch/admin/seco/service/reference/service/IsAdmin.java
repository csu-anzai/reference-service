package ch.admin.seco.service.reference.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

import ch.admin.seco.service.reference.security.AuthoritiesConstants;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public @interface IsAdmin {
}
