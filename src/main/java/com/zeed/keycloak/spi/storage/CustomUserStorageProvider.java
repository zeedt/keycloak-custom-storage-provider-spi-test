package com.zeed.keycloak.spi.storage;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator, UserQueryProvider {
  
    // ... private members omitted
    private KeycloakSession ksession;
    private ComponentModel model;

    private Logger log = Logger.getLogger(CustomUserStorageProvider.class);

    public CustomUserStorageProvider(KeycloakSession ksession, ComponentModel model) {
      this.ksession = ksession;
      this.model = model;
    }

    @Override
    public void close() {
        log.info("[I30] close()");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
//        log.info("[I35] getUserById({})",id);
        StorageId sid = new StorageId(id);
        return getUserByUsername(sid.getExternalId(),realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("[I41] getUserByUsername({})" + username);
        if (!username.equalsIgnoreCase("yusufsaheedtaiwo@gmail.com"))
            return null;
        return getMockedUser(realm);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info("[I48] getUserByEmail({})" + email);
        if (!email.equalsIgnoreCase("yusufsaheedtaiwo@gmail.com"))
            return null;
        return getMockedUser(realm);

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("[I57] supportsCredentialType({})" + credentialType);
        return PasswordCredentialModel.TYPE.endsWith(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info(String.format("[I57] isConfiguredFor(realm=%s,user=%s,credentialType=%s)",realm.getName(), user.getUsername(), credentialType));
        // In our case, password is the only type of credential, so we allways return 'true' if
        // this is the credentialType
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info(String.format("[I57] isValid(realm=%s,user=%s,credentialType=%s)",realm.getName(), user.getUsername(), credentialInput.getType()));
//        log.info("[I57] isValid(realm={},user={},credentialInput.type={})",realm.getName(), user.getUsername(), credentialInput.getType());
        if( !this.supportsCredentialType(credentialInput.getType())) {
            return false;
        }
        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();

        if (!username.equalsIgnoreCase("yusufsaheedtaiwo@gmail.com"))
            return false;
        if (!credentialInput.getChallengeResponse().equals("password"))
            return false;

        return true;

    }

    // UserQueryProvider implementation

    @Override
    public int getUsersCount(RealmModel realm) {
        log.info("[I93] getUsersCount: realm={}"+ realm.getName() );
        return 1;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return getUsers(realm,0, 5000); // Keep a reasonable maxResults
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {

        log.info("[I113] getUsers: realm={}" + realm.getName());
        return Collections.singletonList(getMockedUser(realm));

    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return searchForUser(search,realm,0,5000);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info("[I139] searchForUser: realm={}" + realm.getName());

        return Collections.singletonList(getMockedUser(realm));
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return searchForUser(params,realm,0,5000);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm, firstResult, maxResults);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return Collections.emptyList();
    }


    //------------------- Implementation
    private UserModel mapUser(RealmModel realm, ResultSet rs) throws SQLException {

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        CustomUser user = new CustomUser.Builder(ksession, realm, model, rs.getString("username"))
                .email(rs.getString("email"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .birthDate(rs.getDate("birthDate"))
                .build();

        return user;
    }

    // ... implementation methods for each supported capability


    private UserModel getMockedUser(RealmModel realm) {
        return new CustomUser.Builder(ksession, realm, model, "yusufsaheedtaiwo@gmail.com")
                .email("yusufsaheedtaiwo@gmail.com")
                .firstName("saheed")
                .lastName("Yusuf")
                .birthDate(new Date())
                .build();
    }
}