### **Step 1: Configure Keycloak Realm and Client**

#### **1. Create a New Realm**
- Log in to the Keycloak Admin Console.
- Navigate to the "Realms" section.
- Click on "Add Realm" and name it `GUH`.
- Save the realm.

#### **2. Create a New Client**
- In the `GUH` realm, navigate to the "Clients" section.
- Click on "Create".
- Enter the client ID as `OpenKM`.
- Set the "Client Protocol" to `openid-connect`.
- Under "Access Type", select `confidential`.
- Enable the "Standard Flow Enabled" option.
- Set the "Root URL" to include the URL where OpenKM will handle the callback (e.g., `http://localhost:8080/OpenKM`).
- Set the "Valid Redirect URIs" to include the URL where OpenKM will handle the callback (e.g., `http://localhost:8080/OpenKM/*`).

#### **3. Add Client Scope**
- Ensure the `openid` scope is included by default.
- Go to the "Client Scopes" section of the `OpenKM` client.
- Assign the `openid` scope as a default scope.

#### **4. Add Mapper for Username**
- Navigate to the "Mappers" tab under the `OpenKM` client.
- Click on "Create".
- Set the following properties:
    - Name: `username`
    - Mapper Type: `User Attribute`
    - Token Claim Name: `username`
    - User Attribute: `username`
    - Claim JSON Type: `String`
    - Add to ID Token: `ON`
    - Add to Access Token: `ON`

#### **5. Import Configuration**
All this already configured you can import it from the json file below

