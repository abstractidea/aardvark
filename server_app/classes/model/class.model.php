<?php
	class model {
		public function parse_json($file='') {
			if ($file=='') {
				return FALSE;
			}
			else {
				$file_contents = file_get_contents(ROOT.'resources/json/'.$file);
				$jsonArray = json_decode($file_contents);

				return $jsonArray;
			}
		}
	}
?>