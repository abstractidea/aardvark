<?php
	// Developer Emails
	define('DEV_DAVID_M_EMAIL', 'david@serym.com');
	define('DEV_DAVID_B_EMAIL', '');
	define('DEV_MATTHEW_V_EMAIL', '');
	define('DEV_MATTHEW_M_EMAIL', '');
	define('DEV_JACOB_S_EMAIL', '');

	// Directory Structure - Server Constants
	define('CLASSES', ROOT.'classes/');
	define('LOGS', ROOT.'logs/');

	// Directory Structure - Local Constants
	define('RESOURCES', WEB_ROOT.'resources/');

	// Database
	define('DB', '');
	define('DB_HOST', '');
	define('DB_USER', '');
	define('DB_PASS', '');
	define('DB_TABLE_USERS', '');
	define('DB_TABLE_USERS_ROLES', '');
	define('TOKEN_LENGTH', 32);

	// GCM Defaults
	define('GCM_API_KEY', '');
	define('GCM_SEND_URL', 'https://android.googleapis.com/gcm/send');

	// PHP Settings
	define('DATE_FORMAT', 'ymd');
	ini_set('error_log', LOGS.date(DATE_FORMAT).'_error.log');
	ini_set('log_errors', 1);
	ini_set('log_errors_max_len', 1024);

	// Resource Settings
	define('SERVER_URL', $_SERVER['SERVER_NAME'].$_SERVER['REQUEST_URI']);

	// Session Settings
	session_start();
	define('SESSION_ID_LENGTH', 16);
	define('LOGIN_SLEEP_TIME', 3);
	define('USERNAME_WHITELIST', '@.-_0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
	define('USERNAME_MIN_LENGTH', 8);
	define('USERNAME_MAX_LENGTH', 64);
	define('PASSWORD_WHITELIST', '!@#$%^&*()._-+=0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
	define('PASSWORD_SALT', '');
	define('PASSWORD_MIN_LENGTH', 8);
	define('PASSWORD_MAX_LENGTH', 512);
?>