```json
{
  "id": "468069fe-b011-45f6-9cdf-c4182d5139f7",
  "realm": "GUH",
  "notBefore": 0,
  "defaultSignatureAlgorithm": "RS256",
  "revokeRefreshToken": false,
  "refreshTokenMaxReuse": 0,
  "accessTokenLifespan": 300,
  "accessTokenLifespanForImplicitFlow": 900,
  "ssoSessionIdleTimeout": 1800,
  "ssoSessionMaxLifespan": 36000,
  "ssoSessionIdleTimeoutRememberMe": 0,
  "ssoSessionMaxLifespanRememberMe": 0,
  "offlineSessionIdleTimeout": 2592000,
  "offlineSessionMaxLifespanEnabled": false,
  "offlineSessionMaxLifespan": 5184000,
  "clientSessionIdleTimeout": 0,
  "clientSessionMaxLifespan": 0,
  "clientOfflineSessionIdleTimeout": 0,
  "clientOfflineSessionMaxLifespan": 0,
  "accessCodeLifespan": 60,
  "accessCodeLifespanUserAction": 300,
  "accessCodeLifespanLogin": 1800,
  "actionTokenGeneratedByAdminLifespan": 43200,
  "actionTokenGeneratedByUserLifespan": 300,
  "oauth2DeviceCodeLifespan": 600,
  "oauth2DevicePollingInterval": 5,
  "enabled": true,
  "sslRequired": "external",
  "registrationAllowed": false,
  "registrationEmailAsUsername": false,
  "rememberMe": false,
  "verifyEmail": false,
  "loginWithEmailAllowed": true,
  "duplicateEmailsAllowed": false,
  "resetPasswordAllowed": false,
  "editUsernameAllowed": false,
  "bruteForceProtected": false,
  "permanentLockout": false,
  "maxTemporaryLockouts": 0,
  "bruteForceStrategy": "MULTIPLE",
  "maxFailureWaitSeconds": 900,
  "minimumQuickLoginWaitSeconds": 60,
  "waitIncrementSeconds": 60,
  "quickLoginCheckMilliSeconds": 1000,
  "maxDeltaTimeSeconds": 43200,
  "failureFactor": 30,
  "roles": {
    "realm": [
      {
        "id": "a73ed423-5d23-48d4-924e-ea638e0dae6a",
        "name": "offline_access",
        "description": "${role_offline-access}",
        "composite": false,
        "clientRole": false,
        "containerId": "468069fe-b011-45f6-9cdf-c4182d5139f7",
        "attributes": {}
      },
      {
        "id": "0303a200-c653-47d2-832f-bf3d5f768e97",
        "name": "uma_authorization",
        "description": "${role_uma_authorization}",
        "composite": false,
        "clientRole": false,
        "containerId": "468069fe-b011-45f6-9cdf-c4182d5139f7",
        "attributes": {}
      },
      {
        "id": "98fc21a3-eb91-4f46-afb2-1b1da9b267ca",
        "name": "default-roles-guh",
        "description": "${role_default-roles}",
        "composite": true,
        "composites": {
          "realm": [
            "offline_access",
            "uma_authorization"
          ],
          "client": {
            "account": [
              "view-profile",
              "manage-account"
            ]
          }
        },
        "clientRole": false,
        "containerId": "468069fe-b011-45f6-9cdf-c4182d5139f7",
        "attributes": {}
      }
    ],
    "client": {
      "realm-management": [
        {
          "id": "39642c4f-1736-4d5c-95eb-770dd9914149",
          "name": "manage-users",
          "description": "${role_manage-users}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "e8191422-7c24-4921-9e09-8f469956674a",
          "name": "view-clients",
          "description": "${role_view-clients}",
          "composite": true,
          "composites": {
            "client": {
              "realm-management": [
                "query-clients"
              ]
            }
          },
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "17b4f815-a0a1-483c-906f-64544f438c21",
          "name": "view-events",
          "description": "${role_view-events}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "9c301f74-d4e2-4a16-94a1-256bc266a8fb",
          "name": "manage-events",
          "description": "${role_manage-events}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "c8632adb-b237-4dd1-b7a2-6e5166c67d21",
          "name": "create-client",
          "description": "${role_create-client}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "41a1d9a8-4a10-4cf7-bacb-ee8fda1606e5",
          "name": "view-realm",
          "description": "${role_view-realm}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "715284e8-4133-446a-b40d-b03a4493f4e8",
          "name": "manage-clients",
          "description": "${role_manage-clients}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "d1cfe604-f166-4c59-8f35-41875e38b80c",
          "name": "query-groups",
          "description": "${role_query-groups}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "e8055d24-023d-43c3-b6d8-ebe20e1cc692",
          "name": "query-realms",
          "description": "${role_query-realms}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "a96dca80-59cd-4016-8d42-e43fb15640e5",
          "name": "impersonation",
          "description": "${role_impersonation}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "351af768-44da-4d80-a874-e1f901fc6200",
          "name": "manage-realm",
          "description": "${role_manage-realm}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "5b33288a-840e-4df1-8862-225b4b12ab82",
          "name": "manage-authorization",
          "description": "${role_manage-authorization}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "fcddc6b2-a85f-4f6c-9e73-a2f7a3b24dd9",
          "name": "manage-identity-providers",
          "description": "${role_manage-identity-providers}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "cc48a05d-dd2a-44ec-83ba-d2b84ab43d6d",
          "name": "query-users",
          "description": "${role_query-users}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "2b4d4a37-815e-4225-a530-a50c7ee4c434",
          "name": "view-users",
          "description": "${role_view-users}",
          "composite": true,
          "composites": {
            "client": {
              "realm-management": [
                "query-users",
                "query-groups"
              ]
            }
          },
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "4c2d2144-0617-4d4d-910d-a0384e0ef548",
          "name": "realm-admin",
          "description": "${role_realm-admin}",
          "composite": true,
          "composites": {
            "client": {
              "realm-management": [
                "manage-users",
                "view-clients",
                "view-events",
                "manage-events",
                "view-realm",
                "create-client",
                "manage-clients",
                "query-groups",
                "query-realms",
                "manage-realm",
                "impersonation",
                "manage-authorization",
                "manage-identity-providers",
                "query-users",
                "view-users",
                "query-clients",
                "view-identity-providers",
                "view-authorization"
              ]
            }
          },
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "02bcecc3-36b5-434b-8ee6-ac05c716a5db",
          "name": "query-clients",
          "description": "${role_query-clients}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "f06a2d77-f6e2-4a6f-bcfb-55b1dde23cc0",
          "name": "view-identity-providers",
          "description": "${role_view-identity-providers}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        },
        {
          "id": "de676af9-d2d6-4772-a5ac-3ad5c1a93aed",
          "name": "view-authorization",
          "description": "${role_view-authorization}",
          "composite": false,
          "clientRole": true,
          "containerId": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
          "attributes": {}
        }
      ],
      "OpenKM": [
        {
          "id": "66de3ff1-788f-4f28-b120-bdd112ccf1f7",
          "name": "ROLE_USER",
          "description": "",
          "composite": false,
          "clientRole": true,
          "containerId": "4ec3a53d-8563-429f-8805-c19699a3a7a6",
          "attributes": {}
        },
        {
          "id": "301de2e3-eac9-4713-9844-543204b556d7",
          "name": "ROLE_ADMIN",
          "description": "",
          "composite": false,
          "clientRole": true,
          "containerId": "4ec3a53d-8563-429f-8805-c19699a3a7a6",
          "attributes": {}
        }
      ],
      "security-admin-console": [],
      "admin-cli": [],
      "account-console": [],
      "broker": [
        {
          "id": "7fcd22a4-f1c7-4422-ad62-345f8154b0e9",
          "name": "read-token",
          "description": "${role_read-token}",
          "composite": false,
          "clientRole": true,
          "containerId": "3b6c0aed-9153-4103-ae75-f0bb98af2e82",
          "attributes": {}
        }
      ],
      "account": [
        {
          "id": "f68a9858-ea1b-48e6-911d-4a330ebadb95",
          "name": "manage-account-links",
          "description": "${role_manage-account-links}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "2d57db97-0627-455e-913d-a7b91bbbd77b",
          "name": "view-applications",
          "description": "${role_view-applications}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "b0931f77-28f7-4f57-b9b7-f1ae7c43936d",
          "name": "view-groups",
          "description": "${role_view-groups}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "d8bb811b-ea17-4633-bc20-dcf4a5990810",
          "name": "view-profile",
          "description": "${role_view-profile}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "d023ed34-43b5-49bd-afc0-f9ede100f79e",
          "name": "delete-account",
          "description": "${role_delete-account}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "c9bdb30d-c9c3-4217-9b5f-c7065c23eaa5",
          "name": "manage-account",
          "description": "${role_manage-account}",
          "composite": true,
          "composites": {
            "client": {
              "account": [
                "manage-account-links"
              ]
            }
          },
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "d7c8ec27-437a-4bbe-80cb-0dda7ac9e0a2",
          "name": "manage-consent",
          "description": "${role_manage-consent}",
          "composite": true,
          "composites": {
            "client": {
              "account": [
                "view-consent"
              ]
            }
          },
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        },
        {
          "id": "c9cd52a5-c92f-4e59-8773-365f72e8fdce",
          "name": "view-consent",
          "description": "${role_view-consent}",
          "composite": false,
          "clientRole": true,
          "containerId": "a24e97c6-922c-4003-a3c0-48312bb6a483",
          "attributes": {}
        }
      ]
    }
  },
  "groups": [],
  "defaultRole": {
    "id": "98fc21a3-eb91-4f46-afb2-1b1da9b267ca",
    "name": "default-roles-guh",
    "description": "${role_default-roles}",
    "composite": true,
    "clientRole": false,
    "containerId": "468069fe-b011-45f6-9cdf-c4182d5139f7"
  },
  "requiredCredentials": [
    "password"
  ],
  "otpPolicyType": "totp",
  "otpPolicyAlgorithm": "HmacSHA1",
  "otpPolicyInitialCounter": 0,
  "otpPolicyDigits": 6,
  "otpPolicyLookAheadWindow": 1,
  "otpPolicyPeriod": 30,
  "otpPolicyCodeReusable": false,
  "otpSupportedApplications": [
    "totpAppFreeOTPName",
    "totpAppGoogleName",
    "totpAppMicrosoftAuthenticatorName"
  ],
  "localizationTexts": {},
  "webAuthnPolicyRpEntityName": "keycloak",
  "webAuthnPolicySignatureAlgorithms": [
    "ES256"
  ],
  "webAuthnPolicyRpId": "",
  "webAuthnPolicyAttestationConveyancePreference": "not specified",
  "webAuthnPolicyAuthenticatorAttachment": "not specified",
  "webAuthnPolicyRequireResidentKey": "not specified",
  "webAuthnPolicyUserVerificationRequirement": "not specified",
  "webAuthnPolicyCreateTimeout": 0,
  "webAuthnPolicyAvoidSameAuthenticatorRegister": false,
  "webAuthnPolicyAcceptableAaguids": [],
  "webAuthnPolicyExtraOrigins": [],
  "webAuthnPolicyPasswordlessRpEntityName": "keycloak",
  "webAuthnPolicyPasswordlessSignatureAlgorithms": [
    "ES256"
  ],
  "webAuthnPolicyPasswordlessRpId": "",
  "webAuthnPolicyPasswordlessAttestationConveyancePreference": "not specified",
  "webAuthnPolicyPasswordlessAuthenticatorAttachment": "not specified",
  "webAuthnPolicyPasswordlessRequireResidentKey": "not specified",
  "webAuthnPolicyPasswordlessUserVerificationRequirement": "not specified",
  "webAuthnPolicyPasswordlessCreateTimeout": 0,
  "webAuthnPolicyPasswordlessAvoidSameAuthenticatorRegister": false,
  "webAuthnPolicyPasswordlessAcceptableAaguids": [],
  "webAuthnPolicyPasswordlessExtraOrigins": [],
  "scopeMappings": [
    {
      "clientScope": "offline_access",
      "roles": [
        "offline_access"
      ]
    }
  ],
  "clientScopeMappings": {
    "account": [
      {
        "client": "account-console",
        "roles": [
          "manage-account",
          "view-groups"
        ]
      }
    ]
  },
  "clients": [
    {
      "id": "4ec3a53d-8563-429f-8805-c19699a3a7a6",
      "clientId": "OpenKM",
      "name": "",
      "description": "",
      "rootUrl": "https://localhost:8443/OpenKM",
      "adminUrl": "https://localhost:8443/OpenKM",
      "baseUrl": "",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": true,
      "clientAuthenticatorType": "client-secret",
      "secret": "**********",
      "redirectUris": [
        "https://localhost:8443/OpenKM/*",
        "https://localhost:8443/*"
      ],
      "webOrigins": [
        "https://localhost:8443"
      ],
      "notBefore": 0,
      "bearerOnly": false,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": true,
      "serviceAccountsEnabled": false,
      "publicClient": false,
      "frontchannelLogout": true,
      "protocol": "openid-connect",
      "attributes": {
        "client.secret.creation.time": "1741021628",
        "login_theme": "keycloak",
        "post.logout.redirect.uris": "+",
        "frontchannel.logout.session.required": "true",
        "oauth2.device.authorization.grant.enabled": "false",
        "backchannel.logout.revoke.offline.tokens": "false",
        "use.refresh.tokens": "true",
        "tls-client-certificate-bound-access-tokens": "false",
        "realm_client": "false",
        "oidc.ciba.grant.enabled": "false",
        "backchannel.logout.session.required": "true",
        "client_credentials.use_refresh_token": "false",
        "consent.screen.text": "Welcome",
        "require.pushed.authorization.requests": "false",
        "acr.loa.map": "{}",
        "display.on.consent.screen": "true",
        "token.response.type.bearer.lower-case": "false"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": true,
      "nodeReRegistrationTimeout": -1,
      "protocolMappers": [
        {
          "id": "1589f07f-06c7-4ac6-9af5-1da193e1fd7e",
          "name": "username",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "username",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "preferred_username",
            "jsonType.label": "String"
          }
        },
        {
          "id": "1e81dec5-0086-4ca4-953d-c525183cc616",
          "name": "email",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "email",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "email",
            "jsonType.label": "String"
          }
        },
        {
          "id": "21b077d1-a697-41cc-957b-aa66619cd94a",
          "name": "full name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-full-name-mapper",
          "consentRequired": false,
          "config": {
            "id.token.claim": "true",
            "access.token.claim": "true",
            "userinfo.token.claim": "true"
          }
        },
        {
          "id": "5c2f0fb0-ccc6-42ac-9233-71b54f9ac5ac",
          "name": "nickname",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "nickname",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "nickname",
            "jsonType.label": "String"
          }
        },
        {
          "id": "73eb7de6-1c05-4a18-8d8e-655ad60909c0",
          "name": "profile",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "profile",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "profile",
            "jsonType.label": "String"
          }
        }
      ],
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "openid",
        "roles",
        "profile",
        "basic",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "a24e97c6-922c-4003-a3c0-48312bb6a483",
      "clientId": "account",
      "name": "${client_account}",
      "rootUrl": "${authBaseUrl}",
      "baseUrl": "/realms/GUH/account/",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [
        "/realms/GUH/account/*"
      ],
      "webOrigins": [],
      "notBefore": 0,
      "bearerOnly": false,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "publicClient": true,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "false",
        "post.logout.redirect.uris": "+"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": false,
      "nodeReRegistrationTimeout": 0,
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "basic",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "f4f725cc-bcb7-45e2-ae76-544bbdd2ecd5",
      "clientId": "account-console",
      "name": "${client_account-console}",
      "rootUrl": "${authBaseUrl}",
      "baseUrl": "/realms/GUH/account/",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [
        "/realms/GUH/account/*"
      ],
      "webOrigins": [],
      "notBefore": 0,
      "bearerOnly": false,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "publicClient": true,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "false",
        "post.logout.redirect.uris": "+",
        "pkce.code.challenge.method": "S256"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": false,
      "nodeReRegistrationTimeout": 0,
      "protocolMappers": [
        {
          "id": "416b3efe-587c-475e-a553-a9c728a6e14d",
          "name": "audience resolve",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-audience-resolve-mapper",
          "consentRequired": false,
          "config": {}
        }
      ],
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "basic",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "58bef73b-c5a1-4c21-928e-e75361a8bf47",
      "clientId": "admin-cli",
      "name": "${client_admin-cli}",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [],
      "webOrigins": [],
      "notBefore": 0,
      "bearerOnly": false,
      "consentRequired": false,
      "standardFlowEnabled": false,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": true,
      "serviceAccountsEnabled": false,
      "publicClient": true,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "false",
        "client.use.lightweight.access.token.enabled": "true",
        "post.logout.redirect.uris": "+"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": true,
      "nodeReRegistrationTimeout": 0,
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "basic",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "3b6c0aed-9153-4103-ae75-f0bb98af2e82",
      "clientId": "broker",
      "name": "${client_broker}",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [],
      "webOrigins": [],
      "notBefore": 0,
      "bearerOnly": true,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "publicClient": false,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "true",
        "post.logout.redirect.uris": "+"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": false,
      "nodeReRegistrationTimeout": 0,
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "447f7b6a-1680-482f-8a3d-a4162cfe0b65",
      "clientId": "realm-management",
      "name": "${client_realm-management}",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [],
      "webOrigins": [],
      "notBefore": 0,
      "bearerOnly": true,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "publicClient": false,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "true",
        "post.logout.redirect.uris": "+"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": false,
      "nodeReRegistrationTimeout": 0,
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    },
    {
      "id": "fdb3c7cf-2049-44e4-a925-dd037f2ec61f",
      "clientId": "security-admin-console",
      "name": "${client_security-admin-console}",
      "rootUrl": "${authAdminUrl}",
      "baseUrl": "/admin/GUH/console/",
      "surrogateAuthRequired": false,
      "enabled": true,
      "alwaysDisplayInConsole": false,
      "clientAuthenticatorType": "client-secret",
      "redirectUris": [
        "/admin/GUH/console/*"
      ],
      "webOrigins": [
        "+"
      ],
      "notBefore": 0,
      "bearerOnly": false,
      "consentRequired": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "publicClient": true,
      "frontchannelLogout": false,
      "protocol": "openid-connect",
      "attributes": {
        "realm_client": "false",
        "client.use.lightweight.access.token.enabled": "true",
        "post.logout.redirect.uris": "+",
        "pkce.code.challenge.method": "S256"
      },
      "authenticationFlowBindingOverrides": {},
      "fullScopeAllowed": true,
      "nodeReRegistrationTimeout": 0,
      "protocolMappers": [
        {
          "id": "93d59d35-6557-4ab5-b73c-ba1b488bb26c",
          "name": "locale",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "locale",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "locale",
            "jsonType.label": "String"
          }
        }
      ],
      "defaultClientScopes": [
        "web-origins",
        "acr",
        "roles",
        "profile",
        "basic",
        "email"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ]
    }
  ],
  "clientScopes": [
    {
      "id": "13d9c96d-faa8-46a6-b1e4-db9565581dc5",
      "name": "roles",
      "description": "OpenID Connect scope for add user roles to the access token",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "false",
        "display.on.consent.screen": "true",
        "consent.screen.text": "${rolesScopeConsentText}"
      },
      "protocolMappers": [
        {
          "id": "0b7a779d-502b-43ea-818c-7a36fee19207",
          "name": "audience resolve",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-audience-resolve-mapper",
          "consentRequired": false,
          "config": {}
        },
        {
          "id": "a555b766-4509-4f3d-8e0c-5b004b262e70",
          "name": "client roles",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-client-role-mapper",
          "consentRequired": false,
          "config": {
            "user.attribute": "foo",
            "access.token.claim": "true",
            "claim.name": "resource_access.${client_id}.roles",
            "jsonType.label": "String",
            "multivalued": "true"
          }
        },
        {
          "id": "ae8d3cd4-b68a-4f50-9a25-3d8bd8a40e1d",
          "name": "realm roles",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-realm-role-mapper",
          "consentRequired": false,
          "config": {
            "user.attribute": "foo",
            "access.token.claim": "true",
            "claim.name": "realm_access.roles",
            "jsonType.label": "String",
            "multivalued": "true"
          }
        }
      ]
    },
    {
      "id": "1ff0b983-9f8c-4a93-8ff5-07eaee0a3145",
      "name": "acr",
      "description": "OpenID Connect scope for add acr (authentication context class reference) to the token",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "false",
        "display.on.consent.screen": "false"
      },
      "protocolMappers": [
        {
          "id": "75d183ef-fa4f-4d41-9f02-420b2ec7d35a",
          "name": "acr loa level",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-acr-mapper",
          "consentRequired": false,
          "config": {
            "id.token.claim": "true",
            "access.token.claim": "true",
            "userinfo.token.claim": "true"
          }
        }
      ]
    },
    {
      "id": "7c171efe-781e-484c-a1ef-1e1244b4bc93",
      "name": "offline_access",
      "description": "OpenID Connect built-in scope: offline_access",
      "protocol": "openid-connect",
      "attributes": {
        "consent.screen.text": "${offlineAccessScopeConsentText}",
        "display.on.consent.screen": "true"
      }
    },
    {
      "id": "57f2b46d-0095-4cae-87f2-388646dcf716",
      "name": "role_list",
      "description": "SAML role list",
      "protocol": "saml",
      "attributes": {
        "consent.screen.text": "${samlRoleListScopeConsentText}",
        "display.on.consent.screen": "true"
      },
      "protocolMappers": [
        {
          "id": "0ead0b17-3012-4190-ad97-af3d2e18237d",
          "name": "role list",
          "protocol": "saml",
          "protocolMapper": "saml-role-list-mapper",
          "consentRequired": false,
          "config": {
            "single": "false",
            "attribute.nameformat": "Basic",
            "attribute.name": "Role"
          }
        }
      ]
    },
    {
      "id": "55bb58c4-a977-4646-90d7-565852b25f86",
      "name": "service_account",
      "description": "Specific scope for a client enabled for service accounts",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "false",
        "display.on.consent.screen": "false"
      },
      "protocolMappers": [
        {
          "id": "8a77cb5b-16fa-4414-b3fd-baa5a34eecdf",
          "name": "Client IP Address",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usersessionmodel-note-mapper",
          "consentRequired": false,
          "config": {
            "user.session.note": "clientAddress",
            "introspection.token.claim": "true",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "clientAddress",
            "jsonType.label": "String"
          }
        },
        {
          "id": "23485e72-539c-4489-b27b-1e35074ea66c",
          "name": "Client ID",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usersessionmodel-note-mapper",
          "consentRequired": false,
          "config": {
            "user.session.note": "client_id",
            "introspection.token.claim": "true",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "client_id",
            "jsonType.label": "String"
          }
        },
        {
          "id": "bb636584-ce58-40b1-8303-37939c9e7d1f",
          "name": "Client Host",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usersessionmodel-note-mapper",
          "consentRequired": false,
          "config": {
            "user.session.note": "clientHost",
            "introspection.token.claim": "true",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "clientHost",
            "jsonType.label": "String"
          }
        }
      ]
    },
    {
      "id": "7a02c9b7-e8f9-41c3-8f23-be163d440cf4",
      "name": "openid",
      "description": "",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true",
        "gui.order": "",
        "consent.screen.text": ""
      },
      "protocolMappers": [
        {
          "id": "6368e2ef-eeef-4e7c-96fd-e0b055b0d824",
          "name": "client roles",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-client-role-mapper",
          "consentRequired": false,
          "config": {
            "introspection.token.claim": "true",
            "multivalued": "true",
            "user.attribute": "foo",
            "access.token.claim": "true",
            "claim.name": "resource_access.${client_id}.roles",
            "jsonType.label": "String"
          }
        },
        {
          "id": "cdeca6cf-a73e-4376-97f3-bc65d378bdcd",
          "name": "profile",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "profile",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "profile",
            "jsonType.label": "String"
          }
        },
        {
          "id": "e9bbd8aa-4717-4443-bfb1-d1394337b20f",
          "name": "full name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-full-name-mapper",
          "consentRequired": false,
          "config": {
            "id.token.claim": "true",
            "access.token.claim": "true",
            "userinfo.token.claim": "true"
          }
        },
        {
          "id": "d6a7b9b3-1a97-4f93-86a7-86e5ee4fbfd3",
          "name": "nickname",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "nickname",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "nickname",
            "jsonType.label": "String"
          }
        },
        {
          "id": "55a3a1d6-9c7b-410c-b826-eba8c36108a0",
          "name": "realm roles",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-realm-role-mapper",
          "consentRequired": false,
          "config": {
            "introspection.token.claim": "true",
            "multivalued": "true",
            "user.attribute": "foo",
            "access.token.claim": "true",
            "claim.name": "realm_access.roles",
            "jsonType.label": "String"
          }
        },
        {
          "id": "dcc614dd-adb4-49fe-907b-6893f7afc3cc",
          "name": "username",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "username",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "preferred_username",
            "jsonType.label": "String"
          }
        }
      ]
    },
    {
      "id": "760ab0bf-82a1-4d52-8f75-b04ec636c9bf",
      "name": "microprofile-jwt",
      "description": "Microprofile - JWT built-in scope",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "false"
      },
      "protocolMappers": [
        {
          "id": "7b177b76-663c-412d-a2a4-912b66636e3c",
          "name": "groups",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-realm-role-mapper",
          "consentRequired": false,
          "config": {
            "multivalued": "true",
            "userinfo.token.claim": "true",
            "user.attribute": "foo",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "groups",
            "jsonType.label": "String"
          }
        },
        {
          "id": "b1a2e288-9485-4f54-8f62-a060e43abdaa",
          "name": "upn",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "username",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "upn",
            "jsonType.label": "String"
          }
        }
      ]
    },
    {
      "id": "5aaea301-803d-4974-bfca-d479c5117859",
      "name": "basic",
      "description": "OpenID Connect scope for add all basic claims to the token",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "false",
        "display.on.consent.screen": "false"
      },
      "protocolMappers": [
        {
          "id": "29943c86-ca9c-4fa3-9ed6-c28e5b0975fa",
          "name": "auth_time",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usersessionmodel-note-mapper",
          "consentRequired": false,
          "config": {
            "user.session.note": "AUTH_TIME",
            "introspection.token.claim": "true",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "auth_time",
            "jsonType.label": "long"
          }
        },
        {
          "id": "7e71bb4d-2225-4755-a5d9-2058bd246c50",
          "name": "sub",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-sub-mapper",
          "consentRequired": false,
          "config": {
            "introspection.token.claim": "true",
            "access.token.claim": "true"
          }
        }
      ]
    },
    {
      "id": "dde7afa4-5ba9-4169-adb7-ac91d5fcbcb2",
      "name": "web-origins",
      "description": "OpenID Connect scope for add allowed web origins to the access token",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "false",
        "display.on.consent.screen": "false",
        "consent.screen.text": ""
      },
      "protocolMappers": [
        {
          "id": "911c2d3f-8884-4787-8b91-6c9c5e378b68",
          "name": "allowed web origins",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-allowed-origins-mapper",
          "consentRequired": false,
          "config": {}
        }
      ]
    },
    {
      "id": "7afc1d9b-0a01-488d-a4fd-f02ef65ed8b6",
      "name": "address",
      "description": "OpenID Connect built-in scope: address",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true",
        "consent.screen.text": "${addressScopeConsentText}"
      },
      "protocolMappers": [
        {
          "id": "d0693d80-5227-49ca-91bd-a7769c6d5d6f",
          "name": "address",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-address-mapper",
          "consentRequired": false,
          "config": {
            "user.attribute.formatted": "formatted",
            "user.attribute.country": "country",
            "user.attribute.postal_code": "postal_code",
            "userinfo.token.claim": "true",
            "user.attribute.street": "street",
            "id.token.claim": "true",
            "user.attribute.region": "region",
            "access.token.claim": "true",
            "user.attribute.locality": "locality"
          }
        }
      ]
    },
    {
      "id": "f70b977c-fddf-4e1a-8401-5b35470a71f9",
      "name": "profile",
      "description": "OpenID Connect built-in scope: profile",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true",
        "consent.screen.text": "${profileScopeConsentText}"
      },
      "protocolMappers": [
        {
          "id": "7543cd4e-93c6-4d46-b0da-fb54eef84c94",
          "name": "family name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "lastName",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "family_name",
            "jsonType.label": "String"
          }
        },
        {
          "id": "31143f0f-89d8-4158-9c39-ea9202ac5e82",
          "name": "profile",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "profile",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "profile",
            "jsonType.label": "String"
          }
        },
        {
          "id": "ea7b8da4-f0a8-4dd3-b989-aa849749a152",
          "name": "given name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "firstName",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "given_name",
            "jsonType.label": "String"
          }
        },
        {
          "id": "64eb0893-1c15-41f2-91bf-f06c2bc978fb",
          "name": "birthdate",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "birthdate",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "birthdate",
            "jsonType.label": "String"
          }
        },
        {
          "id": "9b03a079-8437-4a18-b6db-4ada47e124ee",
          "name": "website",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "website",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "website",
            "jsonType.label": "String"
          }
        },
        {
          "id": "2beb9f5d-3ed2-4e2e-9977-8a942511c757",
          "name": "full name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-full-name-mapper",
          "consentRequired": false,
          "config": {
            "id.token.claim": "true",
            "access.token.claim": "true",
            "userinfo.token.claim": "true"
          }
        },
        {
          "id": "c5974603-695c-4b6e-8e02-a36a2b174798",
          "name": "updated at",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "updatedAt",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "updated_at",
            "jsonType.label": "long"
          }
        },
        {
          "id": "ab2d77af-5558-49f6-9f0d-caaf662a2bb8",
          "name": "gender",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "gender",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "gender",
            "jsonType.label": "String"
          }
        },
        {
          "id": "64c1316d-6691-4213-8f59-66f0eb4f6d63",
          "name": "locale",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "locale",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "locale",
            "jsonType.label": "String"
          }
        },
        {
          "id": "e20dde3b-c790-4bc8-954f-e3b9186a3908",
          "name": "middle name",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "middleName",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "middle_name",
            "jsonType.label": "String"
          }
        },
        {
          "id": "a5546067-9bb0-485e-b7f2-a8455bfdf682",
          "name": "username",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "username",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "preferred_username",
            "jsonType.label": "String"
          }
        },
        {
          "id": "ccefb90d-3b7c-4091-8cef-1cd98b1b485c",
          "name": "zoneinfo",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "zoneinfo",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "zoneinfo",
            "jsonType.label": "String"
          }
        },
        {
          "id": "257674e0-0afd-458a-9e1b-85c384fcacb9",
          "name": "nickname",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "nickname",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "nickname",
            "jsonType.label": "String"
          }
        },
        {
          "id": "51217c0e-a6de-492a-96e3-ce9866aa728d",
          "name": "picture",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "picture",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "picture",
            "jsonType.label": "String"
          }
        }
      ]
    },
    {
      "id": "906c87af-4025-4a88-9ce5-b88cb4b1aefa",
      "name": "email",
      "description": "OpenID Connect built-in scope: email",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true",
        "consent.screen.text": "${emailScopeConsentText}"
      },
      "protocolMappers": [
        {
          "id": "251bae37-5b1d-4904-a46a-0960e889e0bc",
          "name": "email verified",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "emailVerified",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "email_verified",
            "jsonType.label": "boolean"
          }
        },
        {
          "id": "c2e68a64-3fcd-4b8f-aa6c-a93bc14913b1",
          "name": "email",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-property-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "email",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "email",
            "jsonType.label": "String"
          }
        }
      ]
    },
    {
      "id": "2ae05fb8-9592-4822-8c26-cce4829f85a1",
      "name": "phone",
      "description": "OpenID Connect built-in scope: phone",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true",
        "consent.screen.text": "${phoneScopeConsentText}"
      },
      "protocolMappers": [
        {
          "id": "b633c0a1-0123-4484-8429-7ec23c8cb732",
          "name": "phone number verified",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "phoneNumberVerified",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "phone_number_verified",
            "jsonType.label": "boolean"
          }
        },
        {
          "id": "0c078683-0c29-40a0-b2ab-e584fde72e56",
          "name": "phone number",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-usermodel-attribute-mapper",
          "consentRequired": false,
          "config": {
            "userinfo.token.claim": "true",
            "user.attribute": "phoneNumber",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "phone_number",
            "jsonType.label": "String"
          }
        }
      ]
    }
  ],
  "defaultDefaultClientScopes": [
    "role_list",
    "profile",
    "email",
    "roles",
    "web-origins",
    "acr",
    "openid",
    "basic"
  ],
  "defaultOptionalClientScopes": [
    "offline_access",
    "address",
    "phone",
    "microprofile-jwt"
  ],
  "browserSecurityHeaders": {
    "contentSecurityPolicyReportOnly": "",
    "xContentTypeOptions": "nosniff",
    "referrerPolicy": "no-referrer",
    "xRobotsTag": "none",
    "xFrameOptions": "SAMEORIGIN",
    "contentSecurityPolicy": "frame-src 'self'; frame-ancestors 'self'; object-src 'none';",
    "xXSSProtection": "1; mode=block",
    "strictTransportSecurity": "max-age=31536000; includeSubDomains"
  },
  "smtpServer": {},
  "eventsEnabled": false,
  "eventsListeners": [
    "jboss-logging"
  ],
  "enabledEventTypes": [],
  "adminEventsEnabled": false,
  "adminEventsDetailsEnabled": false,
  "identityProviders": [],
  "identityProviderMappers": [],
  "components": {
    "org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy": [
      {
        "id": "0e575d61-ab6b-4174-acf5-d3639b08a04a",
        "name": "Allowed Client Scopes",
        "providerId": "allowed-client-templates",
        "subType": "authenticated",
        "subComponents": {},
        "config": {
          "allow-default-scopes": [
            "true"
          ]
        }
      },
      {
        "id": "0b7b2d9c-d1b8-4154-8d45-c072b590ad39",
        "name": "Trusted Hosts",
        "providerId": "trusted-hosts",
        "subType": "anonymous",
        "subComponents": {},
        "config": {
          "host-sending-registration-request-must-match": [
            "true"
          ],
          "client-uris-must-match": [
            "true"
          ]
        }
      },
      {
        "id": "f630d497-5845-44dd-baa2-5dabd860078f",
        "name": "Allowed Client Scopes",
        "providerId": "allowed-client-templates",
        "subType": "anonymous",
        "subComponents": {},
        "config": {
          "allow-default-scopes": [
            "true"
          ]
        }
      },
      {
        "id": "565a415d-3ffc-4324-8116-7af47a783d18",
        "name": "Allowed Protocol Mapper Types",
        "providerId": "allowed-protocol-mappers",
        "subType": "authenticated",
        "subComponents": {},
        "config": {
          "allowed-protocol-mapper-types": [
            "oidc-usermodel-attribute-mapper",
            "oidc-sha256-pairwise-sub-mapper",
            "oidc-usermodel-property-mapper",
            "saml-user-property-mapper",
            "saml-user-attribute-mapper",
            "oidc-full-name-mapper",
            "oidc-address-mapper",
            "saml-role-list-mapper"
          ]
        }
      },
      {
        "id": "31c0d809-f768-40bb-958b-a4a758207da5",
        "name": "Consent Required",
        "providerId": "consent-required",
        "subType": "anonymous",
        "subComponents": {},
        "config": {}
      },
      {
        "id": "dbbfe1d1-7a03-4903-ba77-7ca1829672f4",
        "name": "Full Scope Disabled",
        "providerId": "scope",
        "subType": "anonymous",
        "subComponents": {},
        "config": {}
      },
      {
        "id": "be30e3ed-3833-4c6f-884b-1b2a86c56dca",
        "name": "Max Clients Limit",
        "providerId": "max-clients",
        "subType": "anonymous",
        "subComponents": {},
        "config": {
          "max-clients": [
            "200"
          ]
        }
      },
      {
        "id": "4f136959-4ecb-4025-ac2c-538ce21519e9",
        "name": "Allowed Protocol Mapper Types",
        "providerId": "allowed-protocol-mappers",
        "subType": "anonymous",
        "subComponents": {},
        "config": {
          "allowed-protocol-mapper-types": [
            "saml-role-list-mapper",
            "oidc-full-name-mapper",
            "oidc-usermodel-property-mapper",
            "saml-user-attribute-mapper",
            "saml-user-property-mapper",
            "oidc-address-mapper",
            "oidc-sha256-pairwise-sub-mapper",
            "oidc-usermodel-attribute-mapper"
          ]
        }
      }
    ],
    "org.keycloak.userprofile.UserProfileProvider": [
      {
        "id": "517d6944-befb-47bc-8aeb-cb1ec99a2e8e",
        "providerId": "declarative-user-profile",
        "subComponents": {},
        "config": {
          "kc.user.profile.config": [
            "{\"attributes\":[{\"name\":\"username\",\"displayName\":\"${username}\",\"validations\":{\"length\":{\"min\":3,\"max\":255},\"username-prohibited-characters\":{},\"up-username-not-idn-homograph\":{}},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"email\",\"displayName\":\"${email}\",\"validations\":{\"email\":{},\"length\":{\"max\":255}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"firstName\",\"displayName\":\"${firstName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"lastName\",\"displayName\":\"${lastName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false}],\"groups\":[{\"name\":\"user-metadata\",\"displayHeader\":\"User metadata\",\"displayDescription\":\"Attributes, which refer to user metadata\"}],\"unmanagedAttributePolicy\":\"ENABLED\"}"
          ]
        }
      }
    ],
    "org.keycloak.keys.KeyProvider": [
      {
        "id": "c883eb92-2b9b-456f-8dcd-2c694459bc2e",
        "name": "hmac-generated-hs512",
        "providerId": "hmac-generated",
        "subComponents": {},
        "config": {
          "priority": [
            "100"
          ],
          "algorithm": [
            "HS512"
          ]
        }
      },
      {
        "id": "6d99136e-9bcf-450b-afcc-f7feafefa32b",
        "name": "aes-generated",
        "providerId": "aes-generated",
        "subComponents": {},
        "config": {
          "priority": [
            "100"
          ]
        }
      },
      {
        "id": "7d5bdfb5-8e2f-488f-9946-bc4cd636dbae",
        "name": "rsa-generated",
        "providerId": "rsa-generated",
        "subComponents": {},
        "config": {
          "priority": [
            "100"
          ]
        }
      },
      {
        "id": "fc45931f-5775-4974-9098-8949df5026b4",
        "name": "hmac-generated",
        "providerId": "hmac-generated",
        "subComponents": {},
        "config": {
          "priority": [
            "100"
          ],
          "algorithm": [
            "HS256"
          ]
        }
      },
      {
        "id": "968f6f97-8d9e-4bd8-811b-297960b98a4a",
        "name": "rsa-enc-generated",
        "providerId": "rsa-enc-generated",
        "subComponents": {},
        "config": {
          "priority": [
            "100"
          ],
          "algorithm": [
            "RSA-OAEP"
          ]
        }
      }
    ]
  },
  "internationalizationEnabled": false,
  "supportedLocales": [],
  "authenticationFlows": [
    {
      "id": "234ddef2-8724-4101-9f22-9e2b3bdb99cc",
      "alias": "Account verification options",
      "description": "Method with which to verity the existing account",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "idp-email-verification",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "ALTERNATIVE",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "Verify Existing Account by Re-authentication",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "be0fa6bb-8ae3-4755-907b-15f2fd368f10",
      "alias": "Browser - Conditional OTP",
      "description": "Flow to determine if the OTP is required for the authentication",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "conditional-user-configured",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "auth-otp-form",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "85a4587b-55e3-4244-b76f-ce74413ae305",
      "alias": "Direct Grant - Conditional OTP",
      "description": "Flow to determine if the OTP is required for the authentication",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "conditional-user-configured",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "direct-grant-validate-otp",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "b64134e6-f265-4027-b14b-0f3e1b5d986e",
      "alias": "First broker login - Conditional OTP",
      "description": "Flow to determine if the OTP is required for the authentication",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "conditional-user-configured",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "auth-otp-form",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "d60d3aa0-1508-479c-b66e-6f9322656c82",
      "alias": "Handle Existing Account",
      "description": "Handle what to do if there is existing account with same email/username like authenticated identity provider",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "idp-confirm-link",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "Account verification options",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "46f3b271-906d-487e-abed-6e57380517e3",
      "alias": "Reset - Conditional OTP",
      "description": "Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "conditional-user-configured",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "reset-otp",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "119efb72-ae36-4f66-abee-29bc93694c71",
      "alias": "User creation or linking",
      "description": "Flow for the existing/non-existing user alternatives",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticatorConfig": "create unique user config",
          "authenticator": "idp-create-user-if-unique",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "ALTERNATIVE",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "Handle Existing Account",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "31fc13d5-dd03-45ed-9787-c7fa350270de",
      "alias": "Verify Existing Account by Re-authentication",
      "description": "Reauthentication of existing account",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "idp-username-password-form",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "CONDITIONAL",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "First broker login - Conditional OTP",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "3846117d-c65d-4605-91ba-f31695f5e975",
      "alias": "browser",
      "description": "browser based authentication",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "auth-cookie",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "auth-spnego",
          "authenticatorFlow": false,
          "requirement": "DISABLED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "identity-provider-redirector",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 25,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "ALTERNATIVE",
          "priority": 30,
          "autheticatorFlow": true,
          "flowAlias": "forms",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "5cb35167-067f-4e7c-9c2d-873ec00e5aef",
      "alias": "clients",
      "description": "Base authentication for clients",
      "providerId": "client-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "client-secret",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "client-jwt",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "client-secret-jwt",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 30,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "client-x509",
          "authenticatorFlow": false,
          "requirement": "ALTERNATIVE",
          "priority": 40,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "7214e4d6-90d3-4601-bb43-1c9806f04d72",
      "alias": "direct grant",
      "description": "OpenID Connect Resource Owner Grant",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "direct-grant-validate-username",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "direct-grant-validate-password",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "CONDITIONAL",
          "priority": 30,
          "autheticatorFlow": true,
          "flowAlias": "Direct Grant - Conditional OTP",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "63a40827-bd8f-4154-bcb7-7200f8787bba",
      "alias": "docker auth",
      "description": "Used by Docker clients to authenticate against the IDP",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "docker-http-basic-authenticator",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "c3c96e9d-744f-41fa-8575-9083a208f6b1",
      "alias": "first broker login",
      "description": "Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticatorConfig": "review profile config",
          "authenticator": "idp-review-profile",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "User creation or linking",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "332003a7-7521-46fe-90f5-df76fc46c5e9",
      "alias": "forms",
      "description": "Username, password, otp and other auth forms.",
      "providerId": "basic-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "auth-username-password-form",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "CONDITIONAL",
          "priority": 20,
          "autheticatorFlow": true,
          "flowAlias": "Browser - Conditional OTP",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "c9bf4166-5b8f-46c1-af00-df8ffd357810",
      "alias": "registration",
      "description": "registration flow",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "registration-page-form",
          "authenticatorFlow": true,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": true,
          "flowAlias": "registration form",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "a7e7a3b3-020d-4a7c-b983-10ea40ead2b2",
      "alias": "registration form",
      "description": "registration form",
      "providerId": "form-flow",
      "topLevel": false,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "registration-user-creation",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "registration-password-action",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 50,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "registration-recaptcha-action",
          "authenticatorFlow": false,
          "requirement": "DISABLED",
          "priority": 60,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "6d9e60b9-9f5b-46c5-813a-32ec71c00150",
      "alias": "reset credentials",
      "description": "Reset credentials for a user if they forgot their password or something",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "reset-credentials-choose-user",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "reset-credential-email",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 20,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticator": "reset-password",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 30,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        },
        {
          "authenticatorFlow": true,
          "requirement": "CONDITIONAL",
          "priority": 40,
          "autheticatorFlow": true,
          "flowAlias": "Reset - Conditional OTP",
          "userSetupAllowed": false
        }
      ]
    },
    {
      "id": "cbac7173-bc08-43a2-83ed-f63ed7e96638",
      "alias": "saml ecp",
      "description": "SAML ECP Profile Authentication Flow",
      "providerId": "basic-flow",
      "topLevel": true,
      "builtIn": true,
      "authenticationExecutions": [
        {
          "authenticator": "http-basic-authenticator",
          "authenticatorFlow": false,
          "requirement": "REQUIRED",
          "priority": 10,
          "autheticatorFlow": false,
          "userSetupAllowed": false
        }
      ]
    }
  ],
  "authenticatorConfig": [
    {
      "id": "c9de10be-9393-4c5d-af60-4102f3e46de1",
      "alias": "create unique user config",
      "config": {
        "require.password.update.after.registration": "false"
      }
    },
    {
      "id": "392921c4-4f9b-4995-9359-0addf45657e3",
      "alias": "review profile config",
      "config": {
        "update.profile.on.first.login": "missing"
      }
    }
  ],
  "requiredActions": [
    {
      "alias": "CONFIGURE_TOTP",
      "name": "Configure OTP",
      "providerId": "CONFIGURE_TOTP",
      "enabled": true,
      "defaultAction": false,
      "priority": 10,
      "config": {}
    },
    {
      "alias": "TERMS_AND_CONDITIONS",
      "name": "Terms and Conditions",
      "providerId": "TERMS_AND_CONDITIONS",
      "enabled": false,
      "defaultAction": false,
      "priority": 20,
      "config": {}
    },
    {
      "alias": "UPDATE_PASSWORD",
      "name": "Update Password",
      "providerId": "UPDATE_PASSWORD",
      "enabled": true,
      "defaultAction": false,
      "priority": 30,
      "config": {}
    },
    {
      "alias": "UPDATE_PROFILE",
      "name": "Update Profile",
      "providerId": "UPDATE_PROFILE",
      "enabled": true,
      "defaultAction": false,
      "priority": 40,
      "config": {}
    },
    {
      "alias": "VERIFY_EMAIL",
      "name": "Verify Email",
      "providerId": "VERIFY_EMAIL",
      "enabled": true,
      "defaultAction": false,
      "priority": 50,
      "config": {}
    },
    {
      "alias": "delete_account",
      "name": "Delete Account",
      "providerId": "delete_account",
      "enabled": false,
      "defaultAction": false,
      "priority": 60,
      "config": {}
    },
    {
      "alias": "webauthn-register",
      "name": "Webauthn Register",
      "providerId": "webauthn-register",
      "enabled": true,
      "defaultAction": false,
      "priority": 70,
      "config": {}
    },
    {
      "alias": "webauthn-register-passwordless",
      "name": "Webauthn Register Passwordless",
      "providerId": "webauthn-register-passwordless",
      "enabled": true,
      "defaultAction": false,
      "priority": 80,
      "config": {}
    },
    {
      "alias": "delete_credential",
      "name": "Delete Credential",
      "providerId": "delete_credential",
      "enabled": true,
      "defaultAction": false,
      "priority": 100,
      "config": {}
    },
    {
      "alias": "update_user_locale",
      "name": "Update User Locale",
      "providerId": "update_user_locale",
      "enabled": true,
      "defaultAction": false,
      "priority": 1000,
      "config": {}
    }
  ],
  "browserFlow": "browser",
  "registrationFlow": "registration",
  "directGrantFlow": "direct grant",
  "resetCredentialsFlow": "reset credentials",
  "clientAuthenticationFlow": "clients",
  "dockerAuthenticationFlow": "docker auth",
  "firstBrokerLoginFlow": "first broker login",
  "attributes": {
    "cibaBackchannelTokenDeliveryMode": "poll",
    "cibaExpiresIn": "120",
    "cibaAuthRequestedUserHint": "login_hint",
    "oauth2DeviceCodeLifespan": "600",
    "clientOfflineSessionMaxLifespan": "0",
    "oauth2DevicePollingInterval": "5",
    "clientSessionIdleTimeout": "0",
    "parRequestUriLifespan": "60",
    "clientSessionMaxLifespan": "0",
    "clientOfflineSessionIdleTimeout": "0",
    "cibaInterval": "5",
    "realmReusableOtpCode": "false"
  },
  "keycloakVersion": "26.1.3",
  "userManagedAccessAllowed": false,
  "organizationsEnabled": false,
  "verifiableCredentialsEnabled": false,
  "adminPermissionsEnabled": false,
  "clientProfiles": {
    "profiles": []
  },
  "clientPolicies": {
    "policies": []
  }

```

