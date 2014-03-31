package com.smart.security;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;

public class SmartShiroFilter extends ShiroFilter {

    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager webSecurityManager = super.getSecurityManager();
        initRealms(webSecurityManager);
        initCache(webSecurityManager);
    }

    private void initRealms(WebSecurityManager webSecurityManager) {
        String securityRealms = SmartProps.getRealms();
        if (securityRealms != null) {
            String[] securityRealmArray = securityRealms.split(",");
            if (securityRealmArray.length > 0) {
                RealmSecurityManager realmSecurityManager = (RealmSecurityManager) webSecurityManager;
                Set<Realm> realms = new LinkedHashSet<Realm>();
                for (String securityRealm : securityRealmArray) {
                    if (securityRealm.equalsIgnoreCase("jdbc")) {
                        addJdbcRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase("ad")) {
                        addAdRealm(realms);
                    }
                }
                realmSecurityManager.setRealms(realms);
            }
        }
    }

    private void addJdbcRealm(Set<Realm> realms) {
        SmartJdbcRealm smartJdbcRealm = new SmartJdbcRealm();
        realms.add(smartJdbcRealm);
    }

    private void addAdRealm(Set<Realm> realms) {
        ActiveDirectoryRealm realm = new ActiveDirectoryRealm();
        realm.setUrl(SmartProps.getAdUrl());
        realm.setSystemUsername(SmartProps.getAdSystemUsername());
        realm.setSystemPassword(SmartProps.getAdSystemPassword());
        realm.setSearchBase(SmartProps.getAdSearchBase());
        realms.add(realm);
    }

    private void initCache(WebSecurityManager webSecurityManager) {
        if (SmartProps.isCache()) {
            CachingSecurityManager cachingSecurityManager = (CachingSecurityManager) webSecurityManager;
            CacheManager cacheManager = new MemoryConstrainedCacheManager();
            cachingSecurityManager.setCacheManager(cacheManager);
        }
    }
}
