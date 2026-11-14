package io.github.deanmave.hplclims.configs.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity rev = (AuditRevisionEntity) revisionEntity;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            rev.setUsername(auth.getName());
        } else {
            rev.setUsername("SYSTEM");
        }
    }
}
