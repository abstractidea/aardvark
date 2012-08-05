<?php
	require_once('common/config.php');


	if (isset($_REQUEST['logout'])) {
		loadModel('session');
		$model = new session;

		$model->deauthenticate();
		header('refresh:0;url="'.WEB_ROOT.'"');
	}
	else if (isset($_REQUEST['login'])) {
		loadModel('session');
		$model = new session;

		if ($model->session_verify()) {
			loadView('session');
			$view = new sessionPage;

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
				loadView('session');
				$view = new sessionPage;

				$view->gen_page('login_failed');
			}
		}
		else {
			loadView('session');
			$view = new sessionPage;

			$view->gen_page('login');
		}
	}
	else if (isset($_REQUEST['checkin'])) {
		loadModel();
		$model = new model;

		$model->store_json();
	}
	else if (isset($_REQUEST['send_gcm'])) {
		if ((isset($_REQUEST['submit']))&&($_REQUEST['submit']=='TRUE')) {
			loadModel();
			$model = new model;

			$model->send_gcm_message($_REQUEST['device_id']);
		}
		else {
			loadView('gcm_test');

			$view = new gcm_testPage;

			$view->gen_page();
		}
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