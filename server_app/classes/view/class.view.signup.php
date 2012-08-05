<?php
	loadView();

	class signupPage extends page {
		public function gen_content($error='') {
			echo '
				<h3 id="pageTitle">Sign Up</h3>
			';
			for ($i=0; $i<count($error); ++$i) {
				switch($error[$i]) {
					case 'do_not_match':
						echo '<p class="error">Emails or Passwords Do Not Match</p>';
						break;
					case 'username_taken':
						echo '<p class="error">Email Address is Taken<p>';
						break;
					case 'username_characters':
						echo '<p class="error">Email has not allowed characters</p>';
						break;
					case 'password_characters':
						echo '<p class="error">Password has not allowed characters</p>';
						break;
					case 'password_length':
						echo '<p class="error">Password does not meet length requirements</p>';
						break;
					case 'unknown_error':
						echo '<p class="error">Unknown Error</p>';
						break;
					default:
						break;
				}
			}
			echo '
				<form action="'.WEB_ROOT.'signup" method=POST>
					<input type="hidden" name="submit" value="TRUE" />
					<table>
						<tr>
							<td class="formLabel"><label>Email: </label></td>
							<td><input class="formItem" type="text" name="username" /></td>
						<tr>
						<tr>
							<td class="formLabel"><label>Verify Email: </label></td>
							<td><input class="formItem" type="text" name="username_verify" /></td>
						<tr>
						<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
						<tr>
							<td class="formLabel"><label>Password: </label></td>
							<td><input class="formItem" type="password" name="password" /></td>
						</tr>
						<tr>
							<td class="formLabel"><label>Verify Password: </label></td>
							<td><input class="formItem" type="password" name="password_verify" /></td>
						</tr>
						<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
						<tr>
							<td><input class="formSubmit" type="submit" value="Sign Up" /></td>
						</tr>
					</table>
				</form>
			';
		}
	}
?>