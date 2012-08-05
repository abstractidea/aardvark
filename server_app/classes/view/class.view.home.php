<?php
	loadView();

	class homePage extends page {
		public function gen_content($auth_result='') {
			echo '
				<div>'.$auth_result->message.'</div>
			';
		}
	}
?>