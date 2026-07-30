---
subtitle: placeholder page
---

Environments are a set of associated properties used to connect to a database.

The concept of an environment has been created to allow Flyway to work easily with several different databases or configurations from a single configuration file.

## Environment settings

| Setting                                                                                                         | Required                                                      | Type         | Description                                                                            |
|-----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------|--------------|----------------------------------------------------------------------------------------|
| [`url`](<Configuration/Environments Namespace/Environment URL Setting>)                                         | Yes                                                           | String       | Database JDBC URL.                                                                     |
| [`user`](<Configuration/Environments Namespace/Environment User Setting>)                                       | No                                                            | String       | Database user name.                                                                    |
| [`password`](<Configuration/Environments Namespace/Environment Password Setting>)                               | No                                                            | String       | Database password (usually specified using a resolver).                                |
| [`schemas`](<Configuration/Environments Namespace/Environment Schemas Setting>)                                 | No                                                            | String array | The schemas to track.                                                                  |
| [`displayName`](<Configuration/Environments Namespace/Environment Display Name Setting>)                        | No                                                            | String       | The name of the database, as it appears in Flyway Desktop UI.                          |
| [`driver`](<Configuration/Environments Namespace/Environment Driver Setting>)                                   | No                                                            | String       | The jdbc driver to use to connect to the database.                                     |
| [`connectRetries`](<Configuration/Environments Namespace/Environment Connect Retries Setting>)                  | No                                                            | Integer      | The maximum number of retries when attempting to connect to the database.              |
| [`connectRetriesInterval`](<Configuration/Environments Namespace/Environment Connect Retries Interval Setting>) | No                                                            | Integer      | The maximum time between retries when attempting to connect to the database.           |
| [`initSql`](<Configuration/Environments Namespace/Environment Init SQL Setting>)                                | No                                                            | String       | SQL statements to be run immediately after a database connection has been established. |
| [`provisioner`](<Configuration/Environments Namespace/Environment Provisioner Setting>)                         | Determines the type of provisioning to use for this database. |

## Environment namespaces

| Namespace                                                                                        | Description                                                                                            |
|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| [`flyway`](<Configuration/Environments Namespace/Environment Flyway Namespace>)                  | Flyway namespace properties to override for the environment.                                           |
| [`jdbcProperties`](<Configuration/Environments Namespace/Environment JDBC Properties Namespace>) | JDBC properties to pass to the JDBC driver when establishing a connection.                             |
| [`resolvers`](<Configuration/Environments Namespace/Environment Resolvers Namespace>)            | Additional properties relating to resolvers being used in calculating necessary connection properties. |

## TOML Configuration

These parameters should be configured in an [environments](<Configuration/Environments Namespace>) namespace.

```powershell
[environments.example]
url = "jdbc:sqlite:local_database1.db"
username = "bob"
...
[environments.another_example]
url = "jdbc:sqlite:local_database2.db"
username = "jeff"
...
[environments.another_example]
locations = ["defaultLocation","customLocation"]
```

### Resolvers

Usually you won't want to keep sensitive information in a plain text configuration file and so [resolvers](<Configuration/Resolvers>) can be used to pull information into your configuration from a variety of external sources like environment variables and secrets managers.

## Command line configuration

It is possible to define an environment on the commandline, for example:

```powershell
./flyway info -environments.example.url=jdbc:sqlite:local_database.db -environment=example
```

Note: You define an environment using the
`environments` (plural) namespace but you specify which environment to use with the
`environment` (singular) parameter.

## Environment Variables

Any [environment setting](<Configuration/Environments Namespace>) can be set for a specific named environment using an OS environment variable of the form `environments_{environment name}_{setting}`:

```properties
environments_example_url=jdbc:sqlite:local_database.db
environments_example_password=mysecretpassword
```

Settings in a nested namespace (such as [`flyway`](<Configuration/Environments Namespace/Environment Flyway Namespace>) or [`jdbcProperties`](<Configuration/Environments Namespace/Environment JDBC Properties Namespace>)) are addressed the same way, with an extra `_` per level:

```properties
environments_example_flyway_locations=filesystem:./sql1,filesystem:./sql2
environments_example_jdbcProperties_accessToken=mytoken
```

The `environments_` prefix is case-insensitive, so `ENVIRONMENTS_EXAMPLE_PASSWORD` is an equally valid way of setting the same value. This only extends to the prefix itself and to setting names backed by a Java field (which are matched case-insensitively). It does not extend to nested key-value settings such as [`jdbcProperties`](<Configuration/Environments Namespace/Environment JDBC Properties Namespace>), whose keys are passed through to the JDBC driver verbatim and may be case-sensitive: `ENVIRONMENTS_EXAMPLE_JDBCPROPERTIES_ACCESSTOKEN` would be sent to the driver as `accesstoken`, not `accessToken`, so nested settings still need their exact casing.

## Legacy configuration

Properties that were originally part of the regular flyway configuration (`-url`, `-user`,
`-password`) can still be used for backwards compatibility. Under the hood, Flyway will map these onto an environment named
`default` for you and then use them like any other environment.
