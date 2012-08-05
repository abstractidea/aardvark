<?php
	class model {
		private function gen_filename($length=16) {
			$char = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';    
			$string = '';

			for ($i=0; $i<$length; $i++) {
				$string .= $char{mt_rand(0, strlen($char)-1)};
			}

			return $string;
		}
		private function parse_json($file='') {
			if ($file=='') {
				return FALSE;
			}
			else {
				$file_contents = file_get_contents(ROOT.'resources/json/'.$file);
				$jsonArray = json_decode($file_contents);

				return $jsonArray;
			}
		}
		public function store_json() {
			$contents = file_get_contents('php://input');
			$filename = $this->gen_filename().'.json';

			while (file_exists($filename)) {
				$filename = $this->gen_filename().'.json';
			}

			$file = fopen(ROOT.'resources/json/'.$filename, "c");
			fwrite($file, $contents);
			fclose($file);
		}
		public function send_gcm_message($client_id='') {
			$message = 'This is a test message for GCM.';

			$communication_fields = array(
				'device_registration_id'=>$client_id,
				'user_id'=>'temp_user_id_0123'
			);
			$headers = array(
				'Authorization: key='.GCM_API_KEY,
				'Content-Type: application/json'
			);
			$curl = curl_init();
			
			curl_setopt($curl, CURLOPT_URL, GCM_SEND_URL);
			curl_setopt($curl, CURLOPT_POST, TRUE);
			curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
			curl_setopt($curl, CURLOPT_RETURNTRANSFER, TRUE);
			curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($communication_fields));

			$result = curl_exec($curl);
			curl_close($curl);

			die($result);
		}
	}
?>