---

### **Step 2: Create the CustomOAuth2Filter Class**

#### **Code Implementation**
Create a new Java class named `CustomOAuth2Filter` under the package `com.openkm.security`.

```java
package com.openkm.core;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import com.openkm.api.OKMUserConfig;
import com.openkm.dao.bean.UserConfig;
import com.openkm.module.db.DbAuthModule;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomOAuth2Filter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2Filter.class);
    private final String clientId;
    private final String clientSecret;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private final String userInfoEndpoint;
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 1000;

    public CustomOAuth2Filter(String clientId, String clientSecret, String authorizationEndpoint,
                              String tokenEndpoint, String userInfoEndpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!req.isSecure() && req.getHeader("X-Forwarded-Proto") != null) {
            res.sendRedirect("https://" + req.getServerName() + req.getRequestURI() +
                    (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
            return;
        }

        HttpSession session = req.getSession(true);

        if (isAuthenticated(session)) {
            UserConfig userConfig = (UserConfig) session.getAttribute("userConfig");
            chain.doFilter(request, response);
            return;
        }

        String code = req.getParameter("code");
        if (code != null) {
            handleAuthCallbackWithRetry(req, res, code, chain);
        } else {
            redirectToAuthEndpoint(req, res);
        }
    }

    private boolean isAuthenticated(HttpSession session) {
        if (session == null) {
            log.info("No session found");
            return false;
        }

        SecurityContext ctx = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT);
        if (ctx == null) {
            log.info("No SecurityContext in session");
            return false;
        }

        Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            log.info("No Authentication in SecurityContext");
            return false;
        }

        if (!auth.isAuthenticated()) {
            log.info("Authentication exists but not marked as authenticated");
            return false;
        }

        return true;
    }

    private void handleAuthCallbackWithRetry(HttpServletRequest req, HttpServletResponse res,
                                             String code, FilterChain chain) throws IOException, ServletException {
        Exception lastError = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                chain.doFilter(processOAuthCallback(req, code), res);
                return;
            } catch (Exception e) {
                lastError = e;
                log.warn("Auth attempt {} failed: {}", i+1, e.getMessage());
                try { Thread.sleep(RETRY_DELAY); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        log.error("Auth failed after {} attempts", MAX_RETRIES, lastError);
        res.sendRedirect(req.getContextPath() + "/login?error=max_retries_exceeded");
    }

    private HttpServletRequest processOAuthCallback(HttpServletRequest req, String code) throws Exception {
        HttpSession session = req.getSession(true);
        try {
            String redirectUri = buildRedirectUrl(req);
            JSONObject token = new JSONObject(exchangeCodeForToken(code, redirectUri));
            JSONObject userInfo = new JSONObject(getUserInfo(token.getString("access_token")));

            String username = userInfo.getString("preferred_username");
            Set<GrantedAuthority> authorities = extractAuthorities(token, userInfo);

            synchronizeUser(username);
            setupSecurityContext(username, authorities, session, req);
            configureUserSession(session, username);

            return new AuthRequestWrapper(req, username);
        } catch (Exception e) {
            cleanup(session);
            throw e;
        }
    }

    private String exchangeCodeForToken(String code, String redirectUri) throws IOException {
        HttpURLConnection conn = configureSSL(new URL(tokenEndpoint).openConnection());
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes()));
        conn.setDoOutput(true);
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setHostnameVerifier((hostname, session) -> true);
        }
        String params = "grant_type=authorization_code&code=" + URLEncoder.encode(code, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");
        conn.getOutputStream().write(params.getBytes());
        return readResponse(conn);
    }

    private String getUserInfo(String accessToken) throws IOException {
        HttpURLConnection conn = configureSSL(new URL(userInfoEndpoint).openConnection());
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setHostnameVerifier((hostname, session) -> true);
        }
        return readResponse(conn);
    }

    private Set<GrantedAuthority> extractAuthorities(JSONObject token, JSONObject userInfo) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        extractRoles(token, authorities);
        extractRoles(userInfo, authorities);
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authorities;
    }

    private void extractRoles(JSONObject json, Set<GrantedAuthority> authorities) {
        Optional.ofNullable(json.optJSONObject("realm_access"))
                .map(ra -> ra.optJSONArray("roles"))
                .ifPresent(roles -> roles.forEach(r ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + r))));
    }

    private void synchronizeUser(String username) {
        try {
            DbAuthModule.loadUserData(username);
        } catch (Exception e) {
            log.info("Creating new user: {}", username);
        }
    }

    private void setupSecurityContext(String user, Set<GrantedAuthority> authorities,
                                      HttpSession session, HttpServletRequest req) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        auth.setDetails(new WebAuthenticationDetails(req));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        session.setAttribute(SPRING_SECURITY_CONTEXT, context);
        session.setMaxInactiveInterval(1800);
    }

    private void configureUserSession(HttpSession session, String user) throws AccessDeniedException, RepositoryException, DatabaseException {
        Optional.ofNullable(OKMUserConfig.getInstance().getConfig(null))
                .ifPresent(
                        cfg -> session.setAttribute("userConfig", cfg)
                );
    }

    private HttpURLConnection configureSSL(URLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setHostnameVerifier((h, s) -> true);
        }
        return (HttpURLConnection) conn;
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != 200) throw new IOException("HTTP " + conn.getResponseCode());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void redirectToAuthEndpoint(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String state = UUID.randomUUID().toString();
        req.getSession().setAttribute("oauthState", state);
        res.sendRedirect(authorizationEndpoint + "?response_type=code&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(buildRedirectUrl(req), "UTF-8") +
                "&state=" + state + "&scope=openid roles");
    }

    private String buildRedirectUrl(HttpServletRequest req) {
        StringBuffer url = req.getRequestURL();
        String query = req.getQueryString();
        return url.toString();
    }

    private void cleanup(HttpSession session) {
        SecurityContextHolder.clearContext();
        if (session != null) session.invalidate();
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}

    private static class AuthRequestWrapper extends HttpServletRequestWrapper {
        private final String user;

        public AuthRequestWrapper(HttpServletRequest req, String user) {
            super(req);
            this.user = user;
        }

        @Override public String getRemoteUser() { return user; }
        @Override public Principal getUserPrincipal() { return () -> user; }
    }
}
```

