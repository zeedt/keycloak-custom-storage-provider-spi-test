package com.zeed.keycloak.spi.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider>  {

    @Override
    public String getId() {
        return "custom-user-provider";
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession ksession, ComponentModel model) {
        return new CustomUserStorageProvider(ksession,model);
    }
}
