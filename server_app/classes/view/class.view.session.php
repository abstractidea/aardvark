<?php
	loadView();

	class sessionPage extends page {
		public function gen_content($error='') {
			if ($error=='session_active') {
				echo '
					<h3 id="pageTitle" class="error">You are already logged in.</h3>
				';
			}
			else if ($error=='login_failed') {
				echo '
					<h3 id="pageTitle">Login</h3>
					<p class="error">Username or Password Incorrect</p>
					<form action="'.WEB_ROOT.'login" method=POST>
						<input type="hidden" name="submit" value="TRUE" />
						<table>
							<tr>
								<td class="loginLabel"><label>Username: </label></td>
								<td><input class="LoginFormItem" type="text" name="username" /></td>
							<tr>
							<tr>
								<td class="loginLabel"><label>Password: </label></td>
								<td><input class="LoginFormItem" type="password" name="password" /></td>
							</tr>
							<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
							<tr>
								<td><input class="pageSubmitForm" type="submit" value="Login" /></td>
							</tr>
						</table>
					</form>
				';
			}
			else {
				echo '
					<h3 id="pageTitle">Login</h3>
					<form action="'.WEB_ROOT.'login" method=POST>
						<input type="hidden" name="submit" value="TRUE" />
						<table>
							<tr>
								<td class="loginLabel"><label>Username: </label></td>
								<td><input class="LoginFormItem" type="text" name="username" /></td>
							<tr>
							<tr>
								<td class="loginLabel"><label>Password: </label></td>
								<td><input class="LoginFormItem" type="password" name="password" /></td>
							</tr>
							<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
							<tr>
								<td><input class="pageSubmitForm" type="submit" value="Login" /></td>
							</tr>
						</table>
					</form>
				';
			}
		}
	}
?>