CREATE TABLE 'user'
(
	'recordName' VARCHAR(50) PRIMARY KEY NOT NULL,
	'currentVersion' VARCHAR(50) NOT NULL,
	'upgradeableVersion' VARCHAR(50) NOT NULL,
	'rootDir' VARCHAR(50) NOT NULL,
	'patchDir' VARCHAR(50) NOT NULL,
	'parameterDir' VARCHAR(50),
	'serverType' VARCHAR(50),
	'recordPath' VARCHAR(50) NOT NULL,
	'deploymentModel' VARCHAR(50),
	'userName' VARCHAR(50) NOT NULL,
	'date' TEXT NOT NULL DEFAULT (datetime('now','localtime'))
);

CREATE TABLE 'ref'
(
	'recordName' VARCHAR(50) NOT NULL,
	'version' VARCHAR(50) NOT NULL,
	'refNo' VARCHAR(50) NOT NULL,
	'packageName' VARCHAR(50) NOT NULL,
	'fileName' VARCHAR(100) NOT NULL,
	'status' VARCHAR(50) NOT NULL,
	'type' VARCHAR(50) NOT NULL,
	'date' TEXT NOT NULL DEFAULT (datetime('now','localtime'))
)