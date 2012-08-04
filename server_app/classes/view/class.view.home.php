<?php
	loadView();

	class homePage extends page {
		public function gen_content($json='') {
			echo '
				<h3 id="pageTitle">Project Aarvark JSON Parsing Test</h3>
				<br />
			';
			foreach ($json as $key=>$value) {
				echo $key.'<br />'.$value.'<br /><br />';
			}
		}
	}
?>