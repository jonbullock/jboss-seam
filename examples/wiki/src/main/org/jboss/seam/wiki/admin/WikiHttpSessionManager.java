/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.admin;

import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("wikiHttpSessionManager")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiHttpSessionManager implements Serializable {

    protected static final String SESSION_ATTR_IDENTITY = "org.jboss.seam.security.identity";
    protected static final String SESSION_ATTR_ACCESSLVL = "currentAccessLevel";

    @Logger
    private Log log;

    transient private Map<String, Boolean> selectedSessions = new HashMap<String,Boolean>();
    transient private Map<String, Long> sessionsSize = new HashMap<String,Long>();

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public Map<String, Boolean> getSelectedSessions() { return selectedSessions; }
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public Map<String, Long> getSessionsSize() { return sessionsSize; }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public List<HttpSession> getSessions() {
        return new ArrayList(WikiServletListener.getSessions().values());
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public HttpSession getSession(String id) {
        return WikiServletListener.getSessions().get(id);
    }

    /**
     * Calculate the size of an HttpSession using serialization.
     * <p>
     * This is extremely crude and a guesstimate, especially because this ignores any
     * serialization errors.
     * </p>
     *
     * @param id the identifier of th HttpSession
     * @return size in bytes
     */
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public long getSessionSize(String id) {
        HttpSession session = WikiServletListener.getSessions().get(id);
        long sessionSize = 0;
        if (session != null) {
            Enumeration elem = session.getAttributeNames();
            while (elem.hasMoreElements()) {
                String attName = (String)elem.nextElement();
                log.debug("serializing session attribute: " + attName);
                ByteArrayOutputStream bos = null;
                try {
                    bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos);
                    out.writeObject(
                        session.getAttribute(attName)
                    );
                    out.close();
                } catch (Exception ex) {
                    // Just swallow that
                    log.warn("error during serialization, ignoring: " + ex);
                }
                if (bos != null) {
                    byte[] buf = bos.toByteArray();
                    sessionSize = sessionSize + buf.length;
                }
            }
        }
        return sessionSize;
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public String getUsername(String id) {
        log.debug("trying to get username of Http session: " + id);
        HttpSession session = WikiServletListener.getSessions().get(id);
        String username = User.GUEST_USERNAME;
        if (session != null) {
            Identity identity = (Identity)session.getAttribute(SESSION_ATTR_IDENTITY);
            if (identity != null && identity.getPrincipal() != null)
                username = identity.getPrincipal().getName();
        }
        return username;
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void calculateSelectedSessionsSize() {
        sessionsSize.clear();
        for (Map.Entry<String, Boolean> entry : selectedSessions.entrySet()) {
            if (entry.getValue()) {
                log.debug("calculating size of Http session: " + entry.getKey());
                sessionsSize.put(
                    entry.getKey(),
                    getSessionSize( entry.getKey() )
                );
            }
        }
        selectedSessions.clear();
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void refresh() {
        selectedSessions.clear();
    }

    /* TODO: The way Seam handles sessions conflicts with "destroying" it from the "outside"
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void invalidateSelectedSessions() {
        for (Map.Entry<String, Boolean> entry : selectedSessions.entrySet()) {
            if (entry.getValue()) {
                HttpSession s = getSession(entry.getKey());
                if (s != null) {
                    log.debug("########### invalidating Http session: " + entry.getKey());
                    Session seamSession = (Session)s.getAttribute("org.jboss.seam.web.session");
                    seamSession.invalidate();
                }
            }
        }
        selectedSessions.clear();
    }
    */

    public long getNumberOfOnlineMembers() {
        Collection<HttpSession> sessions = WikiServletListener.getSessions().values();

        long loggedInUsers = 0l;
        for (HttpSession session : sessions) {
            Integer userLevel = (Integer)session.getAttribute(SESSION_ATTR_ACCESSLVL);
            if (userLevel != null && userLevel > Role.GUESTROLE_ACCESSLEVEL) {
                loggedInUsers++;
            }
        }
        return loggedInUsers;

    }
    
    public long getNumberOfOnlineGuests() {
        return WikiServletListener.getSessions().values().size() - getNumberOfOnlineMembers();
    }
    

}