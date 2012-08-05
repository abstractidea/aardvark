<?php
	loadView();

	class gcm_testPage extends page {
		public function gen_content() {
			echo '
				<form action="'.WEB_ROOT.'send_gcm" method="post">
					<input type="hidden" name="submit" value="TRUE" />
					<label>Enter Device ID:</label><br />
					<input type="text" name="device_id" /><br />
					<input type="submit" value="Send GCM Message" />
				</form>
			';
		}
	}
?>