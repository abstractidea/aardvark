<?php
	require_once('common/config.php');

	if (isset($_REQUEST['logout'])) {
		loadModel('session');
		$model = new session;

		$model->deauthenticate();
		header('refresh:0;url="'.WEB_ROOT.'"');
	}
	else if (isset($_REQUEST['signup'])) {
		loadModel();
		loadModel('session');
		$model = new model;
		$session = new session;

		if (!$session->session_verify()) {
			if ((isset($_REQUEST['submit']))&&($_REQUEST['submit']=='TRUE')) {
				$token = $model->gen_token(TOKEN_LENGTH);
				$result = $session->register($_REQUEST['username'], $_REQUEST['password'], $token);

				header('refresh:0;url="'.WEB_ROOT.'"');
			}
			else {
				loadView('signup');
				$view = new signupPage;

				$view->gen_page();
			}
		}
		else {
			header('refresh:0;url="'.WEB_ROOT.'"');
		}
	}
	else if (isset($_REQUEST['login'])) {
		loadModel('session');
		$model = new session;

		if ($model->session_verify()) {
			loadView('login');
			$view = new loginPage;

			$view->gen_page('session_active');
		}
		else if ((isset($_REQUEST['submit']))&&($_REQUEST['submit']=='TRUE')) {
			loadModel('session');
			$model = new session;

			$result = $model->authenticate($_REQUEST['username'], $_REQUEST['password']);
			if ($result) {
				header('refresh:0;url="'.WEB_ROOT.'"');
			}
			else {
				loadView('login');
				$view = new loginPage;

				$view->gen_page('login_failed');
			}
		}
		else {
			loadView('login');
			$view = new loginPage;

			$view->gen_page('login');
		}
	}
	else if (isset($_REQUEST['checkin'])) {
		loadModel();
		$model = new model;

		$json = $model->collect_json();
		$result = $model->authenticate_client($json);
		$request['device_id'] = $json->device_registration_id;
		$request['bouncer_id'] = $json->bouncer_id;

		if ($result) {
			$request['authorized'] = 'TRUE';

			$model->send_gcm_message($request);
			$response = "{
				'status_code':'100',
				'user_name':'$result'
			}";
		}
		else {
			$request['authorized'] = 'FALSE';

			$model->send_gcm_message($request);
			$response = "{
				'status_code':'200',
				'user_name':'Not Given'
			}";
		}
		header('Content-Length: '.strlen($response));
		echo ($response);
	}
	else {
		loadModel();
		loadView('home');

		//$model = new model;
		$view = new homePage;

		//$result = $model->parse_json('test.json');
		$view->gen_page($result);
	}
?>