package nl.weeaboo.vn.impl.test;

import java.security.Permission;

/**
 * Security manager implementation which disallows {@link System#exit}
 */
public final class NoExitSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        return; // Allow all
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        return; // Allow all
    }

    @Override
    public void checkExit(int status) {
        throw new SecurityException("System.exit is not allowed");
    }

}
