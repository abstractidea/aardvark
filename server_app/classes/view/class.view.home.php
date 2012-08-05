<?php
	loadView();

	class homePage extends page {
		public function gen_content($auth_result='') {
			echo '
				<h3 id="pageTitle">Project Aardvark JSON Parsing Test</h3>
				<div>'.$auth_result->message.'</div>
			';
		}
	}
?>