<?php
	class session {
		public function id_gen($length=SESSION_ID_LENGTH) {
			$char = '!@#$%^&*()_-+=0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
			$id = '';

			for ($i=0; $i<$length; ++$i) {
				$id .= $char{mt_rand(0, strlen($char)-1)};
			}

			return $id;
		}
		// Usernames may consist of Numbers, Letters, and non-adjacent underscores
		public function username_verify($username='') {
			if ($username=='') {
				return FALSE;
			}
			for ($i=0; $i<strlen($username); ++$i) {
				if (strstr(USERNAME_WHITELIST, $username{$i})) {
					continue;
				}
				else {
					$this->form_error[] = 'username_characters';
					return FALSE;
				}
			}
			if ((strstr($username, '__'))||(strstr($username, '--'))||(strstr($username, '_-'))||(strstr($username, '-_'))) {
				$this->form_error[] = 'username_characters';
				return FALSE;
			}
			else {
				return TRUE;
			}
		}
		public function password_verify($password=FALSE) {
			if ($password==NULL) {
				log("NULL Password Received: ".$password);
				$this->form_error[] = 'password_characters';

				return FALSE;
			}
			else if (($password)&&(is_string($password))&&(strlen($password)>=PASSWORD_MIN_LENGTH)&&(strlen($password)<=PASSWORD_MAX_LENGTH)) {
				for ($i=0; $i<strlen($password); ++$i) {
					if (strstr(PASSWORD_WHITELIST, $password{$i})) {
						continue;
					}
					else {
						$this->form_error[] = 'password_characters';
						return FALSE;
					}
				}

				return TRUE;
			}
			else {
				$this->form_error[] = 'password_length';
				return FALSE;
			}
			
		}
		public function password_set($password=FALSE) {
			if ($this->password_verify($password)) {
				$salt = PASSWORD_SALT;
				$pass_salted = md5($salt.$password.$salt);

				return $pass_salted;
			}
			else {
				return FALSE;
			}
		}
		public function register($username='', $password_orig='', $token='') {
			$password = $this->password_set($password_orig);

			if (($this->username_verify($username))&&($password)) {
				$db = new mysqli(DB_HOST, DB_USER, DB_PASS, DB);

				$query = "SELECT * FROM ".DB_TABLE_USERS." WHERE username='$username' LIMIT 1";
				$queryResult = $db->query($query);
				$object = $queryResult->fetch_object();
				$queryResult->close();

				if ($username==$object->username) {
					$this->form_error[] = 'username_taken';
					return FALSE;
				}
				else {
					$query = "INSERT INTO ".DB_TABLE_USERS." (username, password, roleID) VALUES ('$username', '$password', '30')";
					$db->query($query);

					$this->authenticate($username, $password_orig);
				}

				$db->close();
			}
			else {
				$this->form_error[] = 'unknown_error';
				return $this->form_error;
			}
		}
		public function authenticate($username='', $password='') {
			$password = $this->password_set($password);
			if (($this->username_verify($username))&&($password)) {
				$db = new mysqli(DB_HOST, DB_USER, DB_PASS, DB);

				$query = "SELECT * FROM ".DB_TABLE_USERS." WHERE username='$username' LIMIT 1";
				$queryResult = $db->query($query);
				$object = $queryResult->fetch_object();
				$queryResult->close();

				$query2 = "SELECT * FROM ".DB_TABLE_USERS_ROLES." WHERE roleID='$object->roleID' LIMIT 1";
				$queryResult2 = $db->query($query2);
				$object2 = $queryResult2->fetch_object();
				$queryResult2->close();

				$db->close();

				// Check password
				if ($password==$object->password) {
					$_SESSION['USERNAME'] = $username;
					$_SESSION['SESSION_ID'] = $this->id_gen();
					$_SESSION['ROLE'] = $object2->role;

					// Security Check
					$_SESSION['BROWSER'] = '';
					$_SESSION['OS'] = '';
				}
				else {
					sleep(LOGIN_SLEEP_TIME);
					return FALSE;
				}

				return TRUE;
			}
			else {
				return FALSE;
			}
		}
		public function deauthenticate() {
			session_unset();
			session_destroy();
		}
		public function session_verify() {
			if (isset($_SESSION['USERNAME'])&&isset($_SESSION['SESSION_ID'])&&isset($_SESSION['ROLE'])) {
				return TRUE;
			}
			else {
				// This will make sure no session variables are still storing information.
				session_unset();

				return FALSE;
			}
		}
	}
?>