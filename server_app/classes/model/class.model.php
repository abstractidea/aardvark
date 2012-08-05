<?php
	class model {
		public function gen_token($length=16) {
			$char = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';    
			$string = '';

			for ($i=0; $i<$length; $i++) {
				$string .= $char{mt_rand(0, strlen($char)-1)};
			}

			return $string;
		}
		private function parse_json($data='') {
			if ($data=='') {
				return FALSE;
			}
			else {
				$jsonArray = json_decode($data);

				return $jsonArray;
			}
		}
		public function collect_json() {
			$contents = file_get_contents('php://input');
			$json = $this->parse_json($contents);
			
			return $json;
		}
		public function send_gcm_message($request='') {
			$client_id = $request['device_id'];
			$authorization = $request['authorized'];
			$communication_fields = array(
				'registration_ids' => array($client_id),
				'data' => array(
					'device_registration_id'=>$client_id,
					'user_id'=>'temp_user_id_0123',
					'authorization'=>$authorization
				)
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
		}
		public function authenticate_client($client_info='') {
			$token = $client_info->token;
			$event_whitelist = array();

			$db = new mysqli(DB_HOST, DB_USER, DB_PASS, DB);
			$query = "SELECT * FROM users_information WHERE token='$token' LIMIT 1";
			$result = $db->query($query);
			$username = $result->fetch_object();
			$username = $username->username;
			$result->close();

			$query = "SELECT * FROM whitelist WHERE event_id='$client_info->event_id'";
			$result = $db->query($query);

			while ($row = $result->fetch_object()) {
				$event_whitelist[] = $row->username;
			}
			$result->close();
			$db->close();
			
			if (in_array($username, $event_whitelist)) {
				return $username;
			}
			else {
				return FALSE;
			}
		}
		private function dbEncode($input='') {
			$db = new mysqli(DB_HOST, DB_USER, DB_PASS, DB);
			$input = htmlspecialchars($input);
			$input = $db->real_escape_string($input);
			$db->close();

			return $input;
		}
		private function dbDecode($input='') {
			$input = stripslashes($input);

			return $input;
		}
	}
?>