---

### **Step 3: Update appContext.xml**

Update the `appContext.xml` file to include the new filter and configure the security settings.

#### **Sample appContext.xml Configuration**

```xml
<beans:beans xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:security="http://www.springframework.org/schema/security"
             xmlns:context="http://www.springframework.org/schema/context"
             xmlns:jee="http://www.springframework.org/schema/jee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
                                 http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
                                 http://www.springframework.org/schema/security
                                 http://www.springframework.org/schema/security/spring-security-3.2.xsd
                                 http://www.springframework.org/schema/context
                                 http://www.springframework.org/schema/context/spring-context-3.1.xsd
                                 http://www.springframework.org/schema/jee
                                 http://www.springframework.org/schema/jee/spring-jee-3.1.xsd">

    <context:component-scan base-package="com.openkm"/>

    <!-- OpenKM API -->
    <beans:import resource="soap.xml"/>
    <beans:import resource="rest.xml"/>
    <beans:import resource="cmis.xml"/>

    <!-- Swagger -->
    <beans:bean id="swagger2Feature" class="com.openkm.core.Swagger2Config" />

    <security:global-method-security secured-annotations="enabled"/>

    <!-- Remove prefix to be able of use custom roles -->
    <beans:bean id="roleVoter" class="org.springframework.security.access.vote.RoleVoter">
        <beans:property name="rolePrefix" value="ROLE_"/>
    </beans:bean>

    <!-- Status -->
    <security:http pattern="/Status" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- Download -->
    <security:http pattern="/Download" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- Workflow deploy -->
    <security:http pattern="/workflow-register" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- WebDAV using Basic authentication -->
    <security:http pattern="/webdav/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- Syndication using Basic authentication -->
    <security:http pattern="/feed/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- OpenCMIS (Browser) using Basic authentication -->
    <security:http pattern="/cmis/browser/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- OpenCMIS (AtomPub) using Basic authentication -->
    <security:http pattern="/cmis/atom/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- OpenCMIS (AtomPub) using Basic authentication -->
    <security:http pattern="/cmis/atom11/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- REST -->
    <security:http pattern="/services/rest/**" create-session="stateless">
        <security:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY"/>
        <security:http-basic/>
    </security:http>

    <!-- Additional filter chain for normal users, matching all other requests -->
    <security:http use-expressions="true" entry-point-ref="customAuthenticationEntryPoint">
        <security:intercept-url pattern="/**" access="isAuthenticated()"/>
        <security:custom-filter position="FORM_LOGIN_FILTER" ref="customOAuth2Filter"/>
        <security:logout logout-success-url="/login.jsp"/>
    </security:http>

    <!-- Custom AuthenticationEntryPoint -->
    <beans:bean id="customAuthenticationEntryPoint" class="com.openkm.core.CustomLoginUrlAuthenticationEntryPoint">
        <beans:constructor-arg value="/login.jsp"/>
    </beans:bean>

    <!-- Custom OAuth2 Filter -->
    <beans:bean id="customOAuth2Filter" class="com.openkm.security.CustomOAuth2Filter">
        <beans:constructor-arg value="OpenKM"/>
        <beans:constructor-arg value="ZFB1qARH2EGhQw2VlxfURUxLntjRBrBI"/>
        <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/auth"/>
        <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/token"/>
        <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/userinfo"/>
    </beans:bean>

    <!-- Security access logger -->
    <beans:bean id="loggerListener" class="com.openkm.spring.LoggerListener"/>

    <jee:jndi-lookup id="dataSource" jndi-name="jdbc/OpenKMDS" resource-ref="true"/>

</beans:beans>

```

