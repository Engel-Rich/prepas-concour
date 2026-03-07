package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.Roles;

public interface RolesServices {

    public Roles saveRoles(Roles roles);

    public Roles getRoleByName(String name);

}
