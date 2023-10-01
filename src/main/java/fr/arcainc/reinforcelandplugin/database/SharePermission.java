package fr.arcainc.reinforcelandplugin.database;

public enum SharePermission {
    SHARE_STORAGE("share_storage"),
    SHARE_BEAK_BYPASS("share_beak_bypass"),
    SHARE_ADD_HEALTH("share_add_health"),
    SHARE_USE("share_use");

    private final String permissionName;

    SharePermission(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }
}