---

### **Step 4: Update Login page**

#### Update the following jsp page from this path: src/main/webapp/login_desktop.jsp

```html
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.dao.LanguageDAO" %>
<%@ page import="com.openkm.dao.bean.Language" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<head>
  <meta charset="utf-8">
  <meta name="author" content="OpenKM">
  <meta content="OpenKM is an EDRMS EDRMS, Document Management System and Record Management, easily to manage digital content, simplify your workload and yield high efficiency." name="description">
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/logo/favicon" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap/bootstrap.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/font-awesome.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/login.css" type="text/css" />
  <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easy-ticker.min.js"></script>

  <% if (Config.RSS_NEWS) { %>
  <script type="text/javascript">
    $(document).ready(function () {
      // Always show sticker when rss is enabled
      var loaded = false;
      $('#stickerController').show();

      // Change div style when sticker is enabled
      $('#openkmNews').width('<%=Config.RSS_NEWS_BOX_WIDTH %>px');
      $('#feedContainer').width('<%=Config.RSS_NEWS_BOX_WIDTH %>px');
      $('#openkmVersion').addClass("vticker-border-right");

      $('#stop').on({
        'click': function () {
          var src = ($('#stopImg').attr('src') === 'img/stop.png')
            ? 'img/pause.png'
            : 'img/stop.png';
          $('#stopImg').attr('src', src);
        }
      });

      $('#eye').on({
        'click': function () {
          if ($('#eyeImg').attr('src') === 'img/eye.png') {
            localStorage.setItem('openkmNews', 'hide');
            $('#eyeImg').attr('src', 'img/eye-disabled.png');
            $('#feedContainer').hide();
            $('#stop').hide();
            $('#forward').hide();
            $('#backward').hide();
          } else {
            localStorage.setItem('openkmNews', 'show');
            $('#eyeImg').attr('src', 'img/eye.png');
            $('#feedContainer').show();
            $('#stop').show();
            $('#forward').show();
            $('#backward').show();
            loadRss();
          }
        }
      });

      if (localStorage.getItem('openkmNews') == 'hide') {
        $('#eyeImg').attr('src', 'img/eye-disabled.png');
        $('#feedContainer').hide();
        $('#stop').hide();
        $('#forward').hide();
        $('#backward').hide();
      } else {
        $('#feedContainer').show();
        loadRss();
      }

      function loadRss() {
        // Rss must be loaded only one time, because easyTicker can not be executed twice
        if (!loaded) {
          loaded = true;
          $.get("Rss", function (data) {
            // Clean sticker
            $('#feedContainer').empty();

            // Parse xml
            var html = '<ul>';
            $(data).find('item').each(function () {
              var $item = $(this);
              var title = $item.find('title').text();
              var link = $item.find('link').text();
              var description = $item.find('description').text();
              var pubDate = $item.find('pubDate').text();

              html += "<li>";
              html += "<h4>" + title + "</h4>";
              html += "<em>" + pubDate + "</em>";
              html += "<p>" + description + "</p>";
              html += "<div align=\"right\"><a href=\"" + link + "\" target=\"_blank\" style=\"cursor:hand !important;\">Read More</a></div>";
              html += "</li>";
            });
            html += '</ul>';

            // Set html
            $('#feedContainer').append(html);

            // Load sticker
            $('#feedContainer').easyTicker({
              direction: 'up',
              easing: 'swing',
              speed: 'slow',
              interval: 5000,
              height: 'auto',
              visible: <%=Config.RSS_NEWS_VISIBLE %>,
              mousePause: 1,
              controls: {
                up: '#forward',
                down: '#backward',
                toggle: '#stop'
              }
            });
          });
        }
      }
    });
  </script>
  <% } %>
  <%
    Locale locale = request.getLocale();
    Cookie[] cookies = request.getCookies();
    String preset = null;

    if (cookies != null) {
      for (int i=0; i<cookies.length; i++) {
        if (cookies[i].getName().equals("lang")) {
          preset = cookies[i].getValue();
        }
      }
    }

    if (preset == null || preset.equals("")) {
      preset = locale.getLanguage() + "-" + locale.getCountry();
    }
  %>
  <title><%=Config.TEXT_TITLE%></title>
</head>
<body onload="document.forms[0].elements[0].focus()">
<div id="openkmNews" class="openkm-news">
  <div id="openkmVersion" class="openkm-version">
    <strong>Community Version</strong>
    <div id="stickerController" class="openkm-sticker" style="display:none;">
      <a href="#" id="backward" style="cursor:hand !important;"><img src="img/backward.png" alt="Backward" title="Backward" /></a>
      <a href="#" id="stop" style="cursor:hand !important;"><img id="stopImg" src="img/stop.png" alt="Stop" title="Stop"/></a>
      <a href="#" id="forward" style="cursor:hand !important;"><img src="img/forward.png" alt="Forward" title="Forward" /></a>
      <a href="#" id="eye" style="cursor:hand !important;"><img id="eyeImg" src="img/eye.png" alt="Show / hide news" title="Show / hide news" /></a>
    </div>
  </div>
  <div style="display:none;" id="feedContainer" class="vticker"></div>
</div>

<div id="login-background" class="background-zen">
  <div id="col-xs-12" class="hidden-xs hidden-sm hidden-md" style="height:100%;">
    <div class="background-zen" style="height:100%;"></div>
  </div>
</div>
<u:constantsMap className="com.openkm.core.Config" var="Config"/>
<div id="login-container">
  <div class="login-title">
    <img id="login-image" class="img-responsive center-block" src="logo/login">
  </div>
  <div class="block remove-margin" style="border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;">
    <form name="loginform" method="post" action="j_spring_security_check" onsubmit="setCookie()"
          class="form-horizontal form-bordered form-control-borderless" id="form-login">
      <div class="form-group form-header text-center">
        <div class="col-xs-12">
          <%=Config.TEXT_BANNER %>
          <%=Config.TEXT_WELCOME %>
        </div>
      </div>
      <c:if test="${not empty param.error}">
        <div class="form-group form-error">
          <div id="col-xs-12">
            <p class="text-danger text-center">
              Authentication error
              <c:if test="${Config.USER_PASSWORD_RESET && Config.PRINCIPAL_ADAPTER == 'com.openkm.principal.DatabasePrincipalAdapter'}">
                (<a href="password_reset.jsp">Forgot your password?</a>)
              </c:if>
            </p>
          </div>
        </div>
      </c:if>
      <div class="form-group">
        <div class="col-xs-12">
          <div class="input-group">
            <% if (Config.SYSTEM_MAINTENANCE) { %>
            <span class="input-group-addon"><i class="gi gi-user"></i></span>
            <input name="j_username" id="j_username" type="hidden"
                   value="<%=Config.SYSTEM_LOGIN_LOWERCASE?Config.ADMIN_USER.toLowerCase():Config.ADMIN_USER%>"
                   class="form-control input-lg" placeholder="System under maintenance"/>
            <% } else { %>
            <span class="input-group-addon"><i class="fa fa-user"></i></span>
            <input name="j_username" id="j_username"
                   type="text" <%=Config.SYSTEM_LOGIN_LOWERCASE ? "onchange=\"makeLowercase();\"" : "" %>
                   class="form-control input-lg" placeholder="User"/>
            <% } %>
          </div>
        </div>
      </div>
      <div class="form-group">
        <div class="col-xs-12">
          <div class="input-group">
            <span class="input-group-addon"><i class="fa fa-asterisk"></i></span>
            <input type="password" id="j_password" name="j_password" class="form-control input-lg"
                   placeholder="Password">
          </div>
        </div>
      </div>
      <div class="form-group form-actions">
        <div class="col-xs-5">
          <select name="j_language" id="j_language" class="form-control"
                  style="border-bottom: 1px solid #eaedf1 !important;">
            <%
              List<Language> langs = LanguageDAO.findAll();
              String whole = null;
              String part = null;

              // Match whole locale
              for (Language lang : langs) {
                String id = lang.getId();

                if (preset.equalsIgnoreCase(id)) {
                  whole = id;
                } else if (preset.substring(0, 2).equalsIgnoreCase(id.substring(0, 2))) {
                  part = id;
                }
              }

              // Select selected
              for (Language lang : langs) {
                String id = lang.getId();
                String selected = "";

                if (whole != null && id.equalsIgnoreCase(whole)) {
                  selected = "selected";
                } else if (whole == null && part != null && id.equalsIgnoreCase(part)) {
                  selected = "selected";
                } else if (whole == null && part == null && Language.DEFAULT.equals(id)) {
                  selected = "selected";
                }

                out.print("<option " + selected + " value=\"" + id + "\">" + lang.getName() + "</option>");
              }
            %>
          </select>
        </div>
        <div class="col-xs-4 pull-right">
          <button name="submit" type="submit" class="btn btn-sm btn-primary btn-block"><i class="fa fa-key"></i> Login
          </button>
        </div>
      </div>
      <div class="form-group text-center">
        <a href="<%=request.getContextPath() %>/oauth2/authorization/keycloak" class="btn btn-sm btn-primary btn-block">
          <i class="fa fa-key"></i> Login with Keycloak
        </a>
      </div>
      <% if (Config.SYSTEM_DEMO) { %>
      <div class="form-group low" style="background-color:white !important;">
        <div class="col-xs-12 hidden-lg">
          <jsp:include flush="true" page="login_demo_users.jsp"/>
        </div>
      </div>
      <% } %>
      <div class="form-group form-footer"
           style="border-bottom-left-radius: 10px !important; border-bottom-right-radius: 10px !important;">
        <div class="col-xs-12 text-center">
          <p>&copy; 2006-2022 OpenKM. All rights reserved.</p>
        </div>
      </div>
    </form>
  </div>
</div>

<% if (Config.SYSTEM_DEMO) { %>
<div class="demo_users high">
  <div class="col-xs-12 hidden-xs hidden-sm hidden-md">
    <jsp:include flush="true" page="login_demo_users.jsp"/>
  </div>
</div>
<% } %>

<script type="text/javascript">
  function makeLowercase() {
    var username = document.getElementById('j_username');
    username.value = username.value.toLowerCase();
  }

  function setCookie() {
    var exdate = new Date();
    var value = document.getElementById('j_language').value;
    exdate.setDate(exdate.getDate() + 7);
    document.cookie = "lang=" + escape(value) + ";expires=" + exdate.toUTCString();
  }
</script>
</body>
</html>

```

### Notes:
* Make sure to adapt the following values from the appContext
```xml
<beans:bean id="customOAuth2Filter" class="com.openkm.security.CustomOAuth2Filter">
    <beans:constructor-arg value="OpenKM"/>
    <beans:constructor-arg value="ZFB1qARH2EGhQw2VlxfURUxLntjRBrBI"/>
    <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/auth"/>
    <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/token"/>
    <beans:constructor-arg value="http://localhost:8180/realms/GUH/protocol/openid-connect/userinfo"/>
</beans:bean>